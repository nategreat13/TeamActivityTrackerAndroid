package com.nategreat13.teamactivitytracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class RosterFragment extends Fragment implements com.nategreat13.teamactivitytracker.RosterAdapter.ItemClickListener {

    private Team team;
    private List<Map.Entry<String, String>> playersSorted;
    private List<Map.Entry<String, String>> coachesSorted;
    private RecyclerView listPlayers;
    private RecyclerView listCoaches;

    private TextView playersTextView;
    private TextView coachesTextView;

    private DB db;
    //private ArrayAdapter<String> playerAdapter;
    //private ArrayAdapter<String> coachAdapter;

    private com.nategreat13.teamactivitytracker.RosterAdapter coachAdapter;
    private com.nategreat13.teamactivitytracker.RosterAdapter playerAdapter;
    private SwipeController swipeController;

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

        //listPlayers = getActivity().findViewById(R.id.listPlayers);
        //listCoaches = getActivity().findViewById(R.id.listCoaches);

        db = new DB();

        ArrayList<String> playerValues = new ArrayList<>();

        playersSorted = sort(team.getPlayers());
        for (int i = 0; i< playersSorted.size(); i++) {
            playerValues.add(playersSorted.get(i).getValue());
        }

        listPlayers = getActivity().findViewById(R.id.listPlayers);
        listPlayers.setLayoutManager(new LinearLayoutManager(getActivity()));
        playerAdapter = new com.nategreat13.teamactivitytracker.RosterAdapter(getActivity(), playerValues);
        playerAdapter.setClickListener(this::onPlayerClick);
        listPlayers.setAdapter(playerAdapter);
        listPlayers.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayout.VERTICAL));

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                removePlayerFromTeam(position);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(listPlayers);

        listPlayers.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        ArrayList<String> coachValues = new ArrayList<>();

        coachesSorted = sort(team.getCoaches());
        for (int i = 0; i< coachesSorted.size(); i++) {
            coachValues.add(coachesSorted.get(i).getValue());
        }

        listCoaches = getActivity().findViewById(R.id.listCoaches);
        listCoaches.setLayoutManager(new LinearLayoutManager(getActivity()));
        coachAdapter = new com.nategreat13.teamactivitytracker.RosterAdapter(getActivity(), coachValues);
        coachAdapter.setClickListener(this);
        listCoaches.setAdapter(coachAdapter);
        listCoaches.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayout.VERTICAL));
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public void onPlayerClick(View view, int position) {
        playerSelected(position);
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

    public void setTeam(Team team) {
        this.team = team;
        playersSorted = sort(team.getPlayers());
        coachesSorted = sort(team.getCoaches());
        updatePlayerListView();
        updateCoachesListView();
    }

    public void updatePlayerListView() {

        ArrayList<String> playerValues = new ArrayList<>();

        for (int i = 0; i< playersSorted.size(); i++) {
            playerValues.add(playersSorted.get(i).getValue());
        }

        playerAdapter.setList(playerValues);
        playerAdapter.notifyDataSetChanged();
    }

    public void removePlayerFromTeam(int index) {
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
    }

    public void updateCoachesListView() {
        ArrayList<String> coachValues = new ArrayList<>();

        for (int i = 0; i< coachesSorted.size(); i++) {
            coachValues.add(coachesSorted.get(i).getValue());
        }
        coachAdapter.setList(coachValues);
        coachAdapter.notifyDataSetChanged();
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
