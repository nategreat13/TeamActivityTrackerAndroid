package com.example.teamactivitytracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.teamactivitytracker.Model.DB;
import com.example.teamactivitytracker.Model.ProfileType;
import com.example.teamactivitytracker.Model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (user != null) {
            updateToolbarTitle();
            updateListView();
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

    public void updateListView() {

        players = user.getPlayers();
        playerIDs = players.keySet().toArray(new String[players.size()]);
        Arrays.sort(playerIDs);
        coaches = user.getCoaches();
        coachIDs = coaches.keySet().toArray(new String[coaches.size()]);
        Arrays.sort(coachIDs);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                User updatedUser = (User) data.getSerializableExtra("USER");
                if (updatedUser != null) {
                    setUser(updatedUser);
                    updateListView();
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
