package com.example.collageify.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collageify.R;
import com.example.collageify.models.Album;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumMoreFragment extends Fragment {

    public AlbumMoreFragment(Album album) {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album_more, container, false);
    }
}