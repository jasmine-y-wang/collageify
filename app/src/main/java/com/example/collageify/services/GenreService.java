package com.example.collageify.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.collageify.interfaces.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GenreService {

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    public static final String TAG = "GenreService";
    private List<String> genres;

    public GenreService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        genres = new LinkedList<>();
    }

    public List<String> getGenres() {
        return genres;
    }

    public void get(String artistId, final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/artists/" + artistId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(endpoint, null, response -> {
            try {
                JSONArray genresArray = response.optJSONArray("genres");
                if (genresArray != null) {
                    for (int i = 0; i < genresArray.length(); i++) {
                        genres.add(genresArray.getString(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callBack.onSuccess();
        }, error -> Log.e(TAG, "an error occurred when fetching genres", error)) {
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
