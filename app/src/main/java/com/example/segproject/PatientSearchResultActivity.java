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

    private FirebaseFirestore db;
    private CollectionReference clinicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_search_result);

        Intent intent = getIntent();
        results = (ArrayList<Clinic>) intent.getSerializableExtra("results");

        db = FirebaseFirestore.getInstance();
        clinicsRef = db.collection("clinics");

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    View dialogView = layoutInflater.inflate(R.layout.patient_clinic_dialog, null);
                    builder.setView(dialogView);

                    builder.setTitle("Leave a review");

                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    builder.setPositiveButton("Add", null);

                    final AlertDialog dialog = builder.create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            final RatingBar ratingField = dialog.findViewById(R.id.rating_bar);
                            final EditText reviewField = dialog.findViewById(R.id.review_edit_text);

                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(final View v) {
                                    final double rating = ratingField.getRating();
                                    final String review = reviewField.getText().toString().trim();


                                    clinicsRef.document(clinic.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();

                                                if (document.exists()) {
                                                    List<String> reviews = (ArrayList<String>) document.get("reviews");

                                                    if (reviews == null)
                                                        reviews = new ArrayList<>(1);

                                                    reviews.add(review);

                                                    ClinicRating prevRating = document.get("rating", ClinicRating.class);

                                                    if (prevRating == null)
                                                        prevRating = new ClinicRating(rating, 1);
                                                    else {
                                                        double ratingAvg = prevRating.getRating(); // 5
                                                        int ratingTotal = prevRating.getTotal(); // 1

                                                        double newTotal = ratingTotal + 1; // 2
                                                        double newAvg = (ratingAvg * ratingTotal + rating) / newTotal;


                                                        prevRating = new ClinicRating(newAvg, ratingTotal + 1);
                                                    }

                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("rating", prevRating);
                                                    data.put("reviews", reviews);

                                                    clinicsRef.document(clinic.getId()).update(data)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    dialog.dismiss();
                                                                    Log.d(TAG, "Updated clinic with ID: " + clinic.getId());
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception err) {
                                                                    Util.ShowToast(v, "Couldn't add review");
                                                                    Log.e(TAG, "Error adding clinic", err);
                                                                }
                                                            });

                                                }
                                            } else {
                                                Util.ShowToast(v, "Couldn't add review");
                                                Log.e(TAG, "Error adding clinic", task.getException());
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                    dialog.show();
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
        }
    }
}
