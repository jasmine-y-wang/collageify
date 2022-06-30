package com.example.collageify.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_SPOTIFY_ID = "spotifyId";
    private static final String TAG = "User";
    public static final String KEY_PFP_URL = "profilePicUrl";

    public String getSpotifyId() {
        return getString(KEY_SPOTIFY_ID);
    }

    public void setSpotifyId(String id) {
        put(KEY_SPOTIFY_ID, id);
    }

    public String getPfpUrl() {
        return getString(KEY_PFP_URL);
    }

    public void setPfpUrl(String id) {
        put(KEY_PFP_URL, id);
    }

    public void setSpotifyInfo(JSONObject jsonObject) {
        try {
            setSpotifyId(jsonObject.getString("id"));
            JSONArray images = jsonObject.getJSONArray("images");
            if (images.length() > 0) {
                setPfpUrl(images.getJSONObject(0).getString("url"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "error while saving", e);
            }
        });
    }
}
