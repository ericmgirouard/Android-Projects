package app.caloriecounter.com.caloriecounter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import data.Constants;
import data.DatabaseHandler;
import data.Day;

public class display_days extends AppCompatActivity {

    private ListView list;
    private TextView numDays;
    private ArrayList<Day> days = new ArrayList<>();
    private DayAdapter dayAdapter;
    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_days);

        list = (ListView) findViewById(R.id.Activity_display_days_ListView);
        numDays = (TextView) findViewById(R.id.Activity_display_days_Header_numDays);

        //refresh the data that needs to currently display in the List:
        refreshDayData();

    }


    private void refreshDayData() {
        days.clear();
        db = new DatabaseHandler(getApplicationContext());

        ArrayList<Day> dbDays = db.getDays(); //get the list of days from the database

        //loop through returned day list and enter each item object into the days arraylist
        for (int i = 0; i < dbDays.size(); i++) {

            String date = dbDays.get(i).getDate_String();
            String items = dbDays.get(i).getNumItems();

            Day currentDay = new Day();
            currentDay.setDate_String(date);
            currentDay.setNumItems(items);

            days.add(currentDay);

        }//end looping
        db.close();

        //setup adapter

        ////ADAPTER CODE GOES HERE ONCE WRITTEN (RIP ERIC)
        DayAdapter dayAdapter = new DayAdapter(display_days.this, R.layout.day_row,days);
        list.setAdapter(dayAdapter);
        dayAdapter.notifyDataSetChanged();


    }//end refredhDayData()

    private class DayAdapter extends ArrayAdapter<Day> {
        Activity activity;
        int layoutResource;
        Day day;
        ArrayList<Day> mdays = new ArrayList<>();

        public DayAdapter(Activity act, int resource, ArrayList<Day> data) {
            super(act, resource, data);
            this.activity = act;
            this.layoutResource = resource;
            mdays = data;
            notifyDataSetChanged();
        }//end constructor

        @Override
        public int getCount() {
            return mdays.size();
        }

        @Nullable
        @Override
        public Day getItem(int position) {
            return mdays.get(position);
        }

        @Override
        public int getPosition(@Nullable Day item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        //lets get down to ListView business
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            final ViewHolderDay holder;

            //if there is no row recycled, create one
            if (row == null || (row.getTag() == null)) {
                LayoutInflater inflater = LayoutInflater.from(activity);

                row = inflater.inflate(layoutResource,null);
                holder = new ViewHolderDay();

                holder.dateString = (TextView) row.findViewById(R.id.day_row_dateString);
                holder.numItems = (TextView) row.findViewById(R.id.day_row_numItems);
               // holder.btn = (ImageView) row.findViewById(R.id.day_row_btn);

                row.setTag(holder);//save the view for later recycling
            } else {
                //this means we are going to recycle a view for use now
                holder = (ViewHolderDay) row.getTag();
            }

            holder.day = getItem(position);

            holder.dateString.setText(holder.day.getDate_String()); //set the datString from the day object pulled
            holder.numItems.setText(holder.day.getNumItems());

            //When the data is clicked: go to displayItems activity for the given day
            holder.dateString.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dayID = holder.mId;
                   // Intent i = new Intent(display_days.this, display_items.class);
                    //i.putExtra(Constants.DAY_ID,dayID); //pass the ID of the Day to the Display_items activity
                    //startActivity(i);
                }
            });
            return row;

        }//end getView()


        class ViewHolderDay {
            TextView dateString;
            TextView numItems;
            int mId;

            Day day;
        }


    }//end customAdapter
}


