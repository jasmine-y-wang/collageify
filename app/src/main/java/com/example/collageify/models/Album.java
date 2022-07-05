package com.example.collageify.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Album {

    private String name;
    private String imageUrl;
    private String artistHref;
    private String artistName;
    private final int ranking; // based on position of top song in top tracks list
    private int songCount;
    private String id;
    private String uri;

    public Album(JSONObject albumData, int ranking) {
        songCount = 1;
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

    public void incrementSongCount() {
        songCount++;
    }

    public int getSongCount() {
        return songCount;
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
}
