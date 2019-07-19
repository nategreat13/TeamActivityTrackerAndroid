package com.example.teamactivitytracker.Model;

import androidx.annotation.NonNull;

import com.example.teamactivitytracker.ActivitiesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
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

    public void addTeam(String tid, String teamName, String cid, String coachName, TeamCallback teamCallback) {
        HashMap<String, Object> coachInfo = new HashMap<>();
        coachInfo.put(cid, coachName);

        HashMap<String, Object> data = new HashMap<>();
        data.put("Name", teamName);
        data.put("Coaches", coachInfo);

        db.collection("Teams").document(tid)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        teamCallback.onCallbackTeam(new Team(tid, teamName, cid, coachName));
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
        HashMap<String, Object> teamInfo = new HashMap<>();
        teamInfo.put(tid, teamName);

        HashMap<String, Object> data = new HashMap<>();
        data.put("Teams", teamInfo);

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
        HashMap<String, Object> teamInfo = new HashMap<>();
        teamInfo.put(tid, teamName);

        HashMap<String, Object> data = new HashMap<>();
        data.put("Teams", teamInfo);

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
                        HashMap<String, Integer> playerPoints = (HashMap<String, Integer>) data.get("PlayerPoints");
                        if (playerPoints == null) {
                            playerPoints = new HashMap<>();
                        }
                        @SuppressWarnings("unchecked")
                        HashMap<String, Integer> playerPeriodPoints = (HashMap<String, Integer>) data.get("PlayerPeriodPoints");
                        if (playerPeriodPoints == null) {
                            playerPeriodPoints = new HashMap<>();
                        }

                        teamCallback.onCallbackTeam(new Team(tid, teamName, players, coaches, playerPoints, playerPeriodPoints));
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

    // Activity Functions

    // Completed Activity Functions

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

    public interface NextIDCallback {
        void onCallbackNextID(int nextID);
    }

    public interface BooleanCallback {
        void onCallbackBoolean(boolean success);
    }

    public interface StringCallback {
        void onCallbackString(String string);
    }
}
