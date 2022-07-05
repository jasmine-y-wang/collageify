package com.example.collageify.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.activities.LoginActivity;
import com.example.collageify.adapters.PostsAdapter;
import com.example.collageify.adapters.ProfilePostsAdapter;
import com.example.collageify.databinding.FragmentProfileBinding;
import com.example.collageify.models.Post;
import com.example.collageify.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    public static final String TAG = "ProfileFragment";
    public User user;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;

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
        String spotifyName = user.getSpotifyDisplayName();
        binding.tvSpotifyId.setText(String.format("%s on Spotify", spotifyName));
        // initialize array that will hold posts and create PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(getContext(), allPosts);

        String profilePicUrl = user.getPfpUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Glide.with(this).load(profilePicUrl).circleCrop().into(binding.ivProfilePic);
        } else {
            Glide.with(this).load(R.drawable.profile_placeholder).circleCrop().into(binding.ivProfilePic);
        }

        RecyclerView rvPosts = view.findViewById(R.id.rvPosts);
        // set adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set layout manager
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        // query posts
        queryPosts();

        binding.btnLogout.setOnClickListener(v -> {
            ParseUser.logOut(); // log out Parse user
            AuthorizationClient.clearCookies(getContext()); // log out Spotify user
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void queryPosts() {
        // specify what type of data we want to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setLimit(20);
        // order posts by creation data (newest first)
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        // start an asynchronous call for posts
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "issue with getting posts", e);
                return;
            }
            // save received posts to list and notify adapter of data
            allPosts.addAll(posts);
            adapter.notifyDataSetChanged();
        });
    }
}