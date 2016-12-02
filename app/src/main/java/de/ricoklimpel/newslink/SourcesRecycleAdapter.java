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

    /**
     * saves which sources are checked and which aren'T
     * 0 = not set
     * 1 = checked
     *
     */
    Boolean[] checkSources;

    Context context;
    View view1;
    SourcesRecycleAdapter.ViewHolder viewHolder1;

    public SourcesRecycleAdapter(Context context1, String[] SubjectValues1, String[] LogoURLS) {
        this.logoURLS = LogoURLS;
        this.TitleValues = SubjectValues1;
        this.context = context1;

        this.checkSources = new Boolean[getItemCount()];
        for (int i = 0; i < checkSources.length; i++) {
            checkSources[i] = false;
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

        if(!checkSources[position]){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.auswahl));
            checkSources[position]=true;
        }else{
            holder.linearLayout.setBackgroundColor(Color.WHITE);
            checkSources[position]=false;
        }
    }


    @Override
    public int getItemCount() {

        return TitleValues.length;
    }
}
