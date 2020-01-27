package com.example.ediaryphysicalactivity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class EDiaryEntry implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "attr_1")
    private String attr_str_1;

    @ColumnInfo(name = "attr_2")
    private String attr_str_2;

    @ColumnInfo(name = "attr_3")
    private String attr_str_3;

    @ColumnInfo(name = "attr_4")
    private boolean attr_bl_4;


    /*
     * Getters and Setters
     * */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAttr_str_1() {
        return attr_str_1;
    }

    public void setAttr_str_1(String attr_str_1) {
        this.attr_str_1 = attr_str_1;
    }

    public String getAttr_str_2() {
        return attr_str_2;
    }

    public void setAttr_str_2(String attr_str_2) {
        this.attr_str_2 = attr_str_2;
    }

    public String getAttr_str_3() {
        return attr_str_3;
    }

    public void setAttr_str_3(String attr_str_3) {
        this.attr_str_3 = attr_str_3;
    }

    public boolean isAttr_bl_4() {
        return attr_bl_4;
    }

    public void setAttr_bl_4(boolean attr_bl_4) {
        this.attr_bl_4 = attr_bl_4;
    }

}