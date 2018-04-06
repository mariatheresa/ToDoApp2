package com.example.theresa.todoapp;

public class Task {

    private String name;
    private String time;
    private Boolean status;

    public Task()
    {

    }

    public Task(String name, String time)
    {
        this.name = name;
        this.time = time;
    }

    public Boolean getStatus() {
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

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
