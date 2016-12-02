package de.ricoklimpel.newslink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ricoklimpel on 02.12.16.
 */

public class JSONHandling {

    /**
     *
     * @param string the hole string with json data
     * @param value Posible values are for example title or description
     * @return array of elements value
     */
    public static String[] ArrayfromJSONString(String string,String value) {

        String[] subjects = new String[0];

        try {

            JSONObject obj = new JSONObject(string);
            //String pageName = obj.getJSONObject("articles").getString("title");
            //tv_output.setText(pageName + "\n\n" + result);

            JSONArray arr = obj.getJSONArray("articles");
            subjects = new String[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                subjects[i] = arr.getJSONObject(i).getString(value);
            }

        } catch (JSONException e) {

        }

        return subjects;
    }

}
