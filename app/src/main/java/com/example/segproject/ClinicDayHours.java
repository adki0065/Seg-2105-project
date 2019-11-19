package com.example.segproject;

import androidx.annotation.NonNull;

public class ClinicDayHours {
    public String start;
    public String end;

    public ClinicDayHours() {

    }

    public ClinicDayHours(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @NonNull
    @Override
    public String toString() {
        if (start == null && end == null) return "{}";
        else if (start == null) return "{end=" + end + "}";
        else if (end == null) return "{start=" + start + "}";
        else
            return "{start=" + (start == null ? "" : start) + ", end=" + end + "}";
    }
}
