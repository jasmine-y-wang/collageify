package com.example.collageify.utils;

import com.example.collageify.adapters.AlbumsAdapter;
import com.example.collageify.models.Album;
import com.example.collageify.models.Song;
import com.example.collageify.services.SongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TopAlbumsUtil {

    /** Set list of topAlbums based on timeframe */
    public static void getTopAlbums(String timeframe, SongService songService, List<Album> topAlbums, AlbumsAdapter albumsAdapter) {
        List<Song> tracks = new ArrayList<>();
        songService.getTopTracks(timeframe, () -> {
            tracks.addAll(songService.getSongs());
            getAlbumsFromTracks(tracks, topAlbums);
            albumsAdapter.notifyDataSetChanged();
        });
    }

    /** Get albums from list of tracks */
    public static void getAlbumsFromTracks(List<Song> songs, List<Album> topAlbums) {
        HashMap<String, Album> albums = new HashMap<>();
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            String albumId = song.getAlbumId();
            if (!albums.containsKey(albumId)) {
                albums.put(albumId, new Album(song.getAlbumData(), i));
            }
            albums.get(albumId).addTopSong(song);
        }
        topAlbums.clear();
        topAlbums.addAll(albums.values());
        topAlbums.sort((a1, a2) -> {
            int compVal = a2.getSongCount() - a1.getSongCount();
            if (compVal == 0) {
                compVal = a1.getRanking() - a2.getRanking();
            }
            return compVal;
        });
    }

}
