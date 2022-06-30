package com.example.collageify.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

public class CollageImageUtil {

    private static final String TAG = "CollageImageUtil";

    public static void shareCollageImage(Context context, View collageView) {
        File collageFile = getCollageFile(collageView);
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        Uri uri = FileProvider.getUriForFile(context, "com.example.fileprovider", collageFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        Intent chooser = Intent.createChooser(shareIntent, "Share image using");
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        context.startActivity(chooser);
    }

    public static void downloadCollageImage(View collageView) {
        Bitmap collageScreenshot = getScreenShot(collageView);
        try {
            saveImage(collageScreenshot, collageView.getContext(), "collageify");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static File getCollageFile(View collageView) {
        Bitmap collageScreenshot = getScreenShot(collageView);
        File collageFile = null;
        try {
            collageFile = bitmapToFile(collageScreenshot, collageView.getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return collageFile;
    }

    // takes a screenshot of a view
    // code from https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-on-android
    public static Bitmap getScreenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // converts a Bitmap to a File
    public static File bitmapToFile(Bitmap bitmap, Context context) throws IOException {
        // configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // compress the image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        // create a new file for the resized bitmap
        File file = getPhotoFileUri("collage.jpg", context);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        // write the bytes of the bitmap to file
        fos.write(bytes.toByteArray());
        fos.close();
        return file;
    }

    // returns the File for a photo stored on disk given the fileName
    public static File getPhotoFileUri(String fileName, Context context) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    // save image to phone's Gallery
    // code from https://stackoverflow.com/questions/36624756/how-to-save-bitmap-to-android-gallery
    private static void saveImage(Bitmap bitmap, Context context, String folderName) throws FileNotFoundException {
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

    private static void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
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
