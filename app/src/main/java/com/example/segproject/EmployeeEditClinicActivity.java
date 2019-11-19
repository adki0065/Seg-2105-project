package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeeEditClinicActivity extends AppCompatActivity {
    final String TAG = "EditClinicLog";

    Button paymentButton;

    private Clinic clinic;

    private AlertDialog currentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_edit_clinic);

        Intent intent = getIntent();
        Serializable clinicSerial = intent.getSerializableExtra("clinic");

        paymentButton = (Button) findViewById(R.id.clinic_payment_button);

        if (clinicSerial != null) clinic = (Clinic) clinicSerial;
        else clinic = new Clinic();
    }

    public void OnSelectClinicPayment(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.clinic_payment_dialog, null);
        builder.setView(dialogView);

        final CheckBox ohipCheck = dialogView.findViewById(R.id.clinic_payment_ohip);
        final CheckBox visaCheck = dialogView.findViewById(R.id.clinic_payment_visa);
        final CheckBox masterCheck = dialogView.findViewById(R.id.clinic_payment_master);
        final CheckBox amexCheck = dialogView.findViewById(R.id.clinic_payment_amex);
        final CheckBox cashCheck = dialogView.findViewById(R.id.clinic_payment_cash);

        builder.setTitle("Select payment methods");

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.setPositiveButton("Select", null);

        final AlertDialog dialog = builder.create();
        currentDialog = dialog;

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        ArrayList<String> payments = new ArrayList<>();
                        if (ohipCheck.isChecked()) payments.add("ohip");
                        if (visaCheck.isChecked()) payments.add("visa");
                        if (masterCheck.isChecked()) payments.add("mastercard");
                        if (amexCheck.isChecked()) payments.add("amex");
                        if (cashCheck.isChecked()) payments.add("cash");

                        if (payments.size() == 0) {
                            Util.ShowToast(view, "Select at least one payment method");
                            return;
                        }

                        clinic.setPayments(payments.toArray(new String[0]));

                        dialog.dismiss();

                        updatePaymentButton();

                        Log.d(TAG, "Payments: " + payments.size());
                    }
                });
            }
        });

        dialog.show();
    }

    private void updatePaymentButton() {
        StringBuilder sb = new StringBuilder();

        String[] payments = clinic.getPayments();

        for (int i = 0; i < payments.length; i++) {
            if (i != 0) sb.append(", ");
            sb.append(payments[i]);
        }

        paymentButton.setText(sb.toString());
    }

    public void OnSetHours(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.clinic_hours_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Set clinic hours");

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.setPositiveButton("Set", null);

        final AlertDialog dialog = builder.create();
        currentDialog = dialog;

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        HashMap<String, ClinicHours> schedule = (HashMap<String, ClinicHours>) clinic.getHours();

                        for (String key : schedule.keySet()) {
                            ClinicHours hours = schedule.get(key);
                            ClinicDayHours day = hours.getDay();

                            if (day != null && (day.getStart() == null || day.getEnd() == null)) {
                                Util.ShowToast(view, "Each day must have a start and end time");
                                return;
                            }
                        }

                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    public void OnSetDayHours(final View view) {
//        final AlertDialog prevDialog = currentDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.date_time_picker, null);
        builder.setView(dialogView);

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                currentDialog = prevDialog;
            }
        });

        builder.setPositiveButton("Set", null);

        final AlertDialog dialog = builder.create();
//        currentDialog = dialog;

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

                        Button timeButton = ((Button) view);
                        String timeButtonID = getResources().getResourceEntryName(timeButton.getId());
                        String[] keys = timeButtonID.split("_");

                        HashMap<String, ClinicHours> schedule = (HashMap<String, ClinicHours>) clinic.getHours();

                        if (schedule == null) {
                            schedule = new HashMap<>();
                        }

                        ClinicHours hours = schedule.get(keys[0]);

                        if (hours == null) {
                            hours = new ClinicHours();
                        }

                        ClinicDayHours day = hours.getDay();

                        if (day == null) {
                            day = new ClinicDayHours();
                        }

                        if (keys[1].equals("start")) day.setStart(hour + ":" + minute);
                        else day.setEnd(hour + ":" + minute);

                        hours.setDay(day);
                        schedule.put(keys[0], hours);

                        Log.d(TAG, schedule.toString());

                        clinic.setHours(schedule);

                        ((Button) view).setText(Util.FormatTime(hour) + ":" + Util.FormatTime(minute));

                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    public void OnClosedDay(View view) {
        CheckBox check = (CheckBox) view;

        int startButtonID;
        int endButtonID;
        String day = check.getTag().toString();

        switch (day) {
            case "monday":
                startButtonID = R.id.monday_start;
                endButtonID = R.id.monday_end;
                break;
            case "tuesday":
                startButtonID = R.id.tuesday_start;
                endButtonID = R.id.tuesday_end;
                break;
            case "wednesday":
                startButtonID = R.id.wednesday_start;
                endButtonID = R.id.wednesday_end;
                break;
            case "thursday":
                startButtonID = R.id.thursday_start;
                endButtonID = R.id.thursday_end;
                break;
            case "friday":
                startButtonID = R.id.friday_start;
                endButtonID = R.id.friday_end;
                break;
            case "saturday":
                startButtonID = R.id.saturday_start;
                endButtonID = R.id.saturday_end;
                break;
            default:
                startButtonID = R.id.sunday_start;
                endButtonID = R.id.sunday_end;
                break;
        }

        Button startButton = (Button) currentDialog.findViewById(startButtonID);
        Button endButton = (Button) currentDialog.findViewById(endButtonID);

        if (check.isChecked()) {
            startButton.setEnabled(true);
            endButton.setEnabled(true);
        } else {
            HashMap<String, ClinicHours> hours = (HashMap<String, ClinicHours>) clinic.getHours();
            if (hours != null) {
                hours.remove(day);
            }
            clinic.setHours(hours);

            startButton.setText("Closed");
            endButton.setText("Closed");
            startButton.setEnabled(false);
            endButton.setEnabled(false);

            Log.d(TAG, clinic.getHours().toString());
        }
    }
}

