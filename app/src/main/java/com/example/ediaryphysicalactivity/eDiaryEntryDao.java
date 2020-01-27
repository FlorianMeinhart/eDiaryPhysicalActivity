package com.example.ediaryphysicalactivity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface eDiaryEntryDao {

    @Query("SELECT * FROM eDiaryEntry")
    List<EDiaryEntry> getAll();

    @Insert
    void insert(EDiaryEntry eDiaryEntry);

    @Delete
    void delete(EDiaryEntry eDiaryEntry);

    @Update
    void update(EDiaryEntry eDiaryEntry);

}