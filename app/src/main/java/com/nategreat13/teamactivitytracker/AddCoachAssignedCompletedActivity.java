package com.nategreat13.teamactivitytracker;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nategreat13.teamactivitytracker.Model.Activity;
import com.nategreat13.teamactivitytracker.Model.CompletedActivity;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Globals;
import com.nategreat13.teamactivitytracker.Model.LimitPeriod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddCoachAssignedCompletedActivity extends AppCompatActivity {

    private String pid;
    private String tid;
    private String playerName;
    private Activity[] activities;
    private Activity selectedActivity;
    private TextView activityNameTextView;
    private TextView activityDescriptionTextView;
    private TextView activityPointsTextView;
    private TextView limitTextView;
    private EditText commentTextField;
    private EditText quantityTextField;

    private TextView errorLabel;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coach_assigned_completed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Complete Activity");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pid = getIntent().getStringExtra("PID");
        playerName = getIntent().getStringExtra("PLAYER_NAME");
        activities = Globals.currentTeam.getActivities();
        tid = Globals.currentTeam.getTid();

        db = new DB();

        activityNameTextView = findViewById(R.id.nameTextViewValue);
        activityDescriptionTextView = findViewById(R.id.descriptionTextViewValue);
        activityPointsTextView = findViewById(R.id.pointsTextViewValue);
        limitTextView = findViewById(R.id.limitTextViewValue);
        commentTextField = findViewById(R.id.commentTextField);
        quantityTextField = findViewById(R.id.quantityTextField);
        errorLabel = findViewById(R.id.errorLabel);

        resetActivityLabels();

        List<String> spinnerArray =  new ArrayList<String>();

        spinnerArray.add("");

        for (int i = 0; i < activities.length; i++) {
            spinnerArray.add(activities[i].getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.activitySpinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    selectedActivity = null;
                    resetActivityLabels();
                }
                else {
                    selectedActivity = activities[position-1];
                    setActivityDetails(selectedActivity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void setActivityDetails(Activity activity) {
        activityNameTextView.setText(activity.getName());
        activityDescriptionTextView.setText(activity.getDescription());
        activityPointsTextView.setText(String.valueOf(activity.getPoints()));
        String limitPeriodString = "";
        if (activity.getLimitPeriod() == LimitPeriod.day) {
            limitPeriodString = "Day";
        }
        else if (activity.getLimitPeriod() == LimitPeriod.week) {
            limitPeriodString = "Week";
        }
        else if (activity.getLimitPeriod() == LimitPeriod.month) {
            limitPeriodString = "Month";
        }
        limitTextView.setText(Integer.toString(activity.getLimit()) + " Per " + limitPeriodString);
        quantityTextField.setText("");
    }

    public void resetActivityLabels() {
        activityNameTextView.setText("");
        activityDescriptionTextView.setText("");
        activityPointsTextView.setText("");
        limitTextView.setText("");
        quantityTextField.setText("");
    }

    public void completeActivity(View view) {
        if (selectedActivity == null) {
            errorLabel.setText(R.string.select_an_activity);
        }
        else {
            errorLabel.setText("");
            String quantityString = quantityTextField.getText().toString();
            if (quantityString.length() == 0) {
                errorLabel.setText(getString(R.string.activity_quantity_required));
                return;
            }
            int quantityTemp = 0;
            try {
                quantityTemp = Integer.parseInt(quantityString);
            }
            catch (NumberFormatException e) {
                errorLabel.setText(R.string.activity_quantity_must_be_number);
                return;
            }
            if (quantityTemp == 0) {
                errorLabel.setText(R.string.activity_quantity_cant_be_zero);
                return;
            }
            final int quantity = quantityTemp;
            EditText commentTextField = findViewById(R.id.commentTextField);
            String comment = commentTextField.getText().toString();

            Long timePeriodStart = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();

            switch (selectedActivity.getLimitPeriod()) {
                case day:
                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    timePeriodStart = calendar.getTimeInMillis();
                    break;
                case week:
                    calendar.setTime(new Date());
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    timePeriodStart = calendar.getTimeInMillis();
                    break;
                case month:
                    calendar.setTime(new Date());
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    timePeriodStart = calendar.getTimeInMillis();
            }

            timePeriodStart = timePeriodStart/1000;

            db.getNumberOfCompletedActivitiesSinceTime(pid, selectedActivity.getAid(), timePeriodStart, value -> {
                if (value >= selectedActivity.getLimit()) {
                    errorLabel.setText(R.string.period_limit_reached);
                }
                else if (value + quantity > selectedActivity.getLimit()) {
                    errorLabel.setText((selectedActivity.getLimit() - value) + " before limit is reached");
                }
                else {
                    db.addCompletedActivity(selectedActivity, System.currentTimeMillis()/1000, quantity, selectedActivity.getPoints() * quantity, comment, pid, tid, playerName, completedActivity -> {
                        if (completedActivity != null) {
                            finish();
                        }
                        else {
                            errorLabel.setText(R.string.error_completing_activity);
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
