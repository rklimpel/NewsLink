package de.ricoklimpel.newslink;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import java.util.ArrayList;
import static de.ricoklimpel.newslink.SidemenuFragment.checkedSources;


public class MainActivity extends AppCompatActivity {

    //Pulldown to refresh Layout
    public static SwipeRefreshLayout mSwipeRefreshLayout;

    //News Sources Lists (static)
    public static ArrayList<NewsSource> newsSources;
    public static ArrayList<NewsSource> newsSourcesChecked;

    //Save Context for static usage
    static Context context;

    //for Sidemenu
    static RecyclerView recyclerView;
    static RelativeLayout relativeLayout;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView.LayoutManager recylerViewLayoutManager;

    static LeftDrawerLayout mLeftDrawerLayout;
    static SidemenuFragment mMenuFragment;
    static FragmentManager fm;
    static FlowingView mFlowingView;

    static TextView tv_maininfotext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = this.getSupportActionBar();

        context = getApplicationContext();

        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        initSwipeRefresh();

        mLeftDrawerLayout = (LeftDrawerLayout)findViewById(R.id.id_drawerlayout);
        fm = getSupportFragmentManager();
        mMenuFragment = (SidemenuFragment) fm.findFragmentById(R.id.id_container_menu);
        mFlowingView = (FlowingView)findViewById(R.id.sv);

        newsSources = new ArrayList<>();
        newsSourcesChecked = new ArrayList<>();
        newsSources = LocalStorage.LoadNewsSources(this,Utils.PREF_ID_SOFURCES_ALL);
        newsSourcesChecked = LocalStorage.LoadNewsSources(this, Utils.PREF_ID_SOURCES_CHECKED);

        tv_maininfotext = (TextView)findViewById(R.id.tv_maininfotext);

        if(newsSources != null){
            initDrawerLayout();
        }


        //If Network is Ok first time download news list
        if (checkNetwork()) {
            reload();
        } else {
            Toast.makeText(context, "No network connection available.", Toast.LENGTH_LONG).show();
        }

        //Load Checked Items from Shared Preferences and Store them Back into Boolean Array
        checkedSources = LocalStorage.StringToBoolArray(LocalStorage.loadArray("checkedSources", context));
        if (checkedSources.length == 0 && newsSources != null) {
            //If there is no data saved in shared preferences init first dataset:
            checkedSources = new Boolean[newsSources.size()];
            for (int i = 0; i < checkedSources.length; i++) {checkedSources[i] = false;
            }
            //Save Checked Items to Shared Preferences
            LocalStorage.saveArray(LocalStorage.BoolToStringArray(checkedSources), "checkedSources", context);
        }

    }

    /**
     * Init Swipe Refresh Layout
     *
     * On Pulldown refresh newslist
     */
    private void initSwipeRefresh() {

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

    /**
     * Init the left side elastic Drawer Layout and set it's Fragment
     *
     * In it you can see complete news sources
     * On Open -> Sidemneu Fragment
     */
    public static void initDrawerLayout() {

        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new SidemenuFragment()).commit();
        }
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
    }

    /**
     * initializes the RecyclerView with News Data in the Main Window
     * should be called every time we get new Data
     *
     * splits the newsArticle Object in Arrays for each component
     *
     * @param newsArticles
     */
    public static void initRecyclerView(ArrayList<NewsArticle> newsArticles) {

        mSwipeRefreshLayout.setRefreshing(false);


        recyclerViewAdapter = new NewsRecycleAdapter(context, newsArticles);
        recyclerView.setAdapter(recyclerViewAdapter);

        if(newsSourcesChecked.size()==0){
            tv_maininfotext.setVisibility(View.VISIBLE);
        }else{
            tv_maininfotext.setVisibility(View.GONE);
        }


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
     * Refeshing NewsList
     *
     * Open Async GetSourceIDs
     * => this one will get All Source ID'S from the API and
     * open Async GetSelectedSources
     * =>  this one will get users selection of sources from Shared Preferences and create an
     * ArrayList with alle the selected source ID'S
     * open Async Get Article
     * => this one will get all Articles for each of the selected sources
     *
     * this method starts the process i described here
     *
     */
    public static void reload() {

        mSwipeRefreshLayout.setRefreshing(true);

        setupCheckedNewsSources();

        if(newsSources==null){
            AsyncGetSourceIDs atask = new AsyncGetSourceIDs();
            atask.executeOnExecutor(AsyncGetSourceIDs.THREAD_POOL_EXECUTOR);
        }else if(newsSourcesChecked == null) {
            AsyncGetSelectedSources atask = new AsyncGetSelectedSources();
            atask.executeOnExecutor(AsyncGetSelectedSources.THREAD_POOL_EXECUTOR,newsSources);
        }else{
            AsyncGetArticle atask = new AsyncGetArticle();
            atask.executeOnExecutor(AsyncGetArticle.THREAD_POOL_EXECUTOR,newsSourcesChecked);
        }

    }

    /**
     *
     * Get Screen Dimensions in Pixels as Array
     *
     * @return [0] = width , [1] = height
     */
    public int[] getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new int[]{width,height};
    }

    public static void setupCheckedNewsSources() {

        ArrayList<NewsSource> checkedNewsSources = new ArrayList<>();

        Boolean[] checkedSourcesIDs =  LocalStorage.StringToBoolArray(
                LocalStorage.loadArray("checkedSources",context));

        if(checkedSourcesIDs.length != 0){
            for (int i = 0; i < checkedSourcesIDs.length; i++) {
                if(checkedSourcesIDs[i]){
                    checkedNewsSources.add(newsSources.get(i));
                }
            }
        }

        newsSourcesChecked = checkedNewsSources;

    }

    public static void setupCheckedNewsSources(Boolean[] checkedSourcesBool){

        ArrayList<NewsSource> checkedNewsSources = new ArrayList<>();

        if(checkedSourcesBool.length != 0){
            for (int i = 0; i < checkedSourcesBool.length; i++) {
                if(checkedSourcesBool[i]){
                    checkedNewsSources.add(newsSources.get(i));
                }
            }
        }

        newsSourcesChecked = checkedNewsSources;
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_newsources:
                if(!SidemenuFragment.isOpen){
                    mLeftDrawerLayout.openDrawer();
                }else{
                    mLeftDrawerLayout.closeDrawer();
                }
                break;
            case R.id.action_info:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

