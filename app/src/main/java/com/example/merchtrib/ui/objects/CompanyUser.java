package com.example.merchtrib.ui.objects;

public class CompanyUser {
    public String email;

    public CompanyUser(){};
    public CompanyUser(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
