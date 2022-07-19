package com.example.collageify.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_SPOTIFY_ID = "spotifyId";
    private static final String TAG = "User";
    public static final String KEY_PFP_URL = "profilePicUrl";
    public static final String KEY_SPOTIFY_DISPLAY_NAME = "spotifyDisplayName";

    public void setSpotifyId(String id) {
        put(KEY_SPOTIFY_ID, id);
    }

    public String getPfpUrl() {
        return getString(KEY_PFP_URL);
    }

    public void setPfpUrl(String id) {
        put(KEY_PFP_URL, id);
    }

    public void setSpotifyDisplayName(String name) {
        put(KEY_SPOTIFY_DISPLAY_NAME, name);
    }

    public String getSpotifyDisplayName() {
        return getString(KEY_SPOTIFY_DISPLAY_NAME);
    }

    public void setSpotifyInfo(JSONObject jsonObject) {
        try {
            String spotifyId = jsonObject.getString("id");
            setSpotifyId(spotifyId);
            String spotifyName = jsonObject.getString("display_name");
            if (spotifyName != null) {
                setSpotifyDisplayName(spotifyName);
            } else {
                setSpotifyDisplayName(spotifyId);
            }
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
