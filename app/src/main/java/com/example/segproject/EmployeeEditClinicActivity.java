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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeEditClinicActivity extends AppCompatActivity {
    final String TAG = "EditClinicLog";

    Button paymentButton;

    private Clinic clinic;

    EditText nameEditText;
    EditText addressEditText;
    EditText phoneEditText;

    private AlertDialog currentDialog;

    private FirebaseFirestore db;
    private CollectionReference servicesRef;
    private CollectionReference clinicsRef;

    private Boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_edit_clinic);

        Intent intent = getIntent();
        Serializable clinicSerial = intent.getSerializableExtra("clinic");

        paymentButton = (Button) findViewById(R.id.clinic_payment_button);

        nameEditText = (EditText) findViewById(R.id.clinic_name_field);
        addressEditText = (EditText) findViewById(R.id.clinic_address_field);
        phoneEditText = (EditText) findViewById(R.id.clinic_phone_field);

        if (clinicSerial != null) {
            clinic = (Clinic) clinicSerial;

            nameEditText.setText(clinic.getName());
            addressEditText.setText(clinic.getAddress());
            phoneEditText.setText(clinic.getPhone());

            updatePaymentButton();

            TextView activityTitle = (TextView) findViewById(R.id.edit_clinic_title);
            activityTitle.setText("Edit: " + clinic.getName());

            Button createButton = (Button) findViewById(R.id.edit_clinic_button);
            createButton.setText("Edit Clinic");

            Button deleteButton = (Button) findViewById(R.id.delete_clinic_button);
            deleteButton.setEnabled(true);

            isEdit = true;

        } else clinic = new Clinic();

        db = FirebaseFirestore.getInstance();
        servicesRef = db.collection("services");
        clinicsRef = db.collection("clinics");
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

                        clinic.setPayments(payments);

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

        ArrayList<String> payments = (ArrayList<String>) clinic.getPayments();

        for (int i = 0; i < payments.size(); i++) {
            if (i != 0) sb.append(", ");
            sb.append(payments.get(i));
        }

        paymentButton.setText(sb.toString());
    }

    public void OnSetServices(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.clinic_services_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Set clinic services");

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
                        dialog.dismiss();
                    }
                });

                servicesRef.orderBy("created", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            ArrayList<ClinicService> results = new ArrayList<>();

                            TableLayout serviceTable = dialog.findViewById(R.id.serviceTable);
                            serviceTable.removeAllViews();

                            if (result.isEmpty()) {
                                // TODO: add not found to serviceTable
//                                services = results;
                                return;
                            }


                            serviceTable.addView(getLayoutInflater().inflate(R.layout.clinic_services_header, serviceTable, false));

                            ArrayList<String> currentServices = (ArrayList<String>) clinic.getServices();

                            int i = 0;
                            for (DocumentSnapshot document : result) {

                                final ClinicService service = document.toObject(ClinicService.class);
                                service.setId(document.getId());

                                TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.clinic_services_row, serviceTable, false);
                                row.setId(i);
//                                results.add(service);

                                if (++i % 2 == 0) {
                                    row.setBackgroundColor(Util.ROW_BG_COLOR);
                                }

                                String name = service.getName();
                                String role = service.getRole();

                                TextView nameCol = (TextView) getLayoutInflater().inflate(R.layout.clinic_services_column, row, false);
                                nameCol.setText(name);

                                TextView roleCol = (TextView) getLayoutInflater().inflate(R.layout.clinic_services_column, row, false);
                                roleCol.setText(role);

                                final CheckBox checkCol = (CheckBox) getLayoutInflater().inflate(R.layout.clinic_services_checkbox, row, false);

                                for (String currentService : currentServices) {
                                    if (currentService.equals(service.getId())) {
                                        checkCol.setChecked(true);
                                    }
                                }

                                checkCol.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> services = (ArrayList<String>) clinic.getServices();

                                        if (checkCol.isChecked()) {
                                            ArrayList<String> newServices = new ArrayList<>(services.size() + 1);

                                            int i = 0;
                                            while (i < services.size()) {
                                                newServices.add(services.get(i));
                                                i++;
                                            }

                                            newServices.add(service.getId());

                                            clinic.setServices(newServices);
                                        } else {
                                            ArrayList<String> newServices = new ArrayList<>(services.size() - 1);

                                            for (int i = 0; i < services.size(); i++) {
                                                String id = service.getId();
                                                if (!services.get(i).equals(id)) {
                                                    newServices.add(services.get(i));
                                                }
                                            }

                                            clinic.setServices(newServices);
                                        }

                                    }
                                });

                                row.addView(checkCol);
                                row.addView(nameCol);
                                row.addView(roleCol);

                                serviceTable.addView(row);

                                i++;
                            }

                            Log.d(TAG, clinic.getServices().toString());
//                            services = results;

                        } else {
                            Log.e(TAG, "Failed to get services", task.getException());
                            Util.ShowToast(view, "Failed to update services.");
                        }
                    }
                });

            }
        });

        dialog.show();
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

        final HashMap<String, ClinicHours> schedule = (HashMap<String, ClinicHours>) clinic.getHours();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


