package com.netdb.nthu.whalecharger.model;



import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by user on 2015/1/15.
 */
public class Message{
    private long id;
    private String item;
    private Integer category;
    private Integer price;
    private Integer year;
    private Integer month;
    private Integer day;

    public Message(){
        this.id = -1;
        this.item = "";
        this.category = 3;
        this.price = 0;
        this.year = 0;
        this.month = 0;
        this.day = 0;
    }

    public Message(long id, String item,Integer category,Integer price,Integer year,Integer month,Integer day) {
        this.id = id;
        this.item = item;
        this.category = category;
        this.price = price;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}