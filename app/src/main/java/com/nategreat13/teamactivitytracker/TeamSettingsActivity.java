package com.nategreat13.teamactivitytracker;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.CheckBox;

import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Team;
import com.nategreat13.teamactivitytracker.R;

public class TeamSettingsActivity extends AppCompatActivity {

    private CheckBox showLeaderboardCheckBox;
    private Team team;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Team Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        showLeaderboardCheckBox = findViewById(R.id.showLeaderboardcheckBox);
        team = (Team) getIntent().getSerializableExtra("TEAM");
        showLeaderboardCheckBox.setChecked(team.getShowLeaderboard());

        db = new DB();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (team.getShowLeaderboard() != showLeaderboardCheckBox.isChecked()) {
            db.updateTeamShowLeaderboard(team.getTid(), showLeaderboardCheckBox.isChecked(), success -> {
                if (!success) {
                    System.out.println("Error updating team");
                }
            });
        }
        onBackPressed();
        return true;
    }

}
