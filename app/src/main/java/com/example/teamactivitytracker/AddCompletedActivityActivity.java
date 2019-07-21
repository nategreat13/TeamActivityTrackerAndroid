package com.example.teamactivitytracker;

import android.content.Context;
import android.os.Bundle;

import com.example.teamactivitytracker.Model.Activity;
import com.example.teamactivitytracker.Model.DB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddCompletedActivityActivity extends AppCompatActivity {

    private DB db;
    private String tid;
    private String pid;
    private String playerName;
    private Activity activity;
    private TextView errorLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_completed_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.complete_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        errorLabel = findViewById(R.id.errorLabel);

        db = new DB();
        tid = getIntent().getStringExtra("TID");
        pid = getIntent().getStringExtra("PID");
        playerName = getIntent().getStringExtra("PLAYER_NAME");
        activity = (Activity) getIntent().getSerializableExtra("ACTIVITY");

        TextView activityNameTextView = findViewById(R.id.nameTextViewValue);
        activityNameTextView.setText(activity.getName());

        TextView activityDescriptionTextView = findViewById(R.id.descriptionTextViewValue);
        activityDescriptionTextView.setText(activity.getDescription());

        TextView activityPointsTextView = findViewById(R.id.pointsTextViewValue);
        activityPointsTextView.setText(Integer.toString(activity.getPoints()));

        String limitString = "Day";
        switch (activity.getLimitPeriod()) {
            case day:
                limitString = "Day";
            case week:
                limitString = "Week";
            case month:
                limitString = "Month";
        }

        TextView limitTextView = findViewById(R.id.limitTextViewValue);
        limitTextView.setText(Integer.toString(activity.getLimit()) + " Per " + limitString);
    }

    public void completeActivity(View view) {
        errorLabel.setText("");
        EditText quantityTextField = findViewById(R.id.quantityTextField);
        String quantityString = quantityTextField.getText().toString();
        if (quantityString.length() == 0) {
            errorLabel.setText(getString(R.string.activity_quantity_required));
            return;
        }
        int quantity = 0;
        try {
            quantity = Integer.parseInt(quantityString);
        }
        catch (NumberFormatException e) {
            errorLabel.setText(R.string.activity_quantity_must_be_number);
            return;
        }
        if (quantity == 0) {
            errorLabel.setText(R.string.activity_quantity_cant_be_zero);
            return;
        }
        EditText commentTextField = findViewById(R.id.commentTextField);
        String comment = commentTextField.getText().toString();

        db.addCompletedActivity(activity, System.currentTimeMillis(), quantity, activity.getPoints() * quantity, comment, pid, tid, playerName, completedActivity -> {
            if (completedActivity != null) {
                finish();
            }
            else {
                errorLabel.setText(R.string.error_completing_activity);
            }
        });
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
