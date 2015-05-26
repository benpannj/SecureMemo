package com.niuteam.database;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
// _id;thread_id;address;person;date,date_sent;protocol;read,status;
// type;reply_path_present;subject;
// body;service_center;locked;error_code;seen,timed;deleted;
// sync_state;marker;source;bind_id;
// mx_status;mx_id;out_time;account;sim_id;block_type;advanced_seen;
import android.widget.Toast;

import com.niuteam.CONST;

// mmssms.db
// packages/providers/TelephonyProvider/src/com/android/providers/telephony/MmsSmsDatabaseHelper.java

// _id: msg id, autoincrement
// thread_id: conversation id, per person.
// address: tel no.
// person: id in contacts.
// date: long recv date
// protocol: 0 sms, 1 mms
// read: 0 unread, 1 readed
// status: -1 receiving, 0 complete, 128 faild
// type 1 inbox; 2 send; 3 draft; 4 ...
// body
public class SmsHander {
	private static final String CONVERSATIONS = "content://mms-sms/conversations?simple=true";
	//"content://sms/conversations/"; 
	private static final String SMS_ALL   = "content://sms/";
	//private static final String BACKUP_FOLDER   = "niuteam";
	//private static final String BACKUP_FILE = "/datacache.db";
	private MainDb maindb;
//	private SQLiteDatabase db;
	private Context context;  

	public SmsHander(Context context) {  
		this.context = context;
		maindb = MainDb.getInst();
	}  
	public void fakeSms(){
		ContentValues cv = new ContentValues();   
		cv.put("_id", "99");   
		cv.put("thread_id", "0");   
		cv.put("address", "13999999999");   
		cv.put("person", "888");   
		cv.put("date", "9999");
		cv.put("protocol", "0");
		cv.put("read", "1");
		cv.put("status", "-1");
		//cv.put("type", "0");
		cv.put("body", "@@@@@@@@@");
		 
		context.getContentResolver().insert(Uri.parse("content://sms/failed"), cv);		
	}
	public String createSMSDatabase() {  
		String sql = "create table if not exists sms("  
				+ "_id long primary key ," // autoincrement  
				+ "address varchar(255),"
				+ "person varchar(255),"  
				+ "body varchar(1024),"
				+ "date long,"  
				+ "type integer)";
		String path="";
		try {
			maindb.open().execSQL(sql);
		}catch(Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return path;
	}

	// 保存手机短信到 SQLite 数据库  
	public void insertSMSToDatabase() {
		Log.d(CONST.TAG, "[B] insert sms" );
		long lastTime = 0;
		SQLiteDatabase db = maindb.open();
		try {
			Cursor dbCount = db.rawQuery("select count(*) from sms", null);
			dbCount.moveToFirst();
			if (dbCount.getInt(0) > 0) {
				Cursor dbcur = db.rawQuery("select date from sms order by date desc limit 1", null);
				dbcur.moveToFirst();
				String s = dbcur.getString(dbcur.getColumnIndex("date"));
				if (s.indexOf(' ') > 0) {
					// like '20140501 0101'
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
					lastTime = df.parse(s).getTime();
				} else {
					lastTime = Long.parseLong(s);
				}
			} else {
				lastTime = 0;
			}
			dbCount.close();
			dbCount = null;
		}catch (Exception e){
			Log.i(CONST.TAG, "get last time error ", e);
		}
		Log.d(CONST.TAG, "last time " + lastTime );
		Uri SMS_CONTENT = Uri.parse(SMS_ALL);  
		String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };  
		Cursor cur = context.getContentResolver().query(SMS_CONTENT, projection, null, null, "date desc");   // 获取手机短信  
		db.beginTransaction(); // 开始事务处理  
		if (cur.moveToFirst()) {  
			String address;  
			String person;  
			String body;  
			long date;  
			int type;
			String id;

			int i_id = cur.getColumnIndex("_id");  
			int iAddress = cur.getColumnIndex("address");  
			int iPerson = cur.getColumnIndex("person");  
			int iBody = cur.getColumnIndex("body");  
			int iDate = cur.getColumnIndex("date");  
			int iType = cur.getColumnIndex("type");  
			
//            String strDate = dateFormat.format(d); 
			do {
				id = cur.getString(i_id);
				address = cur.getString(iAddress);
				address = fmtAddr(address);
				if (address == null){
					continue;
				} else if (address.startsWith("+86") || address.length() == 11 ){
					// this is phone
				} else {
					Log.i(CONST.TAG, "skip address: " + address);
					continue;
				}
				person = cur.getString(iPerson);  
				body = cur.getString(iBody);  
				date = Long.parseLong(cur.getString(iDate));  
				type = cur.getInt(iType);  // 1: send, 2: recv

				if (date > lastTime) {
					Cursor dbcur = db.rawQuery("select date,body from sms where _id="+id, null);
					//dbcur.getCount();
					boolean exist = dbcur.moveToNext();
					if (exist) {
						// lastTime = Long.parseLong(dbcur.getString(dbcur.getColumnIndex("date")));
						Log.i(CONST.TAG, "exist "+id+", "+ dbcur.getString(dbcur.getColumnIndex("body")));
					} else {
						String sql = "insert into sms values(?, ?, ?, ?, ?, ?)";
						Object[] bindArgs = new Object[]{id, address, DateFormat.format("yyyyMMdd kkmmss", date), body, date, type};
						db.execSQL(sql, bindArgs);
					}
//					context.getContentResolver().delete(Uri.parse("content://sms"), "_id=?", new String[]{""+id});
					
				} else {  
					break;  
				}  
			} while (cur.moveToNext());  

			cur.close();  
			cur = null;  
			db.setTransactionSuccessful();  // 设置事务处理成功，不设置会自动回滚不提交  
			db.endTransaction();            // 结束事务处理  
		}  

	}  

