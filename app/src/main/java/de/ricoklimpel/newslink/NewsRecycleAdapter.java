package de.ricoklimpel.newslink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class NewsRecycleAdapter extends RecyclerView.Adapter<NewsRecycleAdapter.ViewHolder> {

    ArrayList<NewsArticle> newsArticles;
    Context context;
    View view1;
    ViewHolder viewHolder1;

    public NewsRecycleAdapter(Context context, ArrayList<NewsArticle> newsArticles) {

        this.context = context;
        this.newsArticles = newsArticles;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TextView tvDescription;
        public ImageView ivNewsImage;
        public TextView tvTimestamp;
        public TextView tvSource;
        public ProgressBar pbNewsimage;
        public ImageView ivSourceLogo;
        public RelativeLayout relayNewsBottom;

        public ViewHolder(View v) {

            super(v);

            tvDescription = (TextView)v.findViewById(R.id.description_textview);
            textView = (TextView) v.findViewById(R.id.subject_textview);
            ivNewsImage = (ImageView) v.findViewById(R.id.iv_newsimage);
            tvTimestamp = (TextView)v.findViewById(R.id.tv_timestamp);
            tvSource = (TextView)v.findViewById(R.id.tv_source);
            pbNewsimage = (ProgressBar)v.findViewById(R.id.pb_newsimage);
            ivSourceLogo = (ImageView)v.findViewById(R.id.iv_sourceImage);
            relayNewsBottom = (RelativeLayout)v.findViewById(R.id.relay_newsLogoBackground);

            ivNewsImage.setVisibility(View.INVISIBLE);
            pbNewsimage.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public NewsRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(context).inflate(R.layout.news_recycler_items, parent, false);
        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //Set Title
        holder.textView.setText(newsArticles.get(position).title);

        //Set Image
        if(newsArticles.get(position).getImageUrl()!= "null"){
            Picasso.with(context).load(newsArticles.get(position).getImageUrl()).fit().centerCrop()
                    .into(holder.ivNewsImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.ivNewsImage.setVisibility(View.VISIBLE);
                            holder.pbNewsimage.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }else{
            holder.ivNewsImage.setVisibility(View.GONE);
            holder.pbNewsimage.setVisibility(View.GONE);
        }


        //Set Timestamp
        holder.tvTimestamp.setText(Utils.getTimeFromTimestamp(newsArticles.get(position).getTimestamp()));

        //Set Description
        holder.tvDescription.setText(newsArticles.get(position).getDescription());
        if(holder.tvDescription.getText()=="null"){
            holder.tvDescription.setVisibility(View.GONE);
        }

        //Set Source Name
        //holder.tvSource.setText(newsArticles.get(position).getNewsSource().getSourceName());

        Log.e("NEWS SOURCE URL",newsArticles.get(position).getNewsSource().getUrl());

        //Set Source Image
        Picasso.with(context).load("https://icon-locator.herokuapp.com/icon?url="
                + newsArticles.get(position).getNewsSource().getUrl() + "&size=80..120..200")
                .into(holder.ivSourceLogo, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Set Source Logo Background with Vibrant Color
                        Bitmap bitmap = ((BitmapDrawable)holder.ivSourceLogo.getDrawable()).getBitmap();
                        holder.relayNewsBottom.setBackgroundColor(Utils.getVibrantColor(bitmap));
                    }

                    @Override
                    public void onError() {

                    }
                });

        //Set OnClick Listeners for WebView
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openWebrowser(newsArticles.get(position).getUrl());
            }
        });
        holder.ivNewsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openWebrowser(newsArticles.get(position).getUrl());
            }
        });

    }

    @Override
    public int getItemCount() {

        return newsArticles.size();
    }

    /**
     *
     * parse Date Format form API download to User readable Format
     *
     * @param timestamp
     * @return
     */
    public String readableTimestamp(String timestamp){

        String readable = null;
        Date date = null;

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat outputFormat = new SimpleDateFormat("dd. MMMM - HH:mm", Locale.GERMAN);

        try {
            date = format.parse(timestamp);
            readable = outputFormat.format(date);
        } catch (ParseException e) {
            //ERROR
        }

        return readable;
    }

}

