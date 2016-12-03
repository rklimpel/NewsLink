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
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import java.util.ArrayList;
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

    static final String CALLER_ID_NEWSLIST = "0";
    static final String CALLER_ID_SOURCEID = "2";
    static final String CALLER_ID_NEWSLISTBUILDER = "3";

    public static String[] SourceIDs;
    public static Boolean[] checkedSources;

    public static ArrayList<String> allTitles;
    public static ArrayList<String> allDescriptions;
    public static ArrayList<String> allUrls;
    public static ArrayList<String> allImageUrls;
    public static ArrayList<String> allTimestamps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        allTitles = new ArrayList<>();
        allDescriptions = new ArrayList();
        allUrls= new ArrayList();
        allImageUrls = new ArrayList();
        allTimestamps = new ArrayList<>();

        //Init Side and Refresh View
        initDrawerLayout();
        initSwipeRefresh();

        //If Network is Ok first time download news list
        if(checkNetwork()){
            getSourceIDs();
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
                    //If Network is Ok first time download news list
                    if(checkNetwork()){
                        getSourceIDs();
                    }else{
                        Toast.makeText(context, "No network connection available.", Toast.LENGTH_LONG).show();
                    }
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
    public static void onPostDownloadNews(String data){

        MainActivity.mWaveSwipeRefreshLayout.setRefreshing(false);

        initRecyclerView(JSONHandling.ArrayfromJSONString(data,"articles","title"),
                JSONHandling.ArrayfromJSONString(data,"articles" ,"description"),
                JSONHandling.ArrayfromJSONString(data,"articles","url"),
                JSONHandling.ArrayfromJSONString(data,"articles","urlToImage"),
                JSONHandling.ArrayfromJSONString(data,"articles","publishedAt"));
    }

    /**
     * initializes the RecyclerView with News Data in the Main Window
     *
     * @param content Array of news Titles
     * @param description Array of news descriptions
     * @param links Array of news links
     * @param imageURLs Array of news image urls
     */
    public static void initRecyclerView(String[] content, String[] description,String[] links,String[] imageURLs, String[] timestamps) {

        recyclerViewAdapter = new NewsRecycleAdapter(context, content,description,links,imageURLs,timestamps);
        recyclerView.setAdapter(recyclerViewAdapter);

        mWaveSwipeRefreshLayout.setRefreshing(false);
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

    /**
     *
     * Download SourceID's for every Source
     *
     * Von hier an wird ein längerer Prozess in gang gesetzt, es werden die hier drunter liegenden
     * Methoden automatisch auch aufgerufen, diese Methode ist als die eigentliche Refresh Newslist
     * Methode!!!!
     *
     */
    public static void getSourceIDs(){

        String Url = "https://newsapi.org/v1/sources?language=";
        new DownloadWebContent().execute(Url,CALLER_ID_SOURCEID);

        //Clear Array Lists, so the previews ones are not before the new load
        allTitles.clear();
        allDescriptions.clear();
        allUrls.clear();
        allImageUrls.clear();
        allTimestamps.clear();
    }

    /**
     *
     * Callback grom getSourceID's after download Source IDs
     * Saves checked sources id's in an arraylist and get start the download
     * process for every single source of this list
     *
     * @param data
     */
    public static void onPostDownloadSources(String data){

        if(JSONHandling.checkAPIStatus(data)){
            SourceIDs = JSONHandling.ArrayfromJSONString(data,"sources","id");
        }

        //Load Checked Items from Shared Preferences and Store them Back into Boolean Array
        checkedSources = LocalStorage.StringToBoolArray(LocalStorage.loadArray(SourcesRecycleAdapter.PREFSNAME,context));
        if(checkedSources[0]==null){
            //If there is no data saved in shared preferences init first dataset:
            checkedSources = new Boolean[SourceIDs.length];
            for (int i = 0; i < checkedSources.length; i++) {
                checkedSources[i]=false;
            }
            //Save Checked Items to Shared Preferences
            LocalStorage.saveArray(LocalStorage.BoolToStringArray(checkedSources),SourcesRecycleAdapter.PREFSNAME,context);
        }

        //Create a List with all checked Source IDs
        ArrayList setSourceIDs = new ArrayList();
        int x=0;
        for (int i = 0; i < checkedSources.length ; i++) {
            if(checkedSources[i]){
                setSourceIDs.add(SourceIDs[i]);
                x++;
            }
        }

        //Download Articles for every of the Sources
        for (int i = 0; i < setSourceIDs.size(); i++) {
            new DownloadWebContent().execute("https://newsapi.org/v1/articles?source="+
                    setSourceIDs.get(i)+"&apiKey=bddae599de5041ab9858c74961886e6c",CALLER_ID_NEWSLISTBUILDER);
        }
    }

    /**
     *
     * Gets called after every download from one sources articles
     * add sources articles to array list and show complete array list in Recycler View
     *
     * //TODO Sort Array List for TIMESTAMP
     *
     * @param data
     */
    public static void onPostDownloadBUILDER(String data) {

        //Get Arrays from JSON Document
        if(JSONHandling.checkAPIStatus(data)){
            String[] Title = JSONHandling.ArrayfromJSONString(data,"articles","title");
            String[] Description = JSONHandling.ArrayfromJSONString(data,"articles" ,"description");
            String[] Url =  JSONHandling.ArrayfromJSONString(data,"articles","url");
            String[] ImageUrl = JSONHandling.ArrayfromJSONString(data,"articles","urlToImage");
            String[] Timestamp = JSONHandling.ArrayfromJSONString(data,"articles","publishedAt");

            //Add Arrays from the one downloaded source to Array List with all sources
            for (int i = 0; i < Title.length ; i++) {

                allTitles.add(Title[i]);
                allDescriptions.add(Description[i]);
                allUrls.add(Url[i]);
                allImageUrls.add(ImageUrl[i]);
                allTimestamps.add(Timestamp[i]);
            }
        }

        //Show ArrayList Data in Newsline
        initRecyclerView(allTitles.toArray(new String[allTitles.size()]),
                allDescriptions.toArray(new String[allTitles.size()]),
                allUrls.toArray(new String[allTitles.size()]),
                allImageUrls.toArray(new String[allTitles.size()]),
                allTimestamps.toArray(new String[allTimestamps.size()]));
    }
}