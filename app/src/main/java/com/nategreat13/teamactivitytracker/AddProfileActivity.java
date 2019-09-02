package com.nategreat13.teamactivitytracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class AddProfileActivity extends AppCompatActivity {

    private DB db;
    private EditText firstNameTextField;
    private EditText lastNameTextField;
    private EditText teamIDTextField;
    private Button addButton;
    private RadioButton playerRadioButton;
    private RadioButton coachRadioButton;
    private TextView errorLabel;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firstNameTextField = findViewById(R.id.firstNameTextField);
        lastNameTextField = findViewById(R.id.lastNameTextField);
        teamIDTextField = findViewById(R.id.teamIDTextField);
        addButton = findViewById(R.id.addButton);
        playerRadioButton = findViewById(R.id.newTeamRadioButton);
        coachRadioButton = findViewById(R.id.coachRadioButton);
        errorLabel = findViewById(R.id.errorLabel);

        user = (User) getIntent().getSerializableExtra("USER");

        db = new DB();
    }

    public void playerRadioButtonSelected(View view) {
        addButton.setText(getString(R.string.add_player));
    }

    public void coachRadioButtonSelected(View view) {
        addButton.setText(getString(R.string.add_coach));
    }

    public void addButtonPressed(View view) {
        String firstName = firstNameTextField.getText().toString();
        String lastName = lastNameTextField.getText().toString();
        String tid = teamIDTextField.getText().toString();

        if (firstName.length() == 0) {
            errorLabel.setText(getString(R.string.first_name_empty));
            return;
        }
        if (lastName.length() == 0) {
            errorLabel.setText(getString(R.string.last_name_empty));
            return;
        }
        if (playerRadioButton.isChecked()) {
            addPlayer(firstName, lastName, tid);
        }
        else {
            addCoach(firstName, lastName, tid);
        }
    }

    public void addPlayer(String firstName, String lastName, String tid) {
        if (tid.equals("")) {
            db.addPlayer(firstName, lastName, "", "", player -> {
                if (player == null) {
                    errorLabel.setText(getString(R.string.error_adding_player));
                }
                else {
                    db.addPlayerToUser(player.getPid(), user.getUid(), firstName + " " + lastName, success -> {
                        if (success) {
                            user.addPlayer(player.getPid(), player.getFirstName() + " " + player.getLastName());
                            Intent intent = new Intent();
                            intent.putExtra("USER", user);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        else {
                            errorLabel.setText(getString(R.string.error_adding_player));
                        }
                    });
                }
            });
        }
        else {
            db.findTeam(tid, teamName -> {
                if (teamName == null) {
                    errorLabel.setText(getString(R.string.team_id_doesnt_exist));
                }
                else {
                    db.addPlayer(firstName, lastName, tid, teamName, player -> {
                        if (player == null) {
                            errorLabel.setText(getString(R.string.error_adding_player));
                        }
                        else {
                            db.addPlayerToUser(player.getPid(), user.getUid(), firstName + " " + lastName, playerToUserSuccess -> {
                                if (playerToUserSuccess) {
                                    db.addPlayerToTeam(tid, player.getPid(), firstName + " " + lastName, playerToTeamSuccess -> {
                                        if (playerToTeamSuccess) {
                                            db.addTeamToPlayer(tid, teamName, player.getPid(), teamToPlayerSuccess -> {
                                                if (teamToPlayerSuccess) {
                                                    player.addTeam(tid, teamName);
                                                    user.addPlayer(player.getPid(), player.getFirstName() + " " + player.getLastName());
                                                    Intent intent = new Intent();
                                                    intent.putExtra("USER", user);
                                                    setResult(Activity.RESULT_OK, intent);
                                                    finish();
                                                }
                                                else {
                                                    errorLabel.setText(R.string.error_joining_team);
                                                }
                                            });
                                        }
                                        else {
                                            errorLabel.setText(getString(R.string.error_adding_player));
                                        }
                                    });
                                }
                                else {
                                    errorLabel.setText(getString(R.string.error_adding_player));
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    public void addCoach(String firstName, String lastName, String tid) {
        if (tid.equals("")) {
            db.addCoach(firstName, lastName, "", "", coach -> {
                if (coach == null) {
                    errorLabel.setText(getString(R.string.error_adding_coach));
                }
                else {
                    db.addCoachToUser(coach.getCid(), user.getUid(), firstName + " " + lastName, success -> {
                        if (success) {
                            user.addCoach(coach.getCid(), coach.getFirstName() + " " + coach.getLastName());
                            Intent intent = new Intent();
                            intent.putExtra("USER", user);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        else {
                            errorLabel.setText(getString(R.string.error_adding_coach));
                        }
                    });
                }
            });
        }
        else {
            db.findTeam(tid, teamName -> {
                if (teamName == null) {
                    errorLabel.setText(getString(R.string.team_id_doesnt_exist));
                }
                else {
                    db.addCoach(firstName, lastName, tid, teamName, coach -> {
                        if (coach == null) {
                            errorLabel.setText(getString(R.string.error_adding_coach));
                        }
                        else {
                            db.addCoachToUser(coach.getCid(), user.getUid(), firstName + " " + lastName, coachToUserSuccess -> {
                                if (coachToUserSuccess) {
                                    db.addCoachToTeam(tid, coach.getCid(), firstName + " " + lastName, coachToTeamSuccess -> {
                                        if (coachToTeamSuccess) {
                                            db.addTeamToCoach(tid, teamName, coach.getCid(), success -> {
                                                if (success) {
                                                    coach.addTeam(tid, teamName);
                                                    user.addCoach(coach.getCid(), coach.getFirstName() + " " + coach.getLastName());
                                                    Intent intent = new Intent();
                                                    intent.putExtra("USER", user);
                                                    setResult(Activity.RESULT_OK, intent);
                                                    finish();
                                                }
                                                else {
                                                    errorLabel.setText(R.string.error_creating_team);
                                                }
                                            });
                                        }
                                        else {
                                            errorLabel.setText(getString(R.string.error_adding_coach));
                                        }
                                    });
                                }
                                else {
                                    errorLabel.setText(getString(R.string.error_adding_coach));
                                }
                            });
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

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
