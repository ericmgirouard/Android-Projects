package data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by egirouard on 5/9/2017.f
 */

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity act) {
        prefs = act.getPreferences(Activity.MODE_PRIVATE);


    }

    public String getCity() {
        //return the stored city, if there isnt one, return the default of Boston, MA
        return prefs.getString("city","Boston,US");//boston is defualt

    }

    public void setCity(String city) {
        prefs.edit().putString("city",city).apply();
    }
}
