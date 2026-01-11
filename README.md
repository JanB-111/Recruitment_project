<h1>Project Description</h1>
The project allows user to quickly request specified user data regarding
 public repositories that aren't forks. It can be easily integrated into
JAVA project. 


The project provides following information about specified user:

* Login,
* Repositories,
* Branches for each repository and last sha.

Project contains integration tests for the application and uses WireMock in order to simulate connection
to GitHub REST API.

<h2>Used technologies</h2>

* Java 25,
* SpringBoot 4.0.1,
* org-JSON 20251224,
* JUnit 6.0.2 (used for testing application),
* WireMock 3.2.0

<h2>How to use</h2>
The project contains two classes: "Client" and "Controller". In order to list
user repositories you have to create new Client, while passing user login in the
class constructor:
    
    Client example_client = new Client("example_login");

After this you can request data about user repositories by calling function .getData() in 
following manner:

    String response = example_client.getData();

The response String will be containing "\n" and "\t" markers to make displaying results
easier.