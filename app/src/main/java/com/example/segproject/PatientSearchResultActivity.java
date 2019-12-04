package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientSearchResultActivity extends AppCompatActivity {
    final String TAG = "PatientSearchResultLog";

    private List<Clinic> results;

    private TableLayout clinicTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_search_result);

        Intent intent = getIntent();
        results = (ArrayList<Clinic>) intent.getSerializableExtra("results");

        clinicTable = (TableLayout) findViewById(R.id.employee_clinic_table);

        clinicTable.addView(getLayoutInflater().inflate(R.layout.employee_clinic_header, clinicTable, false));

        final LayoutInflater layoutInflater = getLayoutInflater();

        for (int i = 0; i < results.size(); i++) {
            final Clinic clinic = results.get(i);

            TableRow row = (TableRow) layoutInflater.inflate(R.layout.employee_clinic_row, clinicTable, false);
            row.setId(i);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PatientSearchResultActivity.this, PatientClinicActivity.class);

                    intent.putExtra("clinic", clinic);

                    startActivity(intent);

                }
            });

            if (i % 2 == 0) {
                row.setBackgroundColor(Util.ROW_BG_COLOR);
            }

            double rating = clinic.getRating().getRating();
            List<String> reviews = clinic.getReviews();
            String name = clinic.getName();
            String address = clinic.getAddress();
            String phone = clinic.getPhone();
            ArrayList<String> payments = (ArrayList<String>) clinic.getPayments();
            ArrayList<String> services = (ArrayList<String>) clinic.getServices();

            TextView ratingCol = (TextView) layoutInflater.inflate(R.layout.employee_clinic_column, row, false);
            if (rating < 0) ratingCol.setText("N/A");
            else ratingCol.setText(String.format("%.1f", rating));

            TextView reviewsCol = (TextView) layoutInflater.inflate(R.layout.employee_clinic_column, row, false);
            reviewsCol.setText(String.valueOf(reviews.size()));

            TextView nameCol = (TextView) layoutInflater.inflate(R.layout.employee_clinic_column, row, false);
            nameCol.setText(name);

            TextView addressCol = (TextView) layoutInflater.inflate(R.layout.employee_clinic_column, row, false);
            addressCol.setText(address);

            TextView phoneCol = (TextView) layoutInflater.inflate(R.layout.employee_clinic_column, row, false);
            phoneCol.setText(phone);

            TextView paymentsCol = (TextView) layoutInflater.inflate(R.layout.employee_clinic_column, row, false);
            StringBuilder paymentSB = new StringBuilder();
            int j = 0;
            for (String payment : payments) {
                if (j != 0) paymentSB.append(", ");
                paymentSB.append(payment);
                j++;
            }
            paymentsCol.setText(paymentSB.toString());

            TextView servicesCol = (TextView) layoutInflater.inflate(R.layout.employee_clinic_column, row, false);
            servicesCol.setText(String.valueOf(services.size()));

            row.addView(nameCol);
            row.addView(ratingCol);
            row.addView(reviewsCol);
            row.addView(addressCol);
            row.addView(phoneCol);
            row.addView(paymentsCol);
            row.addView(servicesCol);

            clinicTable.addView(row);
        }
    }
}
