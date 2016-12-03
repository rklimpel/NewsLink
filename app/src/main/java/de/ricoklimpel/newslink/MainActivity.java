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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    //Pulldown to refresh Layout
    public static WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    //Save Context for static usage
    static Context context;

    //for Sidemenu
    static RecyclerView recyclerView;
    static RelativeLayout relativeLayout;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView.LayoutManager recylerViewLayoutManager;

    /**
     *
     * "Caller ID'S"
     *
     * i use them as Argument for the Download task, to decide onPost Execute of the
     * download task to which Method he should return after finished
     *
     * SOURCE ID : download task return to onPostDownloadSources
     * NEWSLISTBUILDER : download task returns to onPostDownloadBUILDER
     *
     */
    static final String CALLER_ID_SOURCEID = "2";
    static final String CALLER_ID_NEWSLISTBUILDER = "3";

    /**
     *Write a List of all API Sources ID's in SourceIDs and filter them
     * with checkedSources which will be loaded from Shared Preferences
     *
     */
    public static String[] SourceIDs;
    public static Boolean[] checkedSources;

    /**
     *
     * absolute List of all Newlist objects
     * after downlaoding one Source it will be added up to this List
     * the next one will also be added and so on
     *
     * Must be cleard before making a new Newlist request, else the new data
     * will be set to the end of the existing list
     *
     */
    public static ArrayList<NewsArticle> newsArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        newsArticles = new ArrayList<NewsArticle>();

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
     * On Pulldown refresh newslist
     *
     */
    private void initSwipeRefresh() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setWaveColor(getResources().getColor(R.color.colorPrimary));

        if(checkNetwork()){
            mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
                @Override public void onRefresh() {
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
     * In it you can see complete news sources
     * On Open -> Sidemneu Fragment
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
     * initializes the RecyclerView with News Data in the Main Window
     * Will be called after every reload and build a new List
     *
     * @param content Array of news Titles
     * @param description Array of news descriptions
     * @param links Array of news links
     * @param imageURLs Array of news image urls
     */
    public static void initRecyclerView(ArrayList<NewsArticle> newsArticles) {

        String[] titles = new String[newsArticles.size()];
        String[] descriptions = new String[newsArticles.size()];
        String[] urls = new String[newsArticles.size()];
        String[] imageurls = new String[newsArticles.size()];
        String[] timestamps = new String[newsArticles.size()];


        for (int i = 0; i < newsArticles.size(); i++) {

            NewsArticle newsarticle;
            newsarticle = newsArticles.get(i);

            titles[i] = newsarticle.getTitle();
            descriptions[i] = newsarticle.getDescription();
            urls[i] = newsarticle.getUrl();
            imageurls[i] = newsarticle.getImageUrl();
            timestamps[i] = newsarticle.getTimestamp();
        }

        recyclerViewAdapter = new NewsRecycleAdapter(context, titles,descriptions,urls,imageurls,timestamps);
        recyclerView.setAdapter(recyclerViewAdapter);

        mWaveSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     *
     * Gets called by ListAdapter Class
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
     * Check phones Network Status
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

        mWaveSwipeRefreshLayout.setRefreshing(true);

        String Url = "https://newsapi.org/v1/sources?language=";
        new DownloadWebContent().execute(Url,CALLER_ID_SOURCEID);

        //Clear Array List, so the previews ones are not still there on new load
        newsArticles.clear();
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
        }else{
            //ERROR
        }
    }

    /**
     *
     * Gets called after every download from one sources articles
     * add sources articles to array list and show complete array list in Recycler View
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
                newsArticles.add(new NewsArticle(Title[i],Description[i],Url[i],ImageUrl[i],Timestamp[i],null,null));
            }

            // Sorting List for newest Date
            Collections.sort(newsArticles);
            Collections.reverse(newsArticles);

            //Show ArrayList Data in Newsline
            initRecyclerView(newsArticles);

        }else{
            //API ERROR
        }
    }
}