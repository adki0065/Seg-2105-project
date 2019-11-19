package com.example.segproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;

public class LoggedInActivity extends AppCompatActivity {
    private TextView teamAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        teamAddress = (TextView) findViewById(R.id.loggedInText);

        Intent intent = getIntent();

        ClinicAccount account = (ClinicAccount) intent.getSerializableExtra("account");

//        HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("user");

//        String name = hashMap.get("name");
//        String role = hashMap.get("role");
//        String username = hashMap.get("username");

        teamAddress.setText("Welcome " + account.getName() + "! You are logged in as " + account.getRole());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;


    }
}
