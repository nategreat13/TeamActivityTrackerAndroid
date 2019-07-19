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
import java.util.HashMap;

public class RosterFragment extends Fragment {

    private Team team;

    public RosterFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static RosterFragment newInstance(Team team) {
        RosterFragment fragment = new RosterFragment();
        Bundle args = new Bundle();
        args.putSerializable("TEAM", team);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = getActivity().findViewById(R.id.list);

        HashMap<String, String> players = team.getPlayers();
        String[] playerIDs = players.keySet().toArray(new String[players.size()]);
        Arrays.sort(playerIDs);
        HashMap<String, String> coaches = team.getCoaches();
        String[] coachIDs = coaches.keySet().toArray(new String[coaches.size()]);
        Arrays.sort(coachIDs);

        ArrayList<String> values = new ArrayList<>();

        for (int j = 0; j < coachIDs.length; j++) {
            values.add(coaches.get(coachIDs[j]));
        }
        for (int i = 0; i < playerIDs.length; i++) {
            values.add(players.get(playerIDs[i]));
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
        return inflater.inflate(R.layout.fragment_roster, container, false);
    }

}
