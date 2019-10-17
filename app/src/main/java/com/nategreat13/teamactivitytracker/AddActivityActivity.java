package com.nategreat13.teamactivitytracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nategreat13.teamactivitytracker.Model.Activity;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.LimitPeriod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddActivityActivity extends AppCompatActivity {

    private DB db;
    private String tid;
    private Activity activityToEdit;
    private EditText activityNameTextField;
    private TextView activityDescriptionTextView;
    private EditText limitTextField;
    private EditText pointsTextField;
    private Spinner limitSelector;
    private Button addActivityButton;
    private TextView errorLabel;
    private CheckBox hideFromTeamCheckBox;
    private CheckBox assignedByCoachCheckBox;
    private boolean isEdit = false;

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
        isEdit = getIntent().getBooleanExtra("ISEDIT", false);

        activityNameTextField = findViewById(R.id.activityNameTextField);
        activityDescriptionTextView = findViewById(R.id.activityDescriptionTextView);
        limitTextField = findViewById(R.id.limitTextField);
        pointsTextField = findViewById(R.id.pointsTextField);
        limitSelector = findViewById(R.id.limitSelector);
        hideFromTeamCheckBox = findViewById(R.id.hideFromTeamCheckBox);
        assignedByCoachCheckBox = findViewById(R.id.assignedByCoachCheckBox);
        addActivityButton = findViewById(R.id.addActivityButton);
        errorLabel = findViewById(R.id.errorLabel);

        List<String> list = new ArrayList<String>();
        list.add("Day");
        list.add("Week");
        list.add("Month");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        limitSelector.setAdapter(dataAdapter);

        if (isEdit) {
            activityToEdit = (Activity) getIntent().getSerializableExtra("ACTIVITY");
            activityNameTextField.setText(activityToEdit.getName());
            activityDescriptionTextView.setText(activityToEdit.getDescription());
            limitTextField.setText(Integer.toString(activityToEdit.getLimit()));
            pointsTextField.setText(Integer.toString(activityToEdit.getPoints()));
            switch (activityToEdit.getLimitPeriod()) {
                case day:
                    limitSelector.setSelection(0, true);
                    break;
                case week:
                    limitSelector.setSelection(1, true);
                    break;
                case month:
                    limitSelector.setSelection(2, true);
            }
            hideFromTeamCheckBox.setChecked(activityToEdit.isHidden());
            assignedByCoachCheckBox.setChecked(activityToEdit.isCoachAssigned());
            addActivityButton.setText(R.string.edit_activity);
            getSupportActionBar().setTitle(R.string.edit_activity);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void addButtonPressed(View view) {
        errorLabel.setText("");
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
        String pointsString = pointsTextField.getText().toString();
        if (pointsString.length() == 0) {
            errorLabel.setText(getString(R.string.activity_points_required));
            return;
        }
        int points = 0;
        try {
            points = Integer.parseInt(pointsString);
        }
        catch (NumberFormatException e) {
            errorLabel.setText(R.string.activity_points_must_be_number);
            return;
        }
        if (points == 0) {
            errorLabel.setText(R.string.activity_points_cant_be_zero);
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
        LimitPeriod limitPeriod = LimitPeriod.day;
        int limitPeriodIndex = limitSelector.getSelectedItemPosition();
        if (limitPeriodIndex == 1) {
            limitPeriod = LimitPeriod.week;
        }
        else if (limitPeriodIndex == 2) {
            limitPeriod = LimitPeriod.month;
        }
        if (!isEdit) {
            db.addActivity(activityName, activityDescription, points, tid, limit, limitPeriod, hideFromTeamCheckBox.isChecked(), assignedByCoachCheckBox.isChecked(), activity -> {
                if (activity != null) {
                    finish();
                }
                else {
                    errorLabel.setText(R.string.error_adding_activity);
                }
            });
        }
        else {
            db.editActivity(new Activity(activityToEdit.getAid(), activityName, activityDescription, points, tid, limit, limitPeriod, hideFromTeamCheckBox.isChecked(), assignedByCoachCheckBox.isChecked()), activity -> {
                if (activity != null) {
                    finish();
                }
                else {
                    errorLabel.setText(R.string.error_updating_activity);
                }
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(android.app.Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
