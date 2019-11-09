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
}
