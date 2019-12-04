package com.example.segproject;

import androidx.annotation.NonNull;

public class ClinicService {

    public String name;
    public String role;
    public String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    ClinicService() {
    }

    ClinicService(String name, String role) {
        this.name = name;
        this.role = role;
    }


    @NonNull
    @Override
    public String toString() {
        return "{id=\"" + id + "\", name=\"" + name + "\", role=\"" + role + "\"}";
    }
}
