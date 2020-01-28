package com.example.ediaryphysicalactivity;

import android.widget.EditText;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class EDiaryEntry implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date_time")
    private String date_time_str;

    @ColumnInfo(name = "attr_1")
    private String attr_str_1;

    @ColumnInfo(name = "attr_2")
    private String attr_str_2;

    @ColumnInfo(name = "attr_3")
    private String attr_str_3;

    @ColumnInfo(name = "attr_4")
    private boolean attr_bl_4;

    @ColumnInfo(name = "attr_5")
    private Integer attr_i_5;

    @ColumnInfo(name = "attr_6")
    private Integer attr_i_6;

    @ColumnInfo(name = "attr_7")
    private Float attr_f_7;

    @ColumnInfo(name = "attr_8")
    private String attr_str_8;

    @ColumnInfo(name = "attr_9")
    private String attr_str_9;


    /*
     * Getters and Setters
     * */

    public int getId() {
        return id;
    }

    public String getDate_time_str() { return date_time_str; }

    public void setDate_time_str(String date_time_str) { this.date_time_str = date_time_str; }

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

    public Integer getAttr_i_5() { return attr_i_5; }

    public void setAttr_i_5(Integer attr_i_5) { this.attr_i_5 = attr_i_5; }

    public Integer getAttr_i_6() { return attr_i_6; }

    public void setAttr_i_6(Integer attr_i_6) { this.attr_i_6 = attr_i_6; }

    public Float getAttr_f_7() { return attr_f_7; }

    public void setAttr_f_7(Float attr_f_7) { this.attr_f_7 = attr_f_7; }

    public String getAttr_str_8() {
        return attr_str_8;
    }

    public void setAttr_str_8(String attr_str_8) {
        this.attr_str_8 = attr_str_8;
    }

    public String getAttr_str_9() {
        return attr_str_9;
    }

    public void setAttr_str_9(String attr_str_9) {
        this.attr_str_9 = attr_str_9;
    }

}