package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PatientBookActivity extends AppCompatActivity {
    final static String TAG = "PatientBookLog";

    private Clinic clinic;

    private FirebaseFirestore db;
    private CollectionReference clinicsRef;

    ClinicAppointment appointment;

    private Button dateButton;
    private Button timeButton;
    private EditText nameEditText;

    private boolean setDate = false;
    private boolean setTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_book);

        clinic = (Clinic) getIntent().getSerializableExtra("clinic");
        appointment = new ClinicAppointment();

        db = FirebaseFirestore.getInstance();
        clinicsRef = db.collection("clinics");

        dateButton = (Button) findViewById(R.id.ap_date_button);
        timeButton = (Button) findViewById(R.id.ap_time_button);
        nameEditText = (EditText) findViewById(R.id.ap_name_field);

        TextView clinicNameField = (TextView) findViewById(R.id.ap_clinic_name);
        clinicNameField.setText(clinic.getName());
    }

    public void OnSelectDatePress(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.date_picker, null);
        builder.setView(dialogView);

        builder.setTitle("Select date");

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.setPositiveButton("Set", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker);
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth() + 1;
                        int day = datePicker.getDayOfMonth();

                        appointment.setDay(day);
                        appointment.setMonth(month);
                        appointment.setYear(year);

                        setDate = true;

                        dateButton.setText(day + "/" + month + "/" + year);

                        dialog.dismiss();
                    }
                });
            }
        });


        dialog.show();
    }

    public void OnSelectTimePress(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.time_picker, null);
        builder.setView(dialogView);

        builder.setTitle("Select time");

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.setPositiveButton("Set", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.time_picker);
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();

                        appointment.setHour(hour);
                        appointment.setMinute(minute);

                        setTime = true;

                        timeButton.setText(Util.FormatTime(hour) + ":" + Util.FormatTime(minute));

                        dialog.dismiss();
                    }
                });
            }
        });


        dialog.show();
    }

    public void OnBookPress(final View view) {
        String name = nameEditText.getText().toString();
        String validateName = Util.ValidateName(name);
        if (validateName != null) {
            Util.ShowSnackbar(view, validateName, getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        if (!setDate) {
            Util.ShowSnackbar(view, "Select an appointment date", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        if (!setTime) {
            Util.ShowSnackbar(view, "Select an appointment time", getResources().getColor(android.R.color.holo_red_light));
            return;
        }


        String validateDate = Util.ValidateDate(appointment.getYear(), appointment.getMonth(), appointment.getDay(), appointment.getHour(), appointment.getMinute());
        if (validateDate != null) {
            Util.ShowSnackbar(view, validateDate, getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        clinic.setWaitTime(clinic.getWaitTime() + 15);

        clinicsRef.document(clinic.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        int waitTime = clinic.getWaitTime();

                        Map<String, Object> data = new HashMap<>();
                        data.put("waitTime", waitTime);

                        clinicsRef.document(clinic.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(PatientBookActivity.this, PatientClinicActivity.class);

                                    intent.putExtra("clinic", clinic);

                                    startActivity(intent);
                                } else {
                                    Util.ShowSnackbar(view, "Couldn't book appointment", getResources().getColor(android.R.color.holo_red_light));
                                }
                            }
                        });
                    } else {
                        Util.ShowSnackbar(view, "Couldn't book appointment", getResources().getColor(android.R.color.holo_red_light));
                    }
                } else {
                    Util.ShowSnackbar(view, "Couldn't book appointment", getResources().getColor(android.R.color.holo_red_light));
                }
            }
        });
    }
}
