package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Class to handle all operations within the SQLITE Database framework for the CalorieCounter Application
 * Designed by Eric Girouard on April 28th 2017
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    private final ArrayList<FoodItem> wishList = new ArrayList<>();
    private final ArrayList<Day> dayList = new ArrayList<>();

    public DatabaseHandler(Context context) {

        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        Log.v("ENTERING Constructor", "LOGGING ");
       // SQLiteDatabase db = this.getWritableDatabase();
        //onUpgrade(db, Constants.DATABASE_VERSION, Constants.DATABASE_VERSION);
        //db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("ENTERING ONCREATE()", "LOGGING ");

        //create Day Table
        String CREATE_DAY_TABLE =
                "CREATE TABLE " + Constants.DAY_TABLE +
                        "(" + Constants.DAY_ID + " INTEGER PRIMARY KEY,"
                        + Constants.DAY + " TEXT,"
                        + Constants.DOW + " TEXT,"
                        + Constants.MONTH + " TEXT,"
                        + Constants.DATE_STRING + " TEXT,"
                        + Constants.YEAR + " TEXT);";
        //execute the command
        db.execSQL(CREATE_DAY_TABLE);


        //Create FoodItem DB Table
        String CREATE_FOOD_TABLE =
                "CREATE TABLE " + Constants.FOOD_TABLE +
                        "(" + Constants.FOOD_ID + " INTEGER PRIMARY KEY,"
                        + Constants.NAME + " TEXT,"
                        + Constants.CALORIES + " TEXT,"
                        + Constants.DAY_TABLE_ID + " INTEGER,"
                        + Constants.INSERTDATE + " TEXT);";
        //execute the command
        db.execSQL(CREATE_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.FOOD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DAY_TABLE);
        Log.v("ONUPGRADE", "DROPPING THE TABLE AND CREATING A NEW ONE");

        //create a new one
        onCreate(db);
    }




    public boolean foodExists() {
        boolean flag = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql   = "SELECT (COUNT(*) > 0) FROM " + Constants.FOOD_TABLE;
        Cursor cursor = db.rawQuery(sql,null);
        //will only return 1 row, the count of food items. if > 0 return true, if 0 return false
        while (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) flag = true;
        }
        db.close();
        cursor.close();
        return flag;
    }

    public void addFoodItem(FoodItem food) {
        Log.v("ENTERING ADD FOOD ITEM", "LOGGING ADD FOOD LINE 1");
        SQLiteDatabase db = this.getWritableDatabase();


        String insertDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        //If this is the first ever entry into the DB for a specific day, we need to create this day into the day table
        String sql = "SELECT (COUNT(*) < 0) FROM " + Constants.DAY_TABLE + " WHERE " + Constants.DATE_STRING + " = \"" + date + "\";";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String result = cursor.getString(0);
            if (!result.equals("1")) { //first entry for today, enter today into the DB for Days
                enterDay(date, db);
            }
        }

        //after day is entered, enter the actual food item
        //grab the day_id from the day table associated with this food
        sql = "SELECT " + Constants.DAY_ID + " FROM " + Constants.DAY_TABLE + " WHERE " + Constants.DATE_STRING + " =  \"" + date + "\";";
        cursor = db.rawQuery(sql, null);
        //note: since we entered that day record in the step above, this should ALWAYS return 1 row with the corresponding dayID, if it doesn't, we have issues
        int day_ID = -1;
        while (cursor.moveToNext()) {
            day_ID = cursor.getInt(0); //return row 0 of the cursor, the corresponding DAY_ID from DAY table
        }
        cursor.close(); //no longer need this bitch

        ContentValues values = new ContentValues();

        values.put(Constants.DAY_TABLE_ID, day_ID);
        values.put(Constants.NAME, food.getName());
        values.put(Constants.CALORIES, food.getCalories());
        values.put(Constants.INSERTDATE, insertDate);
        //insert the FoodItem into the DB
        db.insert(Constants.FOOD_TABLE, null, values);


        db.close();

    }

    public void deleteFood(int foodID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + Constants.FOOD_TABLE + " WHERE " + Constants.FOOD_ID + " = " + Integer.toString(foodID) + "";
        db.execSQL(sql);
        db.close();
    }

    public ArrayList<FoodItem> getFoodItems() {
        ArrayList<FoodItem> foodsYum = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //TABLE NAME || String[]{COLUMNS] || 4 nulls || order by
        Cursor FUCK = db.query(Constants.FOOD_TABLE,
                new String[]{Constants.FOOD_ID,Constants.NAME,Constants.CALORIES,Constants.INSERTDATE,Constants.DAY_TABLE_ID},
                null,null,null,null,Constants.INSERTDATE + " DESC " );

        //loop through returned sql rows via cursor (get it..? CURSE-OR?)
        if (FUCK.moveToFirst()) {
            do {
                FoodItem food = new FoodItem();
                food.setmID(FUCK.getInt(FUCK.getColumnIndex(Constants.FOOD_ID)));
                food.setName(FUCK.getString(FUCK.getColumnIndex(Constants.NAME)));
                food.setCalories(FUCK.getString(FUCK.getColumnIndex(Constants.CALORIES)));
                food.setInsertDate(FUCK.getString(FUCK.getColumnIndex(Constants.INSERTDATE)));
                foodsYum.add(food);
            } while (FUCK.moveToNext());

        }
        FUCK.close();
        db.close();
        return foodsYum;
    }


  //Begin DAY logic based methods



    private void enterDay(String date, SQLiteDatabase db) {
        ContentValues values = new ContentValues(); //operates like a hashmap/dictionary
        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d); //init the calendar to current date
        String dayString = "";
        int day = cal.get((Calendar.DAY_OF_WEEK));
        switch (day) {
            case (0):
                dayString = "Sunday";
                break;
            case (1):
                dayString = "Monday";
                break;
            case (2):
                dayString = "Tuesday";
                break;
            case (3):
                dayString = "Wednesday";
                break;
            case (4):
                dayString = "Thursday";
                break;
            case (5):
                dayString = "Friday";
                break;
            case (6):
                dayString = "Saturday";
                break;
        }
        values.put(Constants.DAY, cal.get(Calendar.DAY_OF_MONTH));
        values.put(Constants.DOW, dayString);
        values.put(Constants.MONTH, ((cal.get(Calendar.MONTH)) + 1));
        values.put(Constants.YEAR, cal.get(Calendar.YEAR));
        values.put(Constants.DATE_STRING, date);
        db.insert(Constants.DAY_TABLE, null, values);//insert new row in DB
    }

    public ArrayList<Day> getDays() {
        //populate the arrayList
        dayList.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        //select all the days ordered by day_ID, the assumed order of entry of days
        Cursor cursor = db.query(
                Constants.DAY_TABLE, new String[]{Constants.DAY_ID, Constants.DAY, Constants.DOW, Constants.MONTH, Constants.YEAR,
                        Constants.DATE_STRING}, null, null, null, null, Constants.DATE_STRING + " DESC ");
        //loop through cursor while there are days left to analyze
        if (cursor.moveToFirst()) {
            do {
                Day day = new Day();
                day.setNumItems("6");
                day.setDate_String(cursor.getString(cursor.getColumnIndex(Constants.DATE_STRING)));

                dayList.add(day); //add this day to the DayList going to the adapter

            }//end do
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dayList;
    }





}
