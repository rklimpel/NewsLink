package de.ricoklimpel.newslink;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by ricoklimpel on 02.12.16.
 */

public class SourcesRecycleAdapter extends RecyclerView.Adapter<SourcesRecycleAdapter.ViewHolder>{

    String[] TitleValues;
    Context context;
    View view1;
    SourcesRecycleAdapter.ViewHolder viewHolder1;

    public SourcesRecycleAdapter(Context context1, String[] SubjectValues1) {
        this.TitleValues = SubjectValues1;
        this.context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolder(View v) {

            super(v);

            textView = (TextView) v.findViewById(R.id.sources_textview);
        }
    }

    @Override
    public SourcesRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(context).inflate(R.layout.sources_recycler_items, parent, false);
        viewHolder1 = new SourcesRecycleAdapter.ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(TitleValues[position]);
    }

    @Override
    public int getItemCount() {

        return TitleValues.length;
    }
}
