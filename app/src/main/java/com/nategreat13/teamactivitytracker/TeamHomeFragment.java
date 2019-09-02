package com.nategreat13.teamactivitytracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nategreat13.teamactivitytracker.Model.CompletedActivity;
import com.nategreat13.teamactivitytracker.Model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class TeamHomeFragment extends Fragment {

    private Team team;
    private ArrayList<CompletedActivity> completedActivities;
    private ListView periodLeadersList;
    private ListView totalLeadersList;
    private ListView recentActivitiesList;

    private ArrayAdapter periodAdapter;
    private ArrayAdapter totalAdapter;
    private ArrayAdapter recentActivitiesAdapter;

    public TeamHomeFragment() {
    }

    public static TeamHomeFragment newInstance(Team team) {
        TeamHomeFragment fragment = new TeamHomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("TEAM", team);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            team = (Team) getArguments().getSerializable("TEAM");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TeamHomeActivity teamHomeActivity = (TeamHomeActivity) getActivity();
        completedActivities = teamHomeActivity.getCompletedActivities();

        periodLeadersList = getActivity().findViewById(R.id.periodLeadersList);
        totalLeadersList = getActivity().findViewById(R.id.totalLeadersList);
        recentActivitiesList = getActivity().findViewById(R.id.recentActivityList);
        updateListViews();
    }

    public void updateListViews() {
        updatePeriodLeadersListView();
        updateTotalLeadersListView();
        updateRecentActivitiesListView();
    }


    public void updatePeriodLeadersListView() {
        periodAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, getValuesForListView("Period")) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 100;
                view.setLayoutParams(params);

                return view;
            }
        };

        // Assign adapter to ListView
        periodLeadersList.setAdapter(periodAdapter);
    }

    public void updateTotalLeadersListView() {
        totalAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, getValuesForListView("Total")) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 100;
                view.setLayoutParams(params);

                return view;
            }
        };

        // Assign adapter to ListView
        totalLeadersList.setAdapter(totalAdapter);
    }

    public void updateRecentActivitiesListView() {

        if (completedActivities == null) {
            return;
        }

        ArrayList<String> values = new ArrayList<>();

        for (CompletedActivity completedActivity: completedActivities) {
            values.add(completedActivity.getPlayerName() + " - " + completedActivity.getActivity().getName());
        }

        recentActivitiesAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 100;
                view.setLayoutParams(params);

                return view;
            }
        };

        // Assign adapter to ListView
        recentActivitiesList.setAdapter(recentActivitiesAdapter);
    }

    public ArrayList<String> getValuesForListView(String period) {
        HashMap<String, Long> pointsMap = new HashMap<>();
        if (period.equals("Total")) {
            pointsMap = team.getPlayerPoints();
        }
        else {
            pointsMap = team.getPlayerPeriodPoints();
        }
        TeamHomeFragment.PointsPair[] pointPairs = new TeamHomeFragment.PointsPair[pointsMap.size()];
        int i = 0;
        for (String pid : pointsMap.keySet().toArray(new String[pointsMap.size()])) {
            int points = pointsMap.get(pid).intValue();
            pointPairs[i] = new TeamHomeFragment.PointsPair(pid, points);
            i++;
        }
        Arrays.sort(pointPairs, new Comparator<TeamHomeFragment.PointsPair>() {
            public int compare(TeamHomeFragment.PointsPair pp1, TeamHomeFragment.PointsPair pp2) {
                return Integer.compare(pp2.points, pp1.points);
            }
        });

        ArrayList<String> values = new ArrayList<>();

        for (i = 0; i < pointPairs.length; i++) {
            values.add((i+1) + ". " + team.getPlayers().get(pointPairs[i].pid) + " -- " + pointPairs[i].points);
        }

        return values;
    }

    public void settCompletedActivities(ArrayList<CompletedActivity> completedActivities) {
        this.completedActivities = completedActivities;
        updateRecentActivitiesListView();
    }

    public void setTeam(Team team) {
        this.team = team;
        updatePeriodLeadersListView();
        updateTotalLeadersListView();
    }

    private class PointsPair {
        private final String pid;
        private final int points;

        public PointsPair(String pid, int points)
        {
            this.pid   = pid;
            this.points = points;
        }

        public String pid()   { return pid; }
        public int points() { return points; }
    }
}
