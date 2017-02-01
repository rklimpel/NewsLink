package de.ricoklimpel.newslink;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mxn.soul.flowingdrawer_core.MenuFragment;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class SidemenuFragment extends MenuFragment {

    static RecyclerView recyclerView;
    static RecyclerView.Adapter recyclerViewAdapter;
    static RecyclerView.LayoutManager recylerViewLayoutManager;
    static View view;
    public static Boolean isOpen;


    /**
     * saves which sources are checked and which aren'T
     * 0 = not set
     * 1 = checked
     * <p>
     * get written by Sources Recycler Class
     */
    static Boolean[] checkedSources;

    static Boolean created = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sidemenu_fragment, container,
                false);

        created = true;

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview1);
        recylerViewLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        isOpen = false;

        initRecyclerView();

        return setupReveal(view);
    }

    /**
     * If the User opens the Sidemnu
     */
    public void onOpenMenu() {

        isOpen = true;

    }

    /**
     * If the Users close the Sidemenu
     *
     * Reload NewsList in MainAcitivty, (maybe there have been Source changes)
     */
    public void onCloseMenu() {

        isOpen = false;

        MainActivity.setupCheckedNewsSources();

        Boolean[] checkSourcesOld = LocalStorage.StringToBoolArray(
                LocalStorage.loadArray("checkedSources", MainActivity.context));

        //Reload just if checked sources changed
        if (!Arrays.equals(checkSourcesOld, checkedSources)) {
            Log.e("Hello", "I'm not equal!");
            //Add delay for finishing Closeing Animation, then reload NewsList



            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                public void run() {

                    AsyncTask asynk;
                    asynk = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            LocalStorage.saveArray(LocalStorage.BoolToStringArray(checkedSources),
                                    "checkedSources", MainActivity.context);
                            MainActivity.setupCheckedNewsSources();

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            MainActivity.reload();
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
            }, 300);
        }
    }

    /**
     * Init the Recycler List View in Sidemenu (Sources)
     */
    public static void initRecyclerView() {

        recyclerViewAdapter = new SourcesRecycleAdapter(view.getContext(), MainActivity.newsSources);

        recyclerView.setAdapter(recyclerViewAdapter);
    }

}
