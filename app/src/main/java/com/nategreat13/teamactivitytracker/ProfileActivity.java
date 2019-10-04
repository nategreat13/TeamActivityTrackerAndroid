package com.nategreat13.teamactivitytracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.nategreat13.teamactivitytracker.Model.Coach;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Player;
import com.nategreat13.teamactivitytracker.Model.ProfileType;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private SwipeMenuListView listView;
    ArrayAdapter<String> adapter;

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

        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, values) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 250;
                view.setLayoutParams(params);

                return view;
            }
        };

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                teamSelected(position);
            }

        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(300);
                // set item title
                deleteItem.setTitle("Delete");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listView.setMenuCreator(creator);


        // Left
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
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
                                   .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                   {
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
                                               }
                                               else {
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
                       break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
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

        adapter.clear();

        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < teamIDs.length; i++) {
            values.add(teams.get(teamIDs[i]));
        }
        adapter.addAll(values);
        adapter.notifyDataSetChanged();
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