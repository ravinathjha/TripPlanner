package com.example.anirban.tripplanner.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;

import com.example.anirban.tripplanner.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Shyak on 13-09-2017;
 */

public class PreferenceHelper {
    private static PreferenceHelper instance = null;
    private final SharedPreferences preferences;
    private final Resources res;

    private PreferenceHelper(Context appContext) {
        preferences = appContext.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        res = appContext.getResources();
    }

    public static PreferenceHelper getInstance() {
        return instance;
    }

    public synchronized static void setInstance(Context appContext) {
        if (instance == null) {
            instance = new PreferenceHelper(appContext);
        }
    }


    // Getters

    /**
     * Get int value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key SharedPreferences key
     * @return int value at 'key' or 'defaultValue' if key not found
     */
    public int getInt(int key) {
        return preferences.getInt(res.getResourceName(key), res.getInteger(key));
    }

    public int getIntOr(int key, int val) {
        return preferences.getInt(res.getResourceName(key), val);
    }

    /**
     * Get String value from SharedPreferences at 'key'. If key not found, return ""
     *
     * @param key SharedPreferences key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    public String getString(int key) {
        return preferences.getString(res.getResourceName(key), res.getString(key));
    }

    public String getStringOr(int key, String val) {
        return preferences.getString(res.getResourceName(key), val);
    }
    // Put methods

    /**
     * Get boolean value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key SharedPreferences key
     * @return boolean value at 'key' or 'defaultValue' if key not found
     */
    public boolean getBoolean(int key) {
        return preferences.getBoolean(res.getResourceName(key), res.getBoolean(key));
    }

    /**
     * Put int value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value int value to be added
     */
    public PreferenceHelper putInt(int key, int value) {
        checkForNullKey(key);
        preferences.edit().putInt(res.getResourceName(key), value).apply();
        return this;
    }

    /**
     * Put String value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value String value to be added
     */
    public PreferenceHelper putString(int key, String value) {
        checkForNullKey(key);
        checkForNullValue(value);
        preferences.edit().putString(res.getResourceName(key), value).apply();
        return this;
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     *
     * @param key        SharedPreferences key
     * @param stringList ArrayList of String to be added
     */
    public void putListString(int key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(res.getResourceName(key), TextUtils.join("‚‗‚", myStringList)).apply();
    }

    /**
     * Put boolean value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value boolean value to be added
     */
    public PreferenceHelper putBoolean(int key, boolean value) {
        checkForNullKey(key);
        preferences.edit().putBoolean(res.getResourceName(key), value).apply();
        return this;
    }

    /**
     * Remove SharedPreferences item with 'key'
     *
     * @param key SharedPreferences key
     */
    public PreferenceHelper remove(int key) {
        preferences.edit().remove(res.getResourceName(key)).apply();
        return this;
    }

    /**
     * Clear SharedPreferences (remove everything)
     */
    public void clear() {
        preferences.edit().clear().apply();
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     */
    private void checkForNullKey(int key) {
        if (res.getResourceName(key) == null) {
            throw new NullPointerException();
        }
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     */
    private void checkForNullValue(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
    }

    public boolean exists(int key) {
        return preferences.contains(res.getResourceName(key));
    }

    /**
     * Get parsed ArrayList of String from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return ArrayList of String
     */
    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }


    /**
     * Get String value from SharedPreferences at 'key'. If key not found, return ""
     *
     * @param key SharedPreferences key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    public String getString(String key) {
        return preferences.getString(key, "pref_id");
    }

    public void putString(String key, String value) {
        checkForNullKey(key);
        checkForNullValue(value);
        preferences.edit().putString(key, value).apply();
    }


    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     *
     * @param key        SharedPreferences key
     * @param stringList ArrayList of String to be added
     */
    public void putListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     *
     * @param key {@link String}
     */
    private void checkForNullKey(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
    }

    public boolean isUserPresent() {
        return !PreferenceHelper.getInstance().getString(R.string.pref_id).equals("-1");
    }
}
