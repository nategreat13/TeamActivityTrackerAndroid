package com.nategreat13.teamactivitytracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.nategreat13.teamactivitytracker.Model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class LeaderboardFragment extends Fragment {

    private Team team;
    private boolean totalSelected = false;
    private ArrayAdapter<String> adapter;
    private Button periodButton;
    private Button totalButton;

    public LeaderboardFragment() {
    }

    public static LeaderboardFragment newInstance(Team team) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putSerializable("TEAM", team);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = getActivity().findViewById(R.id.list);

        adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, getValuesForListView()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 125;
                view.setLayoutParams(params);

                return view;
            }
        };

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        periodButton = getActivity().findViewById(R.id.periodButton);
        totalButton = getActivity().findViewById(R.id.totalButton);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            team = (Team) getArguments().getSerializable("TEAM");
        }
    }

    public ArrayList<String> getValuesForListView() {
        HashMap<String, Long> pointsMap = new HashMap<>();
        if (totalSelected) {
            pointsMap = team.getPlayerPoints();
        }
        else {
            pointsMap = team.getPlayerPeriodPoints();
        }
        PointsPair[] pointPairs = new PointsPair[pointsMap.size()];
        int i = 0;
        for (String pid : pointsMap.keySet().toArray(new String[pointsMap.size()])) {
            int points = pointsMap.get(pid).intValue();
            pointPairs[i] = new PointsPair(pid, points);
            i++;
        }
        Arrays.sort(pointPairs, new Comparator<PointsPair>() {
            public int compare(PointsPair pp1, PointsPair pp2) {
                return Integer.compare(pp2.points, pp1.points);
            }
        });

        ArrayList<String> values = new ArrayList<>();

        for (i = 0; i < pointPairs.length; i++) {
            values.add((i+1) + ". " + team.getPlayers().get(pointPairs[i].pid) + " -- " + pointPairs[i].points);
        }

        return values;
    }

    public void periodButtonPressed() {
        if (totalSelected) {
            totalSelected = false;
            updateListView();
            periodButton.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
            periodButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            totalButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            totalButton.setTextColor(getResources().getColor(R.color.colorSecondary));
        }
    }

    public void totalButtonPressed() {
        if (!totalSelected) {
            totalSelected = true;
            updateListView();
            totalButton.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
            totalButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            periodButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            periodButton.setTextColor(getResources().getColor(R.color.colorSecondary));
        }
    }

    public void updateListView() {
        adapter.clear();
        adapter.addAll(getValuesForListView());
        adapter.notifyDataSetChanged();
    }

    public void setTeam(Team team) {
        this.team = team;
        updateListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
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
