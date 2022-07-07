package com.example.collageify.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.collageify.fragments.AlbumMoreFragment;
import com.example.collageify.fragments.AlbumSongsFragment;
import com.example.collageify.fragments.AlbumStatsFragment;
import com.example.collageify.models.Album;

public class AlbumDetailTabsAdapter extends FragmentStateAdapter {

    public static String[] tabs = {"songs", "stats", "more"};
    private int numTabs;
    private Album album;

    public AlbumDetailTabsAdapter(@NonNull Fragment fragment, int numTabs, Album album) {
        super(fragment);
        this.numTabs = numTabs;
        this.album = album;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AlbumSongsFragment(album);
            case 1:
                return new AlbumStatsFragment();
            case 2:
                return new AlbumMoreFragment();
            default: // should not reach here
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numTabs;
    }
}
