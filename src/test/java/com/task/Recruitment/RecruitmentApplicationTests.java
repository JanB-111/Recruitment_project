package com.task.Recruitment;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RecruitmentApplicationTests {

    private static WireMockServer mock_server;

    @BeforeAll
    public static void setup() {
        mock_server = new WireMockServer();
        mock_server.start();

        configureFor("localhost", mock_server.port());
        Controller.setPath("http://localhost:" + mock_server.port());

        declareWiremockMappings();
    }


    @Test
    public void wiremockConnectionTest() {
        String response = Controller.restWiremockConnectionTest();
        assertEquals("{OK}", response);
    }


    @Test
    public void fetchingBranchDataTest() {
        Client test_client = new Client("existing_user");
        String response = test_client.getData();
        assertEquals("Login: existing_user\n" +
                "\n" +
                "Repository name: Repo_1\n" +
                "\tBranch name: Branch_1\n" +
                "\tBranch sha: 1234\n" +
                "\n" +
                "\tBranch name: Branch_2\n" +
                "\tBranch sha: 5678\n" +
                "\n" +
                "Repository name: Repo_2\n" +
                "\tBranch name: Branch_1\n" +
                "\tBranch sha: 4321\n" +
                "\n" +
                "\tBranch name: Branch_2\n" +
                "\tBranch sha: 8765\n" +
                "\n",response);
    }

    @Test
    public void ignoringForksTest() {
        Client test_client = new Client("alternate_existing_user");
        String response = test_client.getData();
        assertEquals("Login: existing_user\n" +
                "\n" +
                "Repository name: Repo_1\n" +
                "\tBranch name: Branch_1\n" +
                "\tBranch sha: 1234\n" +
                "\n" +
                "\tBranch name: Branch_2\n" +
                "\tBranch sha: 5678\n" +
                "\n",response);
    }

    @Test
    public void incorrectUserTest() {
        Client test_client = new Client("nonexistent_user");
        String response = test_client.getData();
        assertEquals("{\n" +
                "status: 404\n" +
                "message: Not Found\n" +
                "}", response);
    }

    @Test
    public void incompleteUserJSON() {
        Client test_client = new Client("incomplete_user");
        String response = test_client.getData();
        assertEquals("{\n" +
                "status: 404\n" +
                "message: JSONObject[\"branches_url\"] not found.\n" +
                "}", response);
    }

    @AfterAll
    public static void close() {
        mock_server.stop();
    }

    private static void declareWiremockMappings() {

        stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{OK}")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlEqualTo("/users/existing_user"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"login\": \"existing_user\"," +
                                "\"repos_url\": \"http://localhost:" + mock_server.port() + "/users/existing_user/repos\"}")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlEqualTo("/users/incomplete_user"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"login\": \"incomplete_user\"," +
                                "\"repos_url\": \"http://localhost:" + mock_server.port() + "/users/incomplete_user/repos\"}")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlEqualTo("/users/alternate_existing_user"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"login\": \"existing_user\"," +
                                "\"repos_url\": \"http://localhost:" + mock_server.port() + "/users/alternate_existing_user/repos\"}")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlEqualTo("/users/existing_user/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("[" +
                                "{\"name\": \"Repo_1\",\"fork\": false,\"branches_url\": \"http://localhost:" + mock_server.port() + "/repos/existing_user/Repo_1/branches{/branch}\"}," +
                                "{\"name\": \"Repo_2\",\"fork\": false,\"branches_url\": \"http://localhost:" + mock_server.port() + "/repos/existing_user/Repo_2/branches{/branch}\"}," +
                                "]")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlEqualTo("/users/incomplete_user/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("[" +
                                "{\"name\": \"Repo_1\",\"fork\": false}," +
                                "{\"name\": \"Repo_2\",\"fork\": false,\"branches_url\": \"http://localhost:" + mock_server.port() + "/repos/existing_user/Repo_2/branches{/branch}\"}," +
                                "]")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlEqualTo("/users/alternate_existing_user/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("[" +
                                "{\"name\": \"Repo_1\",\"fork\": false,\"branches_url\": \"http://localhost:" + mock_server.port() + "/repos/existing_user/Repo_1/branches{/branch}\"}," +
                                "{\"name\": \"Repo_2\",\"fork\": true,\"branches_url\": \"http://localhost:" + mock_server.port() + "/repos/existing_user/Repo_2/branches{/branch}\"}," +
                                "]")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlEqualTo("/repos/existing_user/Repo_1/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("[" +
                                "{\"name\": \"Branch_1\",\"commit\": {\"sha\": \"1234\"} }," +
                                "{\"name\": \"Branch_2\",\"commit\": {\"sha\": \"5678\"} }" +
                                "]")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlEqualTo("/repos/existing_user/Repo_2/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("[" +
                                "{\"name\": \"Branch_1\",\"commit\": {\"sha\": \"4321\"} }," +
                                "{\"name\": \"Branch_2\",\"commit\": {\"sha\": \"8765\"} }" +
                                "]")
                        .withHeader("Content-Type", "application/json")));

    }
}

