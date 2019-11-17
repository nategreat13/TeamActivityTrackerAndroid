package com.nategreat13.teamactivitytracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.nategreat13.teamactivitytracker.Model.Coach;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Globals;
import com.nategreat13.teamactivitytracker.Model.Player;
import com.nategreat13.teamactivitytracker.Model.ProfileType;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

public class ProfileActivity extends AppCompatActivity {

    private ProfileType profileType = ProfileType.None;
    private String cid = "";
    private String pid = "";
    private DB db;
    private Coach coach;
    private Player player;
    private String[] teamIDs;
    private HashMap<String, String> teams;
    private Activity activity;

    private RecyclerView listView;
    com.nategreat13.teamactivitytracker.BasicRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Teams");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        activity = this;

        listView = findViewById(R.id.list);
        db = new DB();

        String type = getIntent().getStringExtra("PROFILE_TYPE");
        if (type.equals("Player")) {
            pid = getIntent().getStringExtra("PID");
            db.getPlayer(pid, loadedPlayer -> setPlayer(loadedPlayer));
        }
        else if (type.equals("Coach")) {
            cid = getIntent().getStringExtra("CID");
            db.getCoach(cid, loadedCoach -> setCoach(loadedCoach));
        }

        ArrayList<String> values = new ArrayList<>();

        listView = findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new com.nategreat13.teamactivitytracker.BasicRecyclerViewAdapter(this, values);
        adapter.setClickListener(this::onTeamClick);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                removeFromTeam(position);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(listView);

        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void updateToolbarTitle() {
        if (profileType == ProfileType.Player) {
            getSupportActionBar().setTitle(player.getFirstName() + " " + player.getLastName());
        }
        else if (profileType == ProfileType.Coach) {
            getSupportActionBar().setTitle(coach.getFirstName() + " " + coach.getLastName());
        }
    }

    public void updateListView() {

        if (profileType == ProfileType.Player) {
            teams = player.getTeams();
            teamIDs = teams.keySet().toArray(new String[teams.size()]);
        }
        else if (profileType == ProfileType.Coach) {
            teams = coach.getTeams();
            teamIDs = teams.keySet().toArray(new String[teams.size()]);
        }
        else {
            return;
        }

        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < teamIDs.length; i++) {
            values.add(teams.get(teamIDs[i]));
        }
        adapter.setList(values);
        adapter.notifyDataSetChanged();
    }

    public void onTeamClick(View view, int position) {
        teamSelected(position);
    }

    public void teamSelected(int index) {
        if (index < teamIDs.length) {
            String tid = teamIDs[index];
            Intent intent = new Intent(this, TeamHomeActivity.class);
            if (profileType == ProfileType.Player) {
                intent.putExtra("PROFILE_TYPE", "PLAYER");
                intent.putExtra("PLAYER", getPlayer());
            }
            else {
                intent.putExtra("PROFILE_TYPE", "COACH");
                intent.putExtra("COACH", getCoach());
            }
            intent.putExtra("TEAM_ID", tid);
            startActivityForResult(intent, 1);
        }
    }

    public void removeFromTeam(int position) {
        if (profileType == ProfileType.Player) {
            String tid = teamIDs[position];
            new AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Remove player from team?")
                    .setMessage("Are you sure you want to remove " + player.getFirstName() + " " + player.getLastName() + " from " + teams.get(tid) + "? THIS CANNOT BE UNDONE")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.removePlayerFromTeam(pid, tid, success -> {
                                if (success) {
                                    HashMap<String, String> newTeams = player.getTeams();
                                    newTeams.remove(tid);
                                    updateListView();
                                }
                                else {
                                    System.out.println("Fail");
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else if (profileType == ProfileType.Coach) {
            String tid = teamIDs[position];
            new AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Remove coach from team?")
                    .setMessage("Are you sure you want to remove " + coach.getFirstName() + " " + coach.getLastName() + " from " + teams.get(tid) + "? THIS CANNOT BE UNDONE")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.getNumberOfCoachesOnTeam(tid, numberOfCoaches -> {
                                if (numberOfCoaches > 1) {
                                    db.removeCoachFromTeam(cid, tid, success -> {
                                        if (success) {
                                            HashMap<String, String> newTeams = coach.getTeams();
                                            newTeams.remove(tid);
                                            updateListView();
                                        }
                                    });
                                } else {
                                    new AlertDialog.Builder(activity)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Cannot remove coach")
                                            .setMessage("Cannot remove " + coach.getFirstName() + " " + coach.getLastName() + " from " + teams.get(tid) + " because they are the only coach on the team")
                                            .setNegativeButton("Ok", null)
                                            .show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public void goToAdd(View view) {
        Intent intent = new Intent(this, AddTeamActivity.class);
        if (profileType == ProfileType.Player) {
            intent.putExtra("PROFILE_TYPE", "Player");
            intent.putExtra("PLAYER", getPlayer());
        }
        else {
            intent.putExtra("PROFILE_TYPE", "Coach");
            intent.putExtra("COACH", getCoach());
        }
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                boolean deletedTeam = data.getBooleanExtra("DELETED_TEAM", false);
                if (deletedTeam) {
                    String deletedTID = data.getStringExtra("DELETED_TID");
                    teams.remove(deletedTID);
                    updateListView();
                }
                else if (profileType == ProfileType.Player) {
                    Player updatedPlayer = (Player) data.getSerializableExtra("PLAYER");
                    if (updatedPlayer != null) {
                        setPlayer(updatedPlayer);
                        updateListView();
                    }
                }
                else {
                    Coach updatedCoach = (Coach) data.getSerializableExtra("COACH");
                    if (updatedCoach != null) {
                        setCoach(updatedCoach);
                        updateListView();
                    }
                }
            }
        }
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
        this.profileType = ProfileType.Coach;
        if (coach != null) {
            updateToolbarTitle();
            updateListView();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.profileType = ProfileType.Player;
        if (player != null) {
            updateToolbarTitle();
            updateListView();
        }
    }
}