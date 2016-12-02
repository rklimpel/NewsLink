package de.ricoklimpel.newslink;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static de.ricoklimpel.newslink.R.id.title;
import static de.ricoklimpel.newslink.R.id.tv_output;

/**
 * Created by ricoklimpel on 02.12.16.
 */

class DownloadWebContent extends AsyncTask<String, Void, String> {

    String ServerResponse;

    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {

            URL url = new URL(urls[0]);

            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                ServerResponse = readStream(in);
                //Log.e("String", ServerResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return ServerResponse;

        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {

        MainActivity.initRecyclerView(JSONHandling.ArrayfromJSONString(result,"title"),
                JSONHandling.ArrayfromJSONString(result,"description"),
                JSONHandling.ArrayfromJSONString(result,"url"));

    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
