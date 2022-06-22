package com.example.collageify;

import android.app.Application;

import com.example.collageify.models.Post;
import com.example.collageify.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tdKvKxY9rcAQHXrDnXBMHHZkofeh4etMOPQNKeE7")
                .clientKey("EsuHZuZzyqxNv0BM0g0TgumE1Yo1bz5MHL8930nd")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
