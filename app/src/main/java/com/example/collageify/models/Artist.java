package com.example.collageify.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Artist {

    private String id;
    private String name;
    private String imageUrl;

    public static Artist fromJson(JSONObject jsonObject) throws JSONException {
        Artist artist = new Artist();
        artist.id = jsonObject.getString("id");
        artist.name = jsonObject.getString("name");
        artist.imageUrl = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
        return artist;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
