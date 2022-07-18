package com.example.collageify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageify.interfaces.OnPostSelectedListener;
import com.example.collageify.R;
import com.example.collageify.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class SelectPostsAdapter extends RecyclerView.Adapter<SelectPostsAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> posts;
    private OnPostSelectedListener listener;
    private int selectedIndex;

    public SelectPostsAdapter(Context context, List<Post> posts, OnPostSelectedListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
        selectedIndex = RecyclerView.NO_POSITION;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        public void bind(Post post, int position) {
            ParseFile image = post.getImage();
            Glide.with(context).load(image.getUrl()).into(ivImage);
            itemView.setSelected(selectedIndex == position);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(selectedIndex);
            selectedIndex = getAdapterPosition();
            notifyItemChanged(selectedIndex);
            listener.onPostSelected();
        }
    }
}
