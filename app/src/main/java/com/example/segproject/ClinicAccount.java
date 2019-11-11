package com.example.segproject;

import androidx.annotation.NonNull;

public class ClinicAccount {
    public String name;
    public String role;
    public String username;
    public String id;

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
