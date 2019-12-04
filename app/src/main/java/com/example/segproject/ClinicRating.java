package com.example.segproject;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ClinicRating implements Serializable {
    public double rating;
    public int total;

    public ClinicRating() {
        rating = -1;
        total = -1;
    }

    public ClinicRating(double rating, int total) {
        this.rating = rating;
        this.total = total;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" + "rating=" + rating + " total=" + total + "}";
    }
}
