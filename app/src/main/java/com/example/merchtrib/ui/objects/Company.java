package com.example.merchtrib.ui.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Company {

    public String name;
    public Map<String, CompanyUser> users;
    public ArrayList<TaskList> tasks;

    public String address;
    public String phone;

    public Company(String name, Map<String, CompanyUser> users, ArrayList<TaskList> tasks, String address, String phone) {
        this.name = name;
        if (users != null) {
            this.users = users;
        } else {
            this.users = new HashMap<>();
        }
        if (tasks != null) {
            this.tasks = tasks;
        } else {
            this.tasks = new ArrayList<>();
        }
        this.address = address;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, CompanyUser> getUsers() {
        return users;
    }

    public void addUser(String userID, CompanyUser user) {
        users.put(userID, user);
    }

    public void setUsers(Map<String, CompanyUser> users) {
        this.users = users;
    }

    public ArrayList<TaskList> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskList> tasks) {
        this.tasks = tasks;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
