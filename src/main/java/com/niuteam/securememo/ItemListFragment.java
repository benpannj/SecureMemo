package com.niuteam.securememo;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


import com.niuteam.CONST;
import com.niuteam.database.MainDb;
import com.niuteam.database.MySimpleCursorAdapter;
import com.niuteam.database.SmsItem;


/**
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ItemDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(SmsItem item);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(SmsItem id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: replace with a real list adapter.
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(
//                getActivity(),
//                android.R.layout.simple_list_item_activated_1,
//                android.R.id.text1,
//                DummyContent.ITEMS));
        // updateByDb();
        getLoaderManager().initLoader(1, null, this);
    }

    private void updateByDb(){
        MainDb maindb = MainDb.getInst();
        // // LIMIT X OFFSET X*Y 是对数据库分页的重要语句，X=5是每次显示的数目条数，Y是表示第几页
        Cursor cr = maindb.open().rawQuery("select _id,address,person,body from sms order by _id desc", null);

        String[] ColumnNames = { cr.getColumnName(0), cr.getColumnName(1), cr.getColumnName(2), cr.getColumnName(3) };
        int[] vwIds = new int[] { R.id.id, R.id.job, R.id.addr, R.id.student };
        ListAdapter adapter = new MySimpleCursorAdapter(this.getActivity(), R.layout.listviewlayout, cr, ColumnNames, vwIds);

//        String[] ColumnNames = { cr.getColumnName(2), cr.getColumnName(1), cr.getColumnName(3) };
//        int[] vwIds = new int[] { R.id.sms_datetime, R.id.sms_person, R.id.sms_txt };
//        ListAdapter adapter = new MySimpleCursorAdapter(this.getActivity(), R.layout.sms_item, cr, ColumnNames, vwIds);
        setListAdapter(adapter);
    }
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
     return new CursorLoader(this.getActivity());
    }
    public void onLoaderReset(Loader<Cursor> arg0) {

    }
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        String[] ColumnNames = { cursor.getColumnName(0), cursor.getColumnName(1), cursor.getColumnName(2), cursor.getColumnName(3) };
        int[] vwIds = new int[] { R.id.id, R.id.job, R.id.addr, R.id.student };
        ListAdapter adapter = new MySimpleCursorAdapter(this.getActivity(), R.layout.listviewlayout, cursor, ColumnNames, vwIds);

//        String[] ColumnNames = { cr.getColumnName(2), cr.getColumnName(1), cr.getColumnName(3) };
//        int[] vwIds = new int[] { R.id.sms_datetime, R.id.sms_person, R.id.sms_txt };
//        ListAdapter adapter = new MySimpleCursorAdapter(this.getActivity(), R.layout.sms_item, cr, ColumnNames, vwIds);
        setListAdapter(adapter);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
//        MySimpleCursorAdapter scr = (MySimpleCursorAdapter)getListAdapter();
//        scr.getItemId()
        Object o = this.getListAdapter().getItem(position);
        Log.i(CONST.TAG, "select obj at " + position +",  val: " + o );
        if (o instanceof Cursor){
            Cursor cr = (Cursor)o;
            SmsItem item = SmsItem.cur2item(cr);
//            String s = cr.getString(0);
            Log.i(CONST.TAG, "select obj at " + position +",  val: " + item.datetime +  " ;  " + cr.getString(1) );
            mCallbacks.onItemSelected(item);
        }else {

        }
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        // mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
