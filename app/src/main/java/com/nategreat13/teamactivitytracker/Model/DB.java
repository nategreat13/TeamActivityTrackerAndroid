package com.nategreat13.teamactivitytracker.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DB {

    private FirebaseFirestore db;

    public DB() {
        db = FirebaseFirestore.getInstance();
    }

    // User Functions

    public void addUser(String uid, String email, String firstName, String lastName, UserCallback userCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Email", email);
        data.put("FirstName", firstName);
        data.put("LastName", lastName);

        db.collection("Users").document(uid)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userCallback.onCallbackUser(new User(uid, email, firstName, lastName));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userCallback.onCallbackUser(null);
                    }
                });
    }

    public void registerSuccess(User newUser) {

    }

    public void getUser(String uid, UserCallback userCallback) {
        DocumentReference docRef = db.collection("Users").document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        String email = data.get("Email").toString();
                        String firstName = data.get("FirstName").toString();
                        String lastName = data.get("LastName").toString();
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> players = (HashMap<String, String>) data.get("Players");
                        if (players == null) {
                            players = new HashMap<>();
                        }
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> coaches = (HashMap<String, String>) data.get("Coaches");
                        if (coaches == null) {
                            coaches = new HashMap<>();
                        }

                        User user = new User(uid, email, firstName, lastName, players, coaches);
                        userCallback.onCallbackUser(user);
                    } else {
                        userCallback.onCallbackUser(null);
                    }

                } else {
                    userCallback.onCallbackUser(null);
                }
            }
        });
    }

    // Player Functions

    public void addPlayer(String firstName, String lastName, String tid, String teamName, PlayerCallback playerCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("FirstName", firstName);
        data.put("LastName", lastName);
        if (tid != "") {
            data.put(tid, teamName);
        }

        getNextId(NextIDType.player, nextID -> {
            if (nextID != -1) {
                String pid = "P-" + nextID;
                db.collection("Players").document(pid)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                setNextID(NextIDType.player, nextID + 1);
                                if (tid == "") {
                                    playerCallback.onCallbackPlayer(new Player(pid, firstName, lastName));
                                }
                                else {
                                    playerCallback.onCallbackPlayer(new Player(pid, firstName, lastName, tid, teamName));
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                playerCallback.onCallbackPlayer(null);
                            }
                        });
            }
        });
    }

    public void addPlayerToTeam(String tid, String pid, String playerName, BooleanCallback booleanCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Players." + pid, playerName);
        data.put("PlayerPoints." + pid, 0);
        data.put("PlayerPeriodPoints." + pid, 0);

        db.collection("Teams").document(tid).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallbackBoolean(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallbackBoolean(false);
                    }
                });
    }

    public void addPlayerToUser(String pid, String uid, String playerName, BooleanCallback booleanCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Players." + pid, playerName);

        db.collection("Users").document(uid).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallbackBoolean(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallbackBoolean(false);
                    }
                });
    }

    public void getPlayer(String pid, PlayerCallback playerCallback) {
        DocumentReference docRef = db.collection("Players").document(pid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        String firstName = data.get("FirstName").toString();
                        String lastName = data.get("LastName").toString();
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> teams = (HashMap<String, String>) data.get("Teams");
                        if (teams == null) {
                            teams = new HashMap<>();
                        }

                        Player player = new Player(pid, firstName, lastName, teams);
                        playerCallback.onCallbackPlayer(player);
                    } else {
                        playerCallback.onCallbackPlayer(null);
                    }

                } else {
                    playerCallback.onCallbackPlayer(null);
                }
            }
        });
    }

    public void removePlayerFromTeam(String pid, String tid, BooleanCallback booleanCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Players." + pid, FieldValue.delete());
        data.put("PlayerPoints." + pid, FieldValue.delete());
        data.put("PlayerPeriodPoints." + pid, FieldValue.delete());
        db.collection("Teams").document(tid).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("Teams." + tid, FieldValue.delete());
                        db.collection("Players").document(pid).update(data);
                        db.collection("CompletedActivities").whereEqualTo("tid", tid).whereEqualTo("pid", pid).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            ArrayList<CompletedActivity> completedActivities = new ArrayList<>();
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String caid = document.getId();
                                                db.collection("CompletedActivities").document(caid).delete();
                                            }
                                        }
                                    }
                                });
                        booleanCallback.onCallbackBoolean(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallbackBoolean(false);
                    }
                });
    }

    // Coach Functions

    public void getCoach(String cid, CoachCallback coachCallback) {
        DocumentReference docRef = db.collection("Coaches").document(cid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        String firstName = data.get("FirstName").toString();
                        String lastName = data.get("LastName").toString();
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> teams = (HashMap<String, String>) data.get("Teams");
                        if (teams == null) {
                            teams = new HashMap<>();
                        }

                        Coach coach = new Coach(cid, firstName, lastName, teams);
                        coachCallback.onCallbackCoach(coach);
                    } else {
                        coachCallback.onCallbackCoach(null);
                    }

                } else {
                    coachCallback.onCallbackCoach(null);
                }
            }
        });
    }

    public void addCoach(String firstName, String lastName, String tid, String teamName, CoachCallback coachCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("FirstName", firstName);
        data.put("LastName", lastName);
        if (tid != "") {
            data.put(tid, teamName);
        }

        getNextId(NextIDType.coach, nextID -> {
            if (nextID != -1) {
                String cid = "C-" + nextID;
                db.collection("Coaches").document(cid)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                setNextID(NextIDType.coach, nextID + 1);
                                if (tid == "") {
                                    coachCallback.onCallbackCoach(new Coach(cid, firstName, lastName));
                                }
                                else {
                                    coachCallback.onCallbackCoach(new Coach(cid, firstName, lastName, tid, teamName));
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                coachCallback.onCallbackCoach(null);
                            }
                        });
            }
        });
    }

    public void addCoachToTeam(String tid, String cid, String coachName, BooleanCallback booleanCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Coaches." + cid, coachName);

        db.collection("Teams").document(tid).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallbackBoolean(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallbackBoolean(false);
                    }
                });
    }

    public void addCoachToUser(String cid, String uid, String coachName, BooleanCallback booleanCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Coaches." + cid, coachName);

        db.collection("Users").document(uid).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallbackBoolean(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallbackBoolean(false);
                    }
                });
    }

    // Team Functions
    public void addTeam(String tid, String teamName, String cid, String coachName, Boolean showFullLeaderboard, TeamCallback teamCallback) {
        HashMap<String, Object> coachInfo = new HashMap<>();
        coachInfo.put(cid, coachName);

        HashMap<String, Object> data = new HashMap<>();
        data.put("Name", teamName);
        data.put("Coaches", coachInfo);
        data.put("ShowLeaderboard", showFullLeaderboard);

        db.collection("Teams").document(tid)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        teamCallback.onCallbackTeam(new Team(tid, teamName, cid, coachName, showFullLeaderboard));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        teamCallback.onCallbackTeam(null);
                    }
                });
    }

    public void addTeamToCoach(String tid, String teamName, String cid, BooleanCallback booleanCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Teams." + tid, teamName);

        db.collection("Coaches").document(cid)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallbackBoolean(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallbackBoolean(false);
                    }
                });
    }

    public void addTeamToPlayer(String tid, String teamName, String pid, BooleanCallback booleanCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Teams." + tid, teamName);

        db.collection("Players").document(pid)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallbackBoolean(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallbackBoolean(false);
                    }
                });
    }

    public void findTeam(String tid, StringCallback stringCallback) {
        DocumentReference docRef = db.collection("Teams").document(tid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        String teamName = data.get("Name").toString();

                        stringCallback.onCallbackString(teamName);
                    } else {
                        stringCallback.onCallbackString(null);
                    }

                } else {
                    stringCallback.onCallbackString(null);
                }
            }
        });
    }

    public void getTeam(String tid, TeamCallback teamCallback) {
        DocumentReference docRef = db.collection("Teams").document(tid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        String teamName = data.get("Name").toString();
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> players = (HashMap<String, String>) data.get("Players");
                        if (players == null) {
                            players = new HashMap<>();
                        }
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> coaches = (HashMap<String, String>) data.get("Coaches");
                        if (coaches == null) {
                            coaches = new HashMap<>();
                        }
                        @SuppressWarnings("unchecked")
                        HashMap<String, Long> playerPoints = (HashMap<String, Long>) data.get("PlayerPoints");
                        if (playerPoints == null) {
                            playerPoints = new HashMap<>();
                        }
                        @SuppressWarnings("unchecked")
                        HashMap<String, Long> playerPeriodPoints = (HashMap<String, Long>) data.get("PlayerPeriodPoints");
                        if (playerPeriodPoints == null) {
                            playerPeriodPoints = new HashMap<>();
                        }
                        Boolean showLeaderboard = (Boolean) data.get("ShowLeaderboard");
                        if (showLeaderboard == null) {
                            showLeaderboard = true;
                        }

                        teamCallback.onCallbackTeam(new Team(tid, teamName, players, coaches, playerPoints, playerPeriodPoints, showLeaderboard));
                    } else {
                        teamCallback.onCallbackTeam(null);
                    }
                } else {
                    teamCallback.onCallbackTeam(null);
                }
            }
        });
    }

    public void getTeamActivities(String tid, ActivitiesCallback activitiesCallback) {
        Query query = db.collection("Activities").whereEqualTo("tid", tid);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                Activity[] activities = new Activity[queryDocumentSnapshots.getDocuments().size()];
                int i = 0;
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    Map<String, Object> data = snapshot.getData();
                    String aid = snapshot.getId();
                    String name = data.get("Name").toString();
                    String description = data.get("Description").toString();
                    int points = Integer.parseInt(data.get("Points").toString());
                    int limit = Integer.parseInt(data.get("Limit").toString());
                    String limitPeriod = data.get("LimitPeriod").toString();
                    LimitPeriod limitPeriodEnumVal = LimitPeriod.day;
                    if (limitPeriod.equals("Week")) {
                        limitPeriodEnumVal = LimitPeriod.week;
                    }
                    if (limitPeriod.equals("Month")) {
                        limitPeriodEnumVal = LimitPeriod.month;
                    }
                    activities[i] = (new Activity(aid, name, description, points, tid, limit, limitPeriodEnumVal));
                    i++;
                }
                activitiesCallback.onCallbackActivities(activities);
            }
        });
    }

    public void updateTeamShowLeaderboard(String tid, Boolean showLeaderbooard, BooleanCallback booleanCallback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("ShowLeaderboard", showLeaderbooard);

        db.collection("Teams").document(tid).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallbackBoolean(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallbackBoolean(false);
                    }
                });
    }

    // Activity Functions

    public void addActivity(String name, String description, int points, String tid, int limit, LimitPeriod limitPeriod, ActivityCallback activityCallback) {
        String limitPeriodString = "Day";
        if (limitPeriod == LimitPeriod.week) {
            limitPeriodString = "Week";
        }
        else if (limitPeriod == LimitPeriod.month) {
            limitPeriodString = "Month";
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("Name", name);
        data.put("Description", description);
        data.put("Points", points);
        data.put("tid", tid);
        data.put("Limit", limit);
        data.put("LimitPeriod", limitPeriodString);

        getNextId(NextIDType.activity, nextID -> {
            if (nextID != -1) {
                String aid = "A-" + nextID;
                db.collection("Activities").document(aid)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                setNextID(NextIDType.activity, nextID + 1);
                                activityCallback.onCallbackActivity(new Activity(aid, name, description, points, tid, limit, limitPeriod));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                activityCallback.onCallbackActivity(null);
                            }
                        });
            }
        });
    }

    public void editActivity(Activity activity, ActivityCallback activityCallback) {
        String limitPeriodString = "Day";
        if (activity.getLimitPeriod() == LimitPeriod.week) {
            limitPeriodString = "Week";
        }
        else if (activity.getLimitPeriod() == LimitPeriod.month) {
            limitPeriodString = "Month";
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("Name", activity.getName());
        data.put("Description", activity.getDescription());
        data.put("Points", activity.getPoints());
        data.put("tid", activity.getTid());
        data.put("Limit", activity.getLimit());
        data.put("LimitPeriod", limitPeriodString);

        db.collection("Activities").document(activity.getAid())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        activityCallback.onCallbackActivity(activity);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activityCallback.onCallbackActivity(null);
                    }
                });
    }

    // Completed Activity Functions

    public void addCompletedActivity(Activity activity, long date, int quantity, int totalPoints, String comment, String pid, String tid, String playerName, CompletedActivityCallback completedActivityCallback) {

        HashMap<String, Object> activityData = new HashMap<>();
        activityData.put("aid", activity.getAid());
        activityData.put("Name", activity.getName());
        activityData.put("Description", activity.getDescription());
        activityData.put("Points", activity.getPoints());

        HashMap<String, Object> data = new HashMap<>();
        data.put("Activity", activityData);
        data.put("Date", date);
        data.put("TotalPoints", totalPoints);
        data.put("Comment", comment);
        data.put("tid", tid);
        data.put("pid", pid);
        data.put("Quantity", quantity);
        data.put("PlayerName", playerName);

        getNextId(NextIDType.completedActivity, nextID -> {
            if (nextID != -1) {
                String caid = "CA-" + nextID;
                db.collection("CompletedActivities").document(caid)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                setNextID(NextIDType.completedActivity, nextID + 1);
                                try {
                                    int currentPoints = Globals.currentTeam.getPlayerPoints().get(pid).intValue();
                                    int newPoints = currentPoints + totalPoints;

                                    int currentPeriodPoints = Globals.currentTeam.getPlayerPeriodPoints().get(pid).intValue();
                                    int newPeriodPoints = currentPeriodPoints + totalPoints;

                                    HashMap<String, Object> newPointsData = new HashMap<>();
                                    newPointsData.put("PlayerPoints." + pid, newPoints);
                                    newPointsData.put("PlayerPeriodPoints." + pid, newPeriodPoints);
                                    db.collection("Teams").document(tid).update(newPointsData);

                                    completedActivityCallback.onCallbackCompletedActivity(new CompletedActivity(caid, activity, pid, tid, quantity, totalPoints, playerName, new Date(date*1000)));
                                }
                                catch (Exception e) {
                                    completedActivityCallback.onCallbackCompletedActivity(null);
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                completedActivityCallback.onCallbackCompletedActivity(null);
                            }
                        });
            }
        });
    }

    public void getRecentCompletedActivities(String tid, CompletedActivitiesCallback completedActivitiesCallback) {
        db.collection("CompletedActivities").whereEqualTo("tid", tid).orderBy("Date", Query.Direction.DESCENDING).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<CompletedActivity> completedActivities = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String caid = document.getId();
                                Map<String, Object> completedActivityData = document.getData();
                                @SuppressWarnings("unchecked")
                                Map<String, Object> activityData = (Map<String, Object>) completedActivityData.get("Activity");
                                String aid = activityData.get("aid").toString();
                                String name = activityData.get("Name").toString();
                                String description = activityData.get("Description").toString();
                                int points = Integer.parseInt(activityData.get("Points").toString());
                                String pid = completedActivityData.get("pid").toString();
                                String tid = completedActivityData.get("tid").toString();
                                int quantity = Integer.parseInt(completedActivityData.get("Quantity").toString());
                                int totalPoints = Integer.parseInt(completedActivityData.get("TotalPoints").toString());
                                String playerName = completedActivityData.get("PlayerName").toString();
                                Long dateLong = Long.parseLong(completedActivityData.get("Date").toString());
                                Date date = new Date(dateLong*1000);
                                Activity activity = new Activity(aid, name, description, points, tid, 0, LimitPeriod.day);
                                CompletedActivity completedActivity = new CompletedActivity(caid, activity, pid, tid, quantity, totalPoints, playerName, date);
                                completedActivities.add(completedActivity);
                            }
                            completedActivitiesCallback.onCallbackCompletedActivities(completedActivities);
                        } else {
                            completedActivitiesCallback.onCallbackCompletedActivities(null);
                        }
                    }
                });
    }

    public void getCompletedActivitiesForPlayerSinceTime(String tid, String pid, long time, CompletedActivitiesCallback completedActivitiesCallback) {
        db.collection("CompletedActivities").whereEqualTo("tid", tid).whereEqualTo("pid", pid).whereGreaterThan("Date", time).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<CompletedActivity> completedActivities = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String caid = document.getId();
                                Map<String, Object> completedActivityData = document.getData();
                                @SuppressWarnings("unchecked")
                                Map<String, Object> activityData = (Map<String, Object>) completedActivityData.get("Activity");
                                String aid = activityData.get("aid").toString();
                                String name = activityData.get("Name").toString();
                                String description = activityData.get("Description").toString();
                                int points = Integer.parseInt(activityData.get("Points").toString());
                                String pid = completedActivityData.get("pid").toString();
                                String tid = completedActivityData.get("tid").toString();
                                int quantity = Integer.parseInt(completedActivityData.get("Quantity").toString());
                                int totalPoints = Integer.parseInt(completedActivityData.get("TotalPoints").toString());
                                String playerName = completedActivityData.get("PlayerName").toString();
                                Long dateLong = Long.parseLong(completedActivityData.get("Date").toString());
                                Date date = new Date(dateLong*1000);
                                Activity activity = new Activity(aid, name, description, points, tid, 0, LimitPeriod.day);
                                CompletedActivity completedActivity = new CompletedActivity(caid, activity, pid, tid, quantity, totalPoints, playerName, date);
                                completedActivities.add(completedActivity);
                            }
                            completedActivitiesCallback.onCallbackCompletedActivities(completedActivities);
                        } else {
                            completedActivitiesCallback.onCallbackCompletedActivities(null);
                        }
                    }
                });
    }

    public void getNumberOfCompletedActtivitiesSinceTime(String pid, String aid, long time, IntCallback intCallback) {
        db.collection("CompletedActivities").whereEqualTo("pid", pid).whereEqualTo("Activity.aid", aid).whereGreaterThan("Date", time).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int numberOfCompletedActivities = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                numberOfCompletedActivities += Integer.parseInt(data.get("Quantity").toString());
                            }
                            intCallback.onCallbackInt(numberOfCompletedActivities);
                        } else {
                            intCallback.onCallbackInt(0);
                        }
                    }
                });
    }

    // NextID Functions

    public void setNextID(NextIDType nextIDType, int nextID) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("NextID", nextID);

        db.collection("NextID").document(getNextIDStringFromEnum(nextIDType)).set(data);
    }

    public void getNextId(NextIDType nextIDType, NextIDCallback nextIDCallback) {
        DocumentReference docRef = db.collection("NextID").document(getNextIDStringFromEnum(nextIDType));

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        int nextID = document.getLong("NextID").intValue();
                        nextIDCallback.onCallbackNextID(nextID);
                    } else {
                        nextIDCallback.onCallbackNextID(-1);
                    }

                } else {
                    nextIDCallback.onCallbackNextID(-1);
                }
            }
        });
    }


    public String getNextIDStringFromEnum(NextIDType type) {
        switch (type) {
            case activity:
                return "Activity";
            case coach:
                return "Coach";
            case completedActivity:
                return "CompletedActivity";
            case player:
                return "Player";
        }
        return "";
    }

    // Listeners
    public void listenForTeam(String tid, TeamCallback teamCallback) {
        final DocumentReference docRef = db.collection("Teams").document(tid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    String teamName = data.get("Name").toString();
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> players = (HashMap<String, String>) data.get("Players");
                    if (players == null) {
                        players = new HashMap<>();
                    }
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> coaches = (HashMap<String, String>) data.get("Coaches");
                    if (coaches == null) {
                        coaches = new HashMap<>();
                    }
                    @SuppressWarnings("unchecked")
                    HashMap<String, Long> playerPoints = (HashMap<String, Long>) data.get("PlayerPoints");
                    if (playerPoints == null) {
                        playerPoints = new HashMap<>();
                    }
                    @SuppressWarnings("unchecked")
                    HashMap<String, Long> playerPeriodPoints = (HashMap<String, Long>) data.get("PlayerPeriodPoints");
                    if (playerPeriodPoints == null) {
                        playerPeriodPoints = new HashMap<>();
                    }
                    Boolean showLeaderboard = (Boolean) data.get("ShowLeaderboard");
                    if (showLeaderboard == null) {
                        showLeaderboard = true;
                    }

                    teamCallback.onCallbackTeam(new Team(tid, teamName, players, coaches, playerPoints, playerPeriodPoints, showLeaderboard));
                }
            }
        });
    }

    public void listenForTeamActivities(String tid, ActivitiesCallback activitiesCallback) {
        db.collection("Activities").whereEqualTo("tid", tid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                Activity[] activities = new Activity[value.getDocuments().size()];
                int i = 0;
                for (QueryDocumentSnapshot document : value) {
                    Map<String, Object> data = document.getData();
                    String aid = document.getId();
                    String name = data.get("Name").toString();
                    String description = data.get("Description").toString();
                    int points = Integer.parseInt(data.get("Points").toString());
                    int limit = Integer.parseInt(data.get("Limit").toString());
                    String limitPeriod = data.get("LimitPeriod").toString();
                    LimitPeriod limitPeriodEnumVal = LimitPeriod.day;
                    if (limitPeriod.equals("Week")) {
                        limitPeriodEnumVal = LimitPeriod.week;
                    }
                    if (limitPeriod.equals("Month")) {
                        limitPeriodEnumVal = LimitPeriod.month;
                    }
                    activities[i] = (new Activity(aid, name, description, points, tid, limit, limitPeriodEnumVal));
                    i++;
                }
                activitiesCallback.onCallbackActivities(activities);
            }
        });
    }

    public void listenForCompletedActivites(String tid, CompletedActivitiesCallback completedActivitiesCallback) {
        db.collection("CompletedActivities").whereEqualTo("tid", tid).orderBy("Date", Query.Direction.DESCENDING).limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        ArrayList<CompletedActivity> completedActivities = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            String caid = document.getId();
                            Map<String, Object> completedActivityData = document.getData();
                            @SuppressWarnings("unchecked")
                            Map<String, Object> activityData = (Map<String, Object>) completedActivityData.get("Activity");
                            String aid = activityData.get("aid").toString();
                            String name = activityData.get("Name").toString();
                            String description = activityData.get("Description").toString();
                            int points = Integer.parseInt(activityData.get("Points").toString());
                            String pid = completedActivityData.get("pid").toString();
                            String tid = completedActivityData.get("tid").toString();
                            int quantity = Integer.parseInt(completedActivityData.get("Quantity").toString());
                            int totalPoints = Integer.parseInt(completedActivityData.get("TotalPoints").toString());
                            String playerName = completedActivityData.get("PlayerName").toString();
                            Long dateLong = Long.parseLong(completedActivityData.get("Date").toString());
                            Date date = new Date(dateLong*1000);
                            Activity activity = new Activity(aid, name, description, points, tid, 0, LimitPeriod.day);
                            CompletedActivity completedActivity = new CompletedActivity(caid, activity, pid, tid, quantity, totalPoints, playerName, date);
                            completedActivities.add(completedActivity);
                        }
                        completedActivitiesCallback.onCallbackCompletedActivities(completedActivities);
                    }
                });

    }

    // Callbacks

    public FirebaseFirestore getDb() {
        return db;
    }

    public interface UserCallback {
        void onCallbackUser(User user);
    }

    public interface PlayerCallback {
        void onCallbackPlayer(Player player);
    }

    public interface CoachCallback {
        void onCallbackCoach(Coach coach);
    }

    public interface TeamCallback {
        void onCallbackTeam(Team team);
    }

    public interface ActivitiesCallback {
        void onCallbackActivities(Activity[] activities);
    }

    public interface ActivityCallback {
        void onCallbackActivity(Activity activity);
    }

    public interface CompletedActivitiesCallback {
        void onCallbackCompletedActivities(ArrayList<CompletedActivity> completedActivities);
    }

    public interface CompletedActivityCallback {
        void onCallbackCompletedActivity(CompletedActivity completedActivity);
    }

    public interface NextIDCallback {
        void onCallbackNextID(int nextID);
    }

    public interface BooleanCallback {
        void onCallbackBoolean(boolean success);
    }

    public interface StringCallback {
        void onCallbackString(String string);
    }

    public interface IntCallback {
        void onCallbackInt(int value);
    }
}
