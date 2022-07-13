package com.example.collageify.models;

import androidx.annotation.NonNull;

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
    private String uri;

    private boolean playing;

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
        song.uri = jsonObject.getString("uri");
        song.albumData = jsonObject.getJSONObject("album");
        song.albumId = song.albumData.getString("id");
        song.playing = false;
        return song;
    }

    public static Song fromJsonForTracksList(JSONObject jsonObject) throws JSONException {
        Song song = new Song();
        song.id = jsonObject.getString("id");
        song.name = jsonObject.getString("name");
        song.trackNumber = jsonObject.getInt("track_number");
        int durationMs = jsonObject.getInt("duration_ms");
        song.duration = getStringDuration(durationMs);
        return song;
    }

    @NonNull
    // get duration from ms to minutes and seconds
    public static String getStringDuration(int durationMs) {
        int totalSeconds = durationMs / 1000; // convert to seconds
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;
        return String.format("%d:%02d", minutes, seconds);
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

    public String getUri() {
        return uri;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /** for testing in TopAlbumsUtilTest **/
    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public void setAlbumData(JSONObject albumData) {
        this.albumData = albumData;
    }

}
