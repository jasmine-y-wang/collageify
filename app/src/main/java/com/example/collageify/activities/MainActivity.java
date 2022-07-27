package com.example.collageify.activities;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.collageify.R;
import com.example.collageify.databinding.ActivityMainBinding;
import com.example.collageify.fragments.CollageFragment;
import com.example.collageify.fragments.AlbumDetailFragment;
import com.example.collageify.fragments.CompareFragment;
import com.example.collageify.fragments.FeedFragment;
import com.example.collageify.fragments.ProfileFragment;
import com.example.collageify.models.Album;
import com.example.collageify.models.Post;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private Fragment collageFragment;
    private FragmentManager fragmentManager;
    private AlbumDetailFragment detailFragment;
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();
        final Fragment feedFragment = new FeedFragment();
        collageFragment = new CollageFragment(MainActivity.this);
        final Fragment profileFragment = new ProfileFragment(ParseUser.getCurrentUser());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_home:
                    fragment = feedFragment;
                    break;
                case R.id.action_collage:
                    fragment = collageFragment;
                    break;
                case R.id.action_profile:
                    fragment = profileFragment;
                    break;
                default:
                    fragment = collageFragment;
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return true;
        });
        // set default selection
        binding.bottomNavigation.setSelectedItemId(R.id.action_collage);
    }

    public void goToFeedFrag() {
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    public void goToAlbumDetailFrag(Album album) {
        detailFragment = new AlbumDetailFragment(album);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContainer, detailFragment, AlbumDetailFragment.TAG);
        ft.addToBackStack("collage to detail");
        ft.commit();
    }

    public void goToCollageFrag() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContainer, collageFragment);
        ft.addToBackStack("detail to collage");
        ft.commit();
    }

    public void goToProfileFrag(ParseUser user) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContainer, new ProfileFragment(user));
        ft.addToBackStack("profile");
        ft.commit();
    }

    public void goToCompareFrag(Post postA) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContainer, new CompareFragment(postA));
        ft.addToBackStack("compare");
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        SpotifyAppRemote.connect(this, connectionParams, mConnectionListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    public void playOnSpotify(String uri) {
        mSpotifyAppRemote.getPlayerApi().play(uri);
    }

    /** Spotify connection fields */
    private final ConnectionParams connectionParams = new ConnectionParams.Builder(ConnectSpotifyActivity.CLIENT_ID)
            .setRedirectUri(ConnectSpotifyActivity.REDIRECT_URI)
            .showAuthView(true)
            .build();

    private final Connector.ConnectionListener mConnectionListener = new Connector.ConnectionListener() {
        @Override
        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
            mSpotifyAppRemote = spotifyAppRemote;
            // subscribe to state
            mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(data -> {
                        final Track track = data.track;
                        if (track != null) {
                            AlbumDetailFragment albumDetailFragment = (AlbumDetailFragment)
                                    getSupportFragmentManager().findFragmentByTag(AlbumDetailFragment.TAG);
                            if (albumDetailFragment != null) {
                                albumDetailFragment.showPlaying(track.uri);
                            }
                        }
                    });
        }

        @Override
        public void onFailure(Throwable error) {
            Log.e(TAG, error.getMessage(), error);
        }
    };


}