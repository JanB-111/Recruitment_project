package com.task.Recruitment;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Iterator;

public class Controller {

    private static String PATH = "https://api.github.com";

    public static String getUserData(String user_name) {
        String result_string = "";

        try {

            ResponseEntity<String> user_request_response = getResponseFromAddress(PATH + "/users/" + user_name);
            String user_request_response_body = user_request_response.getBody();
            JSONObject user_request_body_JSON = new JSONObject(user_request_response_body);
            String user_repositories_address = user_request_body_JSON.getString("repos_url");

            result_string += "Login: " + user_request_body_JSON.getString("login") + "\n" + "\n";

            ResponseEntity<String> repository_request_response = getResponseFromAddress(user_repositories_address);
            String repository_request_response_body = repository_request_response.getBody();
            JSONArray repository_JSON_array = new JSONArray(repository_request_response_body);
            Iterator repository_json_array_iterator = repository_JSON_array.iterator();

            while (repository_json_array_iterator.hasNext()) {

                JSONObject curr_json_object = (JSONObject) repository_json_array_iterator.next();

                if (!curr_json_object.getBoolean("fork")) {
                    result_string += "Repository name: " + curr_json_object.getString("name") + "\n";

                    String branch_url = curr_json_object.getString("branches_url");
                    branch_url = branch_url.replace("{/branch}", "");

                    ResponseEntity<String> branch_request_response = getResponseFromAddress(branch_url);

                    String branch_request_response_body = branch_request_response.getBody();
                    JSONArray branch_json_array = new JSONArray(branch_request_response_body);
                    Iterator json_branch_array_iterator = branch_json_array.iterator();
                    while (json_branch_array_iterator.hasNext()) {
                        JSONObject curr_branch_object = (JSONObject) json_branch_array_iterator.next();

                        result_string += "\tBranch name: " + curr_branch_object.getString("name") + "\n";
                        JSONObject commit_data = curr_branch_object.getJSONObject("commit");
                        result_string += "\tBranch sha: " + commit_data.getString("sha") + "\n";
                        result_string += "\n";

                    }

                }

            }
            return result_string;
        } catch (HttpClientErrorException e) {
            String error_text = e.getStatusText();
            HttpStatusCode status_code = e.getStatusCode();

            String errorFormatted = "{\n";
            errorFormatted += "status: " + status_code.toString().substring(0,3) + "\n";
            errorFormatted += "message: " + error_text + "\n";
            errorFormatted += "}";


            return errorFormatted;
        }
        catch (JSONException e){
            String error_text = e.getMessage();

            String error_formatted = "{\n";
            error_formatted += "status: " + 404 + "\n";
            error_formatted += "message: " + error_text + "\n";
            error_formatted += "}";

            return error_formatted;
        }
    }

    public static ResponseEntity<String> getResponseFromAddress(String address) throws HttpClientErrorException {
        try {
            RestClient restClient = RestClient.create();
            ResponseEntity<String> result = restClient.get().uri(address).retrieve().toEntity(String.class);
            return result;
        }catch(Exception e){
            throw e;
        }

    }

    public static void setPath(String new_path){
        PATH = new_path;
    }

    public static String restWiremockConnectionTest(){
        RestClient rest_client = RestClient.create();

        return rest_client.get().uri(PATH + "/test").retrieve().body(String.class);
    }

}
