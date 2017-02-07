package de.ricoklimpel.newslink;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by ricoklimpel on 02.12.16.
 */

class DownloadWebContent extends AsyncTask<String, Void, String> {

    String ServerResponse;

    /**
     *
     *Return an String from a URL in Background job
     *
     * @param urls url to recieve data from
     * @return String with (mostly) json document
     *
     */
    @Override
    protected synchronized String doInBackground(String... urls) {

        Log.d(getClass().getName(),String.valueOf(urls[0]));

        // params comes from the execute() call: params[0] is the url.
        try {

            URL url = new URL(urls[0]);

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

    /**
     * Called after finishing download Task
     *
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {

    }

    /**
     *
     * Input Stream Reader for http url connection
     *
     * @param is
     * @return
     */
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
            return "could not read URL Stream";
        }
    }

    /**
     * Download String from Argument URL
     * Helper for openeing Async task with less code and get callback
     *
     * @param url
     * @return
     */
    public static String downloadUrlData(String url){

        DownloadWebContent download = new DownloadWebContent();
        download.execute(url);
        try {
            return download.get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }
}


