package com.example.segproject;

import java.util.HashMap;
import java.util.Map;

public class Clinic {
    public String id;
    public String name;
    public String address;
    public String phone;
    public String[] payments;
    public String[] services;
    public Map<String, ClinicHours> hours;

    public Clinic() {
        this.payments = new String[0];
        this.hours = new HashMap<>();
    }

    public Map<String, ClinicHours> getHours() {
        return hours;
    }

    public void setHours(Map<String, ClinicHours> hours) {
        this.hours = hours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String[] getPayments() {
        return payments;
    }

    public void setPayments(String[] payments) {
        this.payments = payments;
    }

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

}
