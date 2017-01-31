package de.ricoklimpel.newslink;

import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static de.ricoklimpel.newslink.DownloadWebContent.downloadUrlData;
import static de.ricoklimpel.newslink.MainActivity.initRecyclerView;

/**
 *
 * This little AsycTask uses the List of all selected Soures and fetches the latest Sources News
 * at the end he calls the Recyclerview initiator from MainActivity to wirte news in NewsList
 *
 */
public class AsyncGetArticle extends android.os.AsyncTask<ArrayList<NewsSource>, Void, ArrayList<NewsArticle>>{

    String data;
    String[] Title;
    String[] Description;
    String[] Url;
    String[] ImageUrl;
    String[] Timestamp;
    String source;


    @Override
    protected void onPreExecute() {
        Log.e("NETWORK","Load News Articles");
    }

    @Override
    protected synchronized ArrayList<NewsArticle> doInBackground(ArrayList<NewsSource>... paramNewsSources) {

        final ArrayList<NewsArticle> newsArticles = new ArrayList<>();
        newsArticles.clear();

        ArrayList<NewsSource>checkedSources = paramNewsSources[0];

        for (int i = 0; i < checkedSources.size(); i++) {

            //Get Json Data for this source
            data = downloadUrlData("https://newsapi.org/v1/articles?source=" +
                    checkedSources.get(i).getSourceID() + "&apiKey=bddae599de5041ab9858c74961886e6c");

            //If API Status is "ok" continue
            if (JSONHandling.checkAPIStatus(data)) {

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Get Arrays with specific Values from Json document
                        Title = JSONHandling.ArrayfromJSONString(data, "articles", "title");
                        Description = JSONHandling.ArrayfromJSONString(data, "articles", "description");
                        Url = JSONHandling.ArrayfromJSONString(data, "articles", "url");
                        ImageUrl = JSONHandling.ArrayfromJSONString(data, "articles", "urlToImage");
                        Timestamp = JSONHandling.ArrayfromJSONString(data, "articles", "publishedAt");
                        source = JSONHandling.JsonInfo(data, "source");

                    }});

                t.start(); // spawn thread

                try {
                    t.join();  // wait for thread to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Add Arrays from the one downloaded source to Array List with all sources
                for (int x = 0; x < Title.length; x++) {
                    newsArticles.add(new NewsArticle(Title[x], Description[x], Url[x], ImageUrl[x],
                            Timestamp[x], null, source,checkedSources.get(i)));
                }
            }
        }
        return newsArticles;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsArticle> result) {

        //mWaveSwipeRefreshLayout.setRefreshing(false);

        //IF Sidemenu could not get created on Main Activity start because of missing data,
        //create it now!
        if(SidemenuFragment.created == false){
            MainActivity.initDrawerLayout();
        }

        //If Result is not 0 and invalid:
        if (result.size() > 0) {
            //Sort Array List with all Articles for Timestamp
            Collections.sort(result, new Comparator<NewsArticle>() {
                @Override
                public int compare(NewsArticle o1, NewsArticle o2) {

                    DateFormat format;
                    format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                    if(o1.getTimestamp()!="null"&&o2.getTimestamp()!="null"){
                        try {
                            if(format.parse(o1.getTimestamp()).before(format.parse(o2.getTimestamp()))){
                                return -1;
                            }else if(format.parse(o2.getTimestamp()).equals(format.parse(o2.getTimestamp()))){
                                return 0;
                            }else{
                                return 1;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }else{
                        //If an article has no Timestamp -> stay at position
                        return 0;
                    }
                }
            });




            Collections.reverse(result);
        }

        //Sort For Titles
        /*Collections.sort(result, new Comparator<NewsArticle>() {
                    @Override
                    public int compare(NewsArticle o1, NewsArticle o2) {
                        String a = o1.getTitle();
                        String b = o2.getTitle();
                        int compare = a.compareTo(b);
                        if (compare < 0) {
                            return +1;
                        } else {
                            if (compare > 0)

                                return -1;
                        }
                        return 0;
                    }
                });
        Collections.reverse(result);*/

        initRecyclerView(result);

    }

}


