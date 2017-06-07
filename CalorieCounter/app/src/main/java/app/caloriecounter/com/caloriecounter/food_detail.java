package app.caloriecounter.com.caloriecounter;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import data.Constants;
import data.DatabaseHandler;

public class food_detail extends AppCompatActivity {

    private TextView foodName, calories, date;
    private Button share;
    private int foodID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        share       = (Button)      findViewById(R.id.food_detail_share);
        foodName    = (TextView)    findViewById(R.id.food_detail_Name);
        calories    = (TextView)    findViewById(R.id.food_detail_Calories);
        date        = (TextView)    findViewById(R.id.food_detail_Date);


        Bundle b = getIntent().getExtras();
        //if there are extras (there will be, we cant get here otherwise
        if (!b.isEmpty()) {

            foodName.setText(b.getString(Constants.NAME));
            calories.setText(b.getString(Constants.CALORIES));
            date.setText(b.getString(Constants.INSERTDATE));
            this.foodID = b.getInt(Constants.FOOD_ID);
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDetail();
            }
        });
    }

    private void shareDetail() {

        StringBuilder str = new StringBuilder();
        //declare variables
        String name = foodName.getText().toString();
        String cals = calories.getText().toString();
        String dateString = date.getText().toString();
        //generate email string 9with newlines)
        str.append(" Food: " + name + "\n");
        str.append(" Calories: " + cals + "\n");
        str.append(" Eaten on: " + dateString);
        //generate email Intent
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "My Caloric Intake");
        i.putExtra(Intent.EXTRA_EMAIL, new String[] {"ericmgirouard1@gmail.com"});
        i.putExtra(Intent.EXTRA_TEXT, str.toString());

        try{

            startActivity(Intent.createChooser(i, "Send mail..."));

        }catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Please install email client before sending",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present. (the delete button)
        getMenuInflater().inflate(R.menu.menu_food_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //if the user clicks the delete button, delete this entry from the DB
        if (id == R.id.deleteItem) {
            //show dialog box to confirm deletion
            AlertDialog.Builder alert = new AlertDialog.Builder(food_detail.this);
            alert.setTitle("Delete");
            alert.setMessage("Do you want to delete this food entry?");
            alert.setNegativeButton("No",null);
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //delete the entry and return the the previous activity, close this activity
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    db.deleteFood(foodID);
                    Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(food_detail.this, display_items.class));
                    food_detail.this.finish();//relieve this Activity of its duty
                }
            });

            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

}
