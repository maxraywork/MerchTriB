package com.example.merchtrib.ui.objects;

public class User {

    public String companyID;
    public String email;
    public boolean admin;

    public User() {
    }

    public User(String email, boolean admin) {
        this.email = email;
        this.admin = admin;
    }

    public User(String companyID, boolean admin, String email) {
        this.companyID = companyID;
        this.admin = admin;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
