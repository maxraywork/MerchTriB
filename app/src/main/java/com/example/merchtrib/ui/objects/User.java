package com.example.merchtrib.ui.objects;

public class User {

    public String company;
    public boolean admin;

    public User() {};

    public User(String company, boolean admin) {
        this.company = company;
        this.admin = admin;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
