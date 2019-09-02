package com.nategreat13.teamactivitytracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nategreat13.teamactivitytracker.Model.Coach;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Player;
import com.nategreat13.teamactivitytracker.Model.ProfileType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class AddTeamActivity extends AppCompatActivity {

    private DB db;
    private EditText teamIDTextField;
    private Button addButton;
    private EditText teamNameTextField;
    private RadioButton newTeamRadioButton;
    private RadioButton existingTeamRadioButton;
    private TextView errorLabel;
    private Player player;
    private Coach coach;
    private ProfileType profileType;
    private CheckBox showFullLeaderboardCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Team");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        teamIDTextField = findViewById(R.id.teamIDTextField);
        teamNameTextField = findViewById(R.id.teamNameTextField);
        addButton = findViewById(R.id.addButton);
        newTeamRadioButton = findViewById(R.id.newTeamRadioButton);
        existingTeamRadioButton = findViewById(R.id.coachRadioButton);
        errorLabel = findViewById(R.id.errorLabel);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        showFullLeaderboardCheckBox = findViewById((R.id.showFullLeaderboardCheckBox));

        String type = getIntent().getStringExtra("PROFILE_TYPE");
        profileType = (type.equals("Player")) ? ProfileType.Player : ProfileType.Coach;
        if (profileType == ProfileType.Player) {
            player = (Player) getIntent().getSerializableExtra("PLAYER");
            radioGroup.setVisibility(View.INVISIBLE);
            teamNameTextField.setVisibility(View.INVISIBLE);
            showFullLeaderboardCheckBox.setVisibility(View.INVISIBLE);
            addButton.setText(R.string.join_existing_team);
        }
        else {
            coach = (Coach) getIntent().getSerializableExtra("COACH");
        }

        db = new DB();
    }

    public void newTeamRadioButtonSelected(View view) {
        addButton.setText(getString(R.string.create_new_team));
        teamNameTextField.setVisibility(View.VISIBLE);
        showFullLeaderboardCheckBox.setVisibility(View.VISIBLE);
    }

    public void existingTeamRadioButtonSelected(View view) {
        addButton.setText(getString(R.string.join_existing_team));
        teamNameTextField.setVisibility(View.INVISIBLE);
        showFullLeaderboardCheckBox.setVisibility(View.INVISIBLE);
    }

    public void addButtonPressed(View view) {
        String teamName = teamNameTextField.getText().toString();
        String tid = teamIDTextField.getText().toString();

        if (teamName.length() == 0 && profileType == ProfileType.Coach && newTeamRadioButton.isChecked()) {
            errorLabel.setText(getString(R.string.team_name_empty));
            return;
        }
        if (tid.length() == 0) {
            errorLabel.setText(getString(R.string.team_id_empty));
            return;
        }
        if (profileType == ProfileType.Player) {
            joinTeamWithPlayer(tid);
        }
        else {
            if (newTeamRadioButton.isChecked()) {
                createTeam(tid, teamName, showFullLeaderboardCheckBox.isChecked());
            }
            else {
                joinTeamWithCoach(tid);
            }
        }
    }

    public void joinTeamWithPlayer(String tid) {
        db.getTeam(tid, team -> {
            if (team == null) {
                errorLabel.setText(R.string.team_id_doesnt_exist);
                return;
            }
            else {
                if (team.getPlayers().containsKey(player.getPid())) {
                    errorLabel.setText(R.string.player_already_on_team);
                    return;
                }
                db.addPlayerToTeam(tid, player.getPid(),player.getFirstName() + " " + player.getLastName(), playerToTeamSuccess -> {
                    if (playerToTeamSuccess) {
                        db.addTeamToPlayer(team.getTid(), team.getName(), player.getPid(), teamToPlayerSuccess -> {
                            if (teamToPlayerSuccess) {
                                player.addTeam(team.getTid(), team.getName());
                                Intent intent = new Intent();
                                intent.putExtra("PLAYER", player);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                            else {
                                errorLabel.setText(R.string.error_joining_team);
                            }
                        });
                    }
                    else {
                        errorLabel.setText(R.string.error_joining_team);
                    }
                });
            }
        });
    }

    public void joinTeamWithCoach(String tid) {
        db.getTeam(tid, team -> {
            if (team == null) {
                errorLabel.setText(R.string.team_id_doesnt_exist);
            }
            else {
                if (team.getCoaches().containsKey(coach.getCid())) {
                    errorLabel.setText(R.string.coach_already_on_team);
                }
                db.addCoachToTeam(tid, coach.getCid(),coach.getFirstName() + " " + coach.getLastName(), coachToTeamSuccess -> {
                    if (coachToTeamSuccess) {
                        db.addTeamToCoach(team.getTid(), team.getName(), coach.getCid(), teamToCoachSuccess -> {
                            if (teamToCoachSuccess) {
                                coach.addTeam(team.getTid(), team.getName());
                                Intent intent = new Intent();
                                intent.putExtra("COACH", coach);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                            else {
                                errorLabel.setText(R.string.error_joining_team);
                            }
                        });
                    }
                    else {
                        errorLabel.setText(R.string.error_joining_team);
                    }
                });
            }
        });
    }

    public void createTeam(String tid, String teamName, Boolean showFullLeaderboard) {
        db.addTeam(tid, teamName, coach.getCid(), coach.getFirstName() + " " + coach.getLastName(), showFullLeaderboard, team -> {
            if (team == null) {
                errorLabel.setText(R.string.error_creating_team);
            }
            else {
                db.addTeamToCoach(tid, teamName, coach.getCid(), success -> {
                    if (success) {
                        coach.addTeam(team.getTid(), team.getName());
                        Intent intent = new Intent();
                        intent.putExtra("COACH", coach);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    else {
                        errorLabel.setText(R.string.error_creating_team);
                    }
                });
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

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
