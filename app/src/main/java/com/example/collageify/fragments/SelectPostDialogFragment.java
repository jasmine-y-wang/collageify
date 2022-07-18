package com.example.collageify.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.collageify.adapters.SelectPostsAdapter;
import com.example.collageify.databinding.FragmentSelectPostDialogBinding;
import com.example.collageify.models.Post;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SelectPostDialogFragment extends DialogFragment {

    public static final String TAG = "SelectPostDialogFragment";
    private FragmentSelectPostDialogBinding binding;
    protected SelectPostsAdapter adapter;
    protected List<Post> allPosts;

    public SelectPostDialogFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSelectPostDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnChoose.setEnabled(false);
        allPosts = new ArrayList<>();
        adapter = new SelectPostsAdapter(getContext(), allPosts, () -> binding.btnChoose.setEnabled(true));
        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(new GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false));
        queryPosts();

        binding.btnChoose.setOnClickListener(v -> {
            Post selectedPost = allPosts.get(adapter.getSelectedIndex());
            // send post to CompareFragment
            Bundle result = new Bundle();
            result.putParcelable(Post.class.getSimpleName(), selectedPost);
            getParentFragmentManager().setFragmentResult(CompareFragment.REQUEST_KEY, result);
            dismiss();
        });
    }

    protected void queryPosts() {
        // specify what type of data we want to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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