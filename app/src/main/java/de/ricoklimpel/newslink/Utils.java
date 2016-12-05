package de.ricoklimpel.newslink;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ricoklimpel on 03.12.16.
 */

public class Utils {

    /**
     *
     * Converts ugly API Date to new Date Format
     *
     * @param uglyDate
     * @return
     */
    public static Date convertToReadableDate(String uglyDate){
        DateFormat format;
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = format.parse(uglyDate);
        } catch (ParseException e) {
            //ERROR
        }
        return date;
    }


    /**
     *
     * parse Date Format form API download to User readable Format
     *
     * @param timestamp
     * @return
     */
    public static String readableTimestamp(String timestamp){

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

    /**
     *
     * parse Date Format form API download to User readable Format
     *
     * @param timestamp
     * @return
     */
    public static String getTimeFromTimestamp(String timestamp){

        String readable = null;
        Date date = null;

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.GERMAN);

        try {
            date = format.parse(timestamp);
            readable = outputFormat.format(date);
        } catch (ParseException e) {
            //ERROR
        }

        return readable;
    }
}
