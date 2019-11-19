package com.example.segproject;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ClinicAccount implements Serializable {
    public String name;
    public String role;
    public String username;
    public String id;

    public ClinicAccount() {
    }

    public ClinicAccount(String name, String role, String username, String id) {
        this.name = name;
        this.role = role;
        this.username = username;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return "[id=\"" + id + "\", name=\"" + name + "\", role=\"" + role + "\", username=\"" + username + "\"]";
    }
}
