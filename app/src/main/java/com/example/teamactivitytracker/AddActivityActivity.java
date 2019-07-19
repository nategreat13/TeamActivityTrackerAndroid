package com.example.teamactivitytracker;

import android.os.Bundle;

import com.example.teamactivitytracker.Model.DB;
import com.example.teamactivitytracker.Model.ProfileType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AddActivityActivity extends AppCompatActivity {

    private DB db;
    private String tid;
    private EditText activityNameTextField;
    private TextView activityDescriptionTextView;
    private EditText limitTextField;
    private Spinner limitSelector;
    private Button addActivityButton;
    private TextView errorLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DB();
        tid = getIntent().getStringExtra("TID");

        activityNameTextField = findViewById(R.id.activityNameTextField);
        activityDescriptionTextView = findViewById(R.id.activityDescriptionTextView);
        limitTextField = findViewById(R.id.limitTextField);
        limitSelector = findViewById(R.id.limitSelector);
        addActivityButton = findViewById(R.id.addActivityButton);
        errorLabel = findViewById(R.id.errorLabel);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void addButtonPressed(View view) {
        String activityName = activityNameTextField.getText().toString();
        if (activityName.length() == 0) {
            errorLabel.setText(getString(R.string.activity_name_required));
            return;
        }
        String activityDescription = activityDescriptionTextView.getText().toString();
        if (activityDescription.length() == 0) {
            errorLabel.setText(getString(R.string.activity_description_required));
            return;
        }
        String limitString = limitTextField.getText().toString();
        if (limitString.length() == 0) {
            errorLabel.setText(getString(R.string.activity_limit_required));
            return;
        }
        int limit = 0;
        try {
            limit = Integer.parseInt(limitString);
        }
        catch (NumberFormatException e) {
            errorLabel.setText(R.string.activity_limit_must_be_number);
            return;
        }
        if (limit == 0) {
            errorLabel.setText(R.string.activity_limit_cant_be_zero);
            return;
        }
    }
}
