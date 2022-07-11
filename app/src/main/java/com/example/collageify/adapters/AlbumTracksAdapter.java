package com.example.collageify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageify.R;
import com.example.collageify.models.Song;

import java.util.List;

public class AlbumTracksAdapter extends RecyclerView.Adapter<AlbumTracksAdapter.ViewHolder> {

    private final Context context;
    private final List<String> topTrackIds;
    private final List<Song> tracks;

    public AlbumTracksAdapter(Context context, List<Song> tracks, List<String> topTrackIds) {
        this.tracks = tracks;
        this.context = context;
        this.topTrackIds = topTrackIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = tracks.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvDuration;
        public TextView tvNumber;
        public ImageView ivStar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            ivStar = itemView.findViewById(R.id.ivStar);
        }

        public void bind(Song song) {
            tvTitle.setText(song.getName());
            tvNumber.setText(String.valueOf(song.getTrackNumber()));
            tvDuration.setText(song.getDuration());
            // show star if song is a top track
            if (topTrackIds.contains(song.getId())) {
                ivStar.setVisibility(View.VISIBLE);
            } else {
                ivStar.setVisibility(View.GONE);
            }
        }
    }
}
