package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientClinicActivity extends AppCompatActivity {
    final String TAG = "PatientClinicLog";

    private Clinic clinic;

    private FirebaseFirestore db;
    private CollectionReference clinicsRef;
    private CollectionReference servicesRef;

    List<ClinicService> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_clinic);

        db = FirebaseFirestore.getInstance();
        clinicsRef = db.collection("clinics");
        servicesRef = db.collection("services");

        clinic = (Clinic) getIntent().getSerializableExtra("clinic");

        TextView clinicTitle = (TextView) findViewById(R.id.clinic_title);
        clinicTitle.setText(clinic.getName());

        TextView clinicAddress = (TextView) findViewById(R.id.clinic_address);
        clinicAddress.setText(clinic.getAddress());

        TextView clinicPhone = (TextView) findViewById(R.id.clinic_phone);
        clinicPhone.setText(clinic.getPhone());

        TextView clinicPayments = (TextView) findViewById(R.id.clinic_payments);
        StringBuilder paymentSB = new StringBuilder();
        int j = 0;
        for (String payment : clinic.getPayments()) {
            if (j != 0) paymentSB.append(", ");
            paymentSB.append(payment);
            j++;
        }
        clinicPayments.setText(paymentSB.toString());

        double rating = clinic.getRating().getRating();

        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        View ratingContainer = findViewById(R.id.clinic_rating_container);
        View ratingNA = findViewById(R.id.clinic_rating_na);

        if (rating < 0) {
            ratingNA.setVisibility(View.VISIBLE);
            ratingContainer.setVisibility(View.GONE);
        } else {
            ratingNA.setVisibility(View.GONE);
            ratingContainer.setVisibility(View.VISIBLE);
            ratingBar.setRating((float) rating);
        }

        TextView clinicWait = (TextView) findViewById(R.id.clinic_wait_time);
        clinicWait.setText(clinic.getWaitTime() + " min");
    }

    public void OnViewReviewsPress(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.patient_clinic_service_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Clinic reviews");

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                LayoutInflater layoutInflater = getLayoutInflater();

                TableLayout tableLayout = (TableLayout) dialog.findViewById(R.id.serviceTable);
                tableLayout.removeAllViews();

                List<String> reviews = clinic.getReviews();

                if (reviews.size() == 0) {
                    TableRow row = (TableRow) layoutInflater.inflate(R.layout.clinic_services_row, tableLayout, false);
                    row.setId(0);

                    TextView noReviewCol = (TextView) layoutInflater.inflate(R.layout.clinic_services_column, row, false);
                    noReviewCol.setText("This clinic doesn't have any reviews");

                    row.addView(noReviewCol);

                    tableLayout.addView(row);

                    return;
                }

                int i = 1;
                for (String review : clinic.getReviews()) {
                    TableRow row = (TableRow) layoutInflater.inflate(R.layout.clinic_services_row, tableLayout, false);
                    row.setId(i);

                    if (i % 2 == 0) {
                        row.setBackgroundColor(Util.ROW_BG_COLOR);
                    }

                    TextView reviewCol = (TextView) layoutInflater.inflate(R.layout.clinic_services_column, row, false);
                    reviewCol.setText(review);

                    row.addView(reviewCol);

                    tableLayout.addView(row);

                    i++;
                }
            }
        });

        dialog.show();
    }

    public void OnViewServicesPress(final View view) {
        final List<String> serviceIDs = clinic.getServices();

        if (services != null) {
            showServicesDialog(view);
            return;
        }

        servicesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();

                    services = new ArrayList<>(serviceIDs.size());
                    for (DocumentSnapshot document : result) {
                        String docID = document.getId();

                        if (serviceIDs.contains(docID)) {
                            ClinicService service = document.toObject(ClinicService.class);
                            service.setId(document.getId());
                            services.add(service);
                        }
                    }

                    showServicesDialog(view);
                } else {
                    Log.e(TAG, "Couldn't get services", task.getException());
                }

            }
        });
    }

    public void showServicesDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.patient_clinic_service_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Clinic services");

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                LayoutInflater layoutInflater = getLayoutInflater();

                TableLayout tableLayout = (TableLayout) dialog.findViewById(R.id.serviceTable);
                tableLayout.removeAllViews();

                if (services.size() == 0) {
                    TableRow row = (TableRow) layoutInflater.inflate(R.layout.clinic_services_row, tableLayout, false);
                    row.setId(0);

                    TextView noServicesCol = (TextView) layoutInflater.inflate(R.layout.clinic_services_column, row, false);
                    noServicesCol.setText("This clinic doesn't have any services");

                    row.addView(noServicesCol);
                    tableLayout.addView(row);

                    return;
                }

                tableLayout.addView(layoutInflater.inflate(R.layout.patient_clinic_service_header, tableLayout, false));

                int i = 0;
                for (ClinicService service : services) {
                    TableRow row = (TableRow) layoutInflater.inflate(R.layout.clinic_services_row, tableLayout, false);
                    row.setId(i);

                    if (i % 2 == 0) {
                        row.setBackgroundColor(Util.ROW_BG_COLOR);
                    }

                    String name = service.getName();
                    TextView nameCol = (TextView) layoutInflater.inflate(R.layout.clinic_services_column, row, false);
                    nameCol.setText(name);

                    String role = service.getRole();
                    TextView roleCol = (TextView) layoutInflater.inflate(R.layout.clinic_services_column, row, false);
                    roleCol.setText(role);

                    row.addView(nameCol);
                    row.addView(roleCol);

                    tableLayout.addView(row);

                    i++;
                }
            }
        });

        dialog.show();
    }

    public void OnViewHoursPress(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.patient_clinic_hours_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Clinic services");

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Map<String, ClinicHours> hours =  clinic.getHours();

                for (String day : hours.keySet()) {
                    ClinicHours schedule = hours.get(day);

                    int[] buttonsID = EmployeeEditClinicActivity.getHourButtonsID(day);

                    TextView startButton = dialog.findViewById(buttonsID[0]);
                    startButton.setEnabled(true);
                    startButton.setText(schedule.getStart());

                    TextView endButton = dialog.findViewById(buttonsID[1]);
                    endButton.setEnabled(true);
                    endButton.setText(schedule.getEnd());
                }
            }
        });

        dialog.show();
    }

    public void OnLeaveReviewPress(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.patient_clinic_review_dialog, null);
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

                        String validateReview = Util.ValidateReview(review);
                        if (validateReview != null) {
                            Util.ShowToast(v, validateReview);
                            return;
                        }

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
                                            double ratingAvg = prevRating.getRating();
                                            int ratingTotal = prevRating.getTotal();

                                            double newTotal = ratingTotal + 1;
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

    public void OnBookPress(View view) {
        Intent intent = new Intent(this, PatientBookActivity.class);

        intent.putExtra("clinic", clinic);

        startActivity(intent);
    }
}
