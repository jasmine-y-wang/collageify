package com.example.collageify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;


public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "241d0f56be9e4824b0ededa9f2c4ae3d";
    public static final String REDIRECT_URI = "http://com.example.collageify/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d(TAG, "connected! yay!");
                connected();
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("MainActivity", error.getMessage(), error);
            }
        });
    }

    private void connected() {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}