package app.weatherapp.com.weatherapp;

//http://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b1b15e88fa797225412429c1c50c122a1
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHTTPClient;
import model.Weather;
import util.utils;

public class MainActivity extends AppCompatActivity {

    private TextView city, deg, wind, cloud, pressure, humidity, sunrise, sunset, updated;
    private ImageView thumb;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = (TextView) findViewById(R.id.MainActivity_CityName);
        deg = (TextView) findViewById(R.id.MainActivity_Degrees);
        wind = (TextView) findViewById(R.id.MainActivity_Wind);
        cloud = (TextView) findViewById(R.id.MainActivity_Cloud);
        pressure = (TextView) findViewById(R.id.MainActivity_Pressure);
        humidity = (TextView) findViewById(R.id.MainActivity_Humidity);
        sunrise = (TextView) findViewById(R.id.MainActivity_Sunrise);
        sunset = (TextView) findViewById(R.id.MainActivity_Sunset);
        updated = (TextView) findViewById(R.id.MainActivity_LastUpdated);

        thumb = (ImageView) findViewById(R.id.MainActivity_Thumbnail);

        //fetch sharedPreference data
        CityPreference cityPreference = new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());
        //determine if a city was previously chosen, if not, display Boston, MA


    }

    public void renderWeatherData(String city) {

        //fetch API info for the given city
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=metric"});//API attachment for metric data
    }

    private void showInputDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Change City");
        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Boston,US");
        alert.setView(cityInput);
        alert.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //save the input city to shared preferences
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                String newCity = cityPreference.getCity();
                //refresh the weather data
                renderWeatherData(newCity);

            }
        });
        alert.show();
    }


    private class WeatherTask extends AsyncTask<String, Void, Weather> {
        Bitmap bitmap = null;
        //does tasks in background without affecting UI
        @Override
        protected Weather doInBackground(String[] params) {
            //create new HTTP client and pass its constructor the string passed to us
            //data holds everything from the return of getWeatherData, i.e. the stringBuffer.toString()
            String data = ( (new WeatherHTTPClient()).getWeatherData(params[0]));
            weather.iconData = weather.currentCondition.getIcon();//grab the iconID from the JSON given to us

            //parse the JSON data and attach it to a weather object in the activity
            weather = JSONWeatherParser.getWeather(data);
            Log.v("Data: ", weather.place.getCity());


            //Attempt to grab the weather icon
            HttpURLConnection client = null;
            try {
                //open the URl connection to the icon Image
                //open a connection to the icon supplied to us via JSON in the step above
                client = (HttpURLConnection) (new URL(utils.ICON_URL + weather.iconData + ".png")).openConnection();
                client.setRequestMethod("GET");
                final int statusCode = client.getResponseCode();
                //if the returned status Code for the connection isnt OKAY, break and log error message
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    Log.e("downloadImage","Error " + statusCode);
                    return null;
                }//else get the input stream (image)

                //'download' the image as a stream of bits
                InputStream image = (InputStream) client.getContent();
                //decode the input stream and return its value (returns bitmap)
                bitmap = BitmapFactory.decodeStream(image);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error", "Error in IO Exception of icon");
            }
            return weather;
        }

        //takes data from doInBackground and does stuff with it
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            //format Dates properly
            DateFormat df = DateFormat.getTimeInstance();
            String sunriseDate = df.format(new Date(weather.place.getSunrise()));
            String sunsetDate = df.format(new Date(weather.place.getSunset()));
            String updateDate = df.format(new Date(weather.place.getLastUpdated()));

            DecimalFormat decimalFormat = new DecimalFormat("#.#");//#.# = if we have a double or float, round it to 1 decimal place
            String temperatureFormat = decimalFormat.format(weather.currentCondition.getTemperature());

            //set text fields in App with retrieved weather info
            city.setText(weather.place.getCity() + ", " + weather.place.getCountry());
            deg.setText("" + temperatureFormat + " °C");
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + " hPa");
            wind.setText("Wind: " + weather.wind.getSpeed() + " mps");
            sunrise.setText("Sunrise: " + sunriseDate + "");
            sunset.setText("Sunset: " + sunsetDate + "");
            updated.setText("Last Updated: " + updateDate + "");
            cloud.setText("Condition: " + weather.currentCondition.getCondition() + " ( " + weather.currentCondition.getDescription() + " )");

            //set the thumbnail equal to the returned bitmap if available
            thumb.setImageBitmap(bitmap);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //if this is the ID of the dialog we want (its the only one, it will be)
        if (id == R.id.menu_changeCity) {
            showInputDialog(); //run the change city method
        }
        return super.onOptionsItemSelected(item);//dont know why this
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu item as a View in the activity
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
}//end mainActivity
