package de.ricoklimpel.newslink;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class NewsRecycleAdapter extends RecyclerView.Adapter<NewsRecycleAdapter.ViewHolder> {

    String[] TitleValues;
    String[] DescriptionValues;
    String[] Links;
    String[] imageURLs;
    String[] timestamps;
    Context context;
    View view1;
    ViewHolder viewHolder1;

    public NewsRecycleAdapter(Context context1, String[] SubjectValues1, String[] DescriptionValues, String[] Links, String[] ImageUrls,String[]timestamps) {

        this.imageURLs = ImageUrls;
        this.Links = Links;
        this.DescriptionValues = DescriptionValues;
        this.TitleValues = SubjectValues1;
        this.timestamps = timestamps;
        this.context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TextView tvDescription;
        public ImageView ivNewsImage;
        public TextView tvTimestamp;

        public ViewHolder(View v) {

            super(v);

            tvDescription = (TextView)v.findViewById(R.id.description_textview);
            textView = (TextView) v.findViewById(R.id.subject_textview);
            ivNewsImage = (ImageView) v.findViewById(R.id.iv_newsimage);
            tvTimestamp = (TextView)v.findViewById(R.id.tv_timestamp);
        }
    }

    @Override
    public NewsRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(context).inflate(R.layout.news_recycler_items, parent, false);
        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //Set Title
        holder.textView.setText(TitleValues[position]);

        //Set Image
        Picasso.with(context).load(imageURLs[position]).into(holder.ivNewsImage);

        //Set Timestamp
        holder.tvTimestamp.setText(timestamps[position]);

        //Set Description
        holder.tvDescription.setText(DescriptionValues[position]);

        //Set OnClick Listeners for WebView
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openWebrowser(Links[position]);
            }
        });
        holder.ivNewsImage.setOnClickListener(new View.OnClickListener() {
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

