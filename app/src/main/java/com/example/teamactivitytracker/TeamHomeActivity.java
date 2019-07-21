package com.example.teamactivitytracker;

import android.os.Bundle;

import com.example.teamactivitytracker.Model.Activity;
import com.example.teamactivitytracker.Model.Coach;
import com.example.teamactivitytracker.Model.DB;
import com.example.teamactivitytracker.Model.Globals;
import com.example.teamactivitytracker.Model.Player;
import com.example.teamactivitytracker.Model.ProfileType;
import com.example.teamactivitytracker.Model.Team;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TeamHomeActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private DB db;
    private ProfileType profileType;
    private Team team;
    private Player player;
    private Coach coach;
    private Fragment fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_leaderboard:
                    fragment = LeaderboardFragment.newInstance(team);
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_activities:
                    fragment = ActivitiesFragment.newInstance(team, profileType, player, coach);
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_roster:
                    fragment = RosterFragment.newInstance(team);
                    loadFragment(fragment);
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
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
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
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        Globals.currentTeam = team;
        getSupportActionBar().setTitle(team.getName());
    }
}
