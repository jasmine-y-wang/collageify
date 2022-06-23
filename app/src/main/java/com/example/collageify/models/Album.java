package com.example.collageify.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Album {

    private String name;
    private String imageUrl;
    private String artistName;
    private String artistImageUrl;
    private final int ranking; // based on position of top song in top tracks list
    private int songCount;

    public Album(JSONObject albumData, int ranking) {
        songCount = 1;
        this.ranking = ranking;
        try {
            imageUrl = albumData.getJSONArray("images")
                    .getJSONObject(0).getString("url");
            name = albumData.getString("name");
            JSONObject artist = albumData.getJSONArray("artists").getJSONObject(0);
            artistName = artist.getString("name");
            artistImageUrl = artist.getJSONArray("images").getJSONObject(0).getString("url");
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
}
