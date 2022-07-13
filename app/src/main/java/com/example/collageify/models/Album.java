package com.example.collageify.models;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String name;
    private String imageUrl;
    private String artistHref;
    private final int ranking; // based on position of top song in top tracks list
    private String id;
    private String uri;
    private final List<String> topSongIds;
    private final List<String> allSongIds;
    private Artist artist;

    public Album(JSONObject albumData, int ranking) {
        topSongIds = new ArrayList<>();
        allSongIds = new ArrayList<>();
        this.ranking = ranking;
        try {
            id = albumData.getString("id");
            if (albumData.has("images")) {
                imageUrl = albumData.getJSONArray("images")
                        .getJSONObject(0).getString("url");
            }
            if (albumData.has("name")) {
                name = albumData.getString("name");
            }
            if (albumData.has("uri")) {
                uri = albumData.getString("uri");
            }
            if (albumData.has("artists")) {
                JSONObject artistData = albumData.getJSONArray("artists").getJSONObject(0);
                artistHref = artistData.getString("href");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getSongCount() {
        return topSongIds.size();
    }

    public int getRanking() {
        return ranking;
    }

    public String getName() {
        return name;
    }

    public String getArtistHref() {
        return artistHref;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public List<String> getAllSongIds() {
        return allSongIds;
    }

    public void addSong(Song song) {
        allSongIds.add(song.getId());
    }

    public void addTopSong(Song song) {
        topSongIds.add(song.getId());
    }

    public List<String> getTopSongIds() {
        return topSongIds;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
