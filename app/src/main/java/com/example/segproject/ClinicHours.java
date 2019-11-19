package com.example.segproject;

import androidx.annotation.NonNull;

public class ClinicHours {
    public ClinicDayHours day;

    public ClinicHours() {

    }

    public ClinicHours(ClinicDayHours day) {
        this.day = day;
    }

    public ClinicDayHours getDay() {
        return day;
    }

    public void setDay(ClinicDayHours day) {
        this.day = day;
    }

    @NonNull
    @Override
    public String toString() {
        if (day == null) return "{}";
        else return day.toString();
    }
}
