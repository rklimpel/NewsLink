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

/**
 * A simple {@link Fragment} subclass.
 */
public class SidemenuFragment extends MenuFragment {

    static RecyclerView recyclerView;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView.LayoutManager recylerViewLayoutManager;
    static View view;

    static Boolean created = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sidemenu_fragment, container,
                false);

        created = true;

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        initRecyclerView();

        return  setupReveal(view) ;
    }

    /**
     *
     * If the User opens the Sidemnu
     *
     */
    public void onOpenMenu(){


    }

    /**
     *
     * If the Users close the Sidemenu
     *
     * Reload NewsList in MainAcitivty, (maybe there have been Source changes)
     * //TODO just reload if there have been source changes!
     *
     */
    public void onCloseMenu(){

        MainActivity.setupCheckedNewsSources();

        //Add delay for finishing Closeing Animation, then reload NewsList
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                MainActivity.reload();
            }
        }, 300);

    }


    /**
     *
     * Init the Recycler List View in Sidemenu (Sources)
     *
     */
    public static void initRecyclerView(){

        recyclerViewAdapter = new SourcesRecycleAdapter(view.getContext(),MainActivity.newsSources);

        recyclerView.setAdapter(recyclerViewAdapter);
    }

}
