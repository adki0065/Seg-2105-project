package com.example.segproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog.Builder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Color;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AdminUserFragment extends Fragment {
    final String TAG = "UserFragmentLog";

    private FirebaseFirestore db;
    private CollectionReference accountsRef;

    private TableLayout accountTable;

    private ArrayList<ClinicAccount> accounts;

    boolean initialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);
        db = FirebaseFirestore.getInstance();
        accountsRef = db.collection("accounts");

        accountTable = (TableLayout) view.findViewById(R.id.accountTable);

        accounts = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAccounts();

        initialized = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        if (initialized && isVisibleToUser) getAccounts();
    }

    private void getAccounts() {
        accountsRef.orderBy("created", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();

                    if (result.isEmpty()) {
//                        textView.setText("No accounts found");
                        return;
                    }
                    ArrayList<ClinicAccount> results = new ArrayList<>();

                    for (DocumentSnapshot document : result) {
                        ClinicAccount account = document.toObject(ClinicAccount.class);
                        //                        Log.d(TAG, account.toString());

                        if (!account.getRole().equals("admin")) {
                            account.setId(document.getId());
                            results.add(account);
                        }
                    }
                    accounts = results;
                    updateTable();
                } else {
                    Log.e(TAG, "Failed to get services", task.getException());
                    Util.ShowSnackbar(getView(), "Failed to update accounts.", getResources().getColor(android.R.color.holo_red_light));
                }
            }
        });
    }


    private void updateTable() {
        accountTable.removeAllViews();
        accountTable.addView(getLayoutInflater().inflate(R.layout.admin_user_header, accountTable, false));

        int i = 0;
        for (final ClinicAccount account : accounts) {
            final TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.admin_user_row, accountTable, false);
            row.setId(i);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAlertDialogRowClicked(view, account);

                }
            });

            if (++i % 2 == 0) {
                row.setBackgroundColor(671088640);
            }

            String username = account.getUsername();
            String name = account.getName();
            String role = account.getRole();

            TextView usernameCol = (TextView) getLayoutInflater().inflate(R.layout.admin_user_column, row, false);
            usernameCol.setText(username);

            TextView nameCol = (TextView) getLayoutInflater().inflate(R.layout.admin_user_column, row, false);
            nameCol.setText(name);

            TextView roleCol = (TextView) getLayoutInflater().inflate(R.layout.admin_user_column, row, false);
            roleCol.setText(role);


            row.addView(usernameCol);
            row.addView(nameCol);
            row.addView(roleCol);

            accountTable.addView(row);
        }
    }

    private void showEditAlertDialog(final ClinicAccount account, final View topView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());

        View view = getLayoutInflater().inflate(R.layout.admin_user_edit_dialog, null);
        builder.setView(view);

        final EditText nameEdit = (EditText) view.findViewById(R.id.user_edit_name);
        final ToggleButton roleEdit = (ToggleButton) view.findViewById(R.id.user_edit_role);

        builder.setTitle("Edit " + account.getUsername());

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
                        String validateName = Util.ValidateName(name);
                        if (validateName != null) {
                            Toast.makeText(nameEdit.getContext(), validateName, Toast.LENGTH_SHORT).show();
//                            Util.ShowSnackbar(topView, "Name is required.", getResources().getColor(android.R.color.holo_red_light));
                            return;
                        }

                        final String role = roleEdit.getText().toString().toLowerCase().trim();

                        accountsRef.document(account.getId()).update("name", name, "role", role)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        account.setName(name);
                                        account.setRole(role);
                                        dialog.dismiss();
                                        updateTable();
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(nameEdit.getContext(), "Couldn't update user.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        Log.e(TAG, "Error updating document", e);
                                    }
                                });
                    }
                });
            }
        });

//        builder.setPositiveButton("Edit " + account.getUsername(), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//
//            }
//        });

        dialog.show();
    }

    public void showAlertDialogRowClicked(final View view, final ClinicAccount account) {

        // setup the alert builder
        final View v = view;
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("Edit or Delete " + account.getUsername());

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                showEditAlertDialog(account, view);
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                accountsRef.document(account.getId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String uName = account.getUsername();
                                for (int j = 0; j < accounts.size(); j++) {
                                    if (uName.equals(accounts.get(j).getUsername())) {
                                        accounts.remove(j);
                                        updateTable();
                                        break;
                                    }
                                }
                                Log.d(TAG, "Deleted user");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getView().getContext(), "Couldn't delete user.", Toast.LENGTH_SHORT).show();

//                                Util.ShowSnackbar(getView(), "Failed to delete account.", getResources().getColor(android.R.color.holo_red_light));
                                Log.e(TAG, "Error deleting user", e);
                            }
                        });


//                boolean found = true;
//                int j = -1;
//                while (found) {
//                    j++;
//                    if (account.getUsername().equals(accounts.get(j).getUsername())) {
//                        accounts.remove(j);
//                        found = false;
//                        updateTable();
//                    }
//                }

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