	// 获取 SQLite 数据库中的全部短信  
	public Cursor querySMSFromDatabase() {  
		String sql = "select * from sms order by date desc";  
		return maindb.open().rawQuery(sql, null);
	}  

	// 获取 SQLite 数据库中的最新 size 条短信  
	public Cursor querySMSInDatabase(int size) {  
		String sql;  

		Cursor dbCount = maindb.open().rawQuery("select count(*) from sms", null);
		dbCount.moveToFirst();  
		if (size < dbCount.getInt(0)) { // 不足 size 条短信，则取前 size 条  
			sql = "select * from sms order by date desc limit " + size;  
		} else {  
			sql = "select * from sms order by date desc";  
		}  
		dbCount.close();  
		dbCount = null;  

		return maindb.open().rawQuery(sql, null);
	}  

	// 获取 SQLite数据库的前 second秒短信  
	public Cursor getSMSInDatabaseFrom(long second) {  
		long time = System.currentTimeMillis() / 1000 - second;  
		String sql = "select * from sms order by date desc where date > " + time;  
		return maindb.open().rawQuery(sql, null);
	}  

	// 关闭数据库  
	public String backupSms() {
		StringBuilder buf = new StringBuilder();
		Cursor cursor                 = null;
		try {
			// "content://mms-sms/conversations?simple=true"
			Uri threadListUri = Uri.parse(CONVERSATIONS);
			cursor = context.getContentResolver().query(threadListUri, null, null, null, "date ASC");
			// _id, date;message_count;unread_count;
			// recipient_ids;snippet;snippet_cs;read;type;error;
			// has_attachment;has_drafe;stick_time;private_addr_ids;
			// last_sim_id;sp_type;sync_state;marker;source;
			String[] names = cursor.getColumnNames();
			for (String s: names){
				buf.append( s ).append(";");
			}
			while (cursor != null && cursor.moveToNext()) {
				long theirThreadId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
				String theirRecipients       = cursor.getString(cursor.getColumnIndexOrThrow("recipient_ids"));
				buf.append("\n [").append(theirRecipients).append("]");
//            Recipients ourRecipients     = getOurRecipients(context, theirRecipients);
//            ProgressDescription progress = new ProgressDescription(cursor.getCount(), cursor.getPosition(), 100, 0);
				long ourThreadId = 0;
//            if (ourRecipients != null) {
//              long ourThreadId = threadDatabase.getThreadIdFor(ourRecipients);
              migrateConversation(theirThreadId, ourThreadId, buf);
//            }

//            progress.incrementPrimaryComplete();
//            listener.progressUpdate(progress);
          }
        } finally {
          if (cursor != null)
            cursor.close();
        }    	
        return buf.toString();
//    	SmsManager sms = SmsManager.getDefault();
    }
    private void migrateConversation(long theirThreadId, long ourThreadId, StringBuilder buf){
//    	SmsDatabase ourSmsDatabase = DatabaseFactory.getSmsDatabase(context);
    	Cursor cursor              = null;

    	try {
    		Uri uri                    = Uri.parse("content://sms/conversations/" + theirThreadId);
    		cursor                     = context.getContentResolver().query(uri, null, null, null, null);
//    		SQLiteDatabase transaction = ourSmsDatabase.beginTransaction();
//    		SQLiteStatement statement  = ourSmsDatabase.createInsertStatement(transaction);
    		if (theirThreadId == 17) {
        		buf.append("\n---").append("content://sms/conversations/" ).append(theirThreadId).append("\n");
				String[] names = cursor.getColumnNames();
				for (String s: names){
					buf.append( s ).append(";");
				}
				// _id;thread_id;address;person;date,date_sent;protocol;read,status;
				// type;reply_path_present;subject;
				// body;service_center;locked;error_code;seen,timed;deleted;
				// sync_state;marker;source;bind_id;
				// mx_status;mx_id;out_time;account;sim_id;block_type;advanced_seen;
				buf.append("---\n");
    		}
    		while (cursor != null && cursor.moveToNext()) {
//    			int typeColumn = cursor.getColumnIndex(SmsDatabase.TYPE);

//    			if (cursor.isNull(typeColumn) || isAppropriateTypeForMigration(cursor, typeColumn)) {
//    				getContentValuesForRow(context, masterSecret, cursor, ourThreadId, statement);
//    				statement.execute();
//    			}

//    			listener.progressUpdate(new ProgressDescription(progress, cursor.getCount(), cursor.getPosition()));
    		}

//    		ourSmsDatabase.endTransaction(transaction);
//    		DatabaseFactory.getThreadDatabase(context).update(ourThreadId);
//    		DatabaseFactory.getThreadDatabase(context).notifyConversationListeners(ourThreadId);

    	} finally {
    		if (cursor != null)
    			cursor.close();
    	}
    }
    public JSONObject getsms()
    {
        JSONObject result = null;
        JSONArray jarray = null;
        String link[] = {"content://sms/inbox","content://sms/sent","content://sms/draft"};

        try {

            jarray = new JSONArray();

                result = new JSONObject();
        Uri uri = Uri.parse("content://sms/");
        Cursor c= context.getContentResolver().query(uri, null, null, null, null);
//        context.startManagingCursor(c);

        // Read the sms data and store it in the list
        if(c.moveToFirst()) {

            for(int i=0; i < c.getCount(); i++) {

                   result.put("body",c.getString(c.getColumnIndexOrThrow("body")).toString());

                   result.put("date",c.getString(c.getColumnIndexOrThrow("date")).toString());
                   result.put("read",c.getString(c.getColumnIndexOrThrow("read")).toString());
                   result.put("type",c.getString(c.getColumnIndexOrThrow("type")).toString());
                   if((c.getString(c.getColumnIndexOrThrow("type")).toString()).equals("3"))
                   {
                       //Cursor cur= getContentResolver().query("", null, null ,null,null);
                        //startManagingCursor(cur);

                        String threadid = c.getString(c.getColumnIndexOrThrow("thread_id")).toString();
                        Cursor cur= context.getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), null, "_id ="+threadid ,null,null);
//                        act.startManagingCursor(cur);
                        if(cur.moveToFirst())
                        {
                        String  recipientId = cur.getString(cur.getColumnIndexOrThrow("recipient_ids")).toString();
                        cur=  context.getContentResolver().query(Uri.parse("content://mms-sms/canonical-addresses"), null, "_id = " + recipientId, null, null);
//                        act.startManagingCursor(cur);
                            if(cur.moveToFirst())
                            {
                            String address = cur.getString(cur.getColumnIndexOrThrow("address")).toString();
                            result.put("address",address);
                            cur.close();
                            }
                        }

                   }else
                   {
                       result.put("address",c.getString(c.getColumnIndexOrThrow("address")).toString());
                   }
                   jarray.put(result);
                    result = new JSONObject();


                c.moveToNext();
            }
        }
        c.close();

       result.put("smslist", jarray);
       //result = new JSONObject(jarray.toString());

        } catch (IllegalArgumentException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           } catch (JSONException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
        return result;
    }
	private String fmtAddr(String addr){
		if (addr == null) return null;
		StringBuilder buf = new StringBuilder();
		int pos = addr.indexOf("+86")+1;
		if (pos == 1) {
			pos = 3;
		}
		int len = addr.length();
		while (pos < len){
			char c = addr.charAt(pos);
			if (c != ' ') buf.append(c);
			pos ++;
		}
		return buf.toString();
	}
}  

// getContentResolver().query(Uri.parse("content://sms/"),
//  new String[]{" * from sqlite_master WHERE type = 'table' -- "}, null, null, null); 
// LOG SQL= SELECT * from sqlite_master WHERE type = 'table' -- FROM sms ORDER BY date DESC

// getContentResolver().query( Uri.parse("content://sms/"),
//  new String[]{"count(*) as count, thread_id"},
//  "1=1） group by (thread_id", null, null);
// LOG SQL= SELECT count(*) as count, thread_id FROM sms WHERE ( 1=1） group by (thread_id ) ORDER BY date DESC