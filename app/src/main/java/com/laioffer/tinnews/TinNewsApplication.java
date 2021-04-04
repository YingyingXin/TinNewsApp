package com.laioffer.tinnews;

import android.app.Application;
import androidx.room.Room;
import com.laioffer.tinnews.database.TinNewsDatabase;

//Application自定义class，告诉AndroidMainManifest要使用自定义的而不是默认的。
public class TinNewsApplication extends Application {
    private TinNewsDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        //getApplicationContext() 或者 this都指代TinNewsApplication
        database = Room.databaseBuilder(getApplicationContext(),TinNewsDatabase.class, "tinnews_db").build();
    }
    public TinNewsDatabase getDatabase(){
        return database;
    }
}
