package de.ricoklimpel.newslink;

import android.os.AsyncTask;
import android.util.Log;

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

    /**
     * Caller ID is the argument on index [1] in the urls argument
     * used to tell onPostExecute the class he should return to
     *
     * ID = 0 : MainActivity
     * ID = 1 : SidemnuFragment
     * ID = 999: no CallerID
     */
    public int CallerID;


    /**
     *
     *Download Task in Background,
     *Calls the API with URL from Argument
     *
     * @param urls
     * @return
     */
    @Override
    protected String doInBackground(String... urls) {

        CallerID = 999;

        if(urls[1]!=null){
            CallerID = Integer.parseInt(urls[1]);
        }
        Log.e("CallerID",String.valueOf(CallerID));

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

        if (JSONHandling.checkAPIStatus(result)){
            switch (CallerID){
                case 0 :
                    MainActivity.onPostDownloadNews(result);
                    break;
                case 1 :
                    SidemenuFragment.onPostDownload(result);
                    break;
                case 2 :
                    MainActivity.onPostDownloadSources(result);
                case 3:
                    MainActivity.onPostDownloadBUILDER(result);
                    break;
                default:
                    //No CallerID
                    break;
            }
        }else{
            //ERROR
        }

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
}


