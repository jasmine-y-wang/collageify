package com.example.collageify;

import com.example.collageify.models.Album;
import com.example.collageify.models.Song;
import com.example.collageify.utils.TopAlbumsUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopAlbumsUtilTest {

    List<Song> topSongs;
    List<Album> topAlbums;

    @Before
    public void setup() {
        // populate list of songs
        topSongs = new ArrayList<>();

        String[] songAlbumsIds = {"1", "7", "5", "4", "5", "2", "7", "8", "6", "1", "5", "2"};
        for (String albumId : songAlbumsIds) {
            Song song = new Song();
            song.setAlbumId(albumId);
            JSONObject albumData = new JSONObject();
            try {
                albumData.put("id", albumId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            song.setAlbumData(albumData);
            topSongs.add(song);
        }
        topAlbums = new ArrayList<>();
    }

    @Test
    public void getTopAlbumsFromTracks_albums_areCorrect() {
        TopAlbumsUtil.getAlbumsFromTracks(topSongs, topAlbums);
        assert albumsAreSorted();
        assert noDuplicateAlbums();
    }

    // returns true if albums are sorted properly (based on song count and ranking), false otherwise
    private boolean albumsAreSorted() {
        for (int i = 0; i < topAlbums.size() - 1; i++) {
            Album a1 = topAlbums.get(i);
            Album a2 = topAlbums.get(i + 1);
            if (a1.getSongCount() < a2.getSongCount() && a1.getRanking() > a2.getRanking()) {
                // albums are not in the correct order
                return false;
            }
        }
        return true;
    }

    // returns true if there are no duplicate albums, false otherwise
    private boolean noDuplicateAlbums() {
        Set<String> albumIds = new HashSet<>();
        for (Album a : topAlbums) {
            if (!albumIds.add(a.getId())) {
                // album is a duplicate
                return false;
            }
        }
        return true;
    }
}
