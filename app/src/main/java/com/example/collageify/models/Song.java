package com.example.collageify.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Song {
    private String id;
    private String name;
    private String albumImageUrl;
    private String artist;
    private JSONObject albumData;
    private String albumId;

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

    public JSONObject getAlbumData() {
        return albumData;
    }

    public String getAlbumId() {
        return albumId;
    }

    public static Song fromJson(JSONObject jsonObject) throws JSONException {
        Song song = new Song();
        song.id = jsonObject.getString("id");
        song.name = jsonObject.getString("name");
        song.artist = jsonObject.getJSONArray("artists").getJSONObject(0).getString("name");
        song.albumData = jsonObject.getJSONObject("album");
        song.albumId = song.albumData.getString("id");
        song.albumImageUrl = song.albumData.getJSONArray("images")
                .getJSONObject(0).getString("url");
        return song;
    }
}
