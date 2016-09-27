package com.lynkor.hangry;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lynkor.hangry.sqliteDB.DbContract;
import com.lynkor.hangry.sqliteDB.DbHelper;

public class MealsActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor mealsCursor;

    private void getMeals(){
        DbHelper handler = DbHelper.getInstance(this);
        db = handler.getWritableDatabase();
        //get cursor
        mealsCursor = db.rawQuery(DbContract.SELECT_ALL_FROM_MEALS, null);
    }

    //items stored in ListView
    public class Item {
        String ItemString;
        Item(String t){
            ItemString = t;
        }
    }

    //objects passed in Drag and Drop operation
    class PassObject{
        View view;
        Item item;
        List<Item> srcList;

        PassObject(View v, Item i, List<Item> s){
            view = v;
            item = i;
            srcList = s;
        }
    }

    static class ViewHolder {
        TextView text;
    }

    public class ItemsCursorAdapter extends CursorAdapter{

        public ItemsCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }

    public class ItemsArrayAdapter extends ArrayAdapter<Item>{

        public ItemsArrayAdapter(Context context, int resource, Item[] objects) {
            super(context, resource, objects);
        }
    }

    public class ItemsListAdapter extends BaseAdapter {

        private Context context;
        private List<Item> list;

        ItemsListAdapter(Context c, List<Item> l){
            context = c;
            list = l;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.row, null);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.text.setText(list.get(position).ItemString);

            rowView.setOnDragListener(new ItemOnDragListener(list.get(position)));

            return rowView;
        }

        public List<Item> getList(){
            return list;
        }
    }

    List<Item> mealList, mondayList, tuesdayList, wednesdayList, thursdayList, fridayList, saturdayList, sundayList;
    ListView mealListView, mondayListView, tuesdayListView, wednesdayListView, thursdayListView, fridayListView, saturdayListView, sundayListView;
    ItemsListAdapter mealListAdapter, mondayListAdapter, tuesdayListAdapter, wednesdayListAdapter, thursdayListAdapter, fridayListAdapter, saturdayListAdapter, sundayListAdapter;
    LinearLayoutListView area1, area2, area3, area4, area5, area6, area7, area8;

    //Used to resume original color in drop ended/exited
    int resumeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);
        mealListView = (ListView)findViewById(R.id.listview1);
        mondayListView = (ListView)findViewById(R.id.listview2);
        tuesdayListView = (ListView)findViewById(R.id.listview3);
        wednesdayListView = (ListView)findViewById(R.id.listview4);
        thursdayListView = (ListView)findViewById(R.id.listview5);
        fridayListView = (ListView)findViewById(R.id.listview6);
        saturdayListView = (ListView)findViewById(R.id.listview7);
        sundayListView = (ListView)findViewById(R.id.listview8);

        area1 = (LinearLayoutListView)findViewById(R.id.pane1);
        area2 = (LinearLayoutListView)findViewById(R.id.pane2);
        area3 = (LinearLayoutListView)findViewById(R.id.pane3);
        area4 = (LinearLayoutListView)findViewById(R.id.pane4);
        area5 = (LinearLayoutListView)findViewById(R.id.pane5);
        area6 = (LinearLayoutListView)findViewById(R.id.pane6);
        area7 = (LinearLayoutListView)findViewById(R.id.pane7);
        area8 = (LinearLayoutListView)findViewById(R.id.pane8);

        area1.setOnDragListener(myOnDragListener);
        area2.setOnDragListener(myOnDragListener);
        area3.setOnDragListener(myOnDragListener);
        area4.setOnDragListener(myOnDragListener);
        area5.setOnDragListener(myOnDragListener);
        area6.setOnDragListener(myOnDragListener);
        area7.setOnDragListener(myOnDragListener);
        area8.setOnDragListener(myOnDragListener);

        area1.setListView(mealListView);
        area2.setListView(mondayListView);
        area3.setListView(tuesdayListView);
        area4.setListView(wednesdayListView);
        area5.setListView(thursdayListView);
        area6.setListView(fridayListView);
        area7.setListView(saturdayListView);
        area8.setListView(sundayListView);


        initItems();

        mealListAdapter = new ItemsListAdapter(this, mealList);
        mondayListAdapter = new ItemsListAdapter(this, mondayList);
        tuesdayListAdapter = new ItemsListAdapter(this, tuesdayList);
        wednesdayListAdapter = new ItemsListAdapter(this, wednesdayList);
        thursdayListAdapter = new ItemsListAdapter(this, thursdayList);
        fridayListAdapter = new ItemsListAdapter(this, fridayList);
        saturdayListAdapter = new ItemsListAdapter(this, saturdayList);
        sundayListAdapter = new ItemsListAdapter(this, sundayList);

        mealListView.setAdapter(mealListAdapter);
        mondayListView.setAdapter(mondayListAdapter);
        tuesdayListView.setAdapter(tuesdayListAdapter);
        wednesdayListView.setAdapter(wednesdayListAdapter);
        thursdayListView.setAdapter(thursdayListAdapter);
        fridayListView.setAdapter(fridayListAdapter);
        saturdayListView.setAdapter(saturdayListAdapter);
        sundayListView.setAdapter(sundayListAdapter);


  /*
  //Auto scroll to end of ListView
  listView1.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
  listView2.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
  listView3.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
  */

        mealListView.setOnItemClickListener(listOnItemClickListener);
        mondayListView.setOnItemClickListener(listOnItemClickListener);
        tuesdayListView.setOnItemClickListener(listOnItemClickListener);
        wednesdayListView.setOnItemClickListener(listOnItemClickListener);
        thursdayListView.setOnItemClickListener(listOnItemClickListener);
        fridayListView.setOnItemClickListener(listOnItemClickListener);
        saturdayListView.setOnItemClickListener(listOnItemClickListener);
        sundayListView.setOnItemClickListener(listOnItemClickListener);

        mealListView.setOnItemLongClickListener(myOnItemLongClickListener);
        mondayListView.setOnItemLongClickListener(myOnItemLongClickListener);
        tuesdayListView.setOnItemLongClickListener(myOnItemLongClickListener);
        wednesdayListView.setOnItemLongClickListener(myOnItemLongClickListener);
        thursdayListView.setOnItemLongClickListener(myOnItemLongClickListener);
        fridayListView.setOnItemLongClickListener(myOnItemLongClickListener);
        saturdayListView.setOnItemLongClickListener(myOnItemLongClickListener);
        sundayListView.setOnItemLongClickListener(myOnItemLongClickListener);



        resumeColor  = getResources().getColor(android.R.color.background_light);

    }

    OnItemLongClickListener myOnItemLongClickListener = new OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            Item selectedItem = (Item)(parent.getItemAtPosition(position));

            ItemsListAdapter associatedAdapter = (ItemsListAdapter)(parent.getAdapter());
            List<Item> associatedList = associatedAdapter.getList();

            PassObject passObj = new PassObject(view, selectedItem, associatedList);

            ClipData data = ClipData.newPlainText("", "");
            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, passObj, 0);

            return true;
        }

    };

    OnDragListener myOnDragListener = new OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            String area;
            if(v == area1){
                area = "area1";
            }else if(v == area2){
                area = "area2";
            }else if(v == area3){
                area = "area3";
            }else{
                area = "unknown";
            }

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:

                    PassObject passObj = (PassObject)event.getLocalState();
                    View view = passObj.view;
                    Item passedItem = passObj.item;
                    List<Item> srcList = passObj.srcList;
                    ListView oldParent = (ListView)view.getParent();
                    ItemsListAdapter srcAdapter = (ItemsListAdapter)(oldParent.getAdapter());

                    LinearLayoutListView newParent = (LinearLayoutListView)v;
                    ItemsListAdapter destAdapter = (ItemsListAdapter)(newParent.listView.getAdapter());
                    List<Item> destList = destAdapter.getList();

                    if(removeItemToList(srcList, passedItem)){
                        addItemToList(destList, passedItem);
                    }

                    srcAdapter.notifyDataSetChanged();
                    destAdapter.notifyDataSetChanged();

                    //smooth scroll to bottom
                    newParent.listView.smoothScrollToPosition(destAdapter.getCount()-1);

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }

            return true;
        }

    };

    class ItemOnDragListener implements OnDragListener{

        Item  me;

        ItemOnDragListener(Item i){
            me = i;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(0x30000000);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(resumeColor);
                    break;
                case DragEvent.ACTION_DROP:

                    PassObject passObj = (PassObject)event.getLocalState();
                    View view = passObj.view;
                    Item passedItem = passObj.item;
                    List<Item> srcList = passObj.srcList;
                    ListView oldParent = (ListView)view.getParent();
                    ItemsListAdapter srcAdapter = (ItemsListAdapter)(oldParent.getAdapter());

                    ListView newParent = (ListView)v.getParent();
                    ItemsListAdapter destAdapter = (ItemsListAdapter)(newParent.getAdapter());
                    List<Item> destList = destAdapter.getList();

                    int removeLocation = srcList.indexOf(passedItem);
                    int insertLocation = destList.indexOf(me);
    /*
     * If drag and drop on the same list, same position,
     * ignore
     */
                    if(srcList != destList || removeLocation != insertLocation){
                        if(removeItemToList(srcList, passedItem)){
                            destList.add(insertLocation, passedItem);
                        }

                        srcAdapter.notifyDataSetChanged();
                        destAdapter.notifyDataSetChanged();
                    }

                    v.setBackgroundColor(resumeColor);

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(resumeColor);
                default:
                    break;
            }

            return true;
        }

    }

    OnItemClickListener listOnItemClickListener = new OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Toast.makeText(MealsActivity.this,
                    ((Item)(parent.getItemAtPosition(position))).ItemString,
                    Toast.LENGTH_SHORT).show();
        }

    };


    //populate here
    private void initItems(){
        mealList = new ArrayList<Item>();
        mondayList = new ArrayList<Item>();
        tuesdayList = new ArrayList<Item>();
        wednesdayList = new ArrayList<Item>();
        thursdayList = new ArrayList<Item>();
        fridayList = new ArrayList<Item>();
        saturdayList = new ArrayList<Item>();
        sundayList = new ArrayList<Item>();


        getMeals();

        for (mealsCursor.moveToFirst(); !mealsCursor.isAfterLast(); mealsCursor.moveToNext()) {
            String s = mealsCursor.getString(mealsCursor.getColumnIndexOrThrow(DbContract.MealsEntry.COLUMN_NAME));
            Item item = new Item(s);
            mealList.add(item);
        }

    }

    private boolean removeItemToList(List<Item> l, Item it){
        boolean result = l.remove(it);
        return result;
    }

    private boolean addItemToList(List<Item> l, Item it){
        boolean result = l.add(it);
        return result;
    }

}
