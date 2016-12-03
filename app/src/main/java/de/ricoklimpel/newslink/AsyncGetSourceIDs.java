package de.ricoklimpel.newslink;

import java.util.concurrent.ExecutionException;

import static de.ricoklimpel.newslink.DownloadWebContent.downloadUrlData;
import static de.ricoklimpel.newslink.MainActivity.SourceIDs;
import static de.ricoklimpel.newslink.MainActivity.mWaveSwipeRefreshLayout;

class AsyncGetSourceIDs extends android.os.AsyncTask<String, Void, String[]>{

    @Override
    protected void onPreExecute() {
        mWaveSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected synchronized String[] doInBackground(String... urls) {
        String Url = "https://newsapi.org/v1/sources?language=";
        String data = downloadUrlData(Url);

        if (JSONHandling.checkAPIStatus(data)) {
            return JSONHandling.ArrayfromJSONString(data, "sources", "id");

        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String[] result) {
        mWaveSwipeRefreshLayout.setRefreshing(false);

        AsyncGetSelectedSources btask = new AsyncGetSelectedSources();
        btask.executeOnExecutor(AsyncGetSelectedSources.THREAD_POOL_EXECUTOR,result);

    }

}


