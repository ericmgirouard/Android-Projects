package data;

/**
 * Created by egirouard on 4/28/2017.
 * Each FoodItem contains attributes about the food and day it was eaten
 *
 *
 //Food Item table columns
 public static final String  FOOD_ID = "_ID";
 public static final String  NAME = "Name";
 public static final String  CALORIES = "Calories";
 public static final String  DAY_TABLE_ID = "DAY";
 public static final String  INSERTDATE = "InsertDate";




 */

public class FoodItem {

    private int mID;
    private String name;
    private String calories;
    private String insertDate;


    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }
}
