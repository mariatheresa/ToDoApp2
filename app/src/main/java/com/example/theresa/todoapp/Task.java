package com.example.theresa.todoapp;

public class Task {

    private String name;
    private String time;
    private String status;

    public Task()
    {

    }

    public Task(String name, String time)
    {
        this.name = name;
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }


    public String getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
