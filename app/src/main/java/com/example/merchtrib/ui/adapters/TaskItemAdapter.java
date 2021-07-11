package com.example.merchtrib.ui.adapters;

public class TaskItemAdapter {

    private int flagResource; // ресурс флага

    public TaskItemAdapter(int flag){

        this.flagResource=flag;
    }

    public int getFlagResource() {
        return this.flagResource;
    }

    public void setFlagResource(int flagResource) {
        this.flagResource = flagResource;
    }

    
}