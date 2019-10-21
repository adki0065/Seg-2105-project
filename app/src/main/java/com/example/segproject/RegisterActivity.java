package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Map;

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
        // TODO: Proper name validation
        if (name.isEmpty()) {
            // TODO: Show invalid name
            return;
        }

        final String username = UsernameField.getText().toString().trim();
        // TODO: Proper username validation, possibly needs to use email to register?
        if (username.isEmpty()) {
            // TODO: Show invalid username
            return;
        }

        String password = PasswordField.getText().toString().trim();
        // TODO: Proper password validation
        if (password.isEmpty()) {
            // TODO: Show invalid password
            return;
        }

        // TODO: Use a dropdown for role?
        final String role = RoleField.getText().toString().toLowerCase().trim();
        if (role.isEmpty()) {
            // TODO: Show invalid role
            return;
        }

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
        HashMap<String, Object> data = new HashMap<>();

        data.put("name", name);
        data.put("username", username);
        data.put("password", password);
        data.put("role", role);
        data.put("created", FieldValue.serverTimestamp());

        accountsRef.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        setResult(RESULT_OK);
                        finish();
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
