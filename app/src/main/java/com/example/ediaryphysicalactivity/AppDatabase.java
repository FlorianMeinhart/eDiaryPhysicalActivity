package com.example.ediaryphysicalactivity;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {EDiaryEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract eDiaryEntryDao taskDao();
}

// several entities??