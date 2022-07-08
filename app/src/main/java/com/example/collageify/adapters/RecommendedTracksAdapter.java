package com.example.collageify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageify.databinding.ItemRecommendedTrackBinding;
import com.example.collageify.models.Song;

import java.util.List;

public class RecommendedTracksAdapter extends RecyclerView.Adapter<RecommendedTracksAdapter.ViewHolder> {

    private Context context;
    private List<Song> recommendedTracks;
    private ItemRecommendedTrackBinding binding;

    public RecommendedTracksAdapter(Context context, List<Song> recommendedTracks) {
        this.context = context;
        this.recommendedTracks = recommendedTracks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemRecommendedTrackBinding.inflate(LayoutInflater.from(context), parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
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

        public ViewHolder(@NonNull ItemRecommendedTrackBinding binding) {
            super(binding.getRoot());
        }

        public void bind(Song song) {
            binding.tvTrackName.setText(song.getName());
            binding.tvArtistName.setText(song.getArtistName());
            Glide.with(context).load(song.getAlbumImageUrl()).into(binding.ivImage);
        }
    }
}
