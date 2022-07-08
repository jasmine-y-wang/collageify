package com.example.collageify.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Song {
    private String id;
    private String name;
    private JSONObject albumData;
    private String albumId;
    private String duration; // in minutes and seconds
    private int trackNumber;
    private String albumImageUrl;
    private String artistName;

    public Song() {}

    public String getName() {
        return name;
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
        song.albumData = jsonObject.getJSONObject("album");
        song.albumId = song.albumData.getString("id");
        return song;
    }

    public static Song fromJsonForTracksList(JSONObject jsonObject) throws JSONException {
        Song song = new Song();
        song.id = jsonObject.getString("id");
        song.name = jsonObject.getString("name");
        song.trackNumber = jsonObject.getInt("track_number");

        // get duration from ms to minutes and seconds
        int totalSeconds = jsonObject.getInt("duration_ms") / 1000; // convert to seconds
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;
        song.duration = String.format("%d:%02d", minutes, seconds);
        return song;
    }

    public static Song fromJsonForRecommendedList(JSONObject jsonObject) throws JSONException {
        Song song = fromJson(jsonObject);
        song.albumImageUrl = song.albumData.getJSONArray("images")
                .getJSONObject(0).getString("url");
        song.artistName = jsonObject.getJSONArray("artists").getJSONObject(0).getString("name");
        return song;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public String getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public String getArtistName() {
        return artistName;
    }
}
