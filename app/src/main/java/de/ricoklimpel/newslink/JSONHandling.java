package de.ricoklimpel.newslink;

import android.support.annotation.ArrayRes;
import android.support.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static de.ricoklimpel.newslink.R.id.tv_output;

/**
 * Created by ricoklimpel on 02.12.16.
 */

public class JSONHandling {

    public static String[] ArrayfromJSONString(String string) {

        String[] subjects = new String[0];

        try {

            JSONObject obj = new JSONObject(string);

            //String pageName = obj.getJSONObject("articles").getString("title");
            //tv_output.setText(pageName + "\n\n" + result);

            JSONArray arr = obj.getJSONArray("articles");
            subjects = new String[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                subjects[i] = arr.getJSONObject(i).getString("title");
            }

        } catch (JSONException e) {

        }

        return subjects;
    }

}
