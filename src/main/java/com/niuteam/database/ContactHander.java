package com.niuteam.database;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import com.niuteam.MainApp;

public class ContactHander {
	private static final String CONTACTS_LOOKUP = "content://com.android.contacts/phone_lookup/";  

	// addr: +86134xxxxxxxx/12520134xxx
	public String getContactByAddr(Context context, final String addr) {  
	    Uri personUri = Uri.withAppendedPath(  
	            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, addr);  
	    Cursor cur = context.getContentResolver().query(personUri, 
	    		new String[] { PhoneLookup.DISPLAY_NAME },  
	            null, null, null );  
	    if( cur.moveToFirst() ) {  
	        int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);  
//	        ContactItem item = new ContactItem();  
	        String name = cur.getString(nameIdx);  
	        cur.close();  
	        return name;  
	    }  
	    return null;  
	}
	public void dial(){
		Intent it = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:123456"));
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MainApp.getAppContext().startActivity(it);
	}
}
