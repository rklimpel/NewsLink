package de.ricoklimpel.newslink;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView tv_output;
    String ServerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_output = (TextView)findViewById(R.id.tv_output);

        // Gets the URL from the UI's text field.
        String stringUrl = "https://newsapi.org/v1/articles?source=bild&apiKey=bddae599de5041ab9858c74961886e6c";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            tv_output.setText("No network connection available.");
        }


    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
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
                    Log.e("String", ServerResponse);
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    urlConnection.disconnect();
                }
                return ServerResponse;

            }catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try{

                JSONObject obj = new JSONObject(result);

                //String pageName = obj.getJSONObject("articles").getString("title");
                //tv_output.setText(pageName + "\n\n" + result);

                JSONArray arr = obj.getJSONArray("articles");
                for (int i = 0; i < arr.length(); i++)
                {
                    String post_title = arr.getJSONObject(i).getString("title");
                    tv_output.setText(tv_output.getText()+ "\n\n" + post_title);
                    String post_description = arr.getJSONObject(i).getString("description");
                    tv_output.setText(tv_output.getText()+ "\n\n" + post_description);

                }

            }catch (JSONException e){

                tv_output.setText(e.getMessage());
            }
        }
    }


    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


}
