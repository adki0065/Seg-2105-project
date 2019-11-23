package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EmployeeActivity extends AppCompatActivity {
    final String TAG = "ServiceFragmentLog";

    ClinicAccount account;

    private FirebaseFirestore db;
    private CollectionReference clinicsRef;

    private TableLayout clinicTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        Intent intent = getIntent();

        account = (ClinicAccount) intent.getSerializableExtra("account");
        db = FirebaseFirestore.getInstance();
        clinicsRef = db.collection("clinics");

        clinicTable = (TableLayout) findViewById(R.id.employee_clinic_table);

        getClinics();
    }

    public void OnCreateClinicPress(View view) {
        Intent returnIntent = new Intent(this, EmployeeEditClinicActivity.class);

        startActivity(returnIntent);
    }

    private void getClinics() {
        clinicsRef.orderBy("created", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();

                    if (result.isEmpty()) {

                        return;
                    }

                    clinicTable.removeAllViews();
                    clinicTable.addView(getLayoutInflater().inflate(R.layout.employee_clinic_header, clinicTable, false));

                    int i = 0;
                    for (DocumentSnapshot document : result) {
                        final Clinic clinic = document.toObject(Clinic.class);
                        clinic.setId(document.getId());

//                        Log.d(TAG, clinic.toString());

                        LayoutInflater layoutInflater = getLayoutInflater();

                        TableRow row = (TableRow) layoutInflater.inflate(R.layout.employee_clinic_row, clinicTable, false);
                        row.setId(i);

                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent returnIntent = new Intent(clinicTable.getContext(), EmployeeEditClinicActivity.class);

                                returnIntent.putExtra("clinic", clinic);
                                
                                startActivity(returnIntent);
                            }
                        });

                        if (i % 2 == 0) {
                            row.setBackgroundColor(Util.ROW_BG_COLOR);
                        }

                        String name = clinic.getName();
                        String address = clinic.getAddress();
                        String phone = clinic.getPhone();
                        ArrayList<String> payments = (ArrayList<String>) clinic.getPayments();
                        ArrayList<String> services = (ArrayList<String>) clinic.getServices();

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
                        row.addView(addressCol);
                        row.addView(phoneCol);
                        row.addView(paymentsCol);
                        row.addView(servicesCol);

                        clinicTable.addView(row);

                        i++;
                    }

                } else {
                    Log.e(TAG, "Failed to get services", task.getException());
                    Util.ShowSnackbar(clinicTable, "Failed to update services.", getResources().getColor(android.R.color.holo_red_light));
                }
            }
        });
    }
}
