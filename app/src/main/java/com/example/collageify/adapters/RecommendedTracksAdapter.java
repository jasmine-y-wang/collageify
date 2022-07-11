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
import com.example.collageify.models.Song;

import java.util.List;

public class RecommendedTracksAdapter extends RecyclerView.Adapter<RecommendedTracksAdapter.ViewHolder> {

    private final Context context;
    private final List<Song> recommendedTracks;

    public RecommendedTracksAdapter(Context context, List<Song> recommendedTracks) {
        this.context = context;
        this.recommendedTracks = recommendedTracks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommended_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = recommendedTracks.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return recommendedTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTrackName;
        private final TextView tvArtistName;
        private final ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrackName = itemView.findViewById(R.id.tvTrackName);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        public void bind(Song song) {
            tvTrackName.setText(song.getName());
            tvArtistName.setText(song.getArtistName());
            Glide.with(context).load(song.getAlbumImageUrl()).into(ivImage);
        }
    }
}
