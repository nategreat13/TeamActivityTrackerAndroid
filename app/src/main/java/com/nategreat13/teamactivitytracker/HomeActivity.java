package com.nategreat13.teamactivitytracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.ProfileType;
import com.nategreat13.teamactivitytracker.Model.User;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DB db;
    private User user = new User();

    private RecyclerView listPlayers;
    private RecyclerView listCoaches;
    com.nategreat13.teamactivitytracker.BasicRecyclerViewAdapter adapterPlayers;
    com.nategreat13.teamactivitytracker.BasicRecyclerViewAdapter adapterCoaches;

    private TextView playersTextView;
    private TextView coachesTextView;

    private SwipeController playerSwipeController;
    private SwipeController coachSwipeController;

    HashMap<String, String> players;
    String[] playerIDs;
    HashMap<String, String> coaches;
    String[] coachIDs;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        listPlayers = findViewById(R.id.listPlayers);
        listCoaches = findViewById(R.id.listCoaches);
        playersTextView = findViewById(R.id.textViewPlayers);
        coachesTextView = findViewById(R.id.textViewCoaches);

        activity = this;

        mAuth = FirebaseAuth.getInstance();

        db = new DB();

        // Get User
        String currentUserEmail = getIntent().getStringExtra("CURRENT_USER_EMAIL");
        if (currentUserEmail != null) {
            db.getUser(currentUserEmail.replace(".", ""), loadedUser -> setUser(loadedUser));
        }

        ArrayList<String> playerValues = new ArrayList<>();

        listPlayers = findViewById(R.id.listPlayers);
        listPlayers.setLayoutManager(new LinearLayoutManager(this));
        adapterPlayers = new com.nategreat13.teamactivitytracker.BasicRecyclerViewAdapter(this, playerValues);
        adapterPlayers.setClickListener(this::onPlayerClick);
        listPlayers.setAdapter(adapterPlayers);
        listPlayers.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        playerSwipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                deletePlayer(position);
            }
        });

        ItemTouchHelper playerItemTouchhelper = new ItemTouchHelper(playerSwipeController);
        playerItemTouchhelper.attachToRecyclerView(listPlayers);

        listPlayers.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                playerSwipeController.onDraw(c);
            }
        });

        ArrayList<String> coachValues = new ArrayList<>();

        listCoaches = findViewById(R.id.listCoaches);
        listCoaches.setLayoutManager(new LinearLayoutManager(this));
        adapterCoaches = new com.nategreat13.teamactivitytracker.BasicRecyclerViewAdapter(this, coachValues);
        adapterCoaches.setClickListener(this::onCoachClick);
        listCoaches.setAdapter(adapterCoaches);
        listCoaches.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        coachSwipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                deleteCoach(position);
            }
        });

        ItemTouchHelper coachItemTouchhelper = new ItemTouchHelper(coachSwipeController);
        coachItemTouchhelper.attachToRecyclerView(listCoaches);

        listCoaches.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                coachSwipeController.onDraw(c);
            }
        });
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            updateToolbarTitle();
            updateListViews();
        }
    }

    public void goToAdd(View view) {
        Intent intent = new Intent(this, AddProfileActivity.class);
        intent.putExtra("USER", getUser());
        startActivityForResult(intent, 1);
    }

    public void updateToolbarTitle() {
        getSupportActionBar().setTitle(user.getFirstName() + " " + user.getLastName());
    }

    public void updateListViews() {

        players = user.getPlayers();
        playerIDs = players.keySet().toArray(new String[players.size()]);
        Arrays.sort(playerIDs);
        coaches = user.getCoaches();
        coachIDs = coaches.keySet().toArray(new String[coaches.size()]);
        Arrays.sort(coachIDs);

        ArrayList<String> playerValues = new ArrayList<>();
        ArrayList<String> coachValues = new ArrayList<>();

        for (int i = 0; i < playerIDs.length; i++) {
            playerValues.add(players.get(playerIDs[i]) + " (Player)");
        }
        for (int j = 0; j < coachIDs.length; j++) {
            coachValues.add(coaches.get(coachIDs[j]) + " (Coach)");
        }
        adapterPlayers.setList(playerValues);
        adapterPlayers.notifyDataSetChanged();

        adapterCoaches.setList(coachValues);
        adapterCoaches.notifyDataSetChanged();
    }

    public void onPlayerClick(View view, int position) {
        playerSelected(position);
    }

    public void onCoachClick(View view, int position) {
        coachSelected(position);
    }

    public void playerSelected(int index) {
        String pid = playerIDs[index];
        // Go to profile page for pid
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("PROFILE_TYPE", "Player");
        intent.putExtra("PID", pid);
        startActivity(intent);
    }

    public void coachSelected(int index) {
        String cid = coachIDs[index];
        // Go to profile page for cid
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("PROFILE_TYPE", "Coach");
        intent.putExtra("CID", cid);
        startActivity(intent);
    }

    public void deletePlayer(int position) {
        String pid = playerIDs[position];
        String playerName = players.get(pid);
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Player?")
                .setMessage("Are you sure you want to delete " + playerName + "? THIS CANNOT BE UNDONE")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.getNumberOfTeamsForPlayer(pid, numberOfTeams -> {
                            if (numberOfTeams <= 0) {
                                db.deletePlayer(pid, user.getUid(), success -> {
                                    if (success) {
                                        db.getUser(user.getUid(), updatedUser -> {
                                            setUser(updatedUser);
                                        });
                                    }
                                    else {
                                        new AlertDialog.Builder(activity)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("Error")
                                                .setMessage("Error deleting player")
                                                .setNegativeButton("Ok", null)
                                                .show();
                                    }
                                });
                            }
                            else {
                                new AlertDialog.Builder(activity)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Cannot delete player")
                                        .setMessage("Cannot delete player because they are a part of at least one team. Please remove them from all teams and try again.")
                                        .setNegativeButton("Ok", null)
                                        .show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void deleteCoach(int position) {
        String cid = coachIDs[position];
        String coachName = coaches.get(cid);
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Coach?")
                .setMessage("Are you sure you want to delete " + coachName + "? THIS CANNOT BE UNDONE")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.getNumberOfTeamsForCoach(cid, numberOfTeams -> {
                            if (numberOfTeams <= 0) {
                                db.deleteCoach(cid, user.getUid(), success -> {
                                    if (success) {
                                        db.getUser(user.getUid(), updatedUser -> {
                                            setUser(updatedUser);
                                        });
                                    }
                                    else {
                                        new AlertDialog.Builder(activity)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("Error")
                                                .setMessage("Error deleting coach")
                                                .setNegativeButton("Ok", null)
                                                .show();
                                    }
                                });
                            }
                            else {
                                new AlertDialog.Builder(activity)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Cannot delete coach")
                                        .setMessage("Cannot delete coach because they are a part of at least one team. Please remove them from all teams and try again.")
                                        .setNegativeButton("Ok", null)
                                        .show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                User updatedUser = (User) data.getSerializableExtra("USER");
                if (updatedUser != null) {
                    setUser(updatedUser);
                    updateListViews();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_logout:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Logout?")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
