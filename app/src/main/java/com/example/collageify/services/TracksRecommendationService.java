package com.example.collageify.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.collageify.interfaces.VolleyCallBack;
import com.example.collageify.models.Album;
import com.example.collageify.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TracksRecommendationService {

    private final SharedPreferences sharedPreferences;
    private final RequestQueue queue;
    private final List<Song> tracks = new ArrayList<>();
    public static final String TAG = "TracksRecommendationService";

    public TracksRecommendationService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public List<Song> getTracks() { return tracks; }

    public void get(Album album, final VolleyCallBack callBack) {
        tracks.clear();
        // send artist and user's top tracks as seeds
        String seedArtist = album.getArtist().getId();
        String seedTracks = getSeedTracks(album);
        String endpointFormat = "https://api.spotify.com/v1/recommendations?seed_artists=%s&seed_tracks=%s";
        String endpoint = String.format(endpointFormat, seedArtist, seedTracks);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(endpoint, null, response -> {
            JSONArray jsonArray = response.optJSONArray("tracks");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Song song = Song.fromJsonForRecommendedList(object);
                    tracks.add(song);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            callBack.onSuccess();
        }, error -> Log.e(TAG, "an error occurred when fetching album tracks", error)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    @NonNull
    // get up to MAX_TRACKS seed track ids based on user's top tracks from an album
    private String getSeedTracks(Album album) {
        final int MAX_TRACKS = 4;
        StringBuilder sb = new StringBuilder();
        sb.append(album.getTopSongIds().get(0));
        int i = 1;
        while (i < MAX_TRACKS && i < album.getTopSongIds().size()) {
            sb.append(",").append(album.getTopSongIds().get(i));
            i++;
        }
        String seedTracks = sb.toString();
        return seedTracks;
    }
}