//                        Log.d(TAG, schedule.toString());
                        for (String key : schedule.keySet()) {
                            ClinicHours day = schedule.get(key);

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

        for (String key : schedule.keySet()) {
            int checkID = getHourCheckID(key);

            ((CheckBox) dialog.findViewById(checkID)).setChecked(true);

            int[] buttonsID = getHourButtonsID(key);
            int startButtonID = buttonsID[0];
            int endButtonID = buttonsID[1];

            ClinicHours day = schedule.get(key);

            Button startButton = (Button) dialog.findViewById(startButtonID);
            Button endButton = (Button) dialog.findViewById(endButtonID);

            startButton.setEnabled(true);
            endButton.setEnabled(true);

            startButton.setText(day.getStart());
            endButton.setText(day.getEnd());
        }
    }

    public void OnSetDayHours(final View view) {
//        final AlertDialog prevDialog = currentDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.time_picker, null);
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

                        ClinicHours day = schedule.get(keys[0]);

                        if (day == null) {
                            day = new ClinicHours();
                        }

                        if (keys[1].equals("start"))
                            day.setStart(Util.FormatTime(hour) + ":" + Util.FormatTime(minute));
                        else day.setEnd(Util.FormatTime(hour) + ":" + Util.FormatTime(minute));

                        schedule.put(keys[0], day);

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


        String day = check.getTag().toString();

        int[] buttonsID = getHourButtonsID(day);
        int startButtonID = buttonsID[0];
        int endButtonID = buttonsID[1];

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

    public void OnCreateClinic(final View view) {
        Log.d(TAG, clinic.toString());

        String name = nameEditText.getText().toString().trim();
        String validateName = Util.ValidateName(name);
        if (validateName != null) {
            Util.ShowSnackbar(view, validateName, getResources().getColor(android.R.color.holo_red_light));
            return;
        }
        clinic.setName(name);

        String address = addressEditText.getText().toString().trim();
        String validateAddress = Util.ValidateAddress(address);
        if (validateAddress != null) {
            Util.ShowSnackbar(view, validateAddress, getResources().getColor(android.R.color.holo_red_light));
            return;
        }
        clinic.setAddress(address);


        String phone = phoneEditText.getText().toString().trim();
        String validatePhone = Util.ValidatePhone(phone);
        if (validatePhone != null) {
            Util.ShowSnackbar(view, validatePhone, getResources().getColor(android.R.color.holo_red_light));
            return;
        }
        clinic.setPhone(phone);

        ArrayList<String> payments = (ArrayList<String>) clinic.getPayments();
        if (payments.size() == 0) {
            Util.ShowSnackbar(view, "Select at least one payment type", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        ArrayList<String> services = (ArrayList<String>) clinic.getServices();
        if (services.size() == 0) {
            Util.ShowSnackbar(view, "Select at least one service", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        HashMap<String, ClinicHours> hours = (HashMap<String, ClinicHours>) clinic.getHours();
        if (hours.size() == 0) {
            Util.ShowSnackbar(view, "Set clinic hours for at least one day", getResources().getColor(android.R.color.holo_red_light));
            return;
        }


        HashMap<String, Object> data = new HashMap<>();
        data.put("name", clinic.getName());
        data.put("address", clinic.getAddress());
        data.put("phone", clinic.getPhone());
        data.put("payments", clinic.getPayments());
        data.put("services", clinic.getServices());
        data.put("hours", clinic.getHours());
        data.put("created", FieldValue.serverTimestamp());

        if (isEdit) {
            clinicsRef.document(clinic.getId()).set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent returnIntent = new Intent(view.getContext(), EmployeeActivity.class);

                            startActivity(returnIntent);

                            Log.d(TAG, "Updated clinic with ID: " + clinic.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception err) {
                            Util.ShowSnackbar(view, "Error updating clinic.", getResources().getColor(android.R.color.holo_red_light));
                            Log.e(TAG, "Error adding clinic", err);
                        }
                    });
        } else {
            clinicsRef.add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            clinic.setId(documentReference.getId());

                            Intent returnIntent = new Intent(view.getContext(), EmployeeActivity.class);

                            startActivity(returnIntent);

                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception err) {
                            Util.ShowSnackbar(view, "Error creating clinic.", getResources().getColor(android.R.color.holo_red_light));
                            Log.e(TAG, "Error adding clinic", err);
                        }
                    });
        }
    }

    public void OnDeleteClinic(final View view) {
        String id = clinic.getId();

        if (id == null) {
            Util.ShowSnackbar(view, "Clinic hasn't been created.", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        clinicsRef.document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent returnIntent = new Intent(view.getContext(), EmployeeActivity.class);

                        startActivity(returnIntent);

                        Log.d(TAG, "Updated clinic with ID: " + clinic.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception err) {
                        Util.ShowSnackbar(view, "Error updating clinic.", getResources().getColor(android.R.color.holo_red_light));
                        Log.e(TAG, "Error adding clinic", err);
                    }
                });
    }

    public static int getHourCheckID(String day) {
        int checkID;

        switch (day) {
            case "monday":
                checkID = R.id.check_monday;
                break;
            case "tuesday":
                checkID = R.id.check_tuesday;
                break;
            case "wednesday":
                checkID = R.id.check_wednesday;
                break;
            case "thursday":
                checkID = R.id.check_thursday;
                break;
            case "friday":
                checkID = R.id.check_friday;
                break;
            case "saturday":
                checkID = R.id.check_saturday;
                break;
            default:
                checkID = R.id.check_sunday;
                break;
        }

        return checkID;
    }

    public static int[] getHourButtonsID(String day) {
        int startButtonID;
        int endButtonID;
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

        return new int[]{
                startButtonID, endButtonID
        };
    }
}

