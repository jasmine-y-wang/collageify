package com.example.collageify.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageify.R;
import com.example.collageify.activities.MainActivity;
import com.example.collageify.adapters.AlbumsAdapter;
import com.example.collageify.databinding.FragmentCollageBinding;
import com.example.collageify.models.Album;
import com.example.collageify.models.Post;
import com.example.collageify.services.SongService;
import com.example.collageify.utils.CollageImageUtil;
import com.example.collageify.utils.TopAlbumsUtil;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollageFragment extends Fragment {

    private SongService songService;
    private List<Album> topAlbums;
    private FragmentCollageBinding binding;
    private AlbumsAdapter albumsAdapter;
    public static final String TAG = "CollageFragment";
    private MainActivity mainActivity;

    public CollageFragment() {
        // Required empty public constructor
    }

    public CollageFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCollageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songService = new SongService(getContext().getApplicationContext());
        topAlbums = new ArrayList<>();
        albumsAdapter = new AlbumsAdapter(getContext(), topAlbums);
        RecyclerView rvSongs = view.findViewById(R.id.rvCollage);
        rvSongs.setAdapter(albumsAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        rvSongs.setLayoutManager(gridLayoutManager);

        // set up dropdowns
        ArrayAdapter<CharSequence> dimensionAdapter = ArrayAdapter.createFromResource(getContext(), R.array.dimensions, android.R.layout.simple_spinner_item);
        dimensionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnDimensions.setAdapter(dimensionAdapter);
        binding.spnDimensions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spanCount = position + 3; // 0 -> 3, 1 -> 4, 2 -> 5
                gridLayoutManager.setSpanCount(spanCount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gridLayoutManager.setSpanCount(3);
            }
        });
        ArrayAdapter<CharSequence> timeframeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.timeframes, android.R.layout.simple_spinner_item);
        timeframeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnTimeframe.setAdapter(timeframeAdapter);
        binding.spnTimeframe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeframe;
                if (position == 0) {
                    timeframe = "short_term";
                } else if (position == 1) {
                    timeframe = "medium_term";
                } else {
                    timeframe = "long_term";
                }
                TopAlbumsUtil.getTopAlbums(timeframe, songService, topAlbums, albumsAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TopAlbumsUtil.getTopAlbums("short_term", songService, topAlbums, albumsAdapter);
            }
        });

        // post button
        binding.btnPost.setOnClickListener(v -> {
            File collageFile = CollageImageUtil.getCollageFile(binding.rvCollage);
            String caption = binding.etCaption.getText().toString();
            savePost(caption, ParseUser.getCurrentUser(), collageFile, binding.spnTimeframe.getSelectedItemPosition());
        });

        // share button
        binding.ibShare.setOnClickListener(v -> CollageImageUtil.shareCollageImage(getContext(), binding.rvCollage));

        // download button
        binding.ibDownload.setOnClickListener(v -> CollageImageUtil.downloadCollageImage(binding.rvCollage));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // save post to Parse database
    private void savePost(String description, ParseUser user, File photoFile, int timeframeIndex) {
        Post post = new Post();
        post.setCaption(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(user);
        post.setTimeframe(timeframeIndex);
        post.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "error while saving", e);
                Toast.makeText(getContext(), "error while saving :(", Toast.LENGTH_SHORT).show();
            }
            binding.etCaption.setText("");
            confetti();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> mainActivity.goToFeedFrag(), 3000);
        });
    }

    private void confetti() {
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);
        Party party = new PartyFactory(emitterConfig)
                .spread(360)
                .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(new Position.Relative(0.5, 0.3))
                .build();
        binding.konfettiView.start(party);
    }

}