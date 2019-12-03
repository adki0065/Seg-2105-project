package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoggedInActivity extends AppCompatActivity {
    final String TAG = "LoggedInActivityLog";
//    private TextView teamAddress;

    private FirebaseFirestore db;
    private CollectionReference clinicsRef;

    private List<Clinic> clinics;

    private EditText clinicName;
    private EditText clinicAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

//        teamAddress = (TextView) findViewById(R.id.loggedInText);

        Intent intent = getIntent();

        clinicName = findViewById(R.id.clinic_name_field);
        clinicAddress = findViewById(R.id.clinic_address_field);

        ClinicAccount account = (ClinicAccount) intent.getSerializableExtra("account");
        db = FirebaseFirestore.getInstance();
        clinicsRef = db.collection("clinics");
        clinics = new ArrayList<>();

        clinicsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot result = task.getResult();

                    if (result.isEmpty()) return;

                    for (QueryDocumentSnapshot document : result) {
                        Clinic clinic = document.toObject(Clinic.class);
                        clinic.setId(document.getId());
                        clinics.add(clinic);
                    }
                } else {
                    Log.e(TAG, "Couldn't get all clinics", task.getException());

                }
            }
        });

//        teamAddress.setText("Welcome " + account.getName() + "! You are logged in as " + account.getRole());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;
    }

    public void OnSearchClinic(View view) {
        String name = clinicName.getText().toString().trim().toLowerCase();
        String address = clinicAddress.getText().toString().trim().toLowerCase();

        boolean searchName = false;
        boolean searchAddress = false;

        if (name.isEmpty() && address.isEmpty()) {
            Util.ShowSnackbar(view, "At least one field must be entered", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        if (!name.isEmpty()) {
            String validateName = Util.ValidateName(name);
            if (validateName != null) {
                Util.ShowSnackbar(view, validateName, getResources().getColor(android.R.color.holo_red_light));
                return;
            }

            searchName = true;
        }

        if (!address.isEmpty()) {
            String validateAddress = Util.ValidateAddress(address);
            if (validateAddress != null) {
                Util.ShowSnackbar(view, validateAddress, getResources().getColor(android.R.color.holo_red_light));
                return;
            }

            searchAddress = true;
        }

        ArrayList<Clinic> clinicsResult = new ArrayList<>();

        for (Clinic clinic : clinics) {
            if (searchName && searchAddress) {
                if (clinic.getName().toLowerCase().contains(name) && clinic.getAddress().toLowerCase().contains(address))
                    clinicsResult.add(clinic);

            } else if (searchName) {
                if (clinic.getName().toLowerCase().contains(name))
                    clinicsResult.add(clinic);

            } else if (searchAddress)
                if (clinic.getAddress().toLowerCase().contains(address))
                    clinicsResult.add(clinic);
        }

        if (clinicsResult.size() == 0) {
            Util.ShowSnackbar(view, "No clinics found", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        Intent returnIntent = new Intent(this, PatientSearchResultActivity.class);

        returnIntent.putExtra("results", clinicsResult);

        startActivity(returnIntent);
        this.finish();
    }
}
