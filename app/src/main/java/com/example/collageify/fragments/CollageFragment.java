package com.example.collageify.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.collageify.R;
import com.example.collageify.services.SongService;
import com.example.collageify.activities.MainActivity;
import com.example.collageify.adapters.AlbumsAdapter;
import com.example.collageify.databinding.FragmentCollageBinding;
import com.example.collageify.models.Album;
import com.example.collageify.models.Post;
import com.example.collageify.models.Song;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CollageFragment extends Fragment {

    private SongService songService;
    private List<Album> topAlbums;
    private FragmentCollageBinding binding;
    private AlbumsAdapter albumsAdapter;
    public static final String TAG = "CollageFragment";
    private String photoFileName = "collage.jpg";
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
        RecyclerView rvSongs = view.findViewById(R.id.rvSongs);
        rvSongs.setAdapter(albumsAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        rvSongs.setLayoutManager(gridLayoutManager);

        // set up dropdowns
        ArrayAdapter<CharSequence> dimensionAdapter = ArrayAdapter.createFromResource(getContext(), R.array.dimensions, android.R.layout.simple_spinner_item);
        dimensionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
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
        timeframeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
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
                getTopAlbums(timeframe);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getTopAlbums("short_term");
            }
        });

        // post button
        binding.btnPost.setOnClickListener(v -> {
            File collageFile = getCollageFile();
            String caption = binding.etCaption.getText().toString();
            savePost(caption, ParseUser.getCurrentUser(), collageFile);
        });

        // share button
        binding.ibShare.setOnClickListener(v -> shareCollageImage());

        binding.ibDownload.setOnClickListener(v -> {
            Bitmap collageScreenshot = getScreenShot(binding.rvSongs);
            try {
                saveImage(collageScreenshot, getContext(), "collageify");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private void shareCollageImage() {
        File collageFile = getCollageFile();
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        Uri uri = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", collageFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        Intent chooser = Intent.createChooser(shareIntent, "Share image using");
        List<ResolveInfo> resInfoList = getContext().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivity(chooser);
    }

    @Nullable
    private File getCollageFile() {
        Bitmap collageScreenshot = getScreenShot(binding.rvSongs);
        File collageFile = null;
        try {
            collageFile = bitmapToFile(collageScreenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return collageFile;
    }

    private void getTopAlbums(String timeframe) {
        List<Song> tracks = new ArrayList<>();
        songService.getTopTracks(timeframe, () -> {
            tracks.addAll(songService.getSongs());
            getAlbumsFromTracks(tracks);
            albumsAdapter.notifyDataSetChanged();
        });
    }

    private void getAlbumsFromTracks(List<Song> songs) {
        HashMap<String, Album> albums = new HashMap<>();
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            String albumId = song.getAlbumId();
            Album album = albums.get(albumId);
            if (album != null) {
                albums.get(albumId).incrementSongCount();
            } else {
                albums.put(albumId, new Album(song.getAlbumData(), i));
            }
        }
        topAlbums.clear();
        topAlbums.addAll(albums.values());
        topAlbums.sort((a1, a2) -> {
            int compVal = a2.getSongCount() - a1.getSongCount();
            if (compVal == 0) {
                compVal = a1.getRanking() - a2.getRanking();
            }
            return compVal;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // takes a screenshot of a view
    // code from https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-on-android
    public Bitmap getScreenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // converts a Bitmap to a File
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

    // returns the File for a photo stored on disk given the fileName
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

    // save post to Parse database
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
            binding.etCaption.setText("");
            mainActivity.goToFeedFrag();
        });
    }

    // save image to phone's Gallery
    // code from https://stackoverflow.com/questions/36624756/how-to-save-bitmap-to-android-gallery
    private void saveImage(Bitmap bitmap, Context context, String folderName) throws FileNotFoundException {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName);
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                context.getContentResolver().update(uri, values, null, null);
                Toast.makeText(context, "collage saved to gallery!", Toast.LENGTH_SHORT).show();
            }
        } else {
            File dir = new File(context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"");
            // getExternalStorageDirectory is deprecated in API 29

            if (!dir.exists()) {
                dir.mkdirs();
            }

            java.util.Date date = new java.util.Date();
            File imageFile = new File(dir.getAbsolutePath()
                    + File.separator
                    + new Timestamp(date.getTime())
                    + "Image.jpg");

            saveImageToStream(bitmap, new FileOutputStream(imageFile));
            if (imageFile.getAbsolutePath() != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
                // .DATA is deprecated in API 29
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}