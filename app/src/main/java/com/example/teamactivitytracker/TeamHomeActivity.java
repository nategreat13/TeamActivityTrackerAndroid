package com.example.teamactivitytracker;

import android.os.Bundle;

import com.example.teamactivitytracker.Model.Activity;
import com.example.teamactivitytracker.Model.Coach;
import com.example.teamactivitytracker.Model.DB;
import com.example.teamactivitytracker.Model.Player;
import com.example.teamactivitytracker.Model.ProfileType;
import com.example.teamactivitytracker.Model.Team;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private Team team;
    private ProfileType profileType;
    private Player player;
    private Coach coach;

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
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
        db.getTeam(tid, team -> setTeam(team));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        getSupportActionBar().setTitle(team.getName());
    }
}
