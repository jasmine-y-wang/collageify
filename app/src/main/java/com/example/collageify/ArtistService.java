package com.example.collageify;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.collageify.models.Artist;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ArtistService {

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    public static final String TAG = "ArtistService";
    private Artist artist;

    public ArtistService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public Artist getArtist() { return artist; }

    public void get(String endpoint, final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(endpoint, null, response -> {
            try {
                artist = Artist.fromJson(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callBack.onSuccess();
        }, error -> Log.e(TAG, "an error occurred when fetching recent tracks", error)) {
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
