package app.caloriecounter.com.caloriecounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import data.DatabaseHandler;
import data.FoodItem;

public class MainActivity extends AppCompatActivity {

    private Button submit, viewEntries;
    private EditText item, calories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit = (Button) findViewById(R.id.MainActivity_Submit);
        viewEntries = (Button) findViewById(R.id.MainActivity_ViewEntries);
        item = (EditText) findViewById(R.id.MainActivity_ItemName);
        calories = (EditText) findViewById(R.id.MainActivity_ItemCalories);

        //on submit click: store the entry in the database and await further interaction
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = item.getText().toString();
                String cals = calories.getText().toString();
                //only continue if they have entered data properly
                if (validateEntry(title, cals)) {
                    //add data to DB
                    addFoodItem(title, cals);
                    //Toast.makeText(getApplicationContext(),"Wish would be added now",Toast.LENGTH_LONG).show();
                }
            }
        });//end listener

        viewEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //only view Food Entries if at least 1 exists in the DB
                DatabaseHandler db = new DatabaseHandler(getApplication());
                if (db.foodExists()) {
                    //travel to foodItem ListView
                    Intent i = new Intent(MainActivity.this, display_items.class);
                    db.close();
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "There are no food entries to view. Try logging one!", Toast.LENGTH_LONG).show();
                    db.close();
                }
            }
        });


    }//end onCreate


    private boolean validateEntry(String title, String cals) {

        //check that they have entered data:
        if ((title.equals("")) && (cals.equals(""))) {
            Toast.makeText(getApplicationContext(), "Please enter a food item and calorie amount", Toast.LENGTH_LONG).show();
            return false;
        } else if ((title.equals("")) && !(cals.equals(""))) {
            Toast.makeText(getApplicationContext(), "Please enter a title for your food", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(title.equals("")) && (cals.equals(""))) {
            Toast.makeText(getApplicationContext(), "Please enter a calorie count for your food", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private void addFoodItem(String title, String cals) {
        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        FoodItem food = new FoodItem();
        food.setCalories(cals);
        food.setName(title);

        db.addFoodItem(food);
        db.close();
        Toast.makeText(getApplicationContext(), "Item Added!", Toast.LENGTH_LONG).show();
        item.setText("");
        calories.setText("");

    }

}//end class
