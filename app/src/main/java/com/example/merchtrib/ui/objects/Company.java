package com.example.merchtrib.ui.objects;

import com.example.merchtrib.ui.objects.Task;
import com.example.merchtrib.ui.objects.TaskList;

import java.util.ArrayList;

public class Company {

    public String name;
    public ArrayList<CompanyUser> users;
    public ArrayList<TaskList> tasks;

    public Company() {};


    public Company(String name, ArrayList<CompanyUser> users, ArrayList<TaskList> tasks) {
        this.name = name;
        this.users = users;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<CompanyUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<CompanyUser> users) {
        this.users = users;
    }

    public ArrayList<TaskList> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskList> tasks) {
        this.tasks = tasks;
    }
}
