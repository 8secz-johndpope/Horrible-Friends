package com.np.areebwaseem.horriblefriends;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG,"Got notifications");

        // Check if message contains a data payload.



        /*
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        */




        // Check if message contains a notification payload.








        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String click_action=remoteMessage.getNotification().getClickAction();
            Intent intent=new Intent(click_action);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                if (!(remoteMessage.getData().get("type")==null))
                {
                    intent.putExtra("type",(remoteMessage.getData().get("type")));
                }
                if (!(remoteMessage.getData().get("mess_uid")==null))
                {
                    intent.putExtra("mess_uid",(remoteMessage.getData().get("mess_uid")));
                }
            }


            String body_string="";
          // create_notification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),intent);
            if (!(remoteMessage.getData().get("type_extra")==null)&& remoteMessage.getData().get("type").equals("friendrequestnotification"))
            {
                try {
                    body_string= remoteMessage.getData().get("type_extra") +" " + getResources().getString(R.string.new_sent_you_a_request);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (!(remoteMessage.getData().get("type_extra")==null)&& remoteMessage.getData().get("type").equals("fvourite_got_message_notification")) {
                try {
                    body_string= remoteMessage.getData().get("type_extra") +" " +  getResources().getString(R.string.new_got_a_new_message);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (!(remoteMessage.getData().get("type")==null)&& remoteMessage.getData().get("type").equals("new_message_notification")) {
                body_string = getResources().getString(R.string.sent_you_a_new_message);
            }
            else if (!(remoteMessage.getData().get("type")==null)&& remoteMessage.getData().get("type").equals("new_comment_notification")) {
                body_string = getResources().getString(R.string.also_commented);
            }
            else if (!(remoteMessage.getData().get("type")==null)&& remoteMessage.getData().get("type").equals("friendrequestacceptdnotification")) {
                body_string =remoteMessage.getData().get("type_extra") +" "+ getResources().getString(R.string.new_accepted_your_friend_request);
            }
            if (remoteMessage.getData().get("is_muted")!=null) {
                create_notification(remoteMessage.getNotification().getTitle(), body_string, intent, true );
            }
            else {
                create_notification(remoteMessage.getNotification().getTitle(), body_string, intent, false);
            }
        }









        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void create_notification( String title,String body,Intent intent,Boolean is_muted)
    {


        if (is_muted) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.smb_coin);
                NotificationChannel notificationChannel = new NotificationChannel(getResources().getString(R.string.muted_notification_channel_id), "muted horrible channel", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_600));
                notificationChannel.setSound(null, null);
                // notificationChannel.enableVibration(true);
                //notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                int my_int = (int) System.currentTimeMillis();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, my_int, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.muted_notification_channel_id))
                        .setSmallIcon(R.mipmap.sb_icons)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                //  mBuilder.setSound(sound);
                mBuilder.setAutoCancel(true);
                mBuilder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(notificationChannel);
                // NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            /*
            try {
                Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.smb_coin);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            */

                int notificationId = (int) System.currentTimeMillis();
                notificationManager.notify(notificationId, mBuilder.build());

            } else {
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.smb_coin);
// new code my_int
                int my_int = (int) System.currentTimeMillis();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, my_int, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.mipmap.sb_icons)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                mBuilder.setContentIntent(pendingIntent);
               // mBuilder.setSound(null);
                mBuilder.setAutoCancel(true);
                // NotificationManager notificationManager =(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE); notificationManager.createNotificationChannel(notificationChannel);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                int notificationId = (int) System.currentTimeMillis();

                notificationManager.notify(notificationId, mBuilder.build());
            }

        }
        else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.smb_coin);
                NotificationChannel notificationChannel = new NotificationChannel(getResources().getString(R.string.default_notification_channel_id), "horrible channel", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_600));
                notificationChannel.setSound(sound, null);
                // notificationChannel.enableVibration(true);
                //notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                int my_int = (int) System.currentTimeMillis();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, my_int, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.mipmap.sb_icons)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                //  mBuilder.setSound(sound);
                mBuilder.setAutoCancel(true);
                mBuilder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(notificationChannel);
                // NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            /*
            try {
                Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.smb_coin);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            */

                int notificationId = (int) System.currentTimeMillis();
                notificationManager.notify(notificationId, mBuilder.build());

            } else {
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.smb_coin);
// new code my_int
                int my_int = (int) System.currentTimeMillis();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, my_int, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.mipmap.sb_icons)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setSound(sound);
                mBuilder.setAutoCancel(true);
                // NotificationManager notificationManager =(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE); notificationManager.createNotificationChannel(notificationChannel);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                int notificationId = (int) System.currentTimeMillis();
                notificationManager.notify(notificationId, mBuilder.build());
            }
        }








// notificationId is a unique int for each notification that you must define

    }
}
