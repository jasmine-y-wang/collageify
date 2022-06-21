package com.example.collageify;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tdKvKxY9rcAQHXrDnXBMHHZkofeh4etMOPQNKeE7")
                .clientKey("EsuHZuZzyqxNv0BM0g0TgumE1Yo1bz5MHL8930nd")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
