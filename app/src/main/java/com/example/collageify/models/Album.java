package com.example.collageify.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String name;
    private String imageUrl;
    private String artistHref;
    private String artistName;
    private final int ranking; // based on position of top song in top tracks list
    private String id;
    private String uri;
    private final List<String> topSongIds;
    private final List<String> allSongIds;

    public Album(JSONObject albumData, int ranking) {
        topSongIds = new ArrayList<>();
        allSongIds = new ArrayList<>();
        this.ranking = ranking;
        try {
            id = albumData.getString("id");
            imageUrl = albumData.getJSONArray("images")
                    .getJSONObject(0).getString("url");
            name = albumData.getString("name");
            uri = albumData.getString("uri");
            JSONObject artist = albumData.getJSONArray("artists").getJSONObject(0);
            artistHref = artist.getString("href");
            artistName = artist.getString("name");
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

    public String getArtistName() {
        return artistName;
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
}
