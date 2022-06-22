package com.example.collageify;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.collageify.models.User;
import com.google.gson.Gson;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    public static final String ENDPOINT = "https://api.spotify.com/v1/me";
    private SharedPreferences mSharedPreferences;
    private RequestQueue mQueue;
    private User user;

    public UserService(RequestQueue queue, SharedPreferences sharedPreferences) {
        mQueue = queue;
        mSharedPreferences = sharedPreferences;
    }

    public User getUser() {
        return user;
    }

    public void get(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ENDPOINT, null, response -> {
            Gson gson = new Gson();
            User currUser = (User) ParseUser.getCurrentUser();
            currUser.setSpotifyInfo(response);
            user = gson.fromJson(response.toString(), User.class);
            callBack.onSuccess();
        }, error -> get(() -> {
        })) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = mSharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        mQueue.add(jsonObjectRequest);
    }
}
