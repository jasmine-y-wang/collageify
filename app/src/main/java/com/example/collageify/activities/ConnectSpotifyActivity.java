package com.example.collageify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.collageify.services.UserService;
import com.example.collageify.databinding.ActivityConnectSpotifyBinding;
import com.example.collageify.models.User;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class ConnectSpotifyActivity extends AppCompatActivity {

    private ActivityConnectSpotifyBinding binding;
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private RequestQueue queue;
    public static final String TAG = "ConnectSpotifyActivity";
    public static final String CLIENT_ID = "241d0f56be9e4824b0ededa9f2c4ae3d";
    public static final String REDIRECT_URI = "com.example.collageify://callback";
    public static final int REQUEST_CODE = 1337;
    public static final String SCOPES = "user-read-recently-played,user-top-read,user-read-private";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectSpotifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);
        binding.btnLoginSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateSpotify();
            }
        });
    }

    private void authenticateSpotify() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID,
                AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                case TOKEN:
                    // handle successful response
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();
                    waitForUserInfo();
                    break;

                case ERROR:
                    // handle error response
                    Log.e(TAG, "there was an error logging in: " + response.getError());
                    break;

                default:
                    // handle other cases
            }
        }
    }

    private void waitForUserInfo() {
        UserService userService = new UserService(queue, mSharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.getSpotifyId());
            Log.d(TAG, "got user information");
            // use commit instead of apply bc we need the info stored immediately
            editor.commit();
            Toast.makeText(this, "authenticated as " + user.getSpotifyId(), Toast.LENGTH_SHORT).show();
            startMainActivity();
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}