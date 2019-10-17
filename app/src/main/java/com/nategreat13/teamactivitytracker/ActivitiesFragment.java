package com.nategreat13.teamactivitytracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nategreat13.teamactivitytracker.Model.Activity;
import com.nategreat13.teamactivitytracker.Model.Coach;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Player;
import com.nategreat13.teamactivitytracker.Model.ProfileType;
import com.nategreat13.teamactivitytracker.Model.Team;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ActivitiesFragment extends Fragment {

    private Team team;
    private ProfileType profileType;
    private Player player;
    private Coach coach;
    private DB db;
    private FrameLayout layout;
    private FloatingActionButton fab;
    private int indexActivity;
    private RecyclerView listView;
    com.nategreat13.teamactivitytracker.BasicRecyclerViewAdapter adapter;

    public ActivitiesFragment() {
    }

    public static ActivitiesFragment newInstance(Team team, ProfileType profileType, Player player, Coach coach) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putSerializable("TEAM", team);
        if (profileType == ProfileType.Player) {
            args.putString("PROFILE_TYPE", "PLAYER");
            args.putSerializable("PLAYER", player);
        }
        else {
            args.putString("PROFILE_TYPE", "COACH");
            args.putSerializable("COACH", coach);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = getActivity().findViewById(R.id.activitiesLayout);

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(this::goToAddActivity);

        if (profileType == ProfileType.Player) {
            FloatingActionButton fab = getActivity().findViewById(R.id.fab);
            fab.hide();
        }

        listView = getActivity().findViewById(R.id.list);

        Activity[] activities = team.getActivities();

        if (activities.length == 0) {
            db.getTeamActivities(team.getTid(), teamActivities -> {
                team.setActivities(teamActivities);
                updateListView();
            });
        }

        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < activities.length; i++) {
            values.add(activities[i].getPoints() + " - " + activities[i].getName());
        }

        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new com.nategreat13.teamactivitytracker.BasicRecyclerViewAdapter(getActivity(), values);
        adapter.setClickListener(this::onActivityClick);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
    }

    public void onActivityClick(View view, int position) {
        activitySelected(position);
    }

    public void activitySelected(int position) {
        if (profileType == ProfileType.Player) {
            indexActivity = position;
            Intent intent = new Intent(getActivity(), AddCompletedActivityActivity.class);
            intent.putExtra("TID", team.getTid());
            intent.putExtra("PID", player.getPid());
            intent.putExtra("PLAYER_NAME", player.getFirstName() + " " + player.getLastName());
            intent.putExtra("ACTIVITY", team.getActivities()[position]);
            startActivity(intent);
        }
        else {
            indexActivity = position;
            Intent intent = new Intent(getActivity(), AddActivityActivity.class);
            intent.putExtra("TID", team.getTid());
            intent.putExtra("ACTIVITY", team.getActivities()[position]);
            intent.putExtra("ISEDIT", true);
            startActivity(intent);
        }
    }

    public void setTeam(Team team) {
        this.team = team;
        updateListView();
    }

    public void updateListView() {
        Activity[] activities = team.getActivities();

        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < activities.length; i++) {
            values.add(activities[i].getPoints() + " - " + activities[i].getName());
        }

        adapter.setList(values);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            team = (Team) getArguments().getSerializable("TEAM");
            String type = getArguments().getString("PROFILE_TYPE");
            if (type.equals("PLAYER")) {
                player = (Player) getArguments().getSerializable("PLAYER");
                profileType = ProfileType.Player;
            }
            else {
                coach = (Coach) getArguments().getSerializable("COACH");
                profileType = ProfileType.Coach;
            }
        }

        db = new DB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    public void goToAddActivity(View view) {
        Intent intent = new Intent(getActivity(), AddActivityActivity.class);
        intent.putExtra("TID", team.getTid());
        startActivity(intent);
    }
}
