package com.task.Recruitment;



public class Client {
    private final String client_name;


    public Client(String client_name){
        this.client_name = client_name;
    }

    public String getData() {
        return Controller.getUserData(client_name);
    }

}
