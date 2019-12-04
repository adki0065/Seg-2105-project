package com.example.segproject;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clinic implements Serializable {
    public String id;
    public String name;
    public String address;
    public String phone;
    public List<String> payments;
    public List<String> services;
    public Map<String, ClinicHours> hours;
    public ClinicRating rating;
    public List<String> reviews;
    public int waitTime;

    public Clinic() {
        this.payments = new ArrayList<>();
        this.services = new ArrayList<>();
        this.hours = new HashMap<>();
        this.rating = new ClinicRating();
        this.reviews = new ArrayList<>();
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public ClinicRating getRating() {
        return rating;
    }

    public void setRating(ClinicRating rating) {
        this.rating = rating;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
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

    public List<String> getPayments() {
        return payments;
    }

    public void setPayments(List<String> payments) {
        this.payments = payments;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        if (id != null) sb.append("id=" + id + ", ");
        if (name != null) sb.append("name=" + name + ", ");
        if (address != null) sb.append("address=" + address + ", ");
        if (phone != null) sb.append("phone=" + phone + ", ");

        sb.append("payments=" + payments.toString() + ", ");
        sb.append("services=" + services.toString() + ", ");
        sb.append("hours=" + hours.toString() + ", ");
        sb.append("rating=" + rating.toString() + ", ");
        sb.append("reviews=" + reviews.toString());

        sb.append("}");

        return sb.toString();
    }
}
