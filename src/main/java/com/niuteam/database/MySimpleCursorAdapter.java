package com.niuteam.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.niuteam.securememo.R;

/**
 * Created by ben on 6/1/15.
 */
public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    public MySimpleCursorAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to) {
        super(context, layout, c, from, to, 0);
        // TODO Auto-generated constructor stub

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        Cursor c = this.getCursor();
//        if ( !c.moveToNext() ) return convertView;
//        c.moveToPosition(position);

//        SmsItem item = SmsItem.cur2item(c);

//        String txt = "T " + position +", " + sms_id +", " + sms_person +", " + sms_datetime +", " + c.getString(3);
        // TODO Auto-generated method stub
        // listview每次得到一个item，都要view去绘制，通过getView方法得到view
        // position为item的序号
//        ViewHolder vh
        View view = null;
        if (convertView == null) {
            view = super.getView(position, convertView, parent);

        } else {
            view = convertView;
            // 使用缓存的view,节约内存
            // 当listview的item过多时，拖动会遮住一部分item，被遮住的item的view就是convertView保存着。
            // 当滚动条回到之前被遮住的item时，直接使用convertView，而不必再去new view()
        }
//        TextView smsTxt = (TextView)view.findViewById(R.id.sms_txt);
//        smsTxt.setText( txt );
        int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色

       // view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

        int colorPos=position%colors.length;
        if(colorPos==1)
            view.setBackgroundColor(Color.argb(250, 255, 255, 255)); //颜色设置
        else
            view.setBackgroundColor(Color.argb(255, 224, 243, 250));//颜色设置


        return view; //super.getView(position, view, parent);
    }
}
