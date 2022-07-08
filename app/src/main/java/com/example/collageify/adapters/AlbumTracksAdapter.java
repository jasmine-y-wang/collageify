package com.example.collageify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageify.databinding.ItemSongBinding;
import com.example.collageify.models.Song;

import java.util.List;

public class AlbumTracksAdapter extends RecyclerView.Adapter<AlbumTracksAdapter.ViewHolder> {

    private final Context context;
    private final List<String> topTrackIds;
    private final List<Song> tracks;
    private ItemSongBinding binding;

    public AlbumTracksAdapter(Context context, List<Song> tracks, List<String> topTrackIds) {
        this.tracks = tracks;
        this.context = context;
        this.topTrackIds = topTrackIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemSongBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
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

        public ViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
        }

        public void bind(Song song) {
            binding.tvTitle.setText(song.getName());
            binding.tvNumber.setText(String.valueOf(song.getTrackNumber()));
            binding.tvDuration.setText(song.getDuration());
            // show star if song is a top track
            if (topTrackIds.contains(song.getId())) {
                binding.ivStar.setVisibility(View.VISIBLE);
            } else {
                binding.ivStar.setVisibility(View.GONE);
            }
        }
    }
}
