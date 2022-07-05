package com.example.collageify.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.collageify.R;
import com.example.collageify.databinding.ActivityMainBinding;
import com.example.collageify.fragments.CollageFragment;
import com.example.collageify.fragments.DetailFragment;
import com.example.collageify.fragments.FeedFragment;
import com.example.collageify.fragments.ProfileFragment;
import com.example.collageify.models.Album;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment collageFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();
        final Fragment feedFragment = new FeedFragment();
        collageFragment = new CollageFragment(MainActivity.this);
        final Fragment profileFragment = new ProfileFragment(ParseUser.getCurrentUser());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_home:
                    fragment = feedFragment;
                    break;
                case R.id.action_collage:
                    fragment = collageFragment;
                    break;
                case R.id.action_profile:
                    fragment = profileFragment;
                    break;
                default:
                    fragment = collageFragment;
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return true;
        });
        // set default selection
        binding.bottomNavigation.setSelectedItemId(R.id.action_collage);
    }

    public void goToFeedFrag() {
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    public void goToDetailFrag(Album album) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(collageFragment);
        DetailFragment detailFragment = new DetailFragment(album);
        ft.add(R.id.flContainer, detailFragment);
        ft.addToBackStack("collage to detail");
        ft.show(detailFragment);
        ft.commit();
    }

    public void goToCollageFrag(Album album) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContainer, collageFragment);
        ft.commit();
    }

    public void goToProfileFrag(ParseUser user) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContainer, new ProfileFragment(user));
        ft.addToBackStack("profile");
        ft.commit();
    }
}