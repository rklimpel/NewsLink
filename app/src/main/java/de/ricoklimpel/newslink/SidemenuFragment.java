package de.ricoklimpel.newslink;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mxn.soul.flowingdrawer_core.MenuFragment;
import static de.ricoklimpel.newslink.DownloadWebContent.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class SidemenuFragment extends MenuFragment {

    static RecyclerView recyclerView;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView.LayoutManager recylerViewLayoutManager;
    static View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sidemenu_fragment, container,
                false);


        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        initRecyclerView();

        return  setupReveal(view) ;
    }


    public void onOpenMenu(){


    }

    /**
     *
     * If the Users close the Sidemenu with a swipe
     *
     * Reload NewsList in MainAcitivty, (maybe there have been Source changes)
     * //TODO just reload if there have been source changes!
     *
     */
    public void onCloseMenu(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                MainActivity.reload();
            }
        }, 300);

    }


    public static void initRecyclerView(){

        final String stringUrl = "https://newsapi.org/v1/sources?language=";
        String downloadData = downloadUrlData(stringUrl);

        recyclerViewAdapter = new SourcesRecycleAdapter(view.getContext(),
                JSONHandling.ArrayfromJSONString(downloadData,"sources","name"),
                JSONHandling.ArrayfromJSONString(downloadData,"sources","urlsToLogos","small"));

        recyclerView.setAdapter(recyclerViewAdapter);
    }

}
