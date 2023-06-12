package com.example.merchtrib.ui.objects;


public class UsersWaitListItem {

    public String email;

    public String companyID;

    public UsersWaitListItem() {
    }

    public UsersWaitListItem(String email, String companyID) {
        this.email = email;
        if (companyID != null) {
            this.companyID = companyID;
        }
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
}
