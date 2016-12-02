package de.ricoklimpel.newslink;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ricoklimpel on 02.12.16.
 */

public class LocalStorage {


    /**
     *
     * save a simple Array to Shared Preferences
     *
     * @param context
     * @param name
     * @param data
     */
    public static void SaveString(Context context, String name, String data){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name,data);
        editor.apply();
    }

    /**
     *
     * load a simple Array from Shared Preferences
     *
     * @param context
     * @param name
     * @return
     */
    public static String LoadString(Context context, String name){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String data = preferences.getString("Name", "");

        return data;
    }

    /**
     *
     * saves a string Array to Shared Preferences
     *
     * @param array
     * @param arrayName
     * @param mContext
     */
    public static  void saveArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        editor.commit();
    }


    /**
     *
     * load a String Array from Shared Preferences
     *
     * @param arrayName
     * @param mContext
     * @return
     */
    public static String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }

    /**
     *
     * Converts an Boolean Array into an String Array
     *
     * @param boolArray
     * @return
     */
    public static String[] BoolToStringArray(Boolean[] boolArray){
        String[] saveArray = new String[boolArray.length];
        for (int i = 0; i < boolArray.length; i++) {
            if(boolArray[i])saveArray[i]="1";
            if(!boolArray[i])saveArray[i]="0";
        }

        return saveArray;
    }

    /**
     * Converts an String Array into a Boolean Array
     *
     * @param stringArray
     * @return
     */
    public static Boolean[] StringToBoolArray(String[] stringArray){
        Boolean[] boolArray = new Boolean[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            if(stringArray[i].equals("0"))boolArray[i]=false;
            if(stringArray[i].equals("1"))boolArray[i]=true;
        }

        return boolArray;
    }
}
