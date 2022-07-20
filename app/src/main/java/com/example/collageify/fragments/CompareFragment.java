package com.example.collageify.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.activities.MainActivity;
import com.example.collageify.databinding.FragmentCompareBinding;
import com.example.collageify.models.Post;
import com.example.collageify.services.GenreService;
import com.example.collageify.utils.CalculatePostSimilarityUtil;
import com.parse.ParseFile;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Fragment} used to compare two collages
 */
public class CompareFragment extends Fragment {

    private FragmentCompareBinding binding;
    private Post postA;
    private Post postB;
    public static final String REQUEST_KEY = "selectPost";
    private double similarity;

    public CompareFragment(Post postA) {
        this.postA = postA;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getChildFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            postB = result.getParcelable(Post.class.getSimpleName());
            showPostImage(postB, binding.ivPostBImage);
            binding.btnCalculate.setVisibility(View.VISIBLE);
        });
    }

    private void showPostImage(Post post, ImageView imageView) {
        ParseFile image = post.getImage();
        Glide.with(getContext()).load(image.getUrl())
                .placeholder(R.drawable.image_placeholder)
                .into(imageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompareBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showPostImage(postA, binding.ivPostAImage);
        binding.btnSelectPost.setOnClickListener(v -> openSelectPostDialog());
        binding.btnCalculate.setOnClickListener(v -> {
            getSimilarity();
        });
        binding.ibBack.setOnClickListener(v -> ((MainActivity) getContext()).goToFeedFrag());
    }

    private void openSelectPostDialog() {
        new SelectPostDialogFragment().show(getChildFragmentManager(), SelectPostDialogFragment.TAG);
    }

    /**
     * Get and display similarity between postA and postB based on collage color, albums, artists,
     * and top genres
     */
    private void getSimilarity() {
        GenreService genreService = new GenreService(getContext().getApplicationContext());
        Map<String, Integer> genresA = new HashMap<>();
        Map<String, Integer> genresB = new HashMap<>();
        // get genres from top 3 albums in each post by pulling from Spotify API endpoint for
        // the album artists (genres are only linked to artists, not albums)
        genreService.get(postA.getArtistIds().get(0), () -> {
            genresA.putAll(CalculatePostSimilarityUtil.getFreqs(genreService.getGenres()));
            genreService.get(postA.getArtistIds().get(1), () -> {
                genresA.putAll(CalculatePostSimilarityUtil.getFreqs(genreService.getGenres()));
                genreService.get(postA.getArtistIds().get(2), () -> {
                    genresA.putAll(CalculatePostSimilarityUtil.getFreqs(genreService.getGenres()));
                    genreService.get(postB.getArtistIds().get(0), () -> {
                        genresB.putAll(CalculatePostSimilarityUtil.getFreqs(genreService.getGenres()));
                        genreService.get(postB.getArtistIds().get(1), () -> {
                            genresB.putAll(CalculatePostSimilarityUtil.getFreqs(genreService.getGenres()));
                            genreService.get(postB.getArtistIds().get(2), () -> {
                                genresB.putAll(CalculatePostSimilarityUtil.getFreqs(genreService.getGenres()));

                                // calculate and show percentage
                                similarity = CalculatePostSimilarityUtil.calculateSimilarity(postA, postB, genresA, genresB);
                                binding.tvPercentage.setText(String.format("%.2f%%", similarity));
                            });
                        });
                    });
                });
            });
        });
    }
}