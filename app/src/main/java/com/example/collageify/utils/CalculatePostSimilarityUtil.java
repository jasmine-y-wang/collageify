package com.example.collageify.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.collageify.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalculatePostSimilarityUtil {

    public static double calculateSimilarity(Post postA, Post postB) {
        final int PADDING = 20;
        double similarity = PADDING;
        final int ALBUM_WEIGHT = 15;
        final int COLOR_WEIGHT = 15;
        final int GENRE_WEIGHT = 30;
        final int ARTIST_WEIGHT = 20;
        similarity += COLOR_WEIGHT * calculateColorSimilarity(postA.getImage(), postB.getImage());
        similarity += ARTIST_WEIGHT * calculateJaccardSimilarity(postA.getArtistIds(), postB.getArtistIds());
        similarity += ALBUM_WEIGHT * calculateJaccardSimilarity(postA.getAlbumIds(), postB.getAlbumIds());
        similarity += GENRE_WEIGHT;
        return similarity;
    }

    // use the Jaccard similarity to calculate the similarity between listA and listB
    private static double calculateJaccardSimilarity(List<String> listA, List<String> listB) {
        // find union of A and B
        Set<String> union = new HashSet<>();
        union.addAll(listA);
        union.addAll(listB);

        // find intersection of A and B
        Set<String> intersection = new HashSet<>();
        for (String item : listA) {
            if (listB.contains(item)) {
                intersection.add(item);
            }
        }
        return (double) intersection.size() / union.size();
    }

    // calculate color similarity by finding color distance as percentage of total color space
    private static double calculateColorSimilarity(ParseFile imageA, ParseFile imageB) {
        int avgColorA = calculateColorAvg(imageA);
        int avgColorB = calculateColorAvg(imageB);
        double redDiffSquared = Math.pow(Color.red(avgColorA) - Color.red(avgColorB), 2);
        double greenDiffSquared = Math.pow(Color.green(avgColorA) - Color.green(avgColorB), 2);
        double blueDiffSquared = Math.pow(Color.blue(avgColorA) - Color.blue(avgColorB), 2);
        double colorDiff = Math.sqrt(redDiffSquared + greenDiffSquared + blueDiffSquared);
        double totalColorSpace = Math.sqrt(3 * Math.pow(255, 2));
        return 1 - colorDiff / totalColorSpace;
    }

    // get average color in image
    private static int calculateColorAvg(ParseFile image) {
        int color = 0;
        try {
            byte[] data = image.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            long red = 0;
            long green = 0;
            long blue = 0;
            long pixelCount = 0;
            for (int x = 0; x < bitmap.getWidth(); x++) {
                for (int y = 0; y < bitmap.getHeight(); y++) {
                    int c = bitmap.getPixel(x, y);
                    pixelCount++;
                    // get RGB values from int color value
                    red += Math.pow((c >> 16) & 0xff, 2);
                    green += Math.pow((c >> 8) & 0xff, 2);
                    blue += Math.pow(c & 0xff, 2);
                }
            }
            color = Color.argb((float) 1.0, // full alpha value
                    (float) Math.sqrt(red / pixelCount),
                    (float) Math.sqrt(green / pixelCount),
                    (float) Math.sqrt(blue / pixelCount));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return color;
    }
}
