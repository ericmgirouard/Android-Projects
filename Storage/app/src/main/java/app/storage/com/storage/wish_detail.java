package app.storage.com.storage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import data.DatabaseHandler;

public class wish_detail extends AppCompatActivity {

    private TextView title,date,content;
    private Button delete;
    private int currentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_detail);

        delete = (Button) findViewById(R.id.DeleteThisWish);

        title = (TextView) findViewById(R.id.WishDetailText);
        content = (TextView) findViewById(R.id.WishDescription);
        date = (TextView) findViewById(R.id.WishCreated);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //set values
            title.setText(extras.getString("Title"));
            content.setText(" \" " + extras.getString("Content") + "\"" ) ;
            date.setText(extras.getString("Date"));
            currentID = extras.getInt("ID");

        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete this wish from the DB
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());

                db.deleteWish(currentID);
                //then navigate back to home
                db.close(); //because IM a good programmer
                Toast.makeText(getApplicationContext(),"Wish Deleted", Toast.LENGTH_LONG).show();

                //now that this wish is deleted, pass them back to the main activity
                Intent i = new Intent(wish_detail.this, MainActivity.class);
                startActivity(i);

            }
        });

    }
}
