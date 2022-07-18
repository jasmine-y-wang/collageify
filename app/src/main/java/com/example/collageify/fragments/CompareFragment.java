package com.example.collageify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.databinding.FragmentCompareBinding;
import com.example.collageify.models.Post;
import com.example.collageify.utils.CalculatePostSimilarityUtil;
import com.parse.ParseFile;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompareFragment extends Fragment {

    private FragmentCompareBinding binding;
    private Post postA;
    private Post postB;
    public static final String REQUEST_KEY = "selectPost";

    public CompareFragment(Post postA) {
        this.postA = postA;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getChildFragmentManager().setFragmentResultListener(REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                postB = result.getParcelable(Post.class.getSimpleName());
                showPostImage(postB, binding.ivPostBImage);
                binding.btnCalculate.setVisibility(View.VISIBLE);
            }
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
        // Inflate the layout for this fragment
        binding = FragmentCompareBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showPostImage(postA, binding.ivPostAImage);
        binding.btnSelectPost.setOnClickListener(v -> openSelectPostDialog());
        binding.btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double percentage = CalculatePostSimilarityUtil.calculateSimilarity(postA, postB);
                binding.tvPercentage.setText(String.format("%.2f%%", percentage));
            }
        });
    }

    private void openSelectPostDialog() {
        new SelectPostDialogFragment().show(getChildFragmentManager(), SelectPostDialogFragment.TAG);
    }
}