package com.example.collageify.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collageify.R;
import com.example.collageify.SongService;
import com.example.collageify.adapters.SongsAdapter;
import com.example.collageify.databinding.FragmentCollageBinding;
import com.example.collageify.models.Post;
import com.example.collageify.models.Song;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollageFragment extends Fragment {

    private SongService songService;
    private ArrayList<Song> topTracks;
    private FragmentCollageBinding binding;
    private SongsAdapter adapter;
    public static final String TAG = "CollageFragment";
    private String photoFileName = "collage.jpg";

    public CollageFragment() {
        // Required empty public constructor
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
        topTracks = new ArrayList<>();
        adapter = new SongsAdapter(getContext(), topTracks);
        RecyclerView rvSongs = view.findViewById(R.id.rvSongs);
        rvSongs.setAdapter(adapter);
        rvSongs.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        getTracks();
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap collageScreenshot = getScreenShot(binding.rvSongs);
                String caption = binding.etCaption.getText().toString();
                File collageFile = null;
                try {
                    collageFile = bitmapToFile(collageScreenshot);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                savePost(caption, ParseUser.getCurrentUser(), collageFile);
            }
        });
    }

    private void getTracks() {
        songService.getTopTracks(() -> {
            topTracks.addAll(songService.getSongs());
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // code from https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-on-android
    public Bitmap getScreenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        Log.e(TAG, "took screenshot");
        return bitmap;
    }


    public File bitmapToFile(Bitmap bitmap) throws IOException {
        // configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // compress the image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        // create a new file for the resized bitmap
        File file = getPhotoFileUri(photoFileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        // write the bytes of the bitmap to file
        fos.write(bytes.toByteArray());
        fos.close();
        return file;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void savePost(String description, ParseUser user, File photoFile) {
        Post post = new Post();
        post.setCaption(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(user);
        post.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "error while saving", e);
                Toast.makeText(getContext(), "error while saving :(", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "post save was successful");
            binding.etCaption.setText("");
        });
    }
}