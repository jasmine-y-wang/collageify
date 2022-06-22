package com.example.collageify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.collageify.R;
import com.example.collageify.UserService;
import com.example.collageify.databinding.ActivityLoginBinding;
import com.example.collageify.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            startMainActivity();
        }
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignupActivity();
            }
        });
    }

    private void startSignupActivity() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    protected void loginUser(String username, String password) {
        Log.i(TAG, "attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with login", e);
                    Toast.makeText(LoginActivity.this, "issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                startConnectSpotifyActivity();
            }
        });
    }

    private void startConnectSpotifyActivity() {
        Intent intent = new Intent(this, ConnectSpotifyActivity.class);
        startActivity(intent);
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}