package com.example.collageify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collageify.R;
import com.example.collageify.activities.ConnectSpotifyActivity;
import com.example.collageify.adapters.AlbumTracksAdapter;
import com.example.collageify.databinding.FragmentAlbumSongsBinding;
import com.example.collageify.models.Album;
import com.example.collageify.models.Song;
import com.example.collageify.services.AlbumTracksService;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumSongsFragment extends Fragment {

    private FragmentAlbumSongsBinding binding;
    private Album album;
    private List<Song> albumTracks;
    private AlbumTracksAdapter adapter;
    private SpotifyAppRemote mSpotifyAppRemote;
    public static final String TAG = "AlbumSongsFragment";

    public AlbumSongsFragment() {
        // Required empty public constructor
    }

    public AlbumSongsFragment(Album album) {
        this.album = album;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAlbumSongsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumTracks = new ArrayList<>();
        adapter = new AlbumTracksAdapter(getContext(), albumTracks, album.getTopSongIds());
        getAlbumTracksInfo();

        binding.rvSongs.setAdapter(adapter);
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.btnPlayOnSpotify.setOnClickListener(v -> playOnSpotify());
    }

    private void getAlbumTracksInfo() {
        AlbumTracksService albumTracksService = new AlbumTracksService(getContext().getApplicationContext());
        albumTracksService.get(album.getId(), () -> {
            albumTracks.addAll(albumTracksService.getAlbumTracks());
            adapter.notifyDataSetChanged();
        });
    }

    private void playOnSpotify() {
        ConnectionParams connectionParams = new ConnectionParams.Builder(ConnectSpotifyActivity.CLIENT_ID)
                .setRedirectUri(ConnectSpotifyActivity.REDIRECT_URI)
                .showAuthView(true)
                .build();
        SpotifyAppRemote.connect(getContext(), connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                mSpotifyAppRemote.getPlayerApi().play(album.getUri());
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}