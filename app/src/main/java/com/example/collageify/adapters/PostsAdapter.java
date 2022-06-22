package com.example.collageify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.models.Post;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
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

        private TextView tvUsername;
        private TextView tvDescription;
        private ImageView ivImage;
        private TextView tvTimestamp;
        private ImageButton ibLike;
        private TextView tvLikes;
        private ImageView ivPfp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ibLike = itemView.findViewById(R.id.ibLike);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivPfp = itemView.findViewById(R.id.ivPfp);
        }

        public void bind(Post post) {
//            User poster = (User) post.getUser();
            tvDescription.setText(post.getCaption());
//            tvUsername.setText(poster.getUsername());
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl())
                        .placeholder(R.drawable.image_placeholder)
                        .into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }
            // calculate relative date
            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvTimestamp.setText(timeAgo);

            // set profile picture in posts
//            ParseFile profilePic = poster.getPfp();
//            if (profilePic != null) {
//                Glide.with(context).load(profilePic.getUrl()).circleCrop().into(ivPfp);
//            } else {
//                Glide.with(context).load(R.drawable.profile_placeholder).circleCrop().into(ivPfp);
//            }

            if (post.isLikedByCurrentUser()) {
                ibLike.setBackgroundResource(R.drawable.ic_ufi_heart_active);
            } else {
                ibLike.setBackgroundResource(R.drawable.ufi_heart);
            }

            tvLikes.setText(post.getLikesCount());

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.isLikedByCurrentUser()) {
                        // unlike
                        post.unlike();
                        ibLike.setBackgroundResource(R.drawable.ufi_heart);
                    } else {
                        // like
                        post.like();
                        ibLike.setBackgroundResource(R.drawable.ic_ufi_heart_active);
                    }
                    tvLikes.setText(post.getLikesCount());
                }
            });

//            ivPfp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("PostAdapter", "go to profile frag");
//                    ((MainActivity) context).goToProfileFrag(post.getUser());
//                }
//            });
            }
        }
}
