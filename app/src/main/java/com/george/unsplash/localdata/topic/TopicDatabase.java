package com.george.unsplash.localdata.topic;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TopicData.class}, version = 1, exportSchema = false)
public abstract class TopicDatabase extends RoomDatabase {

    private static TopicDatabase instance;
    public abstract TopicDao topicDao();
    public static synchronized TopicDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TopicDatabase.class, "topic_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    public static final RoomDatabase.Callback roomCallBack = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> instance.topicDao());
        }
    };

}
