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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import data.Constants;
import data.DatabaseHandler;
import data.Day;
import data.FoodItem;

/*

class designed to display the generated food items and put them into the listView
will create a custom adapter to display said items
written by Eric Girouard 05/01/2017

 */


public class display_items extends AppCompatActivity {

    private ListView itemList;
    private TextView numItems;
    private ArrayList<FoodItem> foods = new ArrayList<>();
    private FoodAdapter FoodAdapter;
    private DatabaseHandler db;
    private int itemCount = 0;
    private TextView arrowClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_items);
        //refresh current list data from DB
        refreshFoodData();

    }//end onCreate


    private void refreshFoodData() {
        //query the DB and add all food items to an arrayList
        foods.clear();
        db = new DatabaseHandler(getApplicationContext());

        //retrieve the Foods from the DB
        ArrayList<FoodItem> foodsFromDB = db.getFoodItems();
        //loop through newly grabbed AL and place its items in the global AL
        for (int i = 0; i < foodsFromDB.size(); i++) {
            //grab object parameters
            int mid = foodsFromDB.get(i).getmID();
            String mName = foodsFromDB.get(i).getName();
            String mCals = foodsFromDB.get(i).getCalories();
            String mdate = foodsFromDB.get(i).getInsertDate();

            FoodItem currentFood = new FoodItem();

            currentFood.setCalories(mCals);
            currentFood.setmID(mid);
            currentFood.setInsertDate(mdate);
            currentFood.setName(mName);
            //add pulled FoodItem to global ArrayList (AL)
            foods.add(currentFood);
        }

        db.close();//no longer need to access the Database

        FoodAdapter foodAdapter = new FoodAdapter(display_items.this, R.layout.activity_display_items, foods);
        itemList.setAdapter(foodAdapter);
        foodAdapter.notifyDataSetChanged();

    }//end refresh()


    private class FoodAdapter extends ArrayAdapter<FoodItem> {
        Activity activity;
        int layoutResource;
        FoodItem food;
        ArrayList<FoodItem> mFoods = new ArrayList<>();


        public FoodAdapter(Activity act, int resource, ArrayList<FoodItem> data) {
            super(act, resource, data);
            this.layoutResource = resource;
            this.activity = act;
            this.mFoods = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFoods.size();
        }

        @Nullable
        @Override
        public FoodItem getItem(int position) {
            return mFoods.get(position);
        }

        @Override
        public int getPosition(@Nullable FoodItem item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            final ViewHolderFood holder;

            //if null, inflate a new row view for the List
            if (row == null || row.getTag() == null) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                row = inflater.inflate(layoutResource, null);
                holder = new ViewHolderFood();

                holder.mName = (TextView) row.findViewById(R.id.food_row_name);
                holder.mdate = (TextView) row.findViewById(R.id.food_row_date);
                holder.mCals = (TextView) row.findViewById(R.id.food_row_calories);
                holder.arrow = (TextView) row.findViewById(R.id.food_row_arrow);
                row.setTag(holder);
            } else {//else recycle an old one

                holder = (ViewHolderFood) row.getTag();
            }

            holder.foodHolder = getItem(position); //get the holder and the current ListView position
            holder.mdate.setText(holder.foodHolder.getInsertDate());
            holder.mName.setText(holder.foodHolder.getName());
            holder.mCals.setText(holder.foodHolder.getCalories());

            arrowClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //when clicked, go to detail activity with the bundled intent
                    String mName = holder.mName.getText().toString();
                    String mCals = holder.mCals.getText().toString();
                    String mDate = holder.mdate.getText().toString();
                    int mID = holder.mID;
                    Intent i = new Intent(display_items.this, food_detail.class);
                    i.putExtra(Constants.NAME,mName);
                    i.putExtra(Constants.CALORIES,mCals);
                    i.putExtra(Constants.INSERTDATE,mDate);
                    i.putExtra(Constants.FOOD_ID, mID);
                    startActivity(i);
                }
            });
            return row;

        }//end getView

        class ViewHolderFood {
            private int mID;
            private TextView mName;
            private TextView mCals;
            private TextView mdate;
            private TextView arrow;

            FoodItem foodHolder;
        }


    }//end CustomAdapter
}//end class
