package com.nategreat13.teamactivitytracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.CheckBox;

import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Team;
import com.nategreat13.teamactivitytracker.R;

import java.util.HashMap;

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

    public void resetPoints(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Reset Points?")
                .setMessage("Are you sure you want to reset all of the points for this team? THIS CANNOT BE UNDONE")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.resetAllTeamPoints(team.getTid(), success -> {
                            boolean didWork = success;
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void deleteTeam(View view) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Remove player from team?")
                    .setMessage("Are you sure you want to delete " + team.getName() + "? THIS CANNOT BE UNDONE")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteTeam(team, success -> {
                                if (success) {
                                    Intent intent = new Intent();
                                    intent.putExtra("DELETED_TEAM", true);
                                    intent.putExtra("DELETED_TID", team.getTid());
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
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
        Intent intent = new Intent();
        intent.putExtra("DELETED_TEAM", false);
        setResult(Activity.RESULT_OK, intent);
        finish();
        return true;
    }

}
