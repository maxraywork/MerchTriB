package com.example.merchtrib.ui.objects;

import java.util.ArrayList;

public class Task {
    public String name;
    public String address;
    public String id;
    public String addressLink;
    public ArrayList<String> images;

    public Task() {};

    public Task(String id, String name, String address, String addressLink, ArrayList<String> images) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.addressLink = addressLink;
        this.images = images;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddressLink() {
        return addressLink;
    }

    public void setAddressLink(String addressLink) {
        this.addressLink = addressLink;
    }
}