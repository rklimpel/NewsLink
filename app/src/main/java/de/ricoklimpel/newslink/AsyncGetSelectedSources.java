package de.ricoklimpel.newslink;

import android.util.Log;
import java.util.ArrayList;

import static de.ricoklimpel.newslink.MainActivity.context;
import static de.ricoklimpel.newslink.MainActivity.mWaveSwipeRefreshLayout;
import static de.ricoklimpel.newslink.SourcesRecycleAdapter.PREFSNAME;

/**
 * This AsyncTask compares the List of all Sources with the Users checked Soures from
 * Shared Preferences
 *
 * return ArrayList with the SourceIDs of all checkd Sources
 */
public class AsyncGetSelectedSources extends android.os.AsyncTask<ArrayList<NewsSource>, Void, ArrayList<NewsSource>>{

    ArrayList<NewsSource> newsSources;

    @Override
    protected void onPreExecute() {
        mWaveSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected synchronized ArrayList<NewsSource> doInBackground(ArrayList<NewsSource>... paramNewsSources) {

        Boolean[] checkedSources;

        ArrayList<NewsSource> activeSources = new ArrayList<>();
        newsSources = paramNewsSources[0];

        //Load Checked Items from Shared Preferences and Store them Back into Boolean Array
        checkedSources = LocalStorage.StringToBoolArray(
                LocalStorage.loadArray(PREFSNAME, context));

        //If it is the First Start and there are no Shared Preferences create Prefs with all false
        if (checkedSources.length==0) {
            checkedSources = new Boolean[newsSources.size()];
            for (int i = 0; i < checkedSources.length; i++) {
                checkedSources[i] = false;
            }
            //Save Checked Items to Shared Preferences
            LocalStorage.saveArray(LocalStorage.BoolToStringArray(checkedSources),
                    PREFSNAME, context);
        }

        //Create a List with all checked Source IDs
        for (int i = 0; i < checkedSources.length; i++) {
            if (checkedSources[i]) {
                activeSources.add(newsSources.get(i));
                //Log.e("checkedSources ", newsSources.get(i).getSourceName());
            }
        }

        //Return ArrayList of active NewsSources to PostExecute
        return activeSources;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsSource> result) {

        LocalStorage.SaveNewsSources(context,result,Utils.PREF_ID_SOURCES_CHECKED);

        mWaveSwipeRefreshLayout.setRefreshing(false);
        AsyncGetArticle ctask = new AsyncGetArticle();
        ctask.executeOnExecutor(AsyncGetArticle.THREAD_POOL_EXECUTOR,result);
    }

}


