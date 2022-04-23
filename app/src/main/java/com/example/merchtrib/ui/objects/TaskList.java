package com.example.merchtrib.ui.objects;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TaskList {
    public ArrayList<Task> array;

    public TaskList(){};

    public TaskList(ArrayList<Task> array) {
    this.array = array;
    }

    public ArrayList<Task> getArray() {
        return array;
    }

    public void setArray(ArrayList<Task> array) {
        this.array = array;
    }
}
