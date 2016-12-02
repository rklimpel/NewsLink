package de.ricoklimpel.newslink;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mxn.soul.flowingdrawer_core.MenuFragment;

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
        view = inflater.inflate(R.layout.sidemenuFragment, container,
                false);


        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        final String stringUrl = "https://newsapi.org/v1/sources?language=";
        new DownloadWebContent().execute(stringUrl);

        return  setupReveal(view) ;
    }

    public void onOpenMenu(){

    }

    public void onCloseMenu(){

    }

    /**
     * Will be called DownloadWebContent Class after finishing the downloads
     *
     * @param downloadData is the complete JSON String, form API
     */
    public static void onPostDownload(String downloadData){

        String[] Title = JSONHandling.ArrayfromJSONString(downloadData,"sources","name");

        recyclerViewAdapter = new SourcesRecycleAdapter(view.getContext(), Title);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

}