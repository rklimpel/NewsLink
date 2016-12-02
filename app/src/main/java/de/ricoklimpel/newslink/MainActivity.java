package de.ricoklimpel.newslink;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tv_output;


    static Context context;
    static RecyclerView recyclerView;
    static RelativeLayout relativeLayout;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView.LayoutManager recylerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout1);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);

        recylerViewLayoutManager = new LinearLayoutManager(context);

        recyclerView.setLayoutManager(recylerViewLayoutManager);


        tv_output = (TextView) findViewById(R.id.tv_output);

        // Gets the URL from the UI's text field.
        String stringUrl = "https://newsapi.org/v1/articles?source=spiegel-online&apiKey=bddae599de5041ab9858c74961886e6c";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadWebContent().execute(stringUrl);

        } else {
            Toast.makeText(context, "No network connection available.", Toast.LENGTH_LONG).show();
        }


    }

    public static void initRecyclerView(String[] content, String[] description,String[] links,String[] imageURLs) {

        recyclerViewAdapter = new NewsRecycleAdapter(context, content,description,links,imageURLs);

        recyclerView.setAdapter(recyclerViewAdapter);

    }

    public static void openWebrowser(String link){
        String url = link;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }







}
