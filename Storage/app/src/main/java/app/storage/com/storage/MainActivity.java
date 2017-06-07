package app.storage.com.storage;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import data.DatabaseHandler;
import model.MyWish;

public class MainActivity extends AppCompatActivity {

    private Button submit;
    private EditText title;
    private EditText wish_desc;
    private Button  delete;
    private Button viewWishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit = (Button) findViewById(R.id.Save);
        title = (EditText) findViewById(R.id.WishTitle);
        wish_desc = (EditText) findViewById(R.id.enter);
        delete = (Button) findViewById(R.id.deleteMain);
        viewWishes = (Button) findViewById(R.id.ViewWishesButton);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the entered info to the DB if criteria is met
                saveToDB();
            }//end OnClick
            //
        });//end listener


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask the DB to delete all records from the table

                truncateTable();
            }
        });

        viewWishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take them to the display_wishes activity ONLY if there are wishes to view
                //for now take them anyway
                Intent i = new Intent(MainActivity.this, display_wishes.class);
                startActivity(i);
            }
        });
    }//end onCreate




    public void saveToDB() {
        //now that wish is declared, generate a DB instance and pass it to the DB
        DatabaseHandler DB = new DatabaseHandler(MainActivity.this);

        MyWish wish = new MyWish();
        wish.setTitle(title.getText().toString().trim());
        wish.setContent(wish_desc.getText().toString().trim());


        DB.addWishes(wish);
        DB.close();

        //clear
        title.setText("");
        wish_desc.setText("");

        Toast.makeText(getApplicationContext(),"Wish Added!", Toast.LENGTH_LONG).show();
    //new behavior: only go to the ListView of wishes by choosing to
       // Intent i = new Intent(MainActivity.this, display_wishes.class);
       // startActivity(i);


    }


    public void truncateTable() {
        DatabaseHandler DB = new DatabaseHandler(MainActivity.this);
        DB.deleteWishes();
        Toast.makeText(getApplicationContext(),"Wishes Deleted :(", Toast.LENGTH_LONG).show();
    }



}//end class

