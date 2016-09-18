package com.lynkor.hangry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.daimajia.swipe.SwipeLayout;
import com.lynkor.hangry.sqliteDB.DbContract;
import com.lynkor.hangry.sqliteDB.DbHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GroceriesActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final String DEBUG = "DEBUGGIN FAM";
    private ListView listView;
    private ListViewAdapter cursorAdapter;
    private ArrayList<String> friendsList;
    private SwipeLayout swipeLayout;
    private SQLiteDatabase db;
    private Cursor groceryCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);



        listView = (ListView)findViewById(R.id.grocery_list);
        getGroceries();
        setListViewAdapter();
    }


    private void setSwipeViewFeatures() {
        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_wrapper));

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                Log.i(TAG, "onClose");
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Log.i(TAG, "on swiping");
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.i(TAG, "on start open");
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                Log.i(TAG, "the BottomView totally show");
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                Log.i(TAG, "the BottomView totally close");
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });
    }

    private void setListViewAdapter() {
        cursorAdapter = new ListViewAdapter(this, groceryCursor, 0);
        //cursorAdapter = new ListViewAdapter(this, R.layout.activity_groceries_listitem, )
        listView.setAdapter(cursorAdapter);

    }

    public void updateAdapter() {
        cursorAdapter.notifyDataSetChanged();
    }

    public void getGroceries(){
        DbHelper handler = DbHelper.getInstance(this);
        db = handler.getWritableDatabase();

        //get cursor
        groceryCursor = db.rawQuery(DbContract.SELECT_ALL_FROM_GROCERIES, null);
    }
    //setup a cursor to get data from groceries
    //make a listadapter ? recyclerview?
    //make a listitem
    //
}

class ListViewAdapter extends CursorAdapter {
    private final String DEBUG = "DEBUGGIN FAM";
    ViewHolder holder;
    Cursor cf;
    private LayoutInflater cursorInflater;
    String n;
    String q;
    String u;

    public ListViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.cf = c;
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        Log.v(DEBUG, "check");
        return cursorInflater.inflate(R.layout.activity_groceries_listitem, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.grocery_name);
        TextView quantity = (TextView) view.findViewById(R.id.grocery_quantity);
        TextView unit = (TextView) view.findViewById(R.id.grocery_unit);



        Log.v(DEBUG, "CHECKEKE");
        n = cf.getString(cf.getColumnIndexOrThrow(DbContract.GroceriesEntry.COLUMN_NAME));
        q = cf.getString(cf.getColumnIndexOrThrow(DbContract.GroceriesEntry.COLUMN_QUANTITY));
        u = cf.getString(cf.getColumnIndexOrThrow(DbContract.GroceriesEntry.COLUMN_UNIT));
        Log.v(DEBUG, n);
/*        name.setText(n);
        quantity.setText(q);
        unit.setText(u);*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = cursorInflater.inflate(R.layout.activity_groceries_listitem, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }


        //handling buttons event
        holder.btnAdd.setOnClickListener(onAddListener(position, holder));
        holder.btnDelete.setOnClickListener(onDeleteListener(position, holder));

        return convertView;
    }



    private View.OnClickListener onAddListener(final int position, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add to quantity
            }
        };
    }


    private View.OnClickListener onDeleteListener(final int position, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //decrement
//                friends.remove(position);
//                holder.swipeLayout.close();
//                activity.updateAdapter();
            }
        };
    }

    private class ViewHolder {
        public TextView name;
        public TextView qty;
        public TextView unit;
        public Button btnDelete;
        public Button btnAdd;
        public SwipeLayout swipeLayout;

        public ViewHolder(View v) {
            swipeLayout = (SwipeLayout)v.findViewById(R.id.swipe_layout);
            btnDelete = (Button) v.findViewById(R.id.decrement_quantity);
            btnAdd = (Button) v.findViewById(R.id.increment_quantity);
            name = (TextView) v.findViewById(R.id.grocery_name);
            qty = (TextView) v.findViewById(R.id.grocery_quantity);
            unit = (TextView) v.findViewById(R.id.ingredient_unit);

            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        }
    }
}
