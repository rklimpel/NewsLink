package de.ricoklimpel.newslink;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ricoklimpel on 03.12.16.
 *
 * NewsList Objects for saving One Article Data
 *
 */
public class NewsArticle implements Comparable<NewsArticle>{

    String title;
    String description;
    String url;
    String imageUrl;
    String timestamp;
    String author;
    String source;
    Date datetimestamp;

    public NewsArticle(){
        new NewsArticle(null,null,null,null,null,null,null);
    }

    /**
     * Create a new Newsarticle Element
     *
     * @param title
     * @param description
     * @param url
     * @param imageUrl
     * @param timestamp
     * @param author
     * @param source
     */
    public NewsArticle(String title,String description,String url,String imageUrl,
                       String timestamp,String author,String source){

        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.author = author;
        this.source = source;

        this.setDatetimestamp(timestamp);
    }

    public String getTitle(){
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }

    public String getAuthor() {
        return author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDatetimestamp() {
        return datetimestamp;
    }

    /**
     *
     * Set Timestamp from String into Date Format
     *
     * @param timestamp
     */
    public void setDatetimestamp(String timestamp) {

        DateFormat format;
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = format.parse(timestamp);
        } catch (ParseException e) {
            //ERROR
        }
        this.datetimestamp = date;
    }

    /**
     *
     * Comparersion for Sorting NewsList Objects
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(NewsArticle other) {
        try {

            if(this.getDatetimestamp().before(other.getDatetimestamp())){
                return -1;
            }else if(this.getDatetimestamp().after(other.getDatetimestamp())){
                return 1;
            }else if(this.getDatetimestamp().equals(other.getDatetimestamp())){
                return 1;
            }else return 1;

        }catch(NullPointerException e){
            Log.e("compare news Articles",e.getMessage());
            return 1;
        }
    }

}
