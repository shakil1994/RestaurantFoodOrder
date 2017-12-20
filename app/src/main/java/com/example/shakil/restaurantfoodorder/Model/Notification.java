package com.example.shakil.restaurantfoodorder.Model;

/**
 * Created by shaki on 11/7/2017.
 */

public class Notification {

    public String body;
    public String title;

    public Notification(String s) {
    }

    public Notification(String body, String title) {
        this.body = body;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
