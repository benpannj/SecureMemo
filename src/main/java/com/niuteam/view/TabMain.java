package com.niuteam.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * Created by ben on 5/29/15.
 */
public class TabMain  extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOverflowShowingAlways();

        ActionBar bar = getActionBar();
        bar.setDisplayShowTitleEnabled(false);
//		bar.setDisplayHomeAsUpEnabled(true);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = bar
                .newTab()
                .setText("artist")
                .setTabListener(
                        new TabListener<ArtistFragment>(this, "artist",
                                ArtistFragment.class));
        bar.addTab(tab);
        tab = bar
                .newTab()
                .setText("album")
                .setTabListener(
                        new TabListener<AlbumFragment>(this, "album",
                                AlbumFragment.class));
        bar.addTab(tab);


        //txt_input = (EditText) findViewById(R.id.txt_input);
        //txt_show = (TextView) findViewById(R.id.txt_show);
    }
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
