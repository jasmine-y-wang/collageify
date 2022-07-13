package com.example.collageify;

import static org.junit.Assert.assertEquals;

import com.example.collageify.models.Song;

import org.junit.Test;

/**
 * Tests {@link com.example.collageify.models.Song}
 */
public class SongTest {

    @Test
    public void getStringDuration_songDuration_isCorrect() {
        assertEquals("1:10", Song.getStringDuration(70000));
        assertEquals("4:16", Song.getStringDuration(256000));
        assertEquals("2:03", Song.getStringDuration(123456));
        assertEquals("0:00", Song.getStringDuration(0));
        assertEquals("12:45", Song.getStringDuration(765432));
    }
}