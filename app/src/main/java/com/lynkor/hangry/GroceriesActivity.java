package com.lynkor.hangry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.lynkor.hangry.sqliteDB.DbContract;
import com.lynkor.hangry.sqliteDB.DbHelper;

public class GroceriesActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private ListViewAdapter cursorAdapter;
    private SQLiteDatabase db;
    private Cursor groceryCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);

        listView = (ListView)findViewById(R.id.grocery_list);
        getGroceries();
        setListViewAdapter();


        ImageButton close = (ImageButton) findViewById(R.id.close_groceries);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void setListViewAdapter() {
        cursorAdapter = new ListViewAdapter(this, groceryCursor, 0, db);
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


}

class ListViewAdapter extends CursorAdapter {
    Cursor cf;
    private LayoutInflater cursorInflater;
    private SQLiteDatabase db;
    private GroceriesActivity activity;


    public ListViewAdapter(GroceriesActivity context, Cursor c, int flags, SQLiteDatabase db) {
        super(context, c, flags);
        this.cf = c;
        this.db = db;
        this.activity = context;
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return cursorInflater.inflate(R.layout.activity_groceries_listitem, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final ViewHolder holder;


        if(view.getTag() == null){
            String id = cf.getString(cf.getColumnIndexOrThrow(DbContract.GroceriesEntry.ID));
            String n = cf.getString(cf.getColumnIndexOrThrow(DbContract.GroceriesEntry.COLUMN_NAME));
            Integer q = cf.getInt(cf.getColumnIndexOrThrow(DbContract.GroceriesEntry.COLUMN_QUANTITY));
            String u = cf.getString(cf.getColumnIndexOrThrow(DbContract.GroceriesEntry.COLUMN_UNIT));
            holder = new ViewHolder(view, cursor.getPosition(), id);
            holder.name.setText(n);
            holder.quantity.setText(String.valueOf(q));
            holder.unit.setText(u);
            holder.qty = q;

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }



        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(holder.qty){
                    case 0:
                        holder.name.setPaintFlags(holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        db.execSQL(DbContract.INCREMENT_GROCERY_QUANTITY + String.valueOf(holder.id));
                        holder.quantity.setText(String.valueOf(holder.qty + 1));
                        holder.qty = holder.qty + 1;
                        break;
                    default:
                        db.execSQL(DbContract.INCREMENT_GROCERY_QUANTITY + String.valueOf(holder.id));
                        holder.quantity.setText(String.valueOf(holder.qty + 1));
                        holder.qty = holder.qty + 1;
                        break;
                }

                //update/swap cursor
                activity.updateAdapter();
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Strike out text if quantity is zero
                switch (holder.qty){
                    case 1:
                        db.execSQL(DbContract.DECREMENT_GROCERY_QUANTITY + String.valueOf(holder.id));
                        holder.quantity.setText(String.valueOf(holder.qty - 1));
                        holder.qty = holder.qty - 1;
                        holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        break;

                    case 0:
                        break;

                    default:
                        db.execSQL(DbContract.DECREMENT_GROCERY_QUANTITY + String.valueOf(holder.id));
                        holder.quantity.setText(String.valueOf(holder.qty - 1));
                        holder.qty = holder.qty - 1;

                }


                //update/swap cursor
                activity.updateAdapter();
            }
        });


    }


    private class ViewHolder {
        public String id;
        public TextView name;
        public TextView quantity;
        public TextView unit;
        public Button btnDelete;
        public Button btnAdd;
        public SwipeLayout swipeLayout;
        public int position;
        public int qty;

        public ViewHolder(View v, int p, String _id) {
            swipeLayout = (SwipeLayout)v.findViewById(R.id.swipe_layout);
            btnDelete = (Button) v.findViewById(R.id.decrement_quantity);
            btnAdd = (Button) v.findViewById(R.id.increment_quantity);
            name = (TextView) v.findViewById(R.id.grocery_name);
            quantity = (TextView) v.findViewById(R.id.grocery_quantity);
            unit = (TextView) v.findViewById(R.id.grocery_unit);
            position = p;
            id = _id;
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        }
    }
}
