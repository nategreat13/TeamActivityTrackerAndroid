package com.nategreat13.teamactivitytracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nategreat13.teamactivitytracker.Model.DB;
import com.nategreat13.teamactivitytracker.Model.Player;
import com.nategreat13.teamactivitytracker.Model.ProfileType;
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
    private SwipeMenuListView listPlayers;
    private ListView listCoaches;

    private TextView playersTextView;
    private TextView coachesTextView;

    private DB db;
    private ArrayAdapter<String> playerAdapter;

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

        db = new DB();

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

        playerAdapter = new ArrayAdapter<String>
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
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
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
                        String tid = team.getTid();
                        String teamName = team.getName();
                        String pid = playersSorted.get(index).getKey();
                        String playerName = playersSorted.get(index).getValue();
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Remove player from team?")
                                .setMessage("Are you sure you want to remove " + playerName + " from " + teamName + "? THIS CANNOT BE UNDONE")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.removePlayerFromTeam(pid, tid, success -> {
                                            if (success) {
                                                playersSorted.remove(position);
                                                updatePlayerListView();
                                            }
                                            else {
                                                System.out.println("Fail");
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

    public void updatePlayerListView() {

        playerAdapter.clear();

        ArrayList<String> playerValues = new ArrayList<>();

        for (int i = 0; i< playersSorted.size(); i++) {
            playerValues.add(playersSorted.get(i).getValue());
        }

        playerAdapter.addAll(playerValues);
        playerAdapter.notifyDataSetChanged();
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
