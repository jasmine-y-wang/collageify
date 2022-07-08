package com.example.collageify.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.collageify.activities.MainActivity;
import com.example.collageify.adapters.AlbumDetailTabsAdapter;
import com.example.collageify.databinding.FragmentAlbumDetailBinding;
import com.example.collageify.models.Album;
import com.example.collageify.models.Artist;
import com.example.collageify.services.ArtistService;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetailFragment extends Fragment {

    private FragmentAlbumDetailBinding binding;
    private Album album;
    private Artist albumArtist;
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

        binding.ibBack.setOnClickListener(v -> ((MainActivity) getContext()).goToCollageFrag());

        AlbumDetailTabsAdapter albumDetailTabsAdapter = new AlbumDetailTabsAdapter(this, binding.tabLayout.getTabCount(), album);
        binding.pager.setAdapter(albumDetailTabsAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.pager,
                (tab, position) -> tab.setText(AlbumDetailTabsAdapter.tabs[position])).attach();
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