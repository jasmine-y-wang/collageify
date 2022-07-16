package com.example.collageify.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKED_BY = "likedBy";
    public static final String KEY_TIMEFRAME = "timeframe";

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setTimeframe(int timeframeIndex) {
        String timeframe;
        if (timeframeIndex == 0) {
            timeframe = "1 month";
        } else if (timeframeIndex == 1) {
            timeframe = "6 months";
        } else {
            timeframe = "all time";
        }
        put(KEY_TIMEFRAME, timeframe);
    }

    public String getTimeframe() {
        return getString(KEY_TIMEFRAME);
    }

    public List<ParseUser> getLikedBy() {
        List<ParseUser> likedBy = getList(KEY_LIKED_BY);
        if (likedBy == null) {
            return new ArrayList<>();
        }
        return likedBy;
    }

    public void setLikedBy(List<ParseUser> likedBy) {
        put(KEY_LIKED_BY, likedBy);
    }

    public static String calculateTimeAgo(Date createdAt) {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 59 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 120 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        } catch (Exception e) {
            Log.e("Post", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }
        return "";
    }

    public static String formatDate(Date createdAt) {
        LocalDate localDate = createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        return String.format("%d/%d/%d", month, day, year);
    }

    public String getLikesCount() {
        int likes = getLikedBy().size();
        return likes + (likes == 1 ? " like" : " likes");
    }

    public boolean isLikedByCurrentUser() {
        List<ParseUser> likedBy = getLikedBy();
        for (ParseUser user : likedBy) {
            if (ParseUser.getCurrentUser().hasSameId(user)) {
                return true;
            }
        }
        return false;
    }

    public void unlike() {
        List<ParseUser> likedBy = getLikedBy();
        for (int i = likedBy.size() - 1; i >= 0; i--) {
            if (ParseUser.getCurrentUser().hasSameId(likedBy.get(i))) {
                likedBy.remove(i);
            }
        }
        setLikedBy(likedBy);
        saveInBackground(e -> {
            if (e != null) {
                Log.e("post", "unlike failed to register", e);
            }
        });
    }

    public void like() {
        List<ParseUser> likedBy = getLikedBy();
        likedBy.add(ParseUser.getCurrentUser());
        setLikedBy(likedBy);
        saveInBackground(e -> {
            if (e != null) {
                Log.e("post", "like failed to register", e);
            }
        });
    }
}