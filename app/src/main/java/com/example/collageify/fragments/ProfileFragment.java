package com.example.collageify.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collageify.R;
import com.example.collageify.activities.LoginActivity;
import com.example.collageify.databinding.FragmentProfileBinding;
import com.parse.ParseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    public static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "logout");
                ParseUser.logOut(); // log out Parse user
                AuthorizationClient.clearCookies(getContext()); // log out Spotify user
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }
}