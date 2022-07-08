package com.example.collageify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collageify.databinding.ActivitySignupBinding;
import com.example.collageify.models.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;


public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    public static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                signupUser(username, password);
            }
        });

    }

    private void signupUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    startConnectSpotifyActivity();
                } else {
                    Log.e(TAG, "signup failed :(", e);
                }
            }
        });
    }

    private void startConnectSpotifyActivity() {
        Intent intent = new Intent(this, ConnectSpotifyActivity.class);
        startActivity(intent);
        finish();
    }

}