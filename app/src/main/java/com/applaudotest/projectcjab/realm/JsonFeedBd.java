package com.applaudotest.projectcjab.realm;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class JsonFeedBd extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    private int id;
    @SerializedName("team_name")
    private String team_name;
    @SerializedName("since")
    private String since;
    @SerializedName("coach")
    private String coach;
    @SerializedName("team_nickname")
    private String team_nickname;
    @SerializedName("stadium")
    private String stadium;
    @SerializedName("img_logo")
    private String img_logo;
    @SerializedName("img_stadium")
    private String img_stadium;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("website")
    private String website;
    @SerializedName("tickets_url")
    private String tickets_url;
    @SerializedName("address")
    private String address;
    @SerializedName("phone_number")
    private String phone_number;
    @SerializedName("description")
    private String description;
    @SerializedName("video_url")
    private String video_url;
    @SerializedName("Schedule_gamesBd")
    private RealmList<Schedule_gamesBd> Schedule_gamesBd;

    public JsonFeedBd(){ }

    public JsonFeedBd setId(int id){
        this.id = id;
        return this;
    }
    public int getId(){
        return this.id;
    }
    public JsonFeedBd setTeam_name(String team_name){
        this.team_name = team_name;
        return this;
    }
    public String getTeam_name(){
        return this.team_name;
    }
    public JsonFeedBd setSince(String since){
        this.since = since;
        return this;
    }
    public String getSince(){
        return this.since;
    }
    public JsonFeedBd setCoach(String coach){
        this.coach = coach;
        return this;
    }
    public String getCoach(){
        return this.coach;
    }
    public JsonFeedBd setTeam_nickname(String team_nickname){
        this.team_nickname = team_nickname;
        return this;
    }
    public String getTeam_nickname(){
        return this.team_nickname;
    }
    public JsonFeedBd setStadium(String stadium){
        this.stadium = stadium;
        return this;
    }
    public String getStadium(){
        return this.stadium;
    }
    public JsonFeedBd setImg_logo(String img_logo){
        this.img_logo = img_logo;
        return this;
    }
    public String getImg_logo(){
        return this.img_logo;
    }
    public JsonFeedBd setImg_stadium(String img_stadium){
        this.img_stadium = img_stadium;
        return this;
    }
    public String getImg_stadium(){
        return this.img_stadium;
    }
    public JsonFeedBd setLatitude(String latitude){
        this.latitude = latitude;
        return this;
    }
    public String getLatitude(){
        return this.latitude;
    }
    public JsonFeedBd setLongitude(String longitude){
        this.longitude = longitude;
        return this;
    }
    public String getLongitude(){
        return this.longitude;
    }
    public JsonFeedBd setWebsite(String website){
        this.website = website;
        return this;
    }
    public String getWebsite(){
        return this.website;
    }
    public JsonFeedBd setTickets_url(String tickets_url){
        this.tickets_url = tickets_url;
        return this;
    }
    public String getTickets_url(){
        return this.tickets_url;
    }
    public JsonFeedBd setAddress(String address){
        this.address = address;
        return this;
    }
    public String getAddress(){
        return this.address;
    }
    public JsonFeedBd setPhone_number(String phone_number){
        this.phone_number = phone_number;
        return this;
    }
    public String getPhone_number(){
        return this.phone_number;
    }
    public JsonFeedBd setDescription(String description){
        this.description = description;
        return this;
    }
    public String getDescription(){
        return this.description;
    }
    public JsonFeedBd setVideo_url(String video_url){
        this.video_url = video_url;
        return this;
    }
    public String getVideo_url(){
        return this.video_url;
    }
    public JsonFeedBd setSchedule_gamesBd(RealmList<Schedule_gamesBd> Schedule_gamesBd){
        this.Schedule_gamesBd = Schedule_gamesBd;
        return this;
    }
    public RealmList<Schedule_gamesBd> getSchedule_gamesBd(){
        return this.Schedule_gamesBd;
    }
}
