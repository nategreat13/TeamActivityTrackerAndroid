package com.nategreat13.teamactivitytracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.nategreat13.teamactivitytracker.Model.Coach;
import com.nategreat13.teamactivitytracker.Model.CompletedActivity;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Globals;
import com.nategreat13.teamactivitytracker.Model.Player;
import com.nategreat13.teamactivitytracker.Model.ProfileType;
import com.nategreat13.teamactivitytracker.Model.Team;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class TeamHomeActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private DB db;
    private ProfileType profileType;
    private Team team;
    private ArrayList<CompletedActivity> completedActivities;

    private Player player;
    private Coach coach;
    private Fragment fragment;

    private boolean firstLoad = true;
    private boolean isOnHomeFragment = true;
    private boolean isOnLeaderboardFragment = false;
    private BottomNavigationView navView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = TeamHomeFragment.newInstance(team);
                    loadFragment(fragment);
                    isOnHomeFragment = true;
                    isOnLeaderboardFragment = false;
                    return true;
                case R.id.navigation_leaderboard:
                    fragment = LeaderboardFragment.newInstance(team);
                    loadFragment(fragment);
                    isOnHomeFragment = false;
                    isOnLeaderboardFragment = true;
                    return true;
                case R.id.navigation_activities:
                    fragment = ActivitiesFragment.newInstance(team, profileType, player, coach);
                    loadFragment(fragment);
                    isOnHomeFragment = false;
                    isOnLeaderboardFragment = false;
                    return true;
                case R.id.navigation_roster:
                    fragment = RosterFragment.newInstance(team);
                    loadFragment(fragment);
                    isOnHomeFragment = false;
                    isOnLeaderboardFragment = false;
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        this.fragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_home);
        navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DB();

        String type = getIntent().getStringExtra("PROFILE_TYPE");
        profileType = (type.equals("PLAYER")) ? ProfileType.Player : ProfileType.Coach;
        if (profileType == ProfileType.Player) {
            player = (Player) getIntent().getSerializableExtra("PLAYER");
        }
        else {
            coach = (Coach) getIntent().getSerializableExtra("COACH");
        }
        String tid = getIntent().getStringExtra("TEAM_ID");
        listenForTeamUpdates(tid);
        listenForCompletedActivities(tid);
    }

    public void periodButtonPressed(View view) {
        try {
            LeaderboardFragment leaderboardFragment = (LeaderboardFragment) fragment;
            leaderboardFragment.periodButtonPressed();
        }
        catch (Exception e) {
            return;
        }
    }

    public void totalButtonPressed(View view) {
        try {
            LeaderboardFragment leaderboardFragment = (LeaderboardFragment) fragment;
            leaderboardFragment.totalButtonPressed();
        }
        catch (Exception e) {
            return;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void listenForTeamUpdates(String tid) {
        db.listenForTeam(tid, updatedTeam -> setTeam(updatedTeam));
        listenForTeamActivityUpdates(tid);
    }

    public void listenForTeamActivityUpdates(String tid) {
        db.listenForTeamActivities(tid, activities -> {
            team.setActivities(activities);
            setTeam(team);
        });
    }

    public void listenForCompletedActivities(String tid) {
        db.listenForCompletedActivites(tid, completedActivities -> setCompletedActivities(completedActivities));
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        Globals.currentTeam = team;
        if (!team.getShowLeaderboard()) {
            navView.getMenu().removeItem(R.id.navigation_leaderboard);
        }
        getSupportActionBar().setTitle(team.getName());
        if (firstLoad) {
            fragment = TeamHomeFragment.newInstance(team);
            loadFragment(fragment);
            firstLoad = false;
        }
        if (isOnHomeFragment) {
            try {
                TeamHomeFragment teamHomeFragment = (TeamHomeFragment) fragment;
                teamHomeFragment.setTeam(team);
            }
            catch (Exception e) {
                return;
            }
        }
        if (isOnLeaderboardFragment) {
            try {
                LeaderboardFragment leaderboardFragment = (LeaderboardFragment) fragment;
                leaderboardFragment.setTeam(team);
            }
            catch (Exception e) {
                return;
            }
        }
    }

    public ArrayList<CompletedActivity> getCompletedActivities() {
        return completedActivities;
    }

    public void setCompletedActivities(ArrayList<CompletedActivity> completedActivities) {
        this.completedActivities = completedActivities;
        Globals.recentCompletedActivities = completedActivities;
        if (isOnHomeFragment) {
            try {
                TeamHomeFragment teamHomeFragment = (TeamHomeFragment) fragment;
                teamHomeFragment.settCompletedActivities(completedActivities);
            }
            catch (Exception e) {
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.removeItem(R.id.action_logout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, TeamSettingsActivity.class);
                intent.putExtra("TEAM", team);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                boolean deletedTeam = data.getBooleanExtra("DELETED_TEAM", false);
                if (deletedTeam) {
                    String deletedTID = data.getStringExtra("DELETED_TID");
                    Intent intent = new Intent();
                    intent.putExtra("DELETED_TEAM", true);
                    intent.putExtra("DELETED_TID", deletedTID);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }
    }

}
