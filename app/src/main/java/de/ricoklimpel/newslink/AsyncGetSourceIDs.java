package de.ricoklimpel.newslink;

import java.util.ArrayList;

import static de.ricoklimpel.newslink.DownloadWebContent.downloadUrlData;
import static de.ricoklimpel.newslink.MainActivity.mWaveSwipeRefreshLayout;
import static de.ricoklimpel.newslink.MainActivity.*;

/**
 * This AsyncTask get all SourceID's from API
 */
public class AsyncGetSourceIDs extends android.os.AsyncTask<Object, Object, ArrayList<NewsSource>> {

    String[] sourceID;
    String[] sourceName;
    String[] description;
    String[] url;
    String[] category;
    String[] language;
    String[] country;
    String[] urlLogoSmall;
    String[] urlLogoMedium;
    String[] urlLogoLarge;

    @Override
    protected void onPreExecute() {
        mWaveSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected synchronized ArrayList<NewsSource> doInBackground(Object... urls) {

        String Url = "https://newsapi.org/v1/sources?language=";
        String data = downloadUrlData(Url);

        ArrayList<NewsSource> newsSources = new ArrayList<>();

        if (JSONHandling.checkAPIStatus(data)) {

            sourceID = JSONHandling.ArrayfromJSONString(data, "sources", "id");
            sourceName = JSONHandling.ArrayfromJSONString(data, "sources", "name");
            description = JSONHandling.ArrayfromJSONString(data, "sources" , "description");
            url = JSONHandling.ArrayfromJSONString(data,"sources","url");
            category = JSONHandling.ArrayfromJSONString(data,"sources","category");
            language = JSONHandling.ArrayfromJSONString(data,"sources","language");
            country = JSONHandling.ArrayfromJSONString(data,"sources","country");
            urlLogoSmall = JSONHandling.ArrayfromJSONString(data, "sources","urlsToLogos","small");
            urlLogoMedium = JSONHandling.ArrayfromJSONString(data, "sources","urlsToLogos","medium");
            urlLogoLarge = JSONHandling.ArrayfromJSONString(data, "sources" , "urlsToLogos","large");

            for (int i = 0; i < sourceID.length; i++) {
                newsSources.add(new NewsSource(sourceID[i],sourceName[i],description[i],url[i],category[i],
                        language[i],country[i],new String[]{urlLogoSmall[i],
                                                            urlLogoMedium[i],
                                                            urlLogoLarge[i]}));
            }

            return newsSources;

        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<NewsSource> result) {

        LocalStorage.SaveNewsSources(context,result,Utils.PREF_ID_SOFURCES_ALL);

        mWaveSwipeRefreshLayout.setRefreshing(false);

        MainActivity.newsSources = result;

        AsyncGetSelectedSources btask = new AsyncGetSelectedSources();
        btask.executeOnExecutor(AsyncGetSelectedSources.THREAD_POOL_EXECUTOR,result);
    }

}


