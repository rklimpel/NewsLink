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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

import static de.ricoklimpel.newslink.SourcesRecycleAdapter.PREFSNAME;
import static de.ricoklimpel.newslink.DownloadWebContent.*;

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
     * Write a List of all API Sources ID's in SourceIDs and filter them
     * with checkedSources which will be loaded from Shared Preferences
     */
    public static String[] SourceIDs;

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
        if (checkNetwork()) {
            reload();
        } else {
            Toast.makeText(context, "No network connection available.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Init Swipe Refresh Layout
     * On Pulldown refresh newslist
     */
    private void initSwipeRefresh() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setWaveColor(getResources().getColor(R.color.colorPrimary));

        if (checkNetwork()) {
            mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (checkNetwork()) {
                        reload();
                    } else {
                        Toast.makeText(context, "No network connection available.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * Init the left side elastic Drawer Layout and set it's Fragment
     * <p>
     * In it you can see complete news sources
     * On Open -> Sidemneu Fragment
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
     * @param newsArticles
     */
    public static void initRecyclerView(ArrayList<NewsArticle> newsArticles) {

        String[] titles = new String[newsArticles.size()];
        String[] descriptions = new String[newsArticles.size()];
        String[] urls = new String[newsArticles.size()];
        String[] imageurls = new String[newsArticles.size()];
        String[] timestamps = new String[newsArticles.size()];
        String[] sources = new String[newsArticles.size()];

        for (int i = 0; i < newsArticles.size(); i++) {

            NewsArticle newsarticle;
            newsarticle = newsArticles.get(i);

            titles[i] = newsarticle.getTitle();
            descriptions[i] = newsarticle.getDescription();
            urls[i] = newsarticle.getUrl();
            imageurls[i] = newsarticle.getImageUrl();
            timestamps[i] = newsarticle.getTimestamp();
            sources[i] = newsarticle.getSource();
        }

        mWaveSwipeRefreshLayout.setRefreshing(false);

        recyclerViewAdapter = new NewsRecycleAdapter(context, titles, descriptions, urls, imageurls, timestamps, sources);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    /**
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
    private Boolean checkNetwork() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Download SourceID's for every Source
     * *
     */
    public synchronized static String[] getSourceIDs() {

        String Url = "https://newsapi.org/v1/sources?language=";
        String data = downloadUrlData(Url);

        if (JSONHandling.checkAPIStatus(data)) {
            return JSONHandling.ArrayfromJSONString(data, "sources", "id");

        } else {
            return null;
        }
    }

    /**
     * Gives back all checked sources of sources list
     * what is checked? Load checked Sources from SharedPreferences
     *
     * @param allsources List of all sources
     * @return List of all checked sources
     */
    public synchronized static ArrayList<String> getcheckedSources(String[] allsources) {

        Boolean[] checkedSources = null;

        //Load Checked Items from Shared Preferences and Store them Back into Boolean Array
        checkedSources = LocalStorage.StringToBoolArray(
                LocalStorage.loadArray(PREFSNAME, context));

        if (checkedSources.length==0) {
            //If there is no data saved in shared preferences init first dataset:
            checkedSources = new Boolean[allsources.length];
            for (int i = 0; i < checkedSources.length; i++) {
                checkedSources[i] = false;
            }
            //Save Checked Items to Shared Preferences
            LocalStorage.saveArray(LocalStorage.BoolToStringArray(checkedSources),
                    PREFSNAME, context);
        }

        //Create a List with all checked Source IDs
        ArrayList<String> activeSources = new ArrayList();
        for (int i = 0; i < checkedSources.length; i++) {
            if (checkedSources[i]) {
                activeSources.add(SourceIDs[i]);
                Log.e("checkedSources ", SourceIDs[i]);
            }
        }

        for (int i = 0; i < activeSources.size(); i++) {
            Log.e("checked Sources "+ i,activeSources.get(i));

        }
        Log.e("Size",activeSources.size()+"");

        return activeSources;
    }

    /**
     *
     * Load all Article from checked Sources and put them in one big ArrayList,
     * sorted by Timestamp
     *
     * @param checkedSources
     * @return
     */
    public synchronized static ArrayList<NewsArticle> loadArticles(ArrayList<String> checkedSources) {

        ArrayList<NewsArticle> newsArticles = new ArrayList<>();
        newsArticles.clear();

        Log.e("Size 2",checkedSources.size()+"");
        for (int i = 0; i < checkedSources.size(); i++) {

            //Get Data for this source
            Log.e("checkedSoure " +i,checkedSources.get(i).toString());
            String data = downloadUrlData("https://newsapi.org/v1/articles?source=" +
                    checkedSources.get(i) + "&apiKey=bddae599de5041ab9858c74961886e6c");

            if (JSONHandling.checkAPIStatus(data)) {
                //Get Arrays with specific Values from Json document
                String[] Title = JSONHandling.ArrayfromJSONString(data, "articles", "title");
                String[] Description = JSONHandling.ArrayfromJSONString(data, "articles", "description");
                String[] Url = JSONHandling.ArrayfromJSONString(data, "articles", "url");
                String[] ImageUrl = JSONHandling.ArrayfromJSONString(data, "articles", "urlToImage");
                String[] Timestamp = JSONHandling.ArrayfromJSONString(data, "articles", "publishedAt");
                String source = JSONHandling.JsonInfo(data, "source");

                //Add Arrays from the one downloaded source to Array List with all sources
                for (int x = 0; x < Title.length; x++) {
                    newsArticles.add(new NewsArticle(Title[x], Description[x], Url[x], ImageUrl[x],
                            Timestamp[x], null, source,Utils.convertToReadableDate(Timestamp[x])));
                }
            }
        }

        if (newsArticles.size() > 0) {
            // Sorting List for newest Date
            //Collections.sort(newsArticles);
            Collections.sort(newsArticles, new Comparator<NewsArticle>() {
                @Override
                public int compare(NewsArticle o1, NewsArticle o2) {
                    if(o1.getDatetimestamp().before(o2.getDatetimestamp())){
                        return -1;
                    }else if (o2.getDatetimestamp().equals(o2.getDatetimestamp())){
                        return 0;
                    }else{
                        return 1;
                    }
                }
            });

            /*
            //Sort For Titles
            Collections.sort(newsArticles, new Comparator<NewsArticle>() {
                @Override
                public int compare(NewsArticle o1, NewsArticle o2) {
                    String a=o1.getTitle();
                    String b = o2.getTitle();
                    int compare = a.compareTo(b);
                    if (compare < 0)
                    {
                        return +1;
                    }
                    else
                    {
                        if (compare > 0)

                            return -1;
                    }
                    return 0;
            }
            });*/

            Collections.reverse(newsArticles);

        }

        return newsArticles;
    }

    /**
     *
     * Refeshing NewsList
     *
     */
    public static void reload() {

        mWaveSwipeRefreshLayout.setRefreshing(true);
        ArrayList<NewsArticle> asdf = new ArrayList<>();

        SourceIDs = getSourceIDs();
        asdf = loadArticles(getcheckedSources(SourceIDs));
        initRecyclerView(asdf);

    }
}