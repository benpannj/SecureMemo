package com.niuteam.securememo;

import com.niuteam.CONST;
import com.niuteam.MainApp;
import com.niuteam.database.MainDb;
import com.niuteam.database.SmsHander;
import com.niuteam.database.TodoHandler;
import com.niuteam.view.AlbumFragment;
import com.niuteam.view.ArtistFragment;
import com.niuteam.view.TabListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

import be.ppareit.swiftp.gui.FsPreferenceActivity;


public class MainActivity extends Activity {
	private EditText txt_input;
	private TextView txt_show;
	private ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		ActionBar bar = getActionBar();
		bar.setDisplayShowTitleEnabled(false);
//		bar.setDisplayHomeAsUpEnabled(true);


		txt_input = (EditText) findViewById(R.id.txt_input);
		txt_show = (TextView) findViewById(R.id.txt_show);
		lv = (ListView) findViewById(R.id.lv_todos);
		TodoHandler d = new TodoHandler();
		final List<String> todos = d.list();
		lv.setAdapter( new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todos));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		// menu.add(1,1,1,"test");
//		MenuItem searchItem = menu.findItem(R.id.btn_filter);
//		SearchView searchView = (SearchView) searchItem.getActionView();

		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.btn_about:
				MainApp.toast("Test");
//				AboutDialog dialog = new AboutDialog(this);
//				dialog.show();
				return true;

			case R.id.btn_sms:
				backupSms(null);
				return true;
			case R.id.btn_ftp:
				startFtp(this.getCurrentFocus());
				return true;
			case R.id.btn_keepx:
				keepassX(null);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
	public void backupSms(View view) {
		Log.i(CONST.TAG, "backup sms");
		try {
			SmsHander smsHander = new SmsHander(this);
			smsHander.insertSMSToDatabase();
			// txt_show.setText("sms bak done! " + path);
			MainApp.toast("SMS bak done!");
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
	public void todo(View view) {
		String s = txt_input.getText().toString();
		TodoHandler d = new TodoHandler();
		d.add(s);
		ArrayAdapter aa = (ArrayAdapter)lv.getAdapter();
		aa.add(s);
		aa.notifyDataSetChanged();
		txt_input.setText("");
	}
	public void keepassX(View view) {
		     //       LayoutInflater factory = LayoutInflater.from(this);
            // final View v = factory.inflate(R.layout.alert_dialog_text_entry, null);
            final EditText v = new EditText(this);
            Dialog dlg = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Login")
                .setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
		Log.i(CONST.TAG,"pwd " + v.getText() );
						try {
							Intent it = new Intent(MainActivity.this, ItemListActivity.class);
							startActivity(it);
						}catch (Throwable e){
							Log.i(CONST.TAG, "[E] ftp" , e);
						}
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
		dlg.show();
	}
	private void dlg(){

		new AlertDialog.Builder(this).setTitle("请输入").setIcon(
			     android.R.drawable.ic_dialog_info).setView(
			     new TextView(this)).setPositiveButton("确定", null)
			     .setNegativeButton("取消", null).show();		
	}
}
