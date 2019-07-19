package com.example.teamactivitytracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.teamactivitytracker.Model.Activity;
import com.example.teamactivitytracker.Model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class LeaderboardFragment extends Fragment {

    private Team team;

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

        HashMap<String, Integer> playerPoints = team.getPlayerPoints();
        PointsPair[] pointPairs = new PointsPair[playerPoints.size()];
        int i = 0;
        for (String pid : playerPoints.keySet().toArray(new String[playerPoints.size()])) {
            int points = playerPoints.get(pid);
            pointPairs[i] = new PointsPair(pid, points);
            i++;
        }
        Arrays.sort(pointPairs, new Comparator<PointsPair>() {
            public int compare(PointsPair pp1, PointsPair pp2) {
                return Integer.compare(pp1.points, pp2.points);
            }
        });

        ArrayList<String> values = new ArrayList<>();

        for (i = 0; i < pointPairs.length; i++) {
            values.add(i + ". " + team.getPlayers().get(pointPairs[i].pid) + " -- " + pointPairs[i].points);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values) {
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
