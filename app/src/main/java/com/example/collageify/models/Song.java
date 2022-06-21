package com.example.collageify.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Song {
    private String id;
    private String name;
    private String albumImageUrl;
    private String artist;

    public Song(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public Song() {}

    public String getName() {
        return name;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public String getArtist() {
        return artist;
    }

    public static Song fromJson(JSONObject jsonObject) throws JSONException {
        Song song = new Song();
        song.id = jsonObject.getString("id");
        song.name = jsonObject.getString("name");
        song.artist = jsonObject.getJSONArray("artists").getJSONObject(0).getString("name");
        song.albumImageUrl = jsonObject.getJSONObject("album").getJSONArray("images")
                .getJSONObject(0).getString("url");
        return song;
    }
}
