package com.applaudotest.projectcjab.realm;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Schedule_gamesBd extends RealmObject {

    @SerializedName("date")
    private String date;
    @SerializedName("stadium")
    private String stadium;

    public Schedule_gamesBd(){ }

    public Schedule_gamesBd setDate(String date){
        this.date = date;
        return this;
    }
    public String getDate(){
        return this.date;
    }
    public Schedule_gamesBd setStadium(String stadium){
        this.stadium = stadium;
        return this;
    }
    public String getStadium(){
        return this.stadium;
    }
}


