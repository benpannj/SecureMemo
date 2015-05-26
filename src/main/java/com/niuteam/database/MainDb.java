package com.niuteam.database;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.niuteam.CONST;
import com.niuteam.MainApp;

import java.io.File;

/**
 * Created by ben on 5/15/15.
 */
public class MainDb {
    private static final String BACKUP_FOLDER   = "niuteam";
    private static final String BACKUP_FILE = "/datacache.db";
    private SQLiteDatabase db = null;
    private MainDb(){

    }
    private static MainDb inst = null;
    public static MainDb getInst(){
        if (inst == null) inst = new MainDb();
        return inst;
    }
    public SQLiteDatabase open(){
        if (db != null && db.isOpen()) return db;
        //
        String path="";
        try {
            String externalStorageState = Environment.getExternalStorageState();
            if(externalStorageState.equals(Environment.MEDIA_MOUNTED)){
                File fd = new File(Environment.getExternalStorageDirectory(), BACKUP_FOLDER);
                if (fd.exists() && fd.isDirectory()){
                }else {
                    fd.mkdirs();
                }
                path = fd.getAbsolutePath() + BACKUP_FILE;
            } else {
                path = MainApp.getAppContext().getFilesDir().toString() + "/datacache.db";
            }
            Log.i(CONST.TAG, "path is " + path);
            db = SQLiteDatabase.openOrCreateDatabase(path, null);
            // db.execSQL(sql);
        }catch(Exception e){
            MainApp.toast(e.getMessage());
        }
        return db;
    }
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
    }

}
