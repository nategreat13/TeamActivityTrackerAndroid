package com.nategreat13.teamactivitytracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nategreat13.teamactivitytracker.Model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RosterFragment extends Fragment {

    private Team team;
    private List<Map.Entry<String, String>> playersSorted;
    private List<Map.Entry<String, String>> coachesSorted;
    private ListView listPlayers;
    private ListView listCoaches;

    private TextView playersTextView;
    private TextView coachesTextView;

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

        listPlayers = getActivity().findViewById(R.id.listPlayers);
        listCoaches = getActivity().findViewById(R.id.listCoaches);

        ArrayList<String> playerValues = new ArrayList<>();
        ArrayList<String> coachValues = new ArrayList<>();

        coachesSorted = sort(team.getCoaches());
        for (int i = 0; i< coachesSorted.size(); i++) {
            coachValues.add(coachesSorted.get(i).getValue());
        }

        playersSorted = sort(team.getPlayers());
        for (int i = 0; i< playersSorted.size(); i++) {
            playerValues.add(playersSorted.get(i).getValue());
        }

        ArrayAdapter<String> playerAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, playerValues) {
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
        listPlayers.setAdapter(playerAdapter);

        // ListView Item Click Listener
        listPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                playerSelected(position);
            }

        });

        ArrayAdapter<String> coachAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, coachValues) {
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
        listCoaches.setAdapter(coachAdapter);
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

    public void playerSelected(int index) {
        String pid = playersSorted.get(index).getKey();
        Intent intent = new Intent(getActivity(), PlayerDetailActivity.class);
        intent.putExtra("TID", team.getTid());
        intent.putExtra("PID", pid);
        intent.putExtra("PLAYER_NAME", playersSorted.get(index).getValue());
        startActivity(intent);
    }
    public List<Map.Entry<String, String>> sort(HashMap<String, String> values) {
        LinkedList<Map.Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>(values.entrySet());
        Collections.sort(list, comp);
        return list;
    }

    public Comparator<Map.Entry<String, String>> comp =
            new Comparator<Map.Entry<String, String>>()
            {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2 )
                {
                    return ( o1.getValue() ).compareTo( o2.getValue() );
                }
            };

}
