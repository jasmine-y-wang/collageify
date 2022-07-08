package com.example.collageify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collageify.R;
import com.example.collageify.adapters.RecommendedTracksAdapter;
import com.example.collageify.databinding.FragmentAlbumMoreBinding;
import com.example.collageify.models.Album;
import com.example.collageify.models.Song;
import com.example.collageify.services.TracksRecommendationService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumMoreFragment extends Fragment {

    private Album album;
    private List<Song> recommendedTracks;
    private RecommendedTracksAdapter adapter;
    private FragmentAlbumMoreBinding binding;

    public AlbumMoreFragment(Album album) {
        this.album = album;
        recommendedTracks = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAlbumMoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new RecommendedTracksAdapter(getContext(), recommendedTracks);
        binding.rvRecommendedTracks.setAdapter(adapter);
        binding.rvRecommendedTracks.setLayoutManager(new LinearLayoutManager(getContext()));
        getRecommendedTracks();
    }

    private void getRecommendedTracks() {
        TracksRecommendationService tracksRecommendationService = new TracksRecommendationService(getContext().getApplicationContext());
        tracksRecommendationService.get(album, () -> {
            recommendedTracks.addAll(tracksRecommendationService.getTracks());
            adapter.notifyDataSetChanged();
        });
    }
}