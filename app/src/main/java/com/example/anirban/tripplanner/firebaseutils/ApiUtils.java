package com.example.anirban.tripplanner.firebaseutils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shyak on 08-09-2017;
 */

public class ApiUtils extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
