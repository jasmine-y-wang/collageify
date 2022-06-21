package com.example.collageify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.collageify.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "241d0f56be9e4824b0ededa9f2c4ae3d";
    public static final String REDIRECT_URI = "com.example.collageify://callback";
    public static final int REQUEST_CODE = 1337;
    public static final String SCOPES = "user-read-recently-played,user-top-tracks,user-read-private";
    public static final String TAG = "LoginActivity";
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnLoginSpotify = findViewById(R.id.btnLoginSpotify);

        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        btnLoginSpotify.setOnClickListener(new View.OnClickListener() {
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
                    Log.d(TAG, "starting: got auth token");
                    editor.apply();
//                    waitForUserInfo();
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
    }


}