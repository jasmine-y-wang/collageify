package com.example.collageify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collageify.R;
import com.example.collageify.SongService;
import com.example.collageify.adapters.SongsAdapter;
import com.example.collageify.databinding.FragmentCollageBinding;
import com.example.collageify.models.Song;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollageFragment extends Fragment {

    private SongService songService;
    private Song song;
    private ArrayList<Song> topTracks;
    private FragmentCollageBinding binding;
    private SongsAdapter adapter;

    public CollageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCollageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songService = new SongService(getContext().getApplicationContext());
        topTracks = new ArrayList<>();
        adapter = new SongsAdapter(getContext(), topTracks);
        RecyclerView rvSongs = view.findViewById(R.id.rvSongs);
        rvSongs.setAdapter(adapter);
        rvSongs.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        getTracks();
    }

    private void getTracks() {
        songService.getTopTracks(() -> {
            topTracks.addAll(songService.getSongs());
            adapter.notifyDataSetChanged();
            updateSong();
        });
    }

    private void updateSong() {
        if (topTracks.size() > 0) {
            song = topTracks.get(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}