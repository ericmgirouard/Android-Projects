package app.storage.com.storage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import data.DatabaseHandler;
import model.MyWish;

/*

I've figured it out. I couldn't understand how the hell the adapter started and how did it know where to get the data from.
When i extended the BaseAdapter class, in the constructor of that class I initialized the list of items that I wanted to see in the ListView.
But I couldn't figure out how these values would be used and when.

So here's the thing !!! :

In the BaseAdapter there are some methods that need to be overridden. Among these, there is getCount().

When the ListView is created and whatnot, it calls getCount().
If this returns a value different than 0 (I returned the size of the ArrayList which I've previously initialized in the constructor),
then it calls getView() enough times to fill the screen with items. For instance, I initialized the ArrayList with 20 items.
Because only 8 items initially fit on the screen, getView() was called 8 times,
each time asking for the position it required for me to return (more precisely it wanted to know how the row would look like in the list on that specific position,
what data it needed to contain). If I scroll down the list, getView() gets called over and over again, 'til I hit the end of the list, in my case 20 items / rows.

What notifyDataSetChanged() does is ... when called, it looks at what items are displayed on the screen at the moment of its call
(more precisely which row indexes ) and calls getView() with those positions.

i.e. if you're displaying the first 8 items in the list (so those are the ones visible on the screen)
and you add another item between the 2nd and 3rd item in the list and you call notifyDataSetChanged()
then getView() is called 8 times, with positions starting from 0 and ending with 7,
and because in the getView() method you're getting data from the ArrayList then it will automatically return the new item inserted in
 the list alongside 7 out of the previous 8 (7 and not 8 because the last item went one position down, so it is not visible anymore), and the ListView will redraw,
 or whatever, with these items.

Also, important to specify is that if you've implemented getView() correctly,
you'll end up recycling the items (the objects) already displayed (instead of creating new ones).
See this video at around 12:00 minutes to see the correct way to implement getView()

I've figured all this out by placing calls to LogCat in every method and following what was going on.

Hope this helps someone who's just now starting to understand how ListViews work.

P.S. This example also helped me a lot to understand.
 */
public class display_wishes extends AppCompatActivity {

    private DatabaseHandler dba;
    private ArrayList<MyWish> dbWishes = new ArrayList<>();
    private WishAdapter wishAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_wishes);

        listView = (ListView) findViewById(R.id.List);


        refreshData();


    }


    private void refreshData() {

        dbWishes.clear(); //clear current wishes and refresh it with content in DB
        dba = new DatabaseHandler(getApplicationContext());

        ArrayList<MyWish> wishesFromDB = dba.getWishes();


        /*
         Why Cant I do this:? is it an issue with the memory pointers, so that the list's array is a seperate memory set?

         dbWishes.clear();
         dba = new DatabaseHandler(getApplicationContext());
         dbWishes = dba.getWishes();

         */


        //loop through the current wishes stored in the DB and place their info into a Wish, then add that wish to the ListViews ArrayList of wishes
        for (int i = 0; i < wishesFromDB.size(); i++) {

            String title = wishesFromDB.get(i).getTitle();
            String dateText = wishesFromDB.get(i).getRecordDate();
            String contentText = wishesFromDB.get(i).getContent();
            int mid = wishesFromDB.get(i).getItemId();

            MyWish myWish  = new MyWish();
            myWish.setTitle(title);
            myWish.setContent(contentText);
            myWish.setRecordDate(dateText);
            myWish.setItemId(mid);


            dbWishes.add(myWish);
        }
        dba.close();

        //setup adapter: which prepares data to be stored and viewed in the List View
        //Pass it the Java Class context, and the layout of the row of the list view, as well as the array that stores the wishes
        WishAdapter wishAdapter = new WishAdapter(display_wishes.this, R.layout.wish_row,dbWishes); //inflate the wishRow XML and populate it with actual wish Data from the arrayList
        listView.setAdapter(wishAdapter); //set the adapter to the ListView
        wishAdapter.notifyDataSetChanged();


    }//end refresh data


    private class WishAdapter extends ArrayAdapter<MyWish> {
        Activity activity;
        int layoutResource;
        MyWish wish;
        ArrayList<MyWish> mData = new ArrayList<>();

        public WishAdapter(Activity act, int resource, ArrayList<MyWish> data) {
            super(act, resource, data);
            activity = act;
            layoutResource = resource;
            mData = data;
            notifyDataSetChanged();


        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public MyWish getItem(int position) {
            return mData.get(position);
        }

        @Override
        public int getPosition(@Nullable MyWish item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        //this is the heart of the custom list view
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View row = convertView;
            ViewHolder holder;

            //if there is now row, make one
            if ( row == null || (row.getTag()) == null) {
                LayoutInflater inflater = LayoutInflater.from(activity);

                row = inflater.inflate(layoutResource,null);
                holder = new ViewHolder();

                holder.mTitle = (TextView) row.findViewById(R.id.Name);
                holder.mDate = (TextView) row.findViewById(R.id.Date);
                row.setTag(holder);

            } else {
                //not the first time a user is looking at the list View
                holder = (ViewHolder) row.getTag();
            }
                holder.myWish = getItem(position); //contains the current row that the user is tapping

                holder.mTitle.setText(holder.myWish.getTitle());
                holder.mDate.setText(holder.myWish.getRecordDate());

            final ViewHolder finalHolder = holder;
            holder.mTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = finalHolder.myWish.getContent();
                        String dateText = finalHolder.myWish.getRecordDate();
                        String title =finalHolder.myWish.getTitle();
                        int ID = finalHolder.myWish.getItemId();

                        Intent i = new Intent(display_wishes.this, wish_detail.class);
                        i.putExtra("Title",title);
                        i.putExtra("Content",text);
                        i.putExtra("Date",dateText);
                        i.putExtra("ID",ID);
                        startActivity(i);


                    }
                });

            return row;


        }

        class ViewHolder {
            TextView mTitle;
            TextView mID;
            TextView mContent;
            TextView mDate;
            int mId;

            MyWish myWish;
        }


    }//end WishAdapter class
}
