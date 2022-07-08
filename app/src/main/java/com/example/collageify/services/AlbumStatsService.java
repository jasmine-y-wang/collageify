package com.example.collageify.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.collageify.VolleyCallBack;
import com.example.collageify.models.Album;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AlbumStatsService {

    private final SharedPreferences sharedPreferences;
    private final RequestQueue queue;
    public static final String TAG = "AlbumTracksService";
    private HashMap<String, Double> stats = new HashMap<>();

    public AlbumStatsService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public HashMap<String, Double> getStats() { return stats; }

    public void get(Album album, final VolleyCallBack callBack) {
        int numTracks = album.getAllSongIds().size();
        final double[] danceability = {0.0};
        final double[] valence = {0.0};
        final double[] energy = {0.0};
        final double[] tempo = {0.0};
        String albumTrackIds = String.join(",", album.getAllSongIds());
        String endpoint = "https://api.spotify.com/v1/audio-features?ids=" + albumTrackIds;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(endpoint, null, response -> {
            JSONArray jsonArray = response.optJSONArray("audio_features");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    danceability[0] += object.getDouble("danceability");
                    valence[0] += object.getDouble("valence");
                    energy[0] += object.getDouble("energy");
                    tempo[0] += object.getDouble("tempo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            danceability[0] /= numTracks;
            stats.put("danceability", danceability[0]);
            valence[0] /= numTracks;
            stats.put("valence", valence[0]);
            energy[0] /= numTracks;
            stats.put("energy", energy[0]);
            tempo[0] /= numTracks;
            stats.put("tempo", tempo[0]);
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

}
