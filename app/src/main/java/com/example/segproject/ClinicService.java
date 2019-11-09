package com.example.segproject;

public class ClinicService {

    public String name;

    ClinicService() {
    }

    ClinicService(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
