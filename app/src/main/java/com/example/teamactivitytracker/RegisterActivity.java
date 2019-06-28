package com.example.teamactivitytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.teamactivitytracker.Model.DB;
import com.example.teamactivitytracker.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView errorLabel;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        errorLabel = findViewById(R.id.errorLabel);
        emailText = findViewById(R.id.emailTextField);
        passwordText = findViewById(R.id.passwordTextField);
        firstNameText = findViewById(R.id.firstNameTextField);
        lastNameText = findViewById(R.id.lastNameTextField);

        mAuth = FirebaseAuth.getInstance();

        db = new DB();
    }

    public void back(View view) {
        finish();
    }

    public void register(View view) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();

        if (firstName.length() == 0) {
            errorLabel.setText(getString(R.string.first_name_empty));
            return;
        }
        if (lastName.length() == 0) {
            errorLabel.setText(getString(R.string.last_name_empty));
            return;
        }
        if (!isValidEmail(email)) {
            errorLabel.setText(getString(R.string.email_invalid));
            return;
        }
        if (password.length() == 0) {
            errorLabel.setText(getString(R.string.password_invalid));
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            db.addUser(email.replace(".", ""), email, firstName, lastName, newUser -> registerSuccessful(newUser));
                        } else {
                            // If sign in fails, display a message to the user.
                            errorLabel.setText(getString(R.string.registration_error));
                        }
                    }
                });
        hideKeyboard(this);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void registerSuccessful(User user) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("CURRENT_USER_EMAIL", user.getEmail());
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
