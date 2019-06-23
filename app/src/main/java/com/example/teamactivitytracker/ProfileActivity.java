package com.example.teamactivitytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.teamactivitytracker.Model.Coach;
import com.example.teamactivitytracker.Model.DB;
import com.example.teamactivitytracker.Model.Player;
import com.example.teamactivitytracker.Model.ProfileType;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ProfileType profileType = ProfileType.None;
    private String cid = "";
    private String pid = "";
    private DB db;
    private Coach coach;
    private Player player;
    private String[] teamIDs;
    private HashMap<String, String> teams;

    private ListView listView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
            // Go to profile page for pid
            /*
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("PROFILE_TYPE", "Player");
            intent.putExtra("PID", pid);
            startActivity(intent);
            */
        }
    }

    public Coach getCaoch() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
        this.profileType = ProfileType.Coach;
        updateListView();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.profileType = ProfileType.Player;
        updateListView();
    }
}
