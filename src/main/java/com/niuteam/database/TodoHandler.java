package com.niuteam.database;

import android.content.Context;
import android.text.Editable;
import android.widget.Toast;

import com.niuteam.MainApp;

import java.util.List;

/**
 * Created by ben on 6/3/15.
 */
public class TodoHandler {
    private MainDb maindb;
//    private Context context;

    public TodoHandler() {
//        this.context = context;
        maindb = MainDb.getInst();
        create();
    }
    private void create() {
        String sql = "create table if not exists todo("
                + "_id long," // autoincrement
                + "txt varchar(255),"
                + "date long,"
                + "status integer)";
        try {
            maindb.open().execSQL(sql);
        }catch(Exception e){
            Toast.makeText(MainApp.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public List<String> list() {

        return MainDb.query("SELECT txt from todo");
    }

    public void add(String t) {
        create();
        long d = System.currentTimeMillis();
        Object[] args = new Object[]{t,d};
        MainDb.exec("insert into todo (txt, date) values (?,?) ", args);
    }
}
