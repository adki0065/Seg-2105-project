package com.example.segproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    private Spinner serviceRole;

    private ArrayList<ClinicService> services;

    boolean initialized = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_service, container, false);
        serviceTable = (TableLayout) view.findViewById(R.id.serviceTable);
        serviceNameEditText = (EditText) view.findViewById(R.id.newServiceEditText);
        serviceButton = (Button) view.findViewById(R.id.newServiceButton);

        serviceRole = (Spinner) view.findViewById(R.id.serviceRoleSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.service_roles, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        serviceRole.setAdapter(adapter);

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
                        ClinicService service = document.toObject(ClinicService.class);
                        service.setId(document.getId());
                        results.add(service);
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

        for (final ClinicService service : services) {
            TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.admin_user_row, serviceTable, false);
            row.setId(i);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAlertDialogRowClicked(view, service);
                }
            });

            if (++i % 2 == 0) {
                row.setBackgroundColor(671088640);
            }

            String name = service.getName();
            String role = service.getRole();

            TextView nameCol = (TextView) getLayoutInflater().inflate(R.layout.admin_user_column, row, false);
            nameCol.setText(name);


            TextView roleCol = (TextView) getLayoutInflater().inflate(R.layout.admin_user_column, row, false);
            roleCol.setText(role);

            row.addView(nameCol);
            row.addView(roleCol);

            serviceTable.addView(row);
        }
    }

    private void showEditAlertDialog(final ClinicService service, final View topView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());

        View view = getLayoutInflater().inflate(R.layout.admin_service_edit_dialog, null);
        builder.setView(view);

        final EditText nameEdit = (EditText) view.findViewById(R.id.service_edit_name);
        final Spinner roleSpinner = (Spinner) view.findViewById(R.id.service_edit_role);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.service_roles, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        roleSpinner.setAdapter(adapter);

        builder.setTitle("Edit " + service.getName());

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.setPositiveButton("Edit", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        final String name = nameEdit.getText().toString().trim();

                        if (name.isEmpty()) {
                            Toast.makeText(nameEdit.getContext(), "Name is required.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (name.length() > 50) {
                            Toast.makeText(nameEdit.getContext(), "Name must be less than 50 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final String role = roleSpinner.getSelectedItem().toString().toLowerCase().trim();

                        servicesRef.document(service.getId()).update("name", name, "role", role)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        service.setName(name);
                                        service.setRole(role);
                                        dialog.dismiss();
                                        updateTable();
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(nameEdit.getContext(), "Couldn't update service.", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Error updating document", e);
                                    }
                                });
                    }
                });
            }
        });

        dialog.show();
    }

    private void showAlertDialogRowClicked(final View view, final ClinicService service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit/delete " + service.getName());
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showEditAlertDialog(service, view);
            }
        });
        builder.setNegativeButton("Delete", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button deleteButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                deleteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View _view) {
                        servicesRef.document(service.getId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        for (int j = 0; j < services.size(); j++) {
                                            if (service.getId().equals(services.get(j).getId())) {
                                                services.remove(j);
                                                break;
                                            }
                                        }
                                        dialog.dismiss();
                                        updateTable();
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Couldn't delete service.", Toast.LENGTH_SHORT).show();
//                                        Util.ShowSnackbar(view, "Couldn't delete service", getResources().getColor(android.R.color.holo_red_light));
                                        Log.e(TAG, "Error updating document", e);
                                    }
                                });
                    }
                });
            }
        });

        dialog.show();
    }

    public void OnNewServicePress(final View view) {
        final String name = serviceNameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            Util.ShowSnackbar(view, "Name is required", getResources().getColor(android.R.color.holo_red_light));
            return;
        } else if (name.length() > 50) {
            Util.ShowSnackbar(view, "Name must be less than 50 characters.", getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        final String role = serviceRole.getSelectedItem().toString().toLowerCase().trim();

        serviceRole.setEnabled(false);
        serviceNameEditText.setEnabled(false);
        serviceButton.setEnabled(false);

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("role", role);
        data.put("created", FieldValue.serverTimestamp());

        servicesRef.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        ClinicService service = new ClinicService();
                        service.setId(documentReference.getId());
                        service.setName(name);
                        service.setRole(role);

                        services.add(0, service);

                        updateTable();

                        Log.d(TAG, "New service written with ID: " + documentReference.getId());

                        serviceNameEditText.setEnabled(true);
                        serviceButton.setEnabled(true);
                        serviceNameEditText.setText("");
                        serviceRole.setEnabled(true);
                        serviceRole.setSelection(0);
                        Util.ShowSnackbar(view, "Added service.", getResources().getColor(android.R.color.holo_blue_bright));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception err) {
                        serviceNameEditText.setEnabled(true);
                        serviceButton.setEnabled(true);
                        serviceRole.setEnabled(true);
                        Log.e(TAG, "Error adding service", err);
                        Util.ShowSnackbar(view, "Couldn't add service.", getResources().getColor(android.R.color.holo_red_light));
                    }
                });

    }
}
