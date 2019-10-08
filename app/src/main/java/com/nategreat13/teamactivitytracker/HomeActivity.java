package com.nategreat13.teamactivitytracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private SwipeMenuListView listPlayers;
    private SwipeMenuListView listCoaches;
    ArrayAdapter<String> adapterPlayers;
    ArrayAdapter<String> adapterCoaches;

    private TextView playersTextView;
    private TextView coachesTextView;

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

        adapterPlayers = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, playerValues) {
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
        listPlayers.setAdapter(adapterPlayers);

        // ListView Item Click Listener
        listPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                playerSelected(position);
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
        listPlayers.setMenuCreator(creator);

        // Left
        listPlayers.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listPlayers.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
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
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        // set creator
        listCoaches.setMenuCreator(creator);

        // Left
        listCoaches.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listCoaches.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        ArrayList<String> coachValues = new ArrayList<>();

        adapterCoaches = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, coachValues) {
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
        listCoaches.setAdapter(adapterCoaches);

        // ListView Item Click Listener
        listCoaches.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                coachSelected(position);
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

        adapterPlayers.clear();
        adapterCoaches.clear();

        ArrayList<String> playerValues = new ArrayList<>();
        ArrayList<String> coachValues = new ArrayList<>();

        for (int i = 0; i < playerIDs.length; i++) {
            playerValues.add(players.get(playerIDs[i]) + " (Player)");
        }
        for (int j = 0; j < coachIDs.length; j++) {
            coachValues.add(coaches.get(coachIDs[j]) + " (Coach)");
        }
        adapterPlayers.addAll(playerValues);
        adapterPlayers.notifyDataSetChanged();

        adapterCoaches.addAll(coachValues);
        adapterCoaches.notifyDataSetChanged();

        setListViewHeightBasedOnChildren(listPlayers);
        setListViewHeightBasedOnChildren(listCoaches);
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

        /*
        int totalHeight = listAdapter.getCount() * 250;

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
        */
    }
}
