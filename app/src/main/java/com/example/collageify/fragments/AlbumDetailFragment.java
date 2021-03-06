package com.example.collageify.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.collageify.activities.MainActivity;
import com.example.collageify.adapters.AlbumDetailTabsAdapter;
import com.example.collageify.databinding.FragmentAlbumDetailBinding;
import com.example.collageify.models.Album;
import com.example.collageify.models.Artist;
import com.example.collageify.services.ArtistService;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A {@link Fragment} that shows details about an album
 */
public class AlbumDetailFragment extends Fragment {

    private FragmentAlbumDetailBinding binding;
    private Album album;
    public static final String TAG = "AlbumDetailFragment";

    public AlbumDetailFragment(Album album) {
        this.album = album;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAlbumDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArtistInfo();
        binding.tvName.setText(album.getName());
        Glide.with(getContext()).load(album.getImageUrl()).into(binding.ivAlbumImage);

        binding.ibBack.setOnClickListener(v -> ((MainActivity) getContext()).goToCollageFrag());

        AlbumDetailTabsAdapter albumDetailTabsAdapter = new AlbumDetailTabsAdapter(this, binding.tabLayout.getTabCount(), album);
        binding.pager.setAdapter(albumDetailTabsAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.pager,
                (tab, position) -> tab.setText(AlbumDetailTabsAdapter.tabs[position])).attach();
    }

    private void getArtistInfo() {
        ArtistService artistService = new ArtistService(getContext().getApplicationContext());
        artistService.get(album.getArtistHref(), () -> {
            Artist albumArtist = artistService.getArtist();
            binding.tvArtist.setText(albumArtist.getName());
            Glide.with(getContext()).load(albumArtist.getImageUrl()).circleCrop().into(binding.ivArtistImage);
            album.setArtist(albumArtist);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Find AlbumMoreFragment within ViewPager and call its showPlaying() method with
     * the trackUri
     */
    public void showPlaying(String trackUri) {
        String pageTag = "f" + binding.pager.getCurrentItem();
        Fragment page = getChildFragmentManager().findFragmentByTag(pageTag);
        if (page != null) {
            switch (pageTag) {
                case "f2":
                    AlbumMoreFragment albumMoreFragment = (AlbumMoreFragment) page;
                    albumMoreFragment.showPlaying(trackUri);
                    break;
            }
        }
    }
}