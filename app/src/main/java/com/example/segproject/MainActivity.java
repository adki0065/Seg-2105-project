package com.example.segproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityLog";

    private EditText Name;
    private EditText Password;
    private Button Login;
    private Button CreateAccount;

    private FirebaseFirestore db;
    private CollectionReference accountsRef;

    private MessageDigest digest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);
        Login = (Button) findViewById(R.id.btnLogin);

        Name.setText("");
        Password.setText("");

        db = FirebaseFirestore.getInstance();
        accountsRef = db.collection("accounts");

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException err) {
            Log.e(TAG, "Digest error", err);
        }

//        Login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                validate(Name.getText().toString(), Password.getText().toString());
//            }
//        });
    }


    public void OnRegisterPress(View view) {
        Intent returnIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(returnIntent);
    }

    public void OnLoginPress(View view) {
        if (digest == null) return;

        String username = Name.getText().toString().trim();
        final String password = Password.getText().toString().trim();

        String validateUsername = Util.ValidateUsername(username);
        if (validateUsername != null) {
            Util.ShowSnackbar(view, validateUsername, getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        String validatePassword = Util.ValidatePassword(password);
        if (validatePassword != null) {
            Util.ShowSnackbar(view, validatePassword, getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        final View activityView = view;

        Query query = accountsRef.whereEqualTo("username", username).limit(1);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();

                    if (result.isEmpty()) {
                        Util.ShowSnackbar(activityView, "Incorrect username or password.", getResources().getColor(android.R.color.holo_red_light));
                        return;
                    }

                    for (QueryDocumentSnapshot document : result) {
                        String checkHash = document.get("password").toString().trim();

                        String hash = Base64.encodeToString(digest.digest(password.getBytes()), Base64.DEFAULT).trim();

//                        Log.d(TAG, checkHash);
//                        Log.d(TAG, hash);

                        if (hash.equals(checkHash)) {
//                            ShowSnackbar(activityView, "Logged in.");
                            String userRole = document.getString("role").toLowerCase();

                            Class nextActivity;

                            switch (userRole) {
                                case "admin":
                                    nextActivity = AdminActivity.class;
                                    break;
                                case "employee":
                                    nextActivity = EmployeeActivity.class;
                                    break;
                                default:
                                    nextActivity = LoggedInActivity.class;
                                    break;
                            }

//                            if (userRole.equals("admin")) nextActivity = AdminActivity.class;
//                            else nextActivity = LoggedInActivity.class;

                            //Creating a Return intent to pass to the Main Activity
                            Intent returnIntent = new Intent(MainActivity.this, nextActivity);
                            //Adding stuff to the return intent

                            ClinicAccount returnAccount = document.toObject(ClinicAccount.class);
                            returnAccount.setId(document.getId());

//                            HashMap<String, String> hashMap = new HashMap<>();

//                            hashMap.put("name", document.getString("name"));
//                            hashMap.put("role", document.getString("role"));
//                            hashMap.put("username", document.getString("username"));

                            returnIntent.putExtra("account", returnAccount);

                            startActivity(returnIntent);
                        } else {
                            Util.ShowSnackbar(activityView, "Incorrect username or password.", getResources().getColor(android.R.color.holo_red_light));
                        }

//                        Log.d(TAG, document.getId() + " => " + document.getData());
                        break;
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                    Util.ShowSnackbar(activityView, "Error logging in.", getResources().getColor(android.R.color.holo_red_light));
                }
            }
        });


    }
}