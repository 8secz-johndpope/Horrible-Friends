package com.np.areebwaseem.horriblefriends;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by areebwaseem on 4/1/18.
 */

public class MyNotificationManager {
    private static final String TAG = "MyNotificationManager";
    private Context mCtx;
    private static MyNotificationManager mInstance;
    
    
    private MyNotificationManager(Context context)
    {
        mCtx=context;
    }

    public static synchronized MyNotificationManager getInstance(Context context)
    {
        if (mInstance==null)
        {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;

    }

    public void displayNotification(String title, String body)
    {

        Notification.Builder mBuilder = new Notification.Builder(mCtx,Constants_Class.CHANNEL_ID).setSmallIcon(R.drawable.write_icon).setContentTitle(title).setContentText(body);
        Intent intent = new Intent(mCtx,home_activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager!=null)
        {
            notificationManager.notify(1, mBuilder.build());
        }


    }




    
    
    
    
    
}
