package com.example.segproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class EmployeeActivity extends AppCompatActivity {
    final String TAG = "ServiceFragmentLog";

    ClinicAccount account;

    private FirebaseFirestore db;
    private CollectionReference clinicsRef;

    private TableLayout clinicTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        Intent intent = getIntent();

        account = (ClinicAccount) intent.getSerializableExtra("account");
        db = FirebaseFirestore.getInstance();
        clinicsRef = db.collection("clinics");

        clinicTable = (TableLayout) findViewById(R.id.employee_clinic_table);

        getClinics();
    }

    public void OnCreateClinicPress(View view) {
        Intent returnIntent = new Intent(this, EmployeeEditClinicActivity.class);

        startActivity(returnIntent);
    }

    private void getClinics() {
        for (int i = 0; i < 30; i++) {
            TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.employee_clinic_row, clinicTable, false);
            row.setId(i);
            if (i % 2 == 0) {
                row.setBackgroundColor(Util.ROW_BG_COLOR);
            }

//            TextView text = (TextView) row.findViewById(R.id.clinic_text_name);

//            text.setText(i);

            clinicTable.addView(row);
        }
    }
}
