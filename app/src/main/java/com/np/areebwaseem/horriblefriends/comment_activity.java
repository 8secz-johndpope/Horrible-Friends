package com.np.areebwaseem.horriblefriends;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.LoginFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class comment_activity extends AppCompatActivity {
    private static final String TAG = "comment_activity";
    ListView comments_view;
    FirebaseAuth mAuth;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;
    String message_uid;
    volatile boolean is_activity_running;
    ProgressBar progressBar;
    ChildEventListener childEventListener;
    DataSnapshot message_snapshot;
    ArrayList<DataSnapshot> all_comments_data_list;
  EditText comment_edit_text;
  ImageButton send_comment;
  DataSnapshot user_snap;
  DataSnapshot all_messages_snapshot;
  CustomAdapter customAdapter;
  Bitmap bitmap;
  ConnectivityManager connMgr;
  NetworkInfo networkInfo;
    View parentLayout;
    Timer timer;
    long now=0;
    NTP_UTC_TIME client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_activity);
        parentLayout = findViewById(android.R.id.content);
        comments_view = findViewById(R.id.comments_list_view);
        progressBar = findViewById(R.id.comment_activity_progress_bar);
        comment_edit_text = findViewById(R.id.new_comment_edit_text);
        send_comment= findViewById(R.id.send_comment_image_button);

       client = new NTP_UTC_TIME();






        /*


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;



        getWindow().setLayout((int)(width*0.9), (int) (height*0.8));
*/

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_bar));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        start_timer();


        mAuth = FirebaseAuth.getInstance();


        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage= FirebaseStorage.getInstance();
        is_activity_running=true;

        all_comments_data_list= new ArrayList<>();

        customAdapter = new CustomAdapter();

        if (savedInstanceState==null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras ==null)
            {
                message_uid= null;
            }
            else {
                message_uid= extras.getString("message_uid");
            }
        }
        else {
            message_uid = (String) savedInstanceState.getSerializable("message_uid");
        }









        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (get_activity_state() && message_uid!=null) {

                        if (!comment_edit_text.getText().toString().trim().equals("")) {

                            if (is_connected())
                            {


                                progressBar.setVisibility(View.VISIBLE);
                                send_comment.setClickable(false);


                                if (all_comments_data_list.size() > 0) {
                                    Map<String, Object> taskMap = new HashMap<>();
                                    taskMap.put("from", mAuth.getCurrentUser().getUid());
                                    if (user_snap.hasChild("full_name")) {
                                        taskMap.put("from_name", user_snap.child("full_name").getValue().toString());
                                    }
                                    else {
                                        taskMap.put("from_name", "");
                                    }
                                    taskMap.put("message_actual", comment_edit_text.getText().toString());
                                    taskMap.put("timestamp", ServerValue.TIMESTAMP);

                                    databaseReference.child("messages").child(message_uid).child("comments").push().setValue(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (get_activity_state()) {
                                                try {


                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.sent_string), Toast.LENGTH_SHORT).show();
                                                        comment_edit_text.setText("");
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    send_comment.setClickable(true);
                                                }catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    databaseReference.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                            if (get_activity_state()) {
                                                Map<String, Object> taskMap = new HashMap<>();
                                                taskMap.put("from", mAuth.getCurrentUser().getUid());
                                                if (user_snap.hasChild("full_name")) {
                                                    taskMap.put("from_name", user_snap.child("full_name").getValue().toString());
                                                } else {
                                                    taskMap.put("from_name", "");
                                                }
                                                taskMap.put("message_actual", comment_edit_text.getText().toString());
                                                taskMap.put("timestamp", ServerValue.TIMESTAMP);
                                                if (dataSnapshot.hasChild(message_uid)) {
                                                    databaseReference.child("messages").child(message_uid).child("comments").push().setValue(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (get_activity_state()) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sent_string), Toast.LENGTH_SHORT).show();
                                                                    comment_edit_text.setText("");
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                }
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                send_comment.setClickable(true);
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    if (get_activity_state()) {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_deleted), Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            }
                                        }catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            try {

                                                if (get_activity_state()) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    send_comment.setClickable(true);
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                }
                                            }catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                            else {
                                if (get_activity_state()) {
                                    show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                }
                            }
                        } else {
                            if (get_activity_state()) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.comment_cant_empty) ,Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else {
                        if (get_activity_state()) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }catch (Exception e)
                {
                    show_err();
                    e.printStackTrace();
                }



            }
        });

        if (message_uid!=null && get_activity_state()) {

            if (is_connected())
            {

                try {
                    progressBar.setVisibility(View.VISIBLE);
                    send_comment.setClickable(false);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                if (get_activity_state()) {


                                    user_snap = dataSnapshot;
                                    databaseReference.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                           try {

                                            if (get_activity_state()) {
                                                all_messages_snapshot = dataSnapshot;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            comments_view.setAdapter(customAdapter);
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            send_comment.setClickable(true);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });


                                                if (dataSnapshot.hasChild(message_uid)) {
                                                    message_snapshot = dataSnapshot.child(message_uid);
                                                    childEventListener = databaseReference.child("messages").child(message_uid).child("comments").addChildEventListener(new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                            try {

                                                            if (get_activity_state()) {
                                                                all_comments_data_list.add(dataSnapshot);
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            customAdapter.notifyDataSetChanged();
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }


                                                                    }
                                                                });

                                                            }
                                                        }catch (Exception e)
                                                            {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                            try {

                                                            if (get_activity_state()) {

                                                                for (int i = 0; i < all_comments_data_list.size(); i++) {
                                                                    if (all_comments_data_list.get(i).getKey().equals(dataSnapshot.getKey())) {
                                                                        all_comments_data_list.set(i, dataSnapshot);
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    customAdapter.notifyDataSetChanged();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            }
                                                        }catch (Exception e)
                                                            {
                                                                e.printStackTrace();
                                                            }

                                                        }

                                                        @Override
                                                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                            try {

                                                                if (get_activity_state()) {

                                                                    for (int i = 0; i < all_comments_data_list.size(); i++) {
                                                                        if (all_comments_data_list.get(i).getKey().equals(dataSnapshot.getKey())) {
                                                                            all_comments_data_list.remove(all_comments_data_list.get(i));
                                                                            runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {
                                                                                        customAdapter.notifyDataSetChanged();
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                }
                                                                            });

                                                                        }
                                                                    }
                                                                }
                                                            }catch (Exception e)
                                                            {
                                                                e.printStackTrace();
                                                            }

                                                        }

                                                        @Override
                                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (get_activity_state()) {
                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_deleted), Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });

                                                }
                                            }
                                        }catch (Exception e)
                                           {
                                               e.printStackTrace();
                                           }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        if (get_activity_state()) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            send_comment.setClickable(false);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });

                                        }
                                    });
                                }
                            }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (get_activity_state()) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                send_comment.setClickable(false);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                        });
                    }catch (Exception e)
                        {
                            show_err();
                            e.printStackTrace();
                        }
                    }
                }).start();


        }
        else {
                if (get_activity_state()) {
                    try {
                        show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }

        }
        else {
            if (get_activity_state()) {
                try {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                    finish();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }







    }

    @Override
    protected void onResume() {
        super.onResume();
        start_timer();
    }

    private void show_err()
    {
        if (is_activity_running) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        progressBar.setVisibility(View.INVISIBLE);
                        send_comment.setClickable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_activity_running=false;
        if (childEventListener!=null)
        {
            databaseReference.child("messages").child(message_uid).child("comments").removeEventListener(childEventListener);
        }
        stop_timer();
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return all_comments_data_list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.comment_list_item_layout,null);
            TextView comment_view = view.findViewById(R.id.comment_text_view);
            final ImageView profile_pic_view = view.findViewById(R.id.comment_user_profile_pic);
            TextView hours_ago_view = view.findViewById(R.id.comment_text_view_hours_ago);
            final TextView likes_view = view.findViewById(R.id.comment_text_view_like_button);
            TextView num_likes_view = view.findViewById(R.id.no_likes_text_view);
            final ImageView thumbs_up_view = view.findViewById(R.id.thumbs_up_image_view);

           final int req_ref = i;

           if (get_activity_state() && req_ref<all_comments_data_list.size()) {


               final DataSnapshot dataSnapshot = all_comments_data_list.get(req_ref);


         try {
             if (dataSnapshot.hasChild("message_actual")) {
                 comment_view.setText(dataSnapshot.child("message_actual").getValue().toString());
             }
         }catch (Exception e)
         {
             e.printStackTrace();
         }

         try {
             if (dataSnapshot.hasChild("likes")) {
                 num_likes_view.setText(String.valueOf(dataSnapshot.child("likes").getChildrenCount()));
                 thumbs_up_view.setVisibility(View.VISIBLE);

                 for (DataSnapshot dataSnapshot1 : dataSnapshot.child("likes").getChildren()) {
                     if (dataSnapshot1.getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                         likes_view.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));

                     }
                 }
             } else {
                 num_likes_view.setText("");
             }
         }catch (Exception e)
         {
             e.printStackTrace();
         }










         try {
             if (all_messages_snapshot.child(message_uid).child("to").getValue().equals(dataSnapshot.child("from").getValue())) {
                 try {
                     String name = dataSnapshot.child("from_name").getValue().toString() + "\n";
                     SpannableStringBuilder str = new SpannableStringBuilder(name + dataSnapshot.child("message_actual").getValue().toString());
                     str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, dataSnapshot.child("from_name").getValue().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     comment_view.setText(str);
                 }catch (Exception e)
                 {
                     e.printStackTrace();
                 }


                 if (is_activity_running && req_ref<all_comments_data_list.size()) {


                     //   StorageReference prof_reference_one = storageReference_one.child(dataSnapshot.child("from").getValue().toString());

                     try {
                         final StorageReference storageReference_one = firebaseStorage.getReference();
                         Glide.with(getApplicationContext())
                                 .using(new FirebaseImageLoader())
                                 .load(storageReference_one.child(dataSnapshot.child("from").getValue().toString())).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.NONE).into(profile_pic_view);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }






                 /*
                 if (bitmap == null) {


                     prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             if (req_ref < all_comments_data_list.size()) {
                                 if (get_activity_state()) {
                                     runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             try {
                                                 Glide.with(getApplicationContext())
                                                         .using(new FirebaseImageLoader())
                                                         .load(storageReference_one.child(dataSnapshot.child("from").getValue().toString())).into(profile_pic_view);



                                                 Drawable drawable = profile_pic_view.getDrawable();
                                                 bitmap = ((GlideBitmapDrawable) drawable.getCurrent()).getBitmap();

                                             } catch (Exception e) {
                                                 e.printStackTrace();
                                             }
                                         }
                                     });

                                 }
                             }

                             // Got the download URL for 'users/me/profile.png'
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception exception) {

                             // File not found
                         }
                     });
                 } else {
                     try {
                         profile_pic_view.setImageBitmap(bitmap);
                     }catch (Exception e)
                     {
                         e.printStackTrace();
                     }

                 }
                 */

             }
         }catch (Exception e)
         {
             e.printStackTrace();
         }






         try {
             if (dataSnapshot.hasChild("timestamp")) {

                 if (now!=0) {
                     long milli_sec = Long.parseLong(dataSnapshot.child("timestamp").getValue().toString());
                     // int hours   = (int) (((System.currentTimeMillis()-milli_sec)/ (1000*60*60)) % 24);
                     int hours = (int) (((now- milli_sec)) / (1000 * 60 * 60));
                     int mins = (int) (((now - milli_sec) / (1000 * 60)) % 60);

                     if (hours >= 1) {
                         hours_ago_view.setText(String.valueOf(hours) + " " + getResources().getString(R.string.hours_ago));
                     } else {

                         if (mins>0) {
                             //  int minutes = (int) (((System.currentTimeMillis()-milli_sec) / (1000*60) % 60));
                             hours_ago_view.setText(String.valueOf(mins) + " " + getResources().getString(R.string.minutes_ago));
                         }
                         else {
                             hours_ago_view.setText("0 " + getResources().getString(R.string.minutes_ago));
                         }
                     }
                 }
             }
         }catch (Exception e)
         {
             e.printStackTrace();
         }



         try {
             likes_view.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     try {


                     if (get_activity_state()) {
                         if (is_connected()) {


                             boolean found = false;
                             String uid = "";
                             likes_view.setClickable(false);

                             if (dataSnapshot.hasChild("likes")) {

                                 for (DataSnapshot dataSnapshot1 : dataSnapshot.child("likes").getChildren()) {
                                     if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                         uid = dataSnapshot1.getKey();
                                         found = true;
                                     }
                                 }
                             }


                             if (!found) {
                                 databaseReference.child("messages").child(message_uid).child("comments").child(dataSnapshot.getKey()).child("likes").push().setValue(mAuth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (get_activity_state()) {
                                             likes_view.setClickable(true);
                                             if (task.isSuccessful()) {
                                                 likes_view.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                                                 thumbs_up_view.setVisibility(View.VISIBLE);
                                             } else {

                                             }
                                         }
                                     }
                                 });
                             } else {

                                 if (!uid.equals("")) {

                                     databaseReference.child("messages").child(message_uid).child("comments").child(dataSnapshot.getKey()).child("likes").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if (get_activity_state()) {
                                                 likes_view.setClickable(true);
                                                 if (task.isSuccessful()) {
                                                     likes_view.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey_600, null));
                                                     thumbs_up_view.setVisibility(View.INVISIBLE);
                                                 } else {

                                                 }
                                             }
                                         }
                                     });
                                 }


                             }
                         } else {
                             show_snackbar(getResources().getString(R.string.no_internet_connection), false);
                         }
                     }
                 }catch (Exception e)
                     {
                         e.printStackTrace();
                     }

                 }
             });
         }catch (Exception e)
         {
             e.printStackTrace();
         }




           }



            return view;
        }
    }

    public void show_snackbar(String txt, Boolean length)
    {
        if (is_activity_running) {
        try {
            if (parentLayout != null) {
                Snackbar mSnackbar;
                if (length) {
                    mSnackbar = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_LONG);
                } else {
                    mSnackbar = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_SHORT);
                }

                View mView = mSnackbar.getView();
                mView.setBackgroundColor(ContextCompat.getColor(comment_activity.this, R.color.colorPrimaryDark));
                TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
                mTextView.setTextColor(ContextCompat.getColor(comment_activity.this, R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                mSnackbar.show();

                // Snackbar.make(parentLayout, "Please wait!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        }
    }






    private boolean get_activity_state() {
        if ( mAuth != null && mAuth.getCurrentUser() != null && is_activity_running ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean is_connected()
    {
        connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected())
        {
            return true;
        }
        else {
            return false;
        }
    }
    public long get_time_now()
    {

        if (now!=0)
        {
            return now;

        }else {
            return 0;
        }
    }


    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {


            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    if (client==null)
                    {
                        client = new NTP_UTC_TIME();
                    }
                    else {
                        if (client.requestTime("pool.ntp.org", 2000)) {
                            now = client.getNtpTime();
                            Log.d(TAG, String.valueOf(now));
                        }
                    }
                }
            });


        }
    };


    public void start_timer() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 10000);
    }

    public void stop_timer (){
        if (timer!=null) {
            timer.cancel();
        }
        timer = null;
    }

}
