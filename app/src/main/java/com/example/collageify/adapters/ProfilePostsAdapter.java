package com.example.collageify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class ProfilePostsAdapter extends PostsAdapter {

    private final Context context;
    private final List<Post> posts;
    private View view;

    public ProfilePostsAdapter(Context context, List<Post> posts) {
        super(context, posts);
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        ParseFile image = post.getImage();
        ImageView ivImage = view.findViewById(R.id.ivImage);
        if (image != null) {
            Glide.with(context).load(image.getUrl()).into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }
    }
}
