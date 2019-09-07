package com.nategreat13.teamactivitytracker;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nategreat13.teamactivitytracker.Model.CompletedActivity;
import com.nategreat13.teamactivitytracker.Model.DB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PlayerDetailActivity extends AppCompatActivity {

    private DB db;
    private String tid;
    private String pid;
    private Spinner filterSpinner;
    private Button filterButton;
    private ListView completedActivitiesListView;
    private ArrayAdapter completedActivitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DB();
        tid = getIntent().getStringExtra("TID");
        pid = getIntent().getStringExtra("PID");

        filterButton = findViewById(R.id.filterButton);
        filterSpinner = findViewById(R.id.filterSpinner);
        completedActivitiesListView = findViewById(R.id.completedActivitiesListView);

        List<String> list = new ArrayList<String>();
        list.add("Day");
        list.add("Week");
        list.add("Month");
        list.add("All");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        filterSpinner.setAdapter(dataAdapter);
        filterButtonPressed(filterButton);
    }

    public void filterButtonPressed(View view) {
        String filter = "Day";

        Long timePeriodStart = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();

        switch (filterSpinner.getSelectedItemPosition()) {
            case 0:
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                timePeriodStart = calendar.getTimeInMillis();
                break;
            case 1:
                calendar.setTime(new Date());
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                timePeriodStart = calendar.getTimeInMillis();
                break;
            case 2:
                calendar.setTime(new Date());
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                timePeriodStart = calendar.getTimeInMillis();
                break;
            case 3:
                timePeriodStart = 0L;
        }
        db.getCompletedActivitiesForPlayerSinceTime(tid, pid, timePeriodStart/1000, completedActivities -> {
            ArrayList<String> values = new ArrayList<>();

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            for (int i = completedActivities.size() - 1; i >= 0; i--) {
                CompletedActivity completedActivity = completedActivities.get(i);
                String dateString = dateFormat.format(completedActivity.getDate());
                values.add(dateString + " - " + completedActivity.getActivity().getName() + " (" + completedActivity.getTotalPoints() + ")");
            }

            completedActivitiesAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, android.R.id.text1, values) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Get the current item from ListView
                    View view = super.getView(position,convertView,parent);

                    // Get the Layout Parameters for ListView Current Item View
                    ViewGroup.LayoutParams params = view.getLayoutParams();

                    // Set the height of the Item View
                    params.height = 150;
                    view.setLayoutParams(params);

                    return view;
                }
            };
            completedActivitiesListView.setAdapter(completedActivitiesAdapter);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
