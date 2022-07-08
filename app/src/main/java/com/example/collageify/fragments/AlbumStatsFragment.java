package com.example.collageify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collageify.R;
import com.example.collageify.databinding.FragmentAlbumStatsBinding;
import com.example.collageify.models.Album;
import com.example.collageify.services.AlbumStatsService;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumStatsFragment extends Fragment {

    private Album album;
    private FragmentAlbumStatsBinding binding;
    HashMap<String, Double> stats;

    public AlbumStatsFragment() {
        // Required empty public constructor
    }

    public AlbumStatsFragment(Album album) {
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
        binding = FragmentAlbumStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stats = new HashMap<>();
        getAlbumStats();
    }

    private void getAlbumStats() {
        AlbumStatsService albumStatsService = new AlbumStatsService(getContext().getApplicationContext());
        albumStatsService.get(album, () -> {
            stats.putAll(albumStatsService.getStats());
            setStats();
        });
    }

    private void setStats() {
        int danceability = (int) Math.round(stats.get("danceability") * 100);
        binding.pbDanceability.setProgress(danceability);
        int valence = (int) Math.round(stats.get("valence") * 100);
        binding.pbEnergy.setProgress(valence);
        int energy = (int) Math.round(stats.get("energy") * 100);
        binding.pbEnergy.setProgress(energy);
        int tempo = (int) Math.round(stats.get("tempo"));
        final int MAX_TEMPO = 200;
        binding.pbTempo.setMax(MAX_TEMPO); // higher max for tempos
        binding.pbTempo.setProgress(tempo);
    }
}