package com.example.collageify.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.collageify.R;
import com.example.collageify.SongService;
import com.example.collageify.fragments.CollageFragment;
import com.example.collageify.fragments.FeedFragment;
import com.example.collageify.fragments.ProfileFragment;
import com.example.collageify.models.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.collageify.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment feedFragment = new FeedFragment();
        final Fragment collageFragment = new CollageFragment();
        final Fragment profileFragment = new ProfileFragment(ParseUser.getCurrentUser());

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
        // set default selection
        binding.bottomNavigation.setSelectedItemId(R.id.action_collage);
    }

}