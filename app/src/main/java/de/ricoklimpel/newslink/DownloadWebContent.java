package de.ricoklimpel.newslink;

import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ricoklimpel on 02.12.16.
 */

class DownloadWebContent extends AsyncTask<String, Void, String> {

    String ServerResponse;
    URL url;

    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {

            url = new URL(urls[0]);

            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                ServerResponse = readStream(in);
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


    @Override
    protected void onPostExecute(String result) {

        if(url.toString().contains("articles")){
            MainActivity.mWaveSwipeRefreshLayout.setRefreshing(false);

            MainActivity.initRecyclerView(JSONHandling.ArrayfromJSONString(result,"articles","title"),
                    JSONHandling.ArrayfromJSONString(result,"articles" ,"description"),
                    JSONHandling.ArrayfromJSONString(result,"articles","url"),
                    JSONHandling.ArrayfromJSONString(result,"articles","urlToImage"));
        }else{

            BlankFragment.onPostDownload(result);

        }



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


