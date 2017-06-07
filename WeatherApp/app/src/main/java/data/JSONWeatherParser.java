package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Clouds;
import model.Place;
import model.Weather;
import model.Wind;
import util.utils;

/**
 * Created by egirouard on 5/8/2017.
 * //http://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b1b15e88fa797225412429c1c50c122a1
 */

public class JSONWeatherParser {

    public static Weather getWeather(String data) {
        Weather weather = new Weather();
        try {
            //creating a JSON coordinate object that will parse through the returned data via API, and get the JSON tag with name:"coord" which is what we expected from the weather API

            Place place = new Place();

            //Set main JSON Object which contains all objects
            JSONObject jsonObject = new JSONObject(data);

            //Get Coordinate object


            JSONObject coordObj = utils.getObject("coord", jsonObject);
            place.setLastUpdated(utils.getInt("dt", jsonObject));//dt is outside of sys object, but belongs to parent
            place.setCity(utils.getString("name", jsonObject));


            place.setLat(utils.getFloat("lat", coordObj));
            place.setLon(utils.getFloat("lon", coordObj));

            //Get Sys Object
            JSONObject sysObject = utils.getObject("sys", jsonObject);
            place.setCountry(utils.getString("country", sysObject));
            place.setSunrise(utils.getInt("sunrise", sysObject));
            place.setSunset(utils.getInt("sunset", sysObject));

            weather.place = place;//store all this info in the weather object
            //get the Weather info
            JSONArray jsonArray = jsonObject.getJSONArray("weather");//get the weather array inside the jsonObject
            //array only has 1 item in it
            /*
            "weather": [
            {
                "id": 300,
                "main": "Drizzle",
                "description": "light intensity drizzle",
                "icon": "09d"
            }
                        ],
            */
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherID(utils.getInt("id", jsonWeather));
            weather.currentCondition.setDescription(utils.getString("description", jsonWeather));
            weather.currentCondition.setCondition(utils.getString("main", jsonWeather));
            weather.currentCondition.setIcon(utils.getString("icon", jsonWeather));

            //fetch main object (temperature data)
            JSONObject mainObj = utils.getObject("main",jsonObject);
            weather.currentCondition.setHumidity(utils.getInt("humidity",mainObj));
            weather.currentCondition.setTemperature(utils.getFloat("temp",mainObj));
            weather.currentCondition.setPressure(utils.getFloat("pressure",mainObj));
            weather.currentCondition.setMinTemp(utils.getFloat("temp_min",mainObj));
            weather.currentCondition.setMaxTemp(utils.getFloat("temp_max",mainObj));


            //get Wind Object Info
            JSONObject jsonWind = utils.getObject("wind",jsonObject);
            Wind wind = new Wind();
            wind.setSpeed(utils.getFloat("speed",jsonWind));
            wind.setDegree(utils.getFloat("deg",jsonWind));
            weather.wind = wind; //attach this Wind info to our weather

            JSONObject jsonClouds = utils.getObject("clouds",jsonObject);
            weather.clouds.setPrecipitation(utils.getInt("all",jsonClouds));

            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
