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

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.activities.LoginActivity;
import com.example.collageify.databinding.FragmentProfileBinding;
import com.example.collageify.models.User;
import com.parse.ParseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    public static final String TAG = "ProfileFragment";
    public User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(ParseUser user) {
        this.user = (User) user;
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
        binding.tvProfileUsername.setText(user.getUsername());
        binding.tvSpotifyId.setText(String.format("%s on Spotify", user.getSpotifyId()));

        String profilePicUrl = user.getPfpUrl();
        if (profilePicUrl != null) {
            Glide.with(this).load(profilePicUrl).circleCrop().into(binding.ivProfilePic);
        } else {
            Glide.with(this).load(R.drawable.profile_placeholder).circleCrop().into(binding.ivProfilePic);
        }

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "logout");
                ParseUser.logOut(); // log out Parse user
//                AuthorizationClient.clearCookies(getContext()); // log out Spotify user
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}