package com.np.areebwaseem.horriblefriends;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by areebwaseem on 4/1/18.
 */

public class SharedprefManager {
    private static final String SHARED_PREF_NAME="com.example.areebwaseem.horriblefriendsfinal";
    private static final String KEY_ACCESS_TOKEN="token";
    private static Context mCtx;
    private static SharedprefManager mInstance;

    private SharedprefManager(Context context)
    {
       mCtx = context;
    }

    public static synchronized SharedprefManager getInstance(Context context){
        if (mInstance== null)
        {
            mInstance = new SharedprefManager(context);
        }
        return mInstance;
    }
    public boolean storeToken(String token)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        return true;
    }
    public String getToken()
    {
        SharedPreferences sharedPreferences =  mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN,null);

    }



}
