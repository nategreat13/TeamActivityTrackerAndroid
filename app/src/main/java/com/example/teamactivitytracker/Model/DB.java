package com.example.teamactivitytracker.Model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DB {

    private FirebaseFirestore db;

    public DB() {
        db = FirebaseFirestore.getInstance();
    }

    // User Functions

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
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> coaches = (HashMap<String, String>) data.get("Coaches");

                        User user = new User(uid, email, firstName, lastName, players, coaches);
                        userCallback.onCallbackUser(user);
                    } else {
                        // TODO: Error
                        User user = null;
                    }

                } else {
                    // TODO: Error
                    User user = null;
                }
            }
        });
    }

    // Player Functions

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

                        Player player = new Player(pid, firstName, lastName, teams);
                        playerCallback.onCallbackPlayer(player);
                    } else {
                        // TODO: Error
                        Player player = null;
                    }

                } else {
                    // TODO: Error
                    Player player = null;
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

                        Coach coach = new Coach(cid, firstName, lastName, teams);
                        coachCallback.onCallbackCoach(coach);
                    } else {
                        // TODO: Error
                        Player player = null;
                    }

                } else {
                    // TODO: Error
                    Player player = null;
                }
            }
        });
    }

    // Team Functions

    // Activity Functions

    // Completed Activity Functions


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
}
