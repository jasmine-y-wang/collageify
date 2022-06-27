package com.example.collageify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.collageify.ArtistService;
import com.example.collageify.R;
import com.example.collageify.VolleyCallBack;
import com.example.collageify.databinding.FragmentCollageBinding;
import com.example.collageify.databinding.FragmentDetailBinding;
import com.example.collageify.models.Album;
import com.example.collageify.models.Artist;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private Album album;
    private Artist albumArtist;

    public DetailFragment() {
        // Required empty public constructor
    }

    public DetailFragment(Album album) {
        this.album = album;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvName.setText(album.getName());
        binding.tvArtist.setText(album.getArtistName());
        Glide.with(getContext()).load(album.getImageUrl()).into(binding.ivAlbumImage);
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