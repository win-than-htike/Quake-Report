package xyz.winthan.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win Than on 11/29/2016.
 */
public class QueryUtils {

    private static final String LOG_TAG = "Earthquake Report";

    private QueryUtils(){}

//    public static ArrayList<Earthquake> extractEarthquakes(){
//
//        ArrayList<Earthquake> earthquakes = new ArrayList<>();
//
//        try {
//
//            JSONObject root = new JSONObject(SAMPLE_JSON_RESPONSE);
//            JSONArray features = root.getJSONArray("features");
//
//            for (int i = 0; i < features.length(); i++){
//
//                JSONObject earthquake = features.getJSONObject(i);
//                JSONObject properties = earthquake.getJSONObject("properties");
//                double mag = properties.getDouble("mag");
//                String place = properties.getString("place");
//                long time = properties.getLong("time");
//                String url = properties.getString("url");
//                earthquakes.add(new Earthquake(mag,place,time,url));
//
//            }
//
//        } catch (JSONException e) {
//            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
//        }
//
//        return earthquakes;
//
//    }

    private static URL createUrl(String stringUrl){

        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{

        String jsonResponse = null;

        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromString(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {

            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if (inputStream != null){
                inputStream.close();
            }

        }

        return jsonResponse;

    }

    private static String readFromString(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        if (inputStream != null){

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();

    }

    private static List<Earthquake> extractFeatureFromJson(String earthquakeJson){

        if (TextUtils.isEmpty(earthquakeJson)){
            return null;
        }

        List<Earthquake> earthquakeList = new ArrayList<>();

        try {

            JSONObject root = new JSONObject(earthquakeJson);
            JSONArray features = root.getJSONArray("features");

            for (int i = 0; i < features.length(); i++){

                JSONObject earthquake = features.getJSONObject(i);
                JSONObject properties = earthquake.getJSONObject("properties");
                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");
                earthquakeList.add(new Earthquake(mag,place,time,url));

            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return earthquakeList;

    }

     public static List<Earthquake> fetchEarthquakeData(String requestUrl){

         try {
             Thread.sleep(2000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }


         URL url = createUrl(requestUrl);

         String jsonResponse = null;

         try {
             jsonResponse = makeHttpRequest(url);
         } catch (IOException e) {
             e.printStackTrace();
         }

         List<Earthquake> earthquakes = extractFeatureFromJson(jsonResponse);

         return earthquakes;

     }

}
