package com.example.segproject;

import androidx.annotation.NonNull;

public class ClinicService {

    public String name;
    public String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    ClinicService() {
    }

    ClinicService(String name) {
        setName(name);
    }


    @NonNull
    @Override
    public String toString() {
        return "[id=\"" + id + "\", name=\"" + name + "\"]";
    }
}
