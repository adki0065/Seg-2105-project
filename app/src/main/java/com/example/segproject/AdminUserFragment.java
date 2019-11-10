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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
                        results.add(document.toObject(ClinicAccount.class));
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
        for (ClinicAccount account : accounts) {
            final TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.admin_user_row, accountTable, false);
            row.setId(i);
            row.setContentDescription(account.getName());
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAlertDialogRowClicked(view);

            }});

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
    public void rowClick(View view) {
        view.getId();
    }
    public void showAlertDialogRowClicked(View view) {

        // setup the alert builder
        final View v = view;
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        String name = view.getContentDescription().toString();

        builder.setTitle("Edit or Delete " + name);



        builder.setPositiveButton("Edit               ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //edit account
            }
        });

        builder.setNeutralButton("Cancel              ", null);

        builder.setNegativeButton("Delete             ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete account
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
