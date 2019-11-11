package com.example.segproject;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Util {
    static public void ShowSnackbar(View view, String text, int color) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(color)
                .show();
    }

    static public String ValidateName(String name) {
        if (name.isEmpty()) return "Name is required.";
        if (name.length() > 50) return "Name must be less than 50 characters.";
        return null;
    }

    static public String ValidatePassword(String password) {
        if (password.isEmpty())
            return "Password is required.";
        else if (password.length() < 4 || password.length() > 50)
            return "Password must be between 4 and 50 characters.";
        else if (!password.matches("\\S+"))
            return "Password cannot contain any whitespace.";

        return null;
    }

    static public String ValidateUsername(String username) {
        if (username.isEmpty())
            return "Username is required.";
        else if (username.length() < 4 || username.length() > 50)
            return "Username must be between 4 and 50 characters.";
        else if (!username.matches("\\S+"))
            return "Username cannot contain any whitespace.";

        return null;
    }
}
