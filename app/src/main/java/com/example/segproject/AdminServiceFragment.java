package com.example.segproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.HashMap;

public class AdminServiceFragment extends Fragment {
    final String TAG = "ServiceFragmentLog";

    private FirebaseFirestore db;
    private CollectionReference servicesRef;

    private TableLayout serviceTable;
    private EditText serviceNameEditText;
    private Button serviceButton;

    private ArrayList<ClinicService> services;

    boolean initialized = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_service, container, false);
        serviceTable = (TableLayout) view.findViewById(R.id.serviceTable);
        serviceNameEditText = (EditText) view.findViewById(R.id.newServiceEditText);
        serviceButton = (Button) view.findViewById(R.id.newServiceButton);

        db = FirebaseFirestore.getInstance();
        servicesRef = db.collection("services");

        services = new ArrayList<>();

        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnNewServicePress(v);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getServices();

        initialized = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (initialized && isVisibleToUser) getServices();
    }

    private void getServices() {
        servicesRef.orderBy("created", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();

                    ArrayList<ClinicService> results = new ArrayList<>();

                    if (result.isEmpty()) {
                        services = results;
                        return;
                    }

                    for (DocumentSnapshot document : result) {
                        results.add(document.toObject(ClinicService.class));
                    }
                    services = results;
                    updateTable();
                } else {
                    Log.e(TAG, "Failed to get services", task.getException());
                    Util.ShowSnackbar(getView(), "Failed to update services.", getResources().getColor(android.R.color.holo_red_light));
                }
            }
        });
    }

    public void updateTable() {
        serviceTable.removeAllViews();
        serviceTable.addView(getLayoutInflater().inflate(R.layout.admin_service_header, serviceTable, false));

        int i = 0;

        for (int j = 0; j < 10; j++) {
            TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.admin_user_row, serviceTable, false);
            row.setBackgroundColor(671088640);
            serviceTable.addView(row);
        }

        for (ClinicService account : services) {
            TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.admin_user_row, serviceTable, false);


            if (++i % 2 == 0) {
                row.setBackgroundColor(671088640);
            }

            String name = account.getName();

            TextView nameCol = (TextView) getLayoutInflater().inflate(R.layout.admin_user_column, row, false);
            nameCol.setText(name);

            row.addView(nameCol);

            serviceTable.addView(row);
        }
    }

    public void OnNewServicePress(final View view) {
        final String name = serviceNameEditText.getText().toString().trim();
        if (name.isEmpty()) return;

        serviceNameEditText.setEnabled(false);
        serviceButton.setEnabled(false);

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("created", FieldValue.serverTimestamp());

        servicesRef.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        services.add(0, new ClinicService(name));
                        updateTable();
                        Log.d(TAG, "New service written with ID: " + documentReference.getId());
                        serviceNameEditText.setEnabled(true);
                        serviceButton.setEnabled(true);
                        serviceNameEditText.setText("");
                        Util.ShowSnackbar(view, "Added service.", getResources().getColor(android.R.color.holo_blue_bright));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception err) {
                        serviceNameEditText.setEnabled(true);
                        serviceButton.setEnabled(true);
                        Log.e(TAG, "Error adding service", err);
                        Util.ShowSnackbar(view, "Couldn't add service.", getResources().getColor(android.R.color.holo_red_light));
                    }
                });

    }
}
