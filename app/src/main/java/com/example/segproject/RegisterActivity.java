package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.View;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivityLog";

    private EditText NameField;
    private EditText UsernameField;
    private EditText PasswordField;
    private ToggleButton RoleField;

    private MessageDigest digest;

    private FirebaseFirestore db;
    private CollectionReference accountsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        NameField = (EditText) findViewById(R.id.NameField);
        UsernameField = (EditText) findViewById(R.id.UsernameField);
        PasswordField = (EditText) findViewById(R.id.PasswordField);
        RoleField = (ToggleButton) findViewById(R.id.toggleButton);

        NameField.setText("");
        UsernameField.setText("");
        PasswordField.setText("");
        RoleField.setText(RoleField.getTextOff());

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException err) {
            Log.e(TAG, "Digest error", err);
        }

        db = FirebaseFirestore.getInstance();
        accountsRef = db.collection("accounts");
    }

    public void OnCreatePress(View view) {
        if (digest == null) return;

        final String name = NameField.getText().toString().trim();
        if (name.isEmpty()) {
            ShowSnackbar(view, "Name is required.");
            return;
        } else if (name.length() > 50) {
            ShowSnackbar(view, "Name must be less than 50 characters.");
            return;
        }

        final String username = UsernameField.getText().toString().trim();
        if (username.isEmpty()) {
            ShowSnackbar(view, "Username is required.");
            return;
        } else if (username.length() < 4 || username.length() > 50) {
            ShowSnackbar(view, "Username must be between 4 and 50 characters.");
            return;
        } else if (!username.matches("\\S+")) {
            ShowSnackbar(view, "Username cannot contain any whitespace.");
            return;
        }

        String password = PasswordField.getText().toString().trim();
        if (password.isEmpty()) {
            ShowSnackbar(view, "Password is required.");
            return;
        } else if (password.length() < 4 || password.length() > 50) {
            ShowSnackbar(view, "Password must be between 4 and 50 characters.");
            return;
        } else if (!password.matches("\\S+")) {
            ShowSnackbar(view, "Password cannot contain any whitespace.");
            return;
        }

        // TODO: Use a dropdown for role?
        final String role = RoleField.getText().toString().toLowerCase().trim();
        if (role.isEmpty()) return;

        final String passHash = Base64.encodeToString(digest.digest(password.getBytes()), Base64.DEFAULT).trim();

        final View activityView = view;

        Query query = accountsRef.whereEqualTo("username", username).limit(1);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                RegisterUser(name, username, passHash, role, activityView);
                            } else {
                                ShowSnackbar(activityView, "User already registered.");
                            }
                        } else {
                            Log.e(TAG, "Error getting document: ", task.getException());
                            ShowSnackbar(activityView, "Error creating user.");
                        }
                    }
                });

//        Log.d(TAG, "Name: " + name);
//        Log.d(TAG, "Username: " + username);
//        Log.d(TAG, "Password: " + password);
//        Log.d(TAG, "Password Hash: " + passHash);
//        Log.d(TAG, "Role: " + role);
    }

    private void RegisterUser(String name, String username, String password, String role, final View view) {
        final HashMap<String, Object> data = new HashMap<>();

        data.put("name", name);
        data.put("username", username);
        data.put("password", password);
        data.put("role", role);
        data.put("created", FieldValue.serverTimestamp());


        accountsRef.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        data.remove("created");
                        GoToLoggedIn(data);

                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception err) {
                        ShowSnackbar(view, "Error creating user.");
                        Log.e(TAG, "Error adding document", err);
                    }
                });
    }

    private void GoToLoggedIn(HashMap data) {
        Intent returnIntent = new Intent(RegisterActivity.this, LoggedInActivity.class);

        returnIntent.putExtra("user", data);
        returnIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);

        startActivity(returnIntent);
        this.finish();
    }

    private void ShowSnackbar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

}
