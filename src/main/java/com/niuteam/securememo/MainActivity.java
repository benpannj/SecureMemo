package com.niuteam.securememo;

import com.niuteam.CONST;
import com.niuteam.MainApp;
import com.niuteam.database.SmsHander;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.ppareit.swiftp.gui.FsPreferenceActivity;


public class MainActivity extends Activity {
	private EditText txt_input;
	private TextView txt_show;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		txt_input = (EditText) findViewById(R.id.txt_input);
		txt_show = (TextView) findViewById(R.id.txt_show);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		// menu.add(1,1,1,"test");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.btn_about:
				MainApp.toast("Test");
//				AboutDialog dialog = new AboutDialog(this);
//				dialog.show();
				return true;

//			case R.id.menu_app_settings:
//				AppSettingsActivity.Launch(this);
//				return true;
		}

		return super.onOptionsItemSelected(item);
	}
	public void backupSms(View view) {
		Log.i(CONST.TAG, "backup sms");
		try {
			SmsHander smsHander = new SmsHander(this);
			String path = smsHander.createSMSDatabase();
			Log.i(CONST.TAG, path);
			txt_input.setText(path);
			smsHander.insertSMSToDatabase();
//			MainApp.toast(path);
		}catch (Throwable e){
			Log.i(CONST.TAG, "[E] bak sms" , e);
		}
		// String s = smsHander.backupSms();
		// log(s);
		// txt_show.setText(s);
	}

	public void startFtp(View view) {
		//SmsHander sms = new SmsHander(this);
		//sms.fakeSms();
		//log("fake a sms, please check!");
		try {
		Intent it = new Intent(MainActivity.this, FsPreferenceActivity.class);
		startActivity(it);
		}catch (Throwable e){
			Log.i(CONST.TAG, "[E] ftp" , e);
		}
	}
	public void keepassX(View view) {
		     //       LayoutInflater factory = LayoutInflater.from(this);
            // final View v = factory.inflate(R.layout.alert_dialog_text_entry, null);
            final View v = new EditText(this);
            Dialog dlg = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Login")
                .setView(v)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
		Log.i("pwd " + v.getText() );
                        /* User clicked OK so do some stuff */
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
		try {
			//Intent it = new Intent(MainActivity.this, FsPreferenceActivity.class);
			//startActivity(it);
		}catch (Throwable e){
			Log.i(CONST.TAG, "[E] ftp" , e);
		}
	}
	private void dlg(){
		new AlertDialog.Builder(this).setTitle("请输入").setIcon(
			     android.R.drawable.ic_dialog_info).setView(
			     new TextView(this)).setPositiveButton("确定", null)
			     .setNegativeButton("取消", null).show();		
	}
}
