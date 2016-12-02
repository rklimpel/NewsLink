package de.ricoklimpel.newslink;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.MenuFragment;
import com.squareup.picasso.Picasso;

import de.ricoklimpel.newslink.DownloadWebContent;
import de.ricoklimpel.newslink.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends MenuFragment {

    static RecyclerView recyclerView;
    static RelativeLayout relativeLayout;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView.LayoutManager recylerViewLayoutManager;
    static View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blank, container,
                false);


        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        final String stringUrl = "https://newsapi.org/v1/sources?language=en";
        new DownloadWebContent().execute(stringUrl);

        return  setupReveal(view) ;
    }

    public void onOpenMenu(){



    }
    public void onCloseMenu(){

    }

    public static void onPostDownload(String downloadData){

        String[] Title = JSONHandling.ArrayfromJSONString(downloadData,"sources","name");

        recyclerViewAdapter = new SourcesRecycleAdapter(view.getContext(), Title);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

}
