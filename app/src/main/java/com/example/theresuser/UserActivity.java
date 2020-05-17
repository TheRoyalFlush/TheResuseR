package com.example.theresuser;

import java.util.Date;

public class UserActivity {
    public UserActivity(String user_email, int record_id, int post_id, int activity_category, Date contributed_date) {
        this.user_email = user_email;
        this.record_id = record_id;
        this.post_id = post_id;
        this.activity_category = activity_category;
        this.contributed_date = contributed_date;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getActivity_category() {
        return activity_category;
    }

    public void setActivity_category(int activity_category) {
        this.activity_category = activity_category;
    }

    public Date getContributed_date() {
        return contributed_date;
    }

    public void setContributed_date(Date contributed_date) {
        this.contributed_date = contributed_date;
    }

    String user_email;
    int record_id,post_id,activity_category;
    Date contributed_date;
}
