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

    final String PREFSNAME = "checkedSources";

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


        String[] saveArray = LocalStorage.loadArray(PREFSNAME,context);
        checkedSources = new Boolean[saveArray.length];
        for (int i = 0; i < saveArray.length; i++) {
            if(saveArray[i].equals("0"))checkedSources[i]=false;
            if(saveArray[i].equals("1"))checkedSources[i]=true;
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


        if(checkedSources[position])holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.auswahl));

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

    private void checkSources(int position, final ViewHolder holder){

        if(!checkedSources[position]){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.auswahl));
            checkedSources[position]=true;
        }else{
            holder.linearLayout.setBackgroundColor(Color.WHITE);
            checkedSources[position]=false;
        }


        String[] saveArray = new String[checkedSources.length];
        for (int i = 0; i < checkedSources.length; i++) {
            if(checkedSources[i])saveArray[i]="1";
            if(!checkedSources[i])saveArray[i]="0";
        }
        LocalStorage.saveArray(saveArray,PREFSNAME,context);
    }


    @Override
    public int getItemCount() {

        return TitleValues.length;
    }
}
