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
import com.example.collageify.activities.MainActivity;
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
 * A {@link Fragment} that shows a list of songs in an album and lets users play the album
 * through Spotify
 */
public class AlbumSongsFragment extends Fragment {

    private FragmentAlbumSongsBinding binding;
    private Album album;
    private List<Song> albumTracks;
    private AlbumTracksAdapter adapter;
    public static final String TAG = "AlbumSongsFragment";

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
        binding = FragmentAlbumSongsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumTracks = new ArrayList<>();
        getAlbumTracksInfo();
        adapter = new AlbumTracksAdapter(getContext(), albumTracks, album.getTopSongIds());

        binding.rvSongs.setAdapter(adapter);
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.btnPlayOnSpotify.setOnClickListener(v -> ((MainActivity) getContext()).playOnSpotify(album.getUri()));
    }

    private void getAlbumTracksInfo() {
        AlbumTracksService albumTracksService = new AlbumTracksService(getContext().getApplicationContext());
        albumTracksService.get(album, () -> {
            albumTracks.addAll(albumTracksService.getAlbumTracks());
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}