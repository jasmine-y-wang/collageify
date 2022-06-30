package com.example.collageify.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.collageify.VolleyCallBack;
import com.example.collageify.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumTracksService {

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    public static final String TAG = "AlbumTracksService";
    private List<Song> albumTracks = new ArrayList<>();
    private String endpointFormat = "https://api.spotify.com/v1/albums/%s/tracks";

    public AlbumTracksService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public List<Song> getAlbumTracks() { return albumTracks; }

    public void get(String albumId, final VolleyCallBack callBack) {
        String endpoint = String.format(endpointFormat, albumId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(endpoint, null, response -> {
            JSONArray jsonArray = response.optJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Song song = Song.fromJsonForTracksList(object);
                    albumTracks.add(song);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            callBack.onSuccess();
        }, error -> Log.e(TAG, "an error occurred when fetching album tracks", error)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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
