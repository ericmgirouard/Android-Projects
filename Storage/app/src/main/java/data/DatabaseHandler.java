package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.MyWish;


    //handles all things related to the database in the activities
    public class DatabaseHandler extends SQLiteOpenHelper {

        private final ArrayList<MyWish> wishList = new ArrayList<>();


        public DatabaseHandler(Context context) {
            super(context,Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //create our tables here
            String CREATE_WISHES_TABLE =
                    "CREATE TABLE " + Constants.TABLE_NAME +
                            "(" +Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                            + Constants.TITLE_NAME + " TEXT,"
                            + Constants.CONTENT_NAME + " TEXT,"
                            + Constants.DATE_NAME + " TEXT);";
            //execute the command
            db.execSQL(CREATE_WISHES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
            Log.v("ONUPGRADE","DROPPING THE TABLE AND CREATING A NEW ONE");

            //create a new one
            onCreate(db);
        }

        public void deleteWishes() {
            SQLiteDatabase db = this.getWritableDatabase();

            String sql =
                    "DELETE FROM " + Constants.TABLE_NAME;
            db.execSQL(sql);
            db.close();
        }

        public void deleteWish(int ID) {
            //delete just one wish, pass in the Date of said wish
            SQLiteDatabase db = this.getWritableDatabase();
            String sql =
                    "DELETE FROM " + Constants.TABLE_NAME + " WHERE " + Constants.KEY_ID + " = " + ID + ";";
            db.execSQL(sql);
            db.close();
        }

        public void addWishes (MyWish wish){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues(); //operates like a hashmap/dictionary


            //place Wish attributes into the ContentValues
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            values.put(Constants.TITLE_NAME,wish.getTitle());
            values.put(Constants.DATE_NAME,date);
            values.put(Constants.CONTENT_NAME, wish.getContent());

            //push content to SQL
            db.insert(Constants.TABLE_NAME,null,values);
            db.close(); //close the db connection when we are done
        }

        public ArrayList<MyWish> getWishes() {
            //populate the arrayList

            wishList.clear();

           // String query = "SELECT * FROM " + Constants.TABLE_NAME;

            SQLiteDatabase db = this.getReadableDatabase();


            Cursor cursor =
                    db.query(Constants.TABLE_NAME,
                            new String[]{Constants.KEY_ID,Constants.TITLE_NAME,Constants.CONTENT_NAME,Constants.DATE_NAME}
                    ,null,null,null,null,Constants.DATE_NAME + " DESC");

            //loop through cursor while there are items to move through
            if(cursor.moveToFirst()) {
                do {
                    MyWish wish = new MyWish();

                    wish.setTitle(cursor.getString(cursor.getColumnIndex(Constants.TITLE_NAME)));
                    wish.setContent(cursor.getString(cursor.getColumnIndex(Constants.CONTENT_NAME)));
                    wish.setItemId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
                    wish.setRecordDate(cursor.getString(cursor.getColumnIndex(Constants.DATE_NAME)));

                    //add this wish to the arrayList of wishes
                    wishList.add(wish);

                } while(cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return wishList;
        }

    }//end class
