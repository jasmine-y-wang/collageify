package com.example.collageify.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.collageify.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalculatePostSimilarityUtil {

    /**
     * Calculate similarity between postA and postB based on albums, color, and artists
     * within the posts' collages
     */
    public static double calculateSimilarity(Post postA, Post postB) {
        // padding added to reduce effect of differences in other factors
        // otherwise difference in overall similarity is very drastic
        final int PADDING = 40;
        double similarity = PADDING;
        final int ALBUM_WEIGHT = 15;
        final int COLOR_WEIGHT = 20;
        final int ARTIST_WEIGHT = 25;
        similarity += COLOR_WEIGHT * calculateColorSimilarity(postA.getImage(), postB.getImage());
        similarity += ARTIST_WEIGHT * calculateCosineSimilarity(getFreqs(postA.getArtistIds()), getFreqs(postB.getArtistIds()));
        similarity += ALBUM_WEIGHT * calculateJaccardSimilarity(postA.getAlbumIds(), postB.getAlbumIds());
        return similarity;
    }

    /**
     * Calculate the Jaccard similarity between listA and listB
     *
     * The Jaccard similarity is a ratio of common values used as a standard way of comparing
     * datasets, as suggested in this guide:
     * https://developers.google.com/machine-learning/clustering/similarity/manual-similarity
     */
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

        // Jaccard index = intersection / union
        return (double) intersection.size() / union.size();
    }

    /**
     * Calculate color similarity by finding Euclidean color distance as a percentage of
     * total color space
     */
    private static double calculateColorSimilarity(ParseFile imageA, ParseFile imageB) {
        int avgColorA = calculateColorAvg(imageA);
        int avgColorB = calculateColorAvg(imageB);

        // find Euclidean distance between colors
        double redDiffSquared = Math.pow(Color.red(avgColorA) - Color.red(avgColorB), 2);
        double greenDiffSquared = Math.pow(Color.green(avgColorA) - Color.green(avgColorB), 2);
        double blueDiffSquared = Math.pow(Color.blue(avgColorA) - Color.blue(avgColorB), 2);
        double colorDiff = Math.sqrt(redDiffSquared + greenDiffSquared + blueDiffSquared);

        // 255^2 multiplied by 3 for the 3 dimensions: R, G, B
        double totalColorSpace = Math.sqrt(3 * Math.pow(255, 2));

        // subtract difference from 1 to get similarity
        return 1 - colorDiff / totalColorSpace;
    }

    /**
     * Find average color in image by iterating through pixels and averaging color values
     *
     * According to some articles, it is better to calculate color averages by finding the average
     * of squares of the RGB color components and taking the square root at the end rather than
     * just summing the values and dividing by number of pictures
     * https://sighack.com/post/averaging-rgb-colors-the-right-way
     */
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
                    // get RGB values from int color value and sum their squares
                    red += Math.pow((c >> 16) & 0xff, 2);
                    green += Math.pow((c >> 8) & 0xff, 2);
                    blue += Math.pow(c & 0xff, 2);
                }
            }
            // take square root of color averages
            color = Color.argb((float) 1.0, // full alpha value
                    (float) Math.sqrt(red / pixelCount),
                    (float) Math.sqrt(green / pixelCount),
                    (float) Math.sqrt(blue / pixelCount));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return color;
    }

    /**
     * Compare two frequency maps using the cosine similarity, which is a metric used to measure
     * how similar two sequences of numbers are
     *
     * This comparison is useful for data sets of different sizes and repeated items, which is
     * especially beneficial for collages with repeated artists
     * https://stackoverflow.com/questions/14720324/compute-the-similarity-between-two-lists
     */
    private static double calculateCosineSimilarity(Map<String, Integer> mapA, Map<String, Integer> mapB) {
        Set<String> union = new HashSet<>();
        union.addAll(mapA.keySet());
        union.addAll(mapB.keySet());

        // get dot product and magnitudes of both sets of genres
        double dotProduct = 0;
        double magnitudeA = 0;
        double magnitudeB = 0;
        for (String genre : union) {
            dotProduct += mapA.getOrDefault(genre, 0) * mapB.getOrDefault(genre, 0);
            magnitudeA += Math.pow(mapA.getOrDefault(genre, 0), 2);
            magnitudeB += Math.pow(mapB.getOrDefault(genre, 0), 2);
        }
        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);
        return dotProduct / (magnitudeA * magnitudeB);
    }

    /** Get map of frequencies based on list of strings */
    private static Map<String, Integer> getFreqs(List<String> strings) {
        Map<String, Integer> freqs = new HashMap<>();
        for (String s : strings) {
            freqs.put(s, freqs.getOrDefault(s, 0) + 1);
        }
        return freqs;
    }


}
