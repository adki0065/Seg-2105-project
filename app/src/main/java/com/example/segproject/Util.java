package com.example.segproject;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class Util {
    static public int ROW_BG_COLOR = 671088640;

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

    static public String FormatTime(int time) {
        if (time < 10) return "0" + time;
        else return "" + time;
    }

    static public void ShowToast(View view, String text) {
        ShowToast(view, text, true);
    }

    static public void ShowToast(View view, String text, Boolean lengthShort) {
        Toast.makeText(view.getContext(), text, lengthShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
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

    static public String ValidatePhone(String phone) {
        if (phone.isEmpty())
            return "Phone is required.";
        else if (phone.length() < 10 || phone.length() > 11)
            return "Phone must be 10 or 11 characters.";
        else if (!phone.matches("\\S+"))
            return "Phone cannot contain any whitespace.";

        return null;
    }

    static public String ValidateAddress(String address) {
        if (address.isEmpty())
            return "Address is required.";
        else if (address.length() < 4 || address.length() > 100)
            return "Address must be between 4 and 100 characters.";

        return null;
    }
}
