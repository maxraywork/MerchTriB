package com.example.merchtrib.ui.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;

public class Task {
    public String title;

    public String taskID;

    public String userID;


    //Status: created/sent
    public String status;
    public String address;
    public String addressLink;

    public String comment = "";
    public ArrayList<String> images;
    public HashMap<String, Object> timestamp;
    private HashMap<String, Object> timestampNow = new HashMap<>();
    public Task() {};

    public Task(String taskID, String title, String address, String addressLink, String status, ArrayList<String> images) {
        this.taskID = taskID;
        this.status = status;
        this.title = title;
        this.address = address;
        this.addressLink = addressLink;
        this.images = images;
        timestampNow.put("createdAt", ServerValue.TIMESTAMP);
        this.timestamp = timestampNow;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTimestampDone() {
        timestampNow.put("completeAt", ServerValue.TIMESTAMP);
        this.timestamp = timestampNow;
    }

    public HashMap<String, Object> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(HashMap<String, Object> timestamp) {
        this.timestamp = timestamp;
    }
    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestamp.get("createdAt");
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressLink() {
        return addressLink;
    }

    public void setAddressLink(String addressLink) {
        this.addressLink = addressLink;
    }
}