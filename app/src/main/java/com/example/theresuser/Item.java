package com.example.theresuser;
//Class to generate an object of class item to be posted online in the database
public class Item {

    public Integer getPost_id() {
        return post_id;
    }

    public Integer getColor_id() {
        return color_id;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public Integer getYear_id() {
        return year_id;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public void setColor_id(Integer color_id) {
        this.color_id = color_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public void setYear_id(Integer year_id) {
        this.year_id = year_id;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Item(Integer post_id, Integer color_id, Integer item_id, Integer year_id,  float latitude, float longitude) {
        this.post_id = post_id;
        this.color_id = color_id;
        this.item_id = item_id;
        this.year_id = year_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    Integer post_id,color_id,item_id,year_id;
    float latitude,longitude;
}
