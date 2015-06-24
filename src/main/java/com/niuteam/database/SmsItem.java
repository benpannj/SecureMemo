package com.niuteam.database;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by ben on 6/23/15.
 */
public class SmsItem implements Serializable{
    public long id;
    public String person;
    public String datetime;
    public String text;

    public static SmsItem cur2item(Cursor c){
        SmsItem item = new SmsItem();
        item.id = c.getLong(0);
        item.person = c.getString(1);
        item.datetime = c.getString(2);
        item.text = c.getString(3);
        return item;
    }
}
