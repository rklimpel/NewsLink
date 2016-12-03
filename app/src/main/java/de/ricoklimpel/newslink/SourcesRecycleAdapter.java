package de.ricoklimpel.newslink;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by ricoklimpel on 02.12.16.
 */

public class SourcesRecycleAdapter extends RecyclerView.Adapter<SourcesRecycleAdapter.ViewHolder>{

    String[] TitleValues;
    String[] logoURLS;

    public static final String PREFSNAME = "checkedSources";

    /**
     * saves which sources are checked and which aren'T
     * 0 = not set
     * 1 = checked
     *
     */
    Boolean[] checkedSources;

    Context context;
    View view1;
    SourcesRecycleAdapter.ViewHolder viewHolder1;

    public SourcesRecycleAdapter(Context context1, String[] SubjectValues1, String[] LogoURLS) {
        this.logoURLS = LogoURLS;
        this.TitleValues = SubjectValues1;
        this.context = context1;

        //Load Checked Items from Shared Preferences and Store them Back into Boolean Array
        checkedSources = LocalStorage.StringToBoolArray(LocalStorage.loadArray(PREFSNAME,context));
        if(checkedSources[0]==null){
            //If there is no data saved in shared preferences init first dataset:
            checkedSources = new Boolean[TitleValues.length];
            for (int i = 0; i < checkedSources.length; i++) {
                checkedSources[i]=false;
            }
            //Save Checked Items to Shared Preferences
            LocalStorage.saveArray(LocalStorage.BoolToStringArray(checkedSources),PREFSNAME,context);
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView iv_logo;
        public LinearLayout linearLayout;

        public ViewHolder(View v) {

            super(v);

            iv_logo = (ImageView) v.findViewById(R.id.iv_sourcelgo);
            textView = (TextView) v.findViewById(R.id.sources_textview);
            linearLayout = (LinearLayout)v.findViewById(R.id.linearlayout_sourcesItem);
        }
    }

    @Override
    public SourcesRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(context).inflate(R.layout.sources_recycler_items, parent, false);
        viewHolder1 = new SourcesRecycleAdapter.ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //Set Source Name
        holder.textView.setText(TitleValues[position]);

        //Set Image
        Picasso.with(context).load(logoURLS[position]).into(holder.iv_logo);


        //Set checked if Arrays had saved checkd state
        if(checkedSources[position])holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.auswahl));


        /**
         *
         * On Click Listener for set checked
         * sits on all elements because it should be able to click everywhere  to save the state
         *
         */
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSources(position,holder);
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSources(position,holder);
            }
        });
        holder.iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSources(position,holder);
            }
        });

    }

    /**
     *
     * Check if a NewsSource is already checked, if yes uncheck it, else check it
     *
     * @param position
     * @param holder
     */
    private void checkSources(int position, final ViewHolder holder){

        if(!checkedSources[position]){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.auswahl));
            checkedSources[position]=true;
        }else{
            holder.linearLayout.setBackgroundColor(Color.WHITE);
            checkedSources[position]=false;
        }

        //Save Checked Items to Shared Preferences
        LocalStorage.saveArray(LocalStorage.BoolToStringArray(checkedSources),PREFSNAME,context);
    }

    /**
     *
     * get Recyclerview Items (length)
     *
     * @return
     */
    @Override
    public int getItemCount() {

        return TitleValues.length;
    }
}
