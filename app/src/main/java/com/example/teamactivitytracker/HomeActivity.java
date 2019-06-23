package com.example.teamactivitytracker;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import com.example.teamactivitytracker.Model.DB;
import com.example.teamactivitytracker.Model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DB db;
    private User user = new User();

    private ListView listView;
    ArrayAdapter<String> adapter;

    HashMap<String, String> players;
    String[] playerIDs;
    HashMap<String, String> coaches;
    String[] coachIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView = findViewById(R.id.list);

        mAuth = FirebaseAuth.getInstance();

        db = new DB();

        // Get User
        String currentUserEmail = getIntent().getStringExtra("CURRENT_USER_EMAIL");
        if (currentUserEmail != null) {
            db.getUser(currentUserEmail.replace(".", ""), loadedUser -> setUser(loadedUser));
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
                profileSelected(position);
            }

        });

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        updateToolbarTitle();
        updateListView();
    }

    public void updateToolbarTitle() {
        getSupportActionBar().setTitle(user.getFirstName() + " " + user.getLastName());
    }

    public void updateListView() {

        players = user.getPlayers();
        playerIDs = players.keySet().toArray(new String[players.size()]);
        coaches = user.getCoaches();
        coachIDs = coaches.keySet().toArray(new String[coaches.size()]);

        adapter.clear();

        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < playerIDs.length; i++) {
            values.add(players.get(playerIDs[i]));
        }
        for (int j = 0; j < coachIDs.length; j++) {
            values.add(coaches.get(coachIDs[j]));
        }
        adapter.addAll(values);
        adapter.notifyDataSetChanged();
    }

    public void profileSelected(int index) {
        if (index < playerIDs.length) {
            String pid = playerIDs[index];
            // Go to profile page for pid
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("PROFILE_TYPE", "Player");
            intent.putExtra("PID", pid);
            startActivity(intent);
        }
        else if (index < (playerIDs.length + coachIDs.length)) {
            String cid = coachIDs[index - playerIDs.length];
            // Go to profile page for cid
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("PROFILE_TYPE", "Coach");
            intent.putExtra("CID", cid);
            startActivity(intent);
        }
    }

}
