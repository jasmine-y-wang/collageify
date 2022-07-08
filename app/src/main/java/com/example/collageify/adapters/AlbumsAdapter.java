package com.example.collageify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.activities.MainActivity;
import com.example.collageify.databinding.ItemAlbumBinding;
import com.example.collageify.models.Album;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {

    private final Context context;
    private final List<Album> albums;
    public static final String TAG = "AlbumsAdapter";
    private ItemAlbumBinding binding;

    public AlbumsAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemAlbumBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.bind(album);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ViewHolder(@NonNull ItemAlbumBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Album album) {
            String imageUrl = album.getImageUrl();
            Glide.with(context).load(imageUrl).into(binding.ivImage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Album album = albums.get(position);
                ((MainActivity) context).goToAlbumDetailFrag(album);
            }
        }
    }
}
