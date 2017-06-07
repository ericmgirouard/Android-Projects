package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import util.utils;

/**
 * Created by egirouard on 5/8/2017.
 * heart of the APP, will fetch and parse JSON object from the openWeatherApp.org API
 */

public class WeatherHTTPClient {

    public String getWeatherData(String place) {
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            connection = (HttpURLConnection) (new URL(utils.BASE_URL + place)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            //now read response from URL
            StringBuffer stringBuffer = new StringBuffer();
            input = connection.getInputStream();//connect web API inputStream to local InputStream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));//the reader that can read the bits from the inputStream from the Web API
            String line = null;
            //while there is data in the input to Read from
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\r\n"); //append newline after each line from input

            }
            input.close();
            connection.disconnect();
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }//end getWeatherData
}
