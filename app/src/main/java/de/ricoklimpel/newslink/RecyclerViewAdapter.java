package de.ricoklimpel.newslink;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    String[] TitleValues;
    String[] DescriptionValues;
    String[] Links;
    Context context;
    View view1;
    ViewHolder viewHolder1;

    public RecyclerViewAdapter(Context context1, String[] SubjectValues1, String[] DescriptionValues, String[] Links) {

        this.Links = Links;
        this.DescriptionValues = DescriptionValues;
        this.TitleValues = SubjectValues1;
        this.context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TextView tvDescription;

        public ViewHolder(View v) {

            super(v);

            tvDescription = (TextView)v.findViewById(R.id.description_textview);
            textView = (TextView) v.findViewById(R.id.subject_textview);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items, parent, false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.textView.setText(TitleValues[position]);
        holder.tvDescription.setText(DescriptionValues[position]);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openWebrowser(Links[position]);
            }
        });
    }

    @Override
    public int getItemCount() {

        return TitleValues.length;
    }
}

