package de.ricoklimpel.newslink;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    public static WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    static Context context;
    static RecyclerView recyclerView;
    static RelativeLayout relativeLayout;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView.LayoutManager recylerViewLayoutManager;
    // Gets the URL from the UI's text field.
    final String stringUrl = "https://newsapi.org/v1/articles?source=spiegel-online&apiKey=bddae599de5041ab9858c74961886e6c";

    final String CALLER_ID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        //Init Side and Refresh View
        initDrawerLayout();
        initSwipeRefresh();

        //If Network is Ok first time download news list
        if(checkNetwork()){
            new DownloadWebContent().execute(stringUrl,CALLER_ID);
        }else{
            Toast.makeText(context, "No network connection available.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Init Swipe Refresh Layout
     */
    private void initSwipeRefresh() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setWaveColor(getResources().getColor(R.color.colorPrimary));

        if(checkNetwork()){
            mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
                @Override public void onRefresh() {

                    new DownloadWebContent().execute(stringUrl,CALLER_ID);

                }
            });
        }

    }

    /**
     * Init the left side elastic Drawer Layout and set it's Fragment
     *
     */
    private void initDrawerLayout() {
        LeftDrawerLayout mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        FragmentManager fm = getSupportFragmentManager();
        SidemenuFragment mMenuFragment = (SidemenuFragment) fm.findFragmentById(R.id.id_container_menu);
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new SidemenuFragment()).commit();
        }
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
    }


    /**
     * Will be called DownloadWebContent Class after finishing the downloads
     *
     * @param data is the complete JSON String, form API
     */
    public static void onPostDownload(String data){

        MainActivity.mWaveSwipeRefreshLayout.setRefreshing(false);

        initRecyclerView(JSONHandling.ArrayfromJSONString(data,"articles","title"),
                JSONHandling.ArrayfromJSONString(data,"articles" ,"description"),
                JSONHandling.ArrayfromJSONString(data,"articles","url"),
                JSONHandling.ArrayfromJSONString(data,"articles","urlToImage"));

    }

    /**
     * initializes the RecyclerView with News Data in the Main Window
     *
     * @param content Array of news Titles
     * @param description Array of news descriptions
     * @param links Array of news links
     * @param imageURLs Array of news image urls
     */
    public static void initRecyclerView(String[] content, String[] description,String[] links,String[] imageURLs) {

        recyclerViewAdapter = new NewsRecycleAdapter(context, content,description,links,imageURLs);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    /**
     *
     * Gets called by Download Task
     * opens users default phone webbrowser with new content
     *
     * @param link link to open
     */
    public static void openWebrowser(String link) {

        //Open default Webbrowser:
        String url = link;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }

    /**
     * Check Network Status
     *
     * @return true for Network, false for no Network
     */
    private Boolean checkNetwork(){

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
