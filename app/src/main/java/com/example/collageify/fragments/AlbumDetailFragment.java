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

import com.bumptech.glide.Glide;
import com.example.collageify.activities.ConnectSpotifyActivity;
import com.example.collageify.activities.MainActivity;
import com.example.collageify.adapters.AlbumTracksAdapter;
import com.example.collageify.databinding.FragmentAlbumDetailBinding;
import com.example.collageify.models.Song;
import com.example.collageify.services.AlbumTracksService;
import com.example.collageify.services.ArtistService;
import com.example.collageify.models.Album;
import com.example.collageify.models.Artist;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetailFragment extends Fragment {

    private FragmentAlbumDetailBinding binding;
    private Album album;
    private Artist albumArtist;
    private List<Song> albumTracks;
    private AlbumTracksAdapter adapter;
    private SpotifyAppRemote mSpotifyAppRemote;
    public static final String TAG = "AlbumDetailFragment";

    public AlbumDetailFragment() {
        // Required empty public constructor
    }

    public AlbumDetailFragment(Album album) {
        this.album = album;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAlbumDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvName.setText(album.getName());
        binding.tvArtist.setText(album.getArtistName());
        Glide.with(getContext()).load(album.getImageUrl()).into(binding.ivAlbumImage);
        getArtistInfo();

        albumTracks = new ArrayList<>();
        adapter = new AlbumTracksAdapter(getContext(), albumTracks, album.getTopSongIds());
        binding.rvSongs.setAdapter(adapter);
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        getAlbumTracksInfo();

        binding.btnPlayOnSpotify.setOnClickListener(v -> playOnSpotify());
        binding.ibBack.setOnClickListener(v -> ((MainActivity) getContext()).goToCollageFrag());

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
                Log.i(TAG, "connected to spotify player");
                mSpotifyAppRemote.getPlayerApi().play(album.getUri());
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });
    }

    private void getAlbumTracksInfo() {
        AlbumTracksService albumTracksService = new AlbumTracksService(getContext().getApplicationContext());
        albumTracksService.get(album.getId(), () -> {
            albumTracks.addAll(albumTracksService.getAlbumTracks());
            adapter.notifyDataSetChanged();
        });
    }

    private void getArtistInfo() {
        ArtistService artistService = new ArtistService(getContext().getApplicationContext());
        artistService.get(album.getArtistHref(), () -> {
            albumArtist = artistService.getArtist();
            Glide.with(getContext()).load(albumArtist.getImageUrl()).circleCrop().into(binding.ivArtistImage);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}