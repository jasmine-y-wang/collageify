package com.example.collageify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> posts;
    private View view;

    public ProfilePostsAdapter(Context context, List<Post> posts) {
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
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivImage;
        private final TextView tvDate;
        private final TextView tvTimeframe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = view.findViewById(R.id.ivImage);
            tvDate = view.findViewById(R.id.tvDate);
            tvTimeframe = view.findViewById(R.id.tvTimeframe);
        }

        public void bind(Post post) {
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }
            tvTimeframe.setText(post.getTimeframe());
            String date = Post.formatDate(post.getCreatedAt());
            tvDate.setText(date);
        }



    }
}
