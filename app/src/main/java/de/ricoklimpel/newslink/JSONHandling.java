package de.ricoklimpel.newslink;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ricoklimpel on 02.12.16.
 */

public class JSONHandling {

    /**
     *
     * Formats specific Parts of the whole JSON Text file to String Arrays,
     * e.g. alle News Titles or alle News Descriptions
     *
     * @param string the hole string with json data
     * @param value1 First Tree Element in JSON Strukture
     * @param value2 Second Tree Element in JSON Strukture
     * @return array of elements value
     */
    public static String[] ArrayfromJSONString(String string,String value1 ,String value2) {

        String[] subjects = new String[0];

        try {

            JSONObject obj = new JSONObject(string);
            JSONArray arr = obj.getJSONArray(value1);
            subjects = new String[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                subjects[i] = arr.getJSONObject(i).getString(value2);
            }

        } catch (JSONException e) {

        }

        return subjects;
    }

    /**
     *
     * Same Mathod as above, but one element deeper in JSON structure
     *
     * @param string
     * @param value1
     * @param value2
     * @param value3
     * @return
     */
    public static String[] ArrayfromJSONString(String string,String value1 ,String value2, String value3) {

        String[] subjects = new String[0];

        try {

            JSONObject obj = new JSONObject(string);
            JSONArray arr = obj.getJSONArray(value1);
            subjects = new String[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                subjects[i] = arr.getJSONObject(i).getJSONObject(value2).getString(value3);
                Log.e(i + "",subjects[i]);
            }

        } catch (JSONException e) {

        }

        return subjects;
    }

    /**
     *
     * Return a Info value of the First Layer of Json file
     * e.g. status check, source id , or whatever
     *
     * @param json
     * @param info
     * @return
     */
    public static String JsonInfo(String json,String info){
        String jsonInfo = null;
        try {
            JSONObject obj = new JSONObject(json);
            jsonInfo = obj.getString(info);
        } catch (JSONException e) {

        }
        return jsonInfo;
    }

    /**
     *
     * Check API, if there has been an http request api Error it return error instead of ok
     *
     * @param json
     * @return true for ok, false for error
     */
    public static Boolean checkAPIStatus(String json) {

        String status = null;
        try {
            JSONObject obj = new JSONObject(json);
            status = obj.getString("status");
        } catch (JSONException e) {

        }

        return (status.contains("ok"));
    }

}
