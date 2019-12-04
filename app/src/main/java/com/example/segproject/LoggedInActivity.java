package com.example.segproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LoggedInActivity extends AppCompatActivity {
    final String TAG = "LoggedInActivityLog";
//    private TextView teamAddress;

    private FirebaseFirestore db;
    private CollectionReference clinicsRef;
    private CollectionReference servicesRef;

    private List<Clinic> clinics;

    private EditText clinicName;
    private EditText clinicAddress;
    private Button paymentButton;
    private Spinner serviceRole;

    private List<String> payments;
    private List<ClinicService> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

//        teamAddress = (TextView) findViewById(R.id.loggedInText);

        Intent intent = getIntent();

        clinicName = (EditText) findViewById(R.id.clinic_name_field);
        clinicAddress = (EditText) findViewById(R.id.clinic_address_field);
        paymentButton = (Button) findViewById(R.id.payment_button);
        serviceRole = (Spinner) findViewById(R.id.serviceRoleSpinner);

        ClinicAccount account = (ClinicAccount) intent.getSerializableExtra("account");
        db = FirebaseFirestore.getInstance();
        clinicsRef = db.collection("clinics");
        servicesRef = db.collection("services");

        clinics = new ArrayList<>();
        payments = new ArrayList<>();
        services = new ArrayList<>();

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

                    getServices();

                } else {
                    Log.e(TAG, "Couldn't get all clinics", task.getException());

                }
            }
        });

        Spinner serviceRole = (Spinner) findViewById(R.id.serviceRoleSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.service_roles_none, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        serviceRole.setAdapter(adapter);

//        teamAddress.setText("Welcome " + account.getName() + "! You are logged in as " + account.getRole());
    }

    private void getServices() {
        servicesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot result = task.getResult();

                    if (result.isEmpty()) return;

                    for (QueryDocumentSnapshot document : result) {
                        ClinicService service = document.toObject(ClinicService.class);
                        service.setId(document.getId());
                        services.add(service);
                    }
                } else {
                    Log.e(TAG, "Couldn't get all clinics", task.getException());

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;
    }

    public void OnSelectPayments(View view) {
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

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        List<String> selectedPayments = new ArrayList<>();
                        if (ohipCheck.isChecked()) selectedPayments.add("ohip");
                        if (visaCheck.isChecked()) selectedPayments.add("visa");
                        if (masterCheck.isChecked()) selectedPayments.add("mastercard");
                        if (amexCheck.isChecked()) selectedPayments.add("amex");
                        if (cashCheck.isChecked()) selectedPayments.add("cash");

                        payments = selectedPayments;

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
        if (payments.size() == 0) {
            paymentButton.setText("Select payments");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < payments.size(); i++) {
            if (i != 0) sb.append(", ");
            sb.append(payments.get(i));
        }

        paymentButton.setText(sb.toString());
    }

    public void OnSearchClinic(View view) {
        String name = clinicName.getText().toString().trim().toLowerCase();
        String address = clinicAddress.getText().toString().trim().toLowerCase();
        String type = serviceRole.getSelectedItem().toString().toLowerCase();

//        Log.d(TAG, name + ", " + address + ", " + type + ", " + payments.toString());

        boolean searchName = true;
        boolean searchAddress = true;
        boolean searchType = true;
        boolean searchPayments = true;

        if (name.isEmpty()) searchName = false;
        if (address.isEmpty()) searchAddress = false;
        if (type.equals("none")) searchType = false;
        if (payments.size() == 0) searchPayments = false;

        if (!searchName && !searchAddress && !searchType && !searchPayments) {
            Util.ShowSnackbar(view, "At least one field must be entered", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        if (searchName) {
            String validateName = Util.ValidateName(name);
            if (validateName != null) {
                Util.ShowSnackbar(view, validateName, getResources().getColor(android.R.color.holo_red_light));
                return;
            }
        }

        if (searchAddress) {
            String validateAddress = Util.ValidateAddress(address);
            if (validateAddress != null) {
                Util.ShowSnackbar(view, validateAddress, getResources().getColor(android.R.color.holo_red_light));
                return;
            }
        }

        ArrayList<Clinic> searchClinics = new ArrayList<>(clinics);

        if (searchName) {
            Iterator<Clinic> iter = searchClinics.iterator();
            while (iter.hasNext())
                if (!iter.next().getName().toLowerCase().contains(name))
                    iter.remove();
        }

        if (searchAddress) {
            Iterator<Clinic> iter = searchClinics.iterator();
            while (iter.hasNext())
                if (!iter.next().getAddress().toLowerCase().contains(address))
                    iter.remove();
        }

        if (searchType) {
            List<String> selectedIDs = new ArrayList<>(services.size());

            for (ClinicService service : services) {
                if (service.getRole().toLowerCase().equals(type))
                    selectedIDs.add(service.getId());
            }

            Iterator<Clinic> iter = searchClinics.iterator();
            while (iter.hasNext()) {
                Clinic next = iter.next();
                List<String> nextServices = next.getServices();

                boolean found = false;
                for (String id : selectedIDs) {
                    if (nextServices.contains(id)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    iter.remove();
                }
            }
        }

        if (searchPayments) {
            Iterator<Clinic> iter = searchClinics.iterator();
            while (iter.hasNext()) {
                Clinic next = iter.next();

                boolean found = false;
                for (String selectedPayment : payments) {
                    if (next.getPayments().contains(selectedPayment)) {
                        found = true;
                        break;
                    }
                }

                if (!found)
                    iter.remove();
            }
        }


        if (searchClinics.size() == 0) {
            Util.ShowSnackbar(view, "No clinics found", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        Intent returnIntent = new Intent(this, PatientSearchResultActivity.class);

        returnIntent.putExtra("results", searchClinics);

        startActivity(returnIntent);
        this.finish();
    }
}
