package de.ricoklimpel.newslink;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static de.ricoklimpel.newslink.DownloadWebContent.downloadUrlData;
import static de.ricoklimpel.newslink.MainActivity.SourceIDs;
import static de.ricoklimpel.newslink.MainActivity.context;
import static de.ricoklimpel.newslink.MainActivity.mWaveSwipeRefreshLayout;
import static de.ricoklimpel.newslink.SourcesRecycleAdapter.PREFSNAME;

class AsyncGetSelectedSources extends android.os.AsyncTask<String[], Void, ArrayList<String>>{

    @Override
    protected void onPreExecute() {
        mWaveSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected synchronized ArrayList<String> doInBackground(String[]... urls) {
        Boolean[] checkedSources = null;
        String[]allsources = urls[0];

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

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        mWaveSwipeRefreshLayout.setRefreshing(false);

        ArrayList<NewsArticle> asdf = new ArrayList<>();
        AsyncGetArticle ctask = new AsyncGetArticle();
        ctask.executeOnExecutor(AsyncGetArticle.THREAD_POOL_EXECUTOR,result);

    }

}


