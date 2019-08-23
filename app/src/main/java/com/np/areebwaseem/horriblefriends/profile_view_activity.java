package com.np.areebwaseem.horriblefriends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_view_activity extends AppCompatActivity {

    private static final String TAG = "profile_view_activity";




    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    volatile boolean isDownloading;
    ArrayList<String> user_name_list_all;

    DataSnapshot all_users_uid_dataa_all;
    ArrayList<String> all_friends_list;
    //ChildEventListener friends_child_event_listener_may;
    String current_uid;
    DataSnapshot all_users_snapshot;
    ArrayList<String> all_users_name_list;
    EditText search_user_edit_text;
   boolean isSearching=false;
   DataSnapshot personal_data_snap;
   DataSnapshot friend_request_data;
   ArrayList<String> final_uids;
    DataSnapshot all_memes_data_snapshot;
    CustomAdapter_friend_list customAdapter_friend_list;
    boolean is_friends_downloading;

   CustomAdapter customAdapter;
   TextView no_meme_found_text_view;
   ListView all_memes_list_view;

   ArrayList<String>  memes_with_friends_list;

    ShareDialog shareDialog;
    ProgressBar all_bar;
    DataSnapshot user_profile_all_friends_snapshot;
    DataSnapshot user_profile_data_snapshot;
    DataSnapshot user_profile_all_data_snapshot;

    boolean isDownloading_all;
    ArrayList<String> user_uid_list_all;

    DataSnapshot all_user_data_all;

    CircleImageView prof_view;
    boolean isDownloading_friend_list;
    TextView meme_of_text_view;
    TextView friends_of_text_view;


    CallbackManager callbackManager;

    int instagram_code=1101;
    int whatsapp_code= 1102;
    int twitter_code=1103;
    TextView full_name_view;
    TextView nick_view;
    TextView loc_view;
    TextView no_friends_yet_view;

    boolean personal_found;
   boolean is_activity_running;

   ProgressBar memes_bar;

    CustomAdapter_all customAdapter_all;

   TextView no_of_memes;
   TextView no_of_friends;

   ImageButton message_button;
   ImageButton cancel_request_button;
   ImageButton love_button;

   LinearLayout all_buttons;
   ListView users_view_all;
    ChildEventListener valueEventListener_all;

    DataSnapshot personal_friends_data_all;


    int write_meme_request_code = 1001;

    boolean is_downloading_prof_data;


    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    View parentLayout;

    long now = 0;
    public String current_share_uid="";
    private Timer timer;

    NTP_UTC_TIME client;
    DataSnapshot personal_data_snap_all;

    ArrayList<String> all_friends_list_uids;
    ArrayList<String> all_friends_list_names;
    SwipeRefreshLayout refresh_layout;
    ProgressBar friends_list_bar;

    ConstraintLayout all_but_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_view_layout);

        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        customAdapter_all = new CustomAdapter_all();
        customAdapter_friend_list = new CustomAdapter_friend_list();


        progressBar= findViewById(R.id.prof_view_progress_bar);

        parentLayout = findViewById(android.R.id.content);
        no_meme_found_text_view = findViewById(R.id.profile_view_no_memes_text_view);
        all_memes_list_view= findViewById(R.id.profile_view_memes_list_view);
        full_name_view = findViewById(R.id.user_profile_view_fragment_name_text_view);
        nick_view = findViewById(R.id.user_profile_view_fragment_nick_view);
        loc_view = findViewById(R.id.user_profile_view_fragment_location_view);
        no_of_memes = findViewById(R.id.prof_view_no_meme_text_view);
        no_of_friends = findViewById(R.id.prof_view_no_friends_text_view);
        message_button = findViewById(R.id.profilePview_list_item_all_message_button);
        cancel_request_button = findViewById(R.id.profile_view_custom_list_item_cancel_request_button);
        love_button= findViewById(R.id.profile_custom_list_item_love_button);
        all_buttons = findViewById(R.id.linear_layout_custom_buttons_profile_view);
        all_bar = findViewById(R.id.profile_view_all_buttons_progress_bar);
        no_friends_yet_view = findViewById(R.id.profile_view_no_friends_text_view);
        refresh_layout = findViewById(R.id.profile_view_new_swipe_refresh_layout);
        friends_list_bar = findViewById(R.id.progressBar_prof_view_friends_list);
        memes_bar = findViewById(R.id.progressBar_prof_view_memes_list);
        all_but_layout = findViewById(R.id.all_buttons_cons_layout);
        meme_of_text_view = findViewById(R.id.memes_of_profile_view_text_view);
        friends_of_text_view = findViewById(R.id.horrible_friends_of_profile_text_view);





       prof_view =findViewById(R.id.user_profile_pic_profile_view);


        mAuth= FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        isDownloading=false;
        isDownloading_all=false;
        is_activity_running=true;
        isDownloading_friend_list=false;
        is_friends_downloading=false;
        is_downloading_prof_data=false;

        users_view_all = findViewById(R.id.profile_view_all_friends_list_view);


        personal_found=false;

        all_friends_list = new ArrayList<>();
        memes_with_friends_list= new ArrayList<>();
        customAdapter = new CustomAdapter();

        user_uid_list_all = new ArrayList<>();
        user_name_list_all = new ArrayList<>();
        all_friends_list_names= new ArrayList<>();
        all_friends_list_uids  = new ArrayList<>();










        if (savedInstanceState==null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras ==null)
            {

                current_uid= null;

            }
            else {
               current_uid = extras.getString("curr_uid");
            }
        }
        else {
          current_uid = (String) savedInstanceState.getSerializable("curr_uid");
        }




        if (current_uid!=null) {

            if (!current_uid.equals(mAuth.getCurrentUser().getUid()))
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) all_but_layout.getLayoutParams();
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                all_but_layout.setLayoutParams(layoutParams);
            }



            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.status_bar));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


            client = new NTP_UTC_TIME();

            start_timer();


            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog(this);


            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    perform_share();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                    show_snackbar(getResources().getString(R.string.cant_open_facebook), false);

                }
            });




            check_for_favourite();
            get_profile_data();
            get_all_memes();
            get_all_friends();


            refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh_layout.setRefreshing(false);
                    check_for_favourite();
                    get_profile_data();
                    get_all_memes();
                    get_all_friends();
                }
            });


            love_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (is_connected())
                    {

                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && get_activity_state()) {



                                personal_data_snap_all = dataSnapshot;
                                boolean value_was = false;
                                String curr_key = "";
                                for (DataSnapshot dataSnapshot1 : personal_data_snap_all.child("favourites").getChildren()) {
                                        if (dataSnapshot1.getKey().equals(current_uid)) {
                                            if (dataSnapshot1.getValue().equals(true)) {
                                                value_was = true;
                                                curr_key = dataSnapshot1.getKey();
                                            } else {
                                                value_was = false;
                                                curr_key = dataSnapshot1.getKey();
                                            }

                                        }

                                }
                                if (!curr_key.equals("") && !value_was) {
                                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("favourites").child(curr_key).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (get_activity_state()) {
                                                if (task.isSuccessful()) {
                                                    love_button.setColorFilter(getResources().getColor(R.color.red_400));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                    love_button.setPadding(margin, margin, margin, margin);
                                                } else {

                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                    love_button.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._8sdp);
                                                    love_button.setPadding(margin, margin, margin, margin);
                                                }
                                            }

                                        }
                                    });
                                } else if (!curr_key.equals("") && value_was) {
                                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("favourites").child(curr_key).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (get_activity_state()) {
                                                if (task.isSuccessful()) {
                                                    love_button.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._8sdp);
                                                    love_button.setPadding(margin, margin, margin, margin);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                    love_button.setColorFilter(getResources().getColor(R.color.red_400));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                    love_button.setPadding(margin, margin, margin, margin);
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("favourites").child(current_uid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (get_activity_state()) {
                                                if (task.isSuccessful()) {
                                                    love_button.setColorFilter(getResources().getColor(R.color.red_400));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                    love_button.setPadding(margin, margin, margin, margin);
                                                } else {
                                                    Toast.makeText(getApplicationContext(),  getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                    love_button.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._8sdp);
                                                    love_button.setPadding(margin, margin, margin, margin);

                                                }
                                            }
                                        }
                                    });
                                }

                            } else {
                                if (is_activity_running) {
                                    Toast.makeText(getApplicationContext(),  getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (is_activity_running) {
                                Toast.makeText(getApplicationContext(),  getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                        if (is_activity_running) {
                            show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                        }
                    }

                }
            });


            cancel_request_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (get_activity_state()) {
                        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        networkInfo = connMgr.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()) {


                            try {
                            final AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(profile_view_activity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(profile_view_activity.this);
                            }
                            builder.setTitle(getResources().getString(R.string.confirm_string))
                                    .setMessage(getResources().getString(R.string.sure_want_to_unfriend_string)).setPositiveButton(getResources().getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    final DatabaseReference delete_reference = databaseReference.child("users");
                                    //   cancel_request_button.setVisibility(View.INVISIBLE);
                                    all_buttons.setVisibility(View.INVISIBLE);
                                    show_all_bar();
                                    delete_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            if (get_activity_state()) {


                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                        /*
                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        if (dataSnapshot1.getKey().equals(user_uid_list_all.get(req_ref))) {
                                                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                                                if (dataSnapshot2.getKey().equals("friends")) {
                                                                    for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                                                        if (dataSnapshot3.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                            delete_reference.child(user_uid_list_all.get(req_ref)).child("friends").child(dataSnapshot3.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        for (DataSnapshot dataSnapshot11 : dataSnapshot.getChildren()) {
                                                                                            if (dataSnapshot11.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                                                                                for (DataSnapshot dataSnapshot4 : dataSnapshot11.getChildren()) {
                                                                                                    if (dataSnapshot4.getKey().equals("friends")) {
                                                                                                        for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                                                                                            if (dataSnapshot5.getValue().equals(user_uid_list_all.get(req_ref))) {
                                                                                                                DatabaseReference fresh_ref = databaseReference.child("users");
                                                                                                                fresh_ref.child(mAuth.getCurrentUser().getUid()).child("friends").child(dataSnapshot5.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if (task.isSuccessful()) {
                                                                                                                            if (getActivity()!=null)
                                                                                                                            {
                                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                                @Override
                                                                                                                                public void run() {
                                                                                                                                    try {
                                                                                                                                        if (req_ref < user_uid_list_all.size()) {
                                                                                                                                            all_buttons.setVisibility(View.INVISIBLE);
                                                                                                                                        }
                                                                                                                                    } catch (Exception e) {
                                                                                                                                        e.printStackTrace();
                                                                                                                                    }

                                                                                                                                    hide_all_bar();
                                                                                                                                }
                                                                                                                            });
                                                                                                                        }


                                                                                                                        } else {
                                                                                                                            if (getActivity()!=null)
                                                                                                                            {
                                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                                @Override
                                                                                                                                public void run() {
                                                                                                                                    try {
                                                                                                                                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                                                                                                                    } catch (Exception e) {
                                                                                                                                        e.printStackTrace();
                                                                                                                                    }

                                                                                                                                    hide_all_bar();
                                                                                                                                }
                                                                                                                            });
                                                                                                                        }

                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {


                                                                                        if (get_activity_state()) {
                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    try {
                                                                                                        if (req_ref < user_uid_list_all.size()) {
                                                                                                            all_buttons.setVisibility(View.VISIBLE);
                                                                                                        }
                                                                                                        hide_all_bar();
                                                                                                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                    }

                                                                                                }
                                                                                            });
                                                                                        }

                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }
                                                    */


                                                            for (DataSnapshot dataSnapshot11 : dataSnapshot.getChildren()) {
                                                                if (dataSnapshot11.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                                                    for (DataSnapshot dataSnapshot4 : dataSnapshot11.getChildren()) {
                                                                        if (dataSnapshot4.getKey().equals("friends")) {
                                                                            for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                                                                if (dataSnapshot5.getValue().equals(current_uid)) {
                                                                                    DatabaseReference fresh_ref = databaseReference.child("users");
                                                                                    fresh_ref.child(mAuth.getCurrentUser().getUid()).child("friends").child(dataSnapshot5.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                if (is_activity_running) {
                                                                                                    runOnUiThread(new Runnable() {
                                                                                                        @Override
                                                                                                        public void run() {
                                                                                                            try {

                                                                                                                all_buttons.setVisibility(View.INVISIBLE);
                                                                                                            } catch (Exception e) {
                                                                                                                e.printStackTrace();
                                                                                                            }


                                                                                                            hide_all_bar();

                                                                                                            finish();
                                                                                                        }
                                                                                                    });
                                                                                                }


                                                                                            } else {
                                                                                                if (is_activity_running) {
                                                                                                    runOnUiThread(new Runnable() {
                                                                                                        @Override
                                                                                                        public void run() {
                                                                                                            try {
                                                                                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                                                                            } catch (Exception e) {
                                                                                                                e.printStackTrace();
                                                                                                            }

                                                                                                            hide_all_bar();
                                                                                                        }
                                                                                                    });
                                                                                                }

                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                            all_buttons.setVisibility(View.VISIBLE);
                                            hide_all_bar();
                                            if (get_activity_state()) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                    dialog.dismiss();
                                }
                            }).setNegativeButton(getResources().getString(R.string.no_string), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        } else {
                            if (get_activity_state()) {
                                show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                            }
                        }
                    }
                }
            });


            message_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(profile_view_activity.this, write_meme_activity.class);
                    intent.putExtra("selected_user", current_uid);
                    intent.putExtra("selected_user_name", full_name_view.getText().toString());
                    intent.putExtra("selected_user_nick", nick_view.getText().toString());
                    startActivityForResult(intent, write_meme_request_code);
                }
            });


        }











    }

    private void hide_all_bar()
    {
        try {
            isDownloading = false;
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams)  memes_bar.getLayoutParams();
            layoutParams.height= 0;
            memes_bar.setLayoutParams(layoutParams);
            memes_bar.setVisibility(View.INVISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void show_all_bar()
    {
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams)  memes_bar.getLayoutParams();
            layoutParams.height= (LinearLayout.LayoutParams.WRAP_CONTENT);
            memes_bar.setLayoutParams(layoutParams);
            memes_bar.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_activity_running=false;
        stop_timer();
        if (valueEventListener_all!=null)
        {
            databaseReference.child("users").child(current_uid).child("friends").removeEventListener(valueEventListener_all);
            valueEventListener_all=null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (valueEventListener_all!=null)
        {
            databaseReference.child("users").child(current_uid).child("friends").removeEventListener(valueEventListener_all);
            valueEventListener_all=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (current_uid==null)
        {
            finish();
        }
        else {
            start_timer();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        ////////////////////// Facebook Share ////////////////////////

        callbackManager.onActivityResult(requestCode, resultCode, data);

        ///////////////////////////////////////////////////////////








        if ( requestCode==twitter_code)
        {
            if (resultCode==RESULT_OK) {
                //   Toast.makeText(getApplicationContext(), "Share Successful!", Toast.LENGTH_SHORT).show();
                perform_share();
            }
            else{
                //  Toast.makeText(this, "Share Unsuccess!",Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode==whatsapp_code  || requestCode==instagram_code )
        {
            show_alert_dialog();
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
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected())
        {
            return true;
        }
        else {
            return false;
        }
    }
    public void show_snackbar(String txt, Boolean length)
    {
        try {
            if (parentLayout!=null) {
                Snackbar mSnackbar;
                if (length) {
                    mSnackbar  = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_LONG);
                }
                else {
                    mSnackbar = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_SHORT);
                }

                View mView = mSnackbar.getView();
                mView.setBackgroundColor(ContextCompat.getColor(profile_view_activity.this, R.color.colorPrimaryDark));
                TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
                mTextView.setTextColor(ContextCompat.getColor(profile_view_activity.this, R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                else {
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

    public void get_all_memes()
    {




        if (get_activity_state()) {



            if (is_connected()) {


                try {
                    if (!isDownloading)
                    {



                       // no_meme_found_text_view.setVisibility(View.INVISIBLE);

                        hide_no_meme_text_view();


                        all_friends_list.clear();
                        memes_with_friends_list.clear();
                        if (customAdapter != null) {
                            customAdapter.notifyDataSetChanged();
                        }


                        make_list_to_zero();
                        isDownloading=true;
                       show_all_bar();


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DatabaseReference messages_reference = databaseReference.child("messages");
                                    messages_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {

                                                if (get_activity_state()) {

                                                    all_memes_data_snapshot = dataSnapshot;





                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                        if (dataSnapshot1.hasChild("to")) {
                                                            if (dataSnapshot1.child("to").getValue().equals(current_uid)) {
                                                                memes_with_friends_list.add(dataSnapshot1.getKey());
                                                            }
                                                        }

                                                    }
                                                    isDownloading = false;
                                                    if (is_activity_running) {




                                                        if (memes_with_friends_list.size()>0)
                                                        {

                                                            boolean is_true = false;

                                                            while (!is_true) {
                                                                boolean check = false;
                                                                for (int i = 1; i < memes_with_friends_list.size(); i++) {
                                                                    try {
                                                                        if (Long.parseLong(all_memes_data_snapshot.child(memes_with_friends_list.get(i)).child("timestamp").getValue().toString()) < Long.parseLong(all_memes_data_snapshot.child(memes_with_friends_list.get(i - 1)).child("timestamp").getValue().toString())) {
                                                                            String a = memes_with_friends_list.get(i - 1);
                                                                            memes_with_friends_list.set(i - 1, memes_with_friends_list.get(i));
                                                                            memes_with_friends_list.set(i, a);
                                                                            check = true;
                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                if (!check) {
                                                                    is_true = true;
                                                                }
                                                            }
                                                        }


                                                  runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (memes_with_friends_list.size() > 0) {
                                                                        hide_all_bar();
                                                                        Collections.reverse(memes_with_friends_list);
                                                                      //  no_of_memes.setText(String.valueOf(memes_with_friends_list.size()) + " Message(s)");
                                                                        all_memes_list_view.setAdapter(customAdapter);
                                                                        customAdapter.notifyDataSetChanged();
                                                                        make_list_to_wrap();
                                                                    } else {
                                                                      hide_all_bar();
                                                                       // no_of_memes.setText("0 Message(s)");
                                                                        no_meme_found_text_view.setVisibility(View.VISIBLE);
                                                                        show_no_meme_text_view();
                                                                        // Toast.makeText(getActivity(), "No memes found", Toast.LENGTH_SHORT).show();
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
                                                hide_all_bar();
                                                e.printStackTrace();
                                            }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            if (get_activity_state()) {
                                                isDownloading=false;
                                            runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                           hide_all_bar();
                                                            //Toast.makeText(getApplicationContext(), "cancel error", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });

                                            }

                                            //Error

                                        }
                                    });
                                } catch (Exception e) {
                                    hide_all_bar();
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                    else {

                    }
                }catch (Exception e)
                {
                    isDownloading=false;
                    e.printStackTrace();
                }


            }
















            else {
                try {
                    if (is_activity_running) {
                       show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }


        }

    }

    private void make_list_to_wrap()
    {
        try {
            if (get_activity_state() && all_memes_list_view!=null) {
                int totalHeight = 0;
                int desiredWidth = View.MeasureSpec.makeMeasureSpec(all_memes_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                for (int z = 0; z < customAdapter.getCount(); z++) {
                    View listItem = customAdapter.getView(z, null, all_memes_list_view);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params = all_memes_list_view.getLayoutParams();
                params.height = totalHeight + (all_memes_list_view.getDividerHeight() * (customAdapter.getCount() - 1));
                all_memes_list_view.setLayoutParams(params);
                all_memes_list_view.requestLayout();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void make_list_to_zero()
    {
        try {
            if (get_activity_state() && all_memes_list_view!=null) {
                ViewGroup.LayoutParams params = all_memes_list_view.getLayoutParams();
                params.height = 0;
                all_memes_list_view.setLayoutParams(params);
                all_memes_list_view.requestLayout();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void make_friends_list_to_wrap()
    {

        try {
            if (get_activity_state() && users_view_all != null) {
                int totalHeight = 0;
                int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view_all.getWidth(), View.MeasureSpec.AT_MOST);
                for (int z = 0; z < customAdapter_friend_list.getCount(); z++) {
                    View listItem = customAdapter_friend_list.getView(z, null, users_view_all);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params = users_view_all.getLayoutParams();
                params.height = totalHeight + (users_view_all.getDividerHeight() * (customAdapter_friend_list.getCount() - 1));
                users_view_all.setLayoutParams(params);
                users_view_all.requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void make_friends_list_to_zero()
    {
        try {
            if (get_activity_state() && users_view_all!=null) {
                ViewGroup.LayoutParams params = users_view_all.getLayoutParams();
                params.height = 0;
                users_view_all.setLayoutParams(params);
                users_view_all.requestLayout();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    private void error_bar()
    {
        if (is_activity_running) {

           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (is_activity_running) {
                            isDownloading = false;
                            if (memes_bar != null) {
                                memes_bar.setVisibility(View.INVISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return  memes_with_friends_list.size();
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

            view = getLayoutInflater().inflate(R.layout.message_background_layout,null);

            final ImageView to_image_view = view.findViewById(R.id.to_horrible_friend_image_view);
            TextView to_text_view = view.findViewById(R.id.message_list_item_to_name_text_view);
            TextView meme_text_view = view.findViewById(R.id.meme_text_view_message_list_item);
            TextView hours_ago_text_view = view.findViewById(R.id.hours_ago_text_view);
            ConstraintLayout share_icon = view.findViewById(R.id.meme_home_list_item_share);
            ConstraintLayout comment_icon = view.findViewById(R.id.meme_home_list_item_comment);
            TextView no_of_shares = view.findViewById(R.id.no_of_shares_text_view);
            TextView no_of_comments = view.findViewById(R.id.no_of_comments_text_view);
            final ConstraintLayout share_part_constraint_layout = view.findViewById(R.id.meme_background_constraint_view_memes_list_item);



            final int req_ref = i;


            if (get_activity_state() && req_ref<memes_with_friends_list.size())
            {

                try {
                    if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("to_name")) {
                        to_text_view.setText(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("to_name").getValue().toString());
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }






                if (mAuth != null && mAuth.getCurrentUser() != null) {


                    if (is_activity_running && req_ref<memes_with_friends_list.size()) {
                        try {
                            final StorageReference storageReference_one = firebaseStorage.getReference();
                            final String curr_uid = all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("to").getValue().toString();
                            Glide.with(getApplicationContext())
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference_one.child(curr_uid)).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.NONE).into(to_image_view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }






                   /*
                   if (bitmap==null) {

                       final String curr_uid = all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("to").getValue().toString();

                       StorageReference prof_reference_one = storageReference_one.child(curr_uid);
                       prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               if (req_ref < memes_with_friends_list.size()) {
                                   if (getActivity() != null) {
                                       getActivity().runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               try {
                                                   Glide.with(getActivity().getApplicationContext())
                                                           .using(new FirebaseImageLoader())
                                                           .load(storageReference_one.child(curr_uid)).into(to_image_view);


                                                   Drawable drawable = to_image_view.getDrawable();
                                                   bitmap = ((GlideBitmapDrawable)drawable.getCurrent()).getBitmap();

                                                 //  BitmapDrawable prof_drawable = (BitmapDrawable) to_image_view.getDrawable();
                                                 //  bitmap = prof_drawable.getBitmap();

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
                   }
                   else {
                       try {
                           to_image_view.setImageBitmap(bitmap);
                       }catch (Exception e)
                       {
                           e.printStackTrace();
                       }

                   }
                   */
                }


                try {
                    if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("message")) {
                        meme_text_view.setText(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("message").getValue().toString());
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }




                try {
                    if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("timestamp")) {


                        long now_time= get_time_now();
                        if (now_time!=0) {
                            long milli_sec = Long.parseLong(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("timestamp").getValue().toString());
                            // int hours   = (int) (((System.currentTimeMillis()-milli_sec)/ (1000*60*60)) % 24);
                            //int hours = (int) (((now_time - milli_sec)) / (1000 * 60 * 60));
                            //int mins = (int) (((now_time - milli_sec) / (1000 * 60)) % 60);

                           // int hours   = (int) (((now_time-milli_sec) / (1000*60*60)) % 24);
                            // int mins = (int) (((now_time - milli_sec) / (1000 * 60)) % 60);
                            int hours = (int) (((now_time - milli_sec)) / (1000 * 60 * 60));
                            int mins = (int) (((now_time-milli_sec) / (1000*60)) % 60);

                            if (hours >= 1) {
                                hours_ago_text_view.setText(String.valueOf(hours) + " " + getResources().getString(R.string.hours_ago));
                            } else {
                                //  int minutes = (int) (((System.currentTimeMillis()-milli_sec) / (1000*60) % 60));
                                hours_ago_text_view.setText(String.valueOf(mins) + " "+getResources().getString(R.string.minutes_ago));
                            }
                            // Log.d(TAG, String.valueOf(System.currentTimeMillis()));
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }



                try {
                    if(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("shares"))
                    {
                        no_of_shares.setVisibility(View.VISIBLE);
                        no_of_shares.setText(String.valueOf(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("shares").getChildrenCount()));
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }


                try {
                    if(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("comments"))
                    {
                        no_of_comments.setVisibility(View.VISIBLE);
                        no_of_comments.setText(String.valueOf(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("comments").getChildrenCount()));
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }






                share_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // View myView = getViewByPosition(req_ref, all_memes_list_view);

                        // final Bitmap my_bit=  getBitmapFromView(myView);

                        try {
                            View myView = share_part_constraint_layout;

                            final Bitmap my_bit=  getBitmapFromView(myView);


                            final AlertDialog.Builder builder = new AlertDialog.Builder(profile_view_activity.this);
                            View mView = getLayoutInflater().inflate(R.layout.share_dialog_custom_view,null);
                            ImageButton fb_share = mView.findViewById(R.id.fb_share_custom);
                            ImageButton tw_share = mView.findViewById(R.id.tw_share_custom);
                            ImageButton in_share = mView.findViewById(R.id.in_share_custom);
                            ImageButton wh_share = mView.findViewById(R.id.wh_share_custom);

                            builder.setView(mView);
                            final AlertDialog dialog= builder.create();
                            Window window = dialog.getWindow();
                            final int sdk = android.os.Build.VERSION.SDK_INT;
                            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                window.setBackgroundDrawable(ContextCompat.getDrawable(profile_view_activity.this, R.drawable.layout_rounded_white) );
                            } else {
                                window.setBackgroundDrawable(ContextCompat.getDrawable(profile_view_activity.this, R.drawable.layout_rounded_white));
                            }


                         current_share_uid = memes_with_friends_list.get(req_ref);
                            dialog.show();

                            fb_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        dialog.dismiss();

                                   share_facebook(my_bit);
                                    }catch (Exception e)
                                    {
                                        if (is_activity_running)
                                        {
                                           // Toast.makeText(getApplicationContext(),"Exception from fragment",Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                                        }
                                        e.printStackTrace();
                                    }


                                }
                            });
                            tw_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                   /*
                                   try {
                                       dialog.dismiss();
                                       ((home_activity) getActivity()).current_share_uid = memes_with_friends_list.get(req_ref);
                                       ((home_activity) getActivity()).share_with_twitter(my_bit);
                                   }catch (Exception e)
                                   {
                                       if (getActivity()!=null)
                                       {
                                           Toast.makeText(getActivity(),"Exception from fragment",Toast.LENGTH_SHORT).show();
                                       }
                                       e.printStackTrace();
                                   }
                                   */
                                    dialog.dismiss();
                                    if (is_activity_running) {
                                     share_with_twitter(my_bit);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            in_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    dialog.dismiss();
                                    if (is_activity_running) {
                                       createInstagramIntent(my_bit);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                            wh_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                   /*
                                   try {
                                       dialog.dismiss();
                                       ((home_activity) getActivity()).current_share_uid = memes_with_friends_list.get(req_ref);
                                       ((home_activity) getActivity()).share_with_whatsapp(my_bit);
                                   } catch (Exception e)
                                   {
                                       if (getActivity()!=null)
                                       {
                                           Toast.makeText(getActivity(),"Exception from fragment",Toast.LENGTH_SHORT).show();
                                       }
                                       e.printStackTrace();
                                   }
                                   */
                                    dialog.dismiss();
                                    if (is_activity_running) {
                                       share_with_whatsapp(my_bit);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                });

                comment_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (get_activity_state()) {
                                Intent intent = new Intent(profile_view_activity.this, comment_activity.class);
                                intent.putExtra("message_uid", memes_with_friends_list.get(req_ref));
                               startActivity(intent);
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                });





            }






            return view;
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
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


                    if (client == null) {
                        client = new NTP_UTC_TIME();
                    } else {
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


    public void share_facebook(Bitmap bitmap)
    {


        // Bitmap image =  BitmapFactory.decodeResource(getResources(),
        //        R.drawable.logo_icon_one); // replace with meme.


        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareDialog.show(content);


    }

    /*

    public void whatsapp_share()
    {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
        try {
           startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
           Toast.makeText(getApplicationContext(), "Whatsapp not installed",Toast.LENGTH_SHORT).show();
        }
    }

    public void write_message()
    {
        Map<String, Object> taskMap = new HashMap<>();
        String selected_user_uid="abcd";
        taskMap.put("to",selected_user_uid);
        taskMap.put("from",mAuth.getCurrentUser().getUid());
        taskMap.put("message","how are you");
        taskMap.put("timestamp", ServerValue.TIMESTAMP);
        DatabaseReference new_message_reference= databaseReference.child("messages");
        new_message_reference.push().setValue(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Success!",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    */

    public void share_with_whatsapp(Bitmap bmp) {

        Uri bmpUri = getLocalBitmapUri(bmp); // see previous remote images section
        // Construct share intent as described above based on bitmap

        if (bmpUri!=null) {

            Intent shareIntent = new Intent();
            shareIntent.setPackage("com.whatsapp");

            shareIntent.setAction(Intent.ACTION_SEND);

            //  shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my meme on Horrible Friends"  );
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivityForResult(Intent.createChooser(shareIntent, "Share with Whatsapp"), whatsapp_code);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show();
            }
           /*
           catch (Exception e) {
               e.printStackTrace();
           }
           */
        } else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_getting_file),Toast.LENGTH_SHORT).show();
        }


    }
    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.io_exception),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            //  bmpUri = Uri.fromFile(file);
            bmpUri= FileProvider.getUriForFile(this, "com.np.areebwaseem.horriblefriends.provider", file);

        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.file_not_found_exception),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return bmpUri;

    }

    public void createInstagramIntent(Bitmap bmp){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);
        String type = "image/*";
        // Set the MIME type
        share.setPackage("com.instagram.android");

        share.setType(type);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        // Create the URI from the media
        // File media = new File(mediaPath);
        // Uri uri = Uri.fromFile(media);

        Uri uri = getLocalBitmapUri(bmp);



        if (uri!=null) {
            // Add the URI to the Intent.
            share.putExtra(Intent.EXTRA_STREAM, uri);

            // Broadcast the Intent.
            try {
                startActivityForResult(Intent.createChooser(share, "Share with Instagram"), instagram_code);
                //  startActivityForResult(share,instagram_code);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.instagram_not_installed), Toast.LENGTH_SHORT).show();
            }
            /*
            catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Exception",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            */
        }
        else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_getting_file),Toast.LENGTH_SHORT).show();
        }

    }

    private  void perform_share()
    {
        final DatabaseReference cur_ref= databaseReference.child("messages");
        cur_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(current_share_uid))
                {
                    if (mAuth!=null && mAuth.getCurrentUser()!=null) {
                        cur_ref.child(current_share_uid).child("shares").push().setValue(mAuth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {

                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void show_alert_dialog()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(profile_view_activity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(profile_view_activity.this);

        }
        builder.setTitle("Share Result").setMessage("Was your share successful?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                perform_share();

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }



    public void share_with_twitter(Bitmap bmp)
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        String type = "image/*";
        share.setPackage("com.twitter.android");

        // Set the MIME type
        share.setType(type);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);



        // Create the URI from the media
        // File media = new File(mediaPath);
        // Uri uri = Uri.fromFile(media);

        Uri uri = getLocalBitmapUri(bmp);



        if (uri!=null) {
            // Add the URI to the Intent.
            share.putExtra(Intent.EXTRA_STREAM, uri);

            // Broadcast the Intent.
            try {
                startActivityForResult(Intent.createChooser(share, "Share with Twitter"), twitter_code);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.twitter_not_installed), Toast.LENGTH_SHORT).show();
            }
            /*
            catch (Exception e) {
                e.printStackTrace();
            }
            */
        }
        else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_getting_file),Toast.LENGTH_SHORT).show();
        }




    }

    public void  get_friends_data()
    {
        if (is_activity_running) {
            try {
                connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo!=null && networkInfo.isConnected()) {

                    if (!isDownloading) {

                       get_all_memes();

                    }
                    if (!isDownloading_all) {

                        performSearch();

                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }




        }
    }


    private void performSearch() {

        try {


            if (!isDownloading_all) {
                isDownloading_all = true;
                show_all_bar();

                if (user_uid_list_all != null) {
                    user_uid_list_all.clear();
                    if (customAdapter_all != null) {
                        customAdapter_all.notifyDataSetChanged();
                    }

                }
                if (valueEventListener_all != null) {
                    databaseReference.child("users").child(current_uid).child("friends").removeEventListener(valueEventListener_all);
                }

                if (user_name_list_all != null) {
                    user_name_list_all.clear();
                    if (customAdapter_all != null) {
                        customAdapter_all.notifyDataSetChanged();
                    }
                }

                /*
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_friends_yet_view.getLayoutParams();
                layoutParams.height = 0;
                no_friends_yet_view.setLayoutParams(layoutParams);
                */
                hide_no_friends_text_view();


                int totalHeight = 0;
                int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view_all.getWidth(), View.MeasureSpec.AT_MOST);
                for (int z = 0; z < customAdapter_all.getCount(); z++) {
                    View listItem = customAdapter_all.getView(z, null, users_view_all);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params = users_view_all.getLayoutParams();
                params.height = totalHeight + (users_view_all.getDividerHeight() * (customAdapter_all.getCount() - 1));
                users_view_all.setLayoutParams(params);
                users_view_all.requestLayout();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            DatabaseReference databaseReference1 = databaseReference.child("users");
                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {

                                        if (get_activity_state()) {
                                            all_users_uid_dataa_all = dataSnapshot;
                                            all_user_data_all = dataSnapshot;
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                if (dataSnapshot1.getKey().equals(current_uid)) {
                                                    personal_data_snap_all = dataSnapshot1;
                                                    break;
                                                }
                                            }
                                            if (personal_data_snap_all != null) {
                                                boolean found = false;
                                                for (DataSnapshot dataSnapshot1 : personal_data_snap_all.getChildren()) {
                                                    if (dataSnapshot1.getKey().equals("friends")) {
                                                        found = true;
                                                        personal_friends_data_all = dataSnapshot1;
                                                    }
                                                }
                                                if (personal_friends_data_all != null && found) {
                                                    for (DataSnapshot dataSnapshot1 : personal_friends_data_all.getChildren()) {
                                                        user_uid_list_all.add(dataSnapshot1.getValue().toString());
                                                    }
                                                }

                                                if (user_uid_list_all.size() > 0) {
                                                    if (get_activity_state())
                                                    {

                                                   runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    isDownloading_all = false;
                                                                    hide_all_bar();
                                                                    users_view_all.setAdapter(customAdapter_all);
                                                                    customAdapter_all.notifyDataSetChanged();

                                                                    int totalHeight = 0;
                                                                    int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view_all.getWidth(), View.MeasureSpec.AT_MOST);
                                                                    for (int z = 0; z < customAdapter_all.getCount(); z++) {
                                                                        View listItem = customAdapter_all.getView(z, null, users_view_all);
                                                                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                        totalHeight += listItem.getMeasuredHeight();
                                                                    }

                                                                    ViewGroup.LayoutParams params = users_view_all.getLayoutParams();
                                                                    params.height = totalHeight + (users_view_all.getDividerHeight() * (customAdapter_all.getCount() - 1));
                                                                    users_view_all.setLayoutParams(params);
                                                                    users_view_all.requestLayout();


                                                                    new Thread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            try {
                                                                                valueEventListener_all = databaseReference.child("users").child(current_uid).child("friends").addChildEventListener(new ChildEventListener() {
                                                                                    @Override
                                                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                                                        try {

                                                                                            if (user_uid_list_all != null && user_uid_list_all.size() > 0) {
                                                                                                for (int i = 0; i < user_uid_list_all.size(); i++) {
                                                                                                    if (user_uid_list_all.get(i).equals(dataSnapshot.getValue())) {
                                                                                                        user_uid_list_all.remove(user_uid_list_all.get(i));
                                                                                                        if (customAdapter_all != null) {
                                                                                                            if (get_activity_state()) {
                                                                                                               runOnUiThread(new Runnable() {
                                                                                                                    @Override
                                                                                                                    public void run() {
                                                                                                                        try {


                                                                                                                            customAdapter_all.notifyDataSetChanged();
                                                                                                                            int totalHeight = 0;
                                                                                                                            int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view_all.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                            for (int z = 0; z < customAdapter_all.getCount(); z++) {
                                                                                                                                View listItem = customAdapter_all.getView(z, null, users_view_all);
                                                                                                                                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                                totalHeight += listItem.getMeasuredHeight();
                                                                                                                            }

                                                                                                                            ViewGroup.LayoutParams params = users_view_all.getLayoutParams();
                                                                                                                            params.height = totalHeight + (users_view_all.getDividerHeight() * (customAdapter_all.getCount() - 1));
                                                                                                                            users_view_all.setLayoutParams(params);
                                                                                                                            users_view_all.requestLayout();

                                                                                                                            if (user_uid_list_all.size() == 0) {
                                                                                                                                /*
                                                                                                                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_friends_yet_view.getLayoutParams();
                                                                                                                                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                                                                                no_friends_yet_view.setLayoutParams(layoutParams);
                                                                                                                                */
                                                                                                                                show_no_friends_text_view();
                                                                                                                            }
                                                                                                                        } catch (Exception e) {
                                                                                                                            e.printStackTrace();
                                                                                                                        }
                                                                                                                    }
                                                                                                                });

                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        } catch (Exception e) {
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
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }).start();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });
                                                    }

                                                } else {
                                                    isDownloading_all = false;
                                                    if (get_activity_state()) {
                                                      runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                hide_all_bar();
                                                                try {
                                                                    //Toast.makeText(getActivity(), "No horrible friends to show!", Toast.LENGTH_SHORT).show();
                                                                    /*
                                                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_friends_yet_view.getLayoutParams();
                                                                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                    no_friends_yet_view.setLayoutParams(layoutParams);
                                                                    */
                                                                    show_no_friends_text_view();

                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });
                                                    }

                                                }

                                            } else {
                                                isDownloading_all = false;
                                                if (get_activity_state()) {
                                                runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            hide_all_bar();
                                                            try {
                                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });
                                                }

                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    if (is_activity_running) {
                                        isDownloading_all = false;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }).start();














        /*

            DatabaseReference friend_req_data_initial= databaseReference.child("friend_requests");
            friend_req_data_initial.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friend_request_data=dataSnapshot;

                    if (get_activity_state()) {

                        DatabaseReference search_reference = databaseReference.child("users");
                        search_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                    for (DataSnapshot dre: dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildren())
                                    {
                                        if (dre.getKey().equals("friends"))
                                        {
                                            personal_data_snap=dre;
                                        }
                                    }



                                    bottom_main_bar.setVisibility(View.INVISIBLE);
                                    all_users_uid_data = dataSnapshot;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        for (DataSnapshot dsp : snapshot.getChildren()) {

                                            if (dsp.getValue().toString().toLowerCase().contains(search_user_edit_text.getText().toString().toLowerCase())) {
                                                if (!snapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                                    user_uid_list.add(snapshot.getKey());
                                                    break;

                                                }
                                            }

                                        }

                                    }

                                    if (personal_data_snap!=null) {

                                        for (int i = 0; i < user_uid_list.size(); i++) {
                                            DataSnapshot dataSnapshot1 = all_users_uid_data.child(user_uid_list.get(i));
                                            int mutual_count = 0;
                                            for (DataSnapshot dsp : dataSnapshot1.getChildren()) {
                                                if (dsp.getKey().equals("friends")) {

                                                    for (DataSnapshot dataSnapshot2 : dsp.getChildren()) {
                                                        for (DataSnapshot dataSnapshot3 : personal_data_snap.getChildren()) {
                                                            if (dataSnapshot2.getValue().equals(dataSnapshot3.getValue())) {
                                                                mutual_count = mutual_count + 1;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            user_mututal_list.add(String.valueOf(mutual_count));


                                        }
                                    }
                                    else {
                                        for (int i=0;i<user_uid_list.size();i++)
                                        {
                                            user_mututal_list.add("0");
                                        }
                                    }


                                    boolean found =false;
                                    while (!found) {
                                        found=true;

                                        for (int i = 0; i < user_uid_list.size(); i++) {
                                            if (i > 0) {
                                                if (Integer.valueOf(user_mututal_list.get(i)) > Integer.valueOf(user_mututal_list.get(i - 1))) {
                                                    String temp = user_mututal_list.get(i);
                                                    user_mututal_list.set(i, user_mututal_list.get(i - 1));
                                                    user_mututal_list.set(i - 1, temp);
                                                    String temp2 = user_uid_list.get(i);
                                                    user_uid_list.set(i, user_uid_list.get(i - 1));
                                                    user_uid_list.set(i - 1, temp2);
                                                    found = false;
                                                }
                                            }

                                        }
                                    }






                                    for (int i = 0; i < user_uid_list.size(); i++) {
                                        Log.d(TAG, user_uid_list.get(i));
                                    }

                                    if (customAdapter != null) {
                                        users_view.setAdapter(customAdapter);
                                        customAdapter.notifyDataSetChanged();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {
                                    bottom_main_bar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getActivity(), "Error, please try again", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    bottom_main_bar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Error, please try again",Toast.LENGTH_SHORT).show();
                }
            });
            */


            }
        }catch (Exception e)
        {
            isDownloading_all=false;
            e.printStackTrace();
        }



    }



    class CustomAdapter_all extends BaseAdapter {

        @Override
        public int getCount() {

            return user_uid_list_all.size();

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
        public View getView( int i,  View view, ViewGroup viewGroup) {
            final int req_ref = i;
            view = getLayoutInflater().inflate(R.layout.custom_all_user_list_item,null);





            final ImageView imageView = (ImageView) view.findViewById(R.id.all_profile_pic_custom_user_list_item);
            final TextView name_view = view.findViewById(R.id.all_custom_user_list_item_name_text_name_view);
            final TextView mutual_view = view.findViewById(R.id.all_custom_user_list_item_mutual_view);
            final ImageButton add_friend_button = view.findViewById(R.id.all_custom_list_item_add_friend_button);
            final ImageButton cancel_request_button = view.findViewById(R.id.all_custom_list_item_cancel_request_button);
            final ImageButton love_button = view.findViewById(R.id.all_custom_list_item_love_button);
            final TextView nick_view = view.findViewById(R.id.all_users_nick_view);
            final TextView location_view = view.findViewById(R.id.all_users_location_view);
            final ProgressBar bar_friend_request_processing = view.findViewById(R.id.all_custom_user_list_item_progress_bar);
            final LinearLayout all_buttons = view.findViewById(R.id.linear_layout_all_user_list_item_buttons);
            ImageButton message_button = view.findViewById(R.id.all_custom_list_item_all_message_button);

















            if (get_activity_state() && req_ref<user_uid_list_all.size()) {

////////////////////////////// Watch this code ///////////////////////////////////////


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),profile_view_activity.class);
                        intent.putExtra("curr_uid", user_uid_list_all.get(req_ref));
                      startActivityForResult(intent, write_meme_request_code);
                    }
                });



                //////////////////////// new code with love button ///////////////////////////////

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (personal_data_snap_all!=null)
                            {
                                if (personal_data_snap_all.hasChild("favourites")) {
                                    for (DataSnapshot dataSnapshot1 : personal_data_snap_all.child("favourites").getChildren())
                                    {
                                        if (dataSnapshot1.getKey().equals(user_uid_list_all.get(req_ref)))
                                        {

                                            if (dataSnapshot1.getValue().equals(true))
                                            {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            love_button.setColorFilter(getResources().getColor(R.color.red_400));
                                                            love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                            int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                            love_button.setPadding(margin,margin,margin,margin);
                                                        }catch (Exception e)
                                                        {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });

                                            }

                                        }
                                    }

                                }
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();






                love_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        databaseReference.child("users").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot!=null)
                                {
                                    personal_data_snap_all=dataSnapshot;
                                    boolean value_was=false;
                                    String curr_key="";
                                    for (DataSnapshot dataSnapshot1 : personal_data_snap_all.child("favourites").getChildren()) {
                                        if (dataSnapshot1.getKey().equals(user_uid_list_all.get(req_ref)))
                                        {
                                            if (dataSnapshot1.getValue().equals(true))
                                            {
                                                value_was=true;
                                                curr_key= dataSnapshot1.getKey();
                                            }
                                            else {
                                                value_was=false;
                                                curr_key= dataSnapshot1.getKey();
                                            }

                                        }
                                    }
                                    if (!curr_key.equals("") && !value_was) {
                                        databaseReference.child("users").child(current_uid).child("favourites").child(curr_key).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    love_button.setColorFilter(getResources().getColor(R.color.red_400));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                    love_button.setPadding(margin,margin,margin,margin);
                                                }
                                                else {

                                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                                                    love_button.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._8sdp);
                                                    love_button.setPadding(margin,margin,margin,margin);
                                                }
                                            }
                                        });
                                    }
                                    else if (!curr_key.equals("") && value_was) {
                                        databaseReference.child("users").child(current_uid).child("favourites").child(curr_key).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    love_button.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._8sdp);
                                                    love_button.setPadding(margin,margin,margin,margin);
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                                    love_button.setColorFilter(getResources().getColor(R.color.red_400));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                    love_button.setPadding(margin,margin,margin,margin);
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        databaseReference.child("users").child(current_uid).child("favourites").child(user_uid_list_all.get(req_ref)).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    love_button.setColorFilter(getResources().getColor(R.color.red_400));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                    love_button.setPadding(margin,margin,margin,margin);
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                                    love_button.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                                                    love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button));
                                                    int margin = getResources().getDimensionPixelSize(R.dimen._8sdp);
                                                    love_button.setPadding(margin,margin,margin,margin);

                                                }
                                            }
                                        });
                                    }

                                }
                                else {
                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                cancel_request_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (get_activity_state()) {
                            connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            networkInfo = connMgr.getActiveNetworkInfo();
                            if (networkInfo!=null && networkInfo.isConnected()) {
                                final DatabaseReference delete_reference = databaseReference.child("users");
                                //   cancel_request_button.setVisibility(View.INVISIBLE);
                                all_buttons.setVisibility(View.INVISIBLE);
                                show_all_bar();
                                delete_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        if (get_activity_state()) {



                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {



                                                        for (DataSnapshot dataSnapshot11 : dataSnapshot.getChildren()) {
                                                            if (dataSnapshot11.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                                                for (DataSnapshot dataSnapshot4 : dataSnapshot11.getChildren()) {
                                                                    if (dataSnapshot4.getKey().equals("friends")) {
                                                                        for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                                                            if (dataSnapshot5.getValue().equals(user_uid_list_all.get(req_ref))) {
                                                                                DatabaseReference fresh_ref = databaseReference.child("users");
                                                                                fresh_ref.child(mAuth.getCurrentUser().getUid()).child("friends").child(dataSnapshot5.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            if (is_activity_running)
                                                                                            {
                                                                                              runOnUiThread(new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        try {
                                                                                                            if (req_ref < user_uid_list_all.size()) {
                                                                                                                all_buttons.setVisibility(View.INVISIBLE);
                                                                                                            }
                                                                                                        } catch (Exception e) {
                                                                                                            e.printStackTrace();
                                                                                                        }

                                                                                                        hide_all_bar();
                                                                                                    }
                                                                                                });
                                                                                            }


                                                                                        } else {
                                                                                            if (is_activity_running)
                                                                                            {
                                                                                             runOnUiThread(new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        try {
                                                                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                                                                        } catch (Exception e) {
                                                                                                            e.printStackTrace();
                                                                                                        }

                                                                                                        hide_all_bar();
                                                                                                    }
                                                                                                });
                                                                                            }

                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }catch (Exception e)
                                                    {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();




                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        if (req_ref < user_uid_list_all.size()) {
                                            all_buttons.setVisibility(View.VISIBLE);

                                        }
                                        hide_all_bar();
                                        if (get_activity_state()) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else {
                                if(get_activity_state()) {
                                    show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                }
                            }
                        }
                    }
                });




                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                for (DataSnapshot dataSnapshot : all_users_uid_dataa_all.getChildren()) {

                                    if (dataSnapshot.getKey().toString().equals(user_uid_list_all.get(req_ref))) {
                                        final StorageReference storageReference_one = firebaseStorage.getReference();
                                        if (mAuth != null && mAuth.getCurrentUser() != null) {


                                            if (req_ref < user_uid_list_all.size()) {
                                                if (is_activity_running) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                Glide.with(profile_view_activity.this.getApplicationContext())
                                                                        .using(new FirebaseImageLoader())
                                                                        .load(storageReference_one.child(user_uid_list_all.get(req_ref))).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
                                                            }catch (Exception e)
                                                            {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });

                                                }
                                            }


                                            /*
                                            prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if (req_ref < user_uid_list_all.size()) {
                                                        if (is_activity_running) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        Glide.with(profile_view_activity.this.getApplicationContext())
                                                                                .using(new FirebaseImageLoader())
                                                                                .load(storageReference_one.child(user_uid_list_all.get(req_ref))).into(imageView);
                                                                    }catch (Exception e)
                                                                    {
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

                                            */


                                        }





                                        for (final DataSnapshot dsp : dataSnapshot.getChildren()) {

                                            if (get_activity_state())
                                            {


                                                if (dsp.getKey().toString().equals("full_name")) {
                                                   runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (req_ref < user_uid_list_all.size()) {
                                                                    name_view.setText(dsp.getValue().toString());
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });

                                                }
                                                if (dsp.getKey().equals("name")) {
                                                   runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (req_ref < user_uid_list_all.size() && !dsp.getValue().toString().equals("")) {
                                                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nick_view.getLayoutParams();
                                                                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                    nick_view.setLayoutParams(layoutParams);
                                                                    nick_view.setText("@ " + dsp.getValue().toString());
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });
                                                }
                                                if (dsp.getKey().equals("City")) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (req_ref < user_uid_list_all.size()) {
                                                                    location_view.setText(" " + dsp.getValue().toString());
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });
                                                }
                                                if (dsp.getKey().equals("friends")) {
                                                    if (personal_friends_data_all != null) {
                                                        int mutual_count = 0;
                                                        for (DataSnapshot dataSnapshot1 : dsp.getChildren()) {
                                                            for (DataSnapshot dataSnapshot2 : personal_friends_data_all.getChildren()) {
                                                                if (dataSnapshot2.getValue().equals(dataSnapshot1.getValue())) {
                                                                    mutual_count = mutual_count + 1;
                                                                }
                                                            }
                                                        }
                                                        final int my_mutual = mutual_count;

                                                       runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (req_ref < user_uid_list_all.size() && my_mutual > 0) {
                                                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mutual_view.getLayoutParams();
                                                                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                        mutual_view.setLayoutParams(layoutParams);
                                                                        mutual_view.setText(getResources().getString(R.string.mutual_friends_string) + String.valueOf(my_mutual));
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });


                                                    }
                                                }
                                            }
                                        }


                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }

                message_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(profile_view_activity.this,write_meme_activity.class);
                        intent.putExtra("selected_user", user_uid_list_all.get(req_ref));
                        intent.putExtra("selected_user_name",name_view.getText().toString());
                        intent.putExtra("selected_user_nick",nick_view.getText().toString());
                        startActivityForResult(intent,write_meme_request_code);
                    }
                });
            }

            return view;
        }
    }



    /*

    private void performSearch() {


        try {

            if (get_activity_state()) {





                if (is_connected()) {

                    if (!isDownloading_friend_list) {
                        isDownloading_friend_list = true;

                        if (get_activity_state()) {
                            if (users_view != null) {
                                ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                params.height = 0;
                                users_view.setLayoutParams(params);
                                users_view.requestLayout();
                            }
                        }


                        if (!search_user_edit_text.getText().toString().equals("")) {

                            if (childEventListener != null) {
                                databaseReference.child("friend_requests").child(mAuth.getCurrentUser().getUid()).removeEventListener(childEventListener);
                            }
                            if (friends_child_event_listener != null) {
                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").removeEventListener(friends_child_event_listener);
                            }

                            bottom_main_bar.setVisibility(View.VISIBLE);

                            if (user_uid_list != null) {
                                user_uid_list.clear();
                                if (customAdapter_friend_list != null) {
                                    customAdapter_friend_list.notifyDataSetChanged();
                                }
                            }
                            if (temp_list != null) {
                                temp_list.clear();
                            }
                            if (user_mututal_list != null) {
                                user_mututal_list.clear();
                                if (customAdapter_friend_list != null) {
                                    customAdapter_friend_list.notifyDataSetChanged();
                                }
                            }


                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {


                                        DatabaseReference friend_req_data_initial = databaseReference.child("friend_requests");
                                        friend_req_data_initial.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                try {
                                                    friend_request_data = dataSnapshot;

                                                    if (get_activity_state()) {

                                                        DatabaseReference search_reference = databaseReference.child("users");
                                                        search_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                try {
                                                                    if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                                                        personal_found = false;

                                                                        for (DataSnapshot dre : dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildren()) {
                                                                            if (dre.getKey().equals("friends")) {
                                                                                personal_found = true;
                                                                                personal_data_snap = dre;
                                                                            }
                                                                        }

                                                                        if (get_activity_state()) {

                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {
                                                                                        bottom_main_bar.setVisibility(View.INVISIBLE);
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                }
                                                                            });
                                                                        }


                                                                        all_users_uid_data = dataSnapshot;

                                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                                            for (DataSnapshot dsp : snapshot.getChildren()) {

                                                                                if (dsp.getValue().toString().toLowerCase().contains(search_user_edit_text.getText().toString().toLowerCase())) {
                                                                                    if (!snapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                                                                        user_uid_list.add(snapshot.getKey());
                                                                                        break;

                                                                                    }
                                                                                }

                                                                            }

                                                                        }

                                                                        if (personal_data_snap != null && personal_found) {

                                                                            for (int i = 0; i < user_uid_list.size(); i++) {
                                                                                DataSnapshot dataSnapshot1 = all_users_uid_data.child(user_uid_list.get(i));
                                                                                int mutual_count = 0;
                                                                                for (DataSnapshot dsp : dataSnapshot1.getChildren()) {
                                                                                    if (dsp.getKey().equals("friends")) {

                                                                                        for (DataSnapshot dataSnapshot2 : dsp.getChildren()) {
                                                                                            for (DataSnapshot dataSnapshot3 : personal_data_snap.getChildren()) {
                                                                                                if (dataSnapshot2.getValue().equals(dataSnapshot3.getValue())) {
                                                                                                    mutual_count = mutual_count + 1;
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }

                                                                                user_mututal_list.add(String.valueOf(mutual_count));


                                                                            }
                                                                        } else {
                                                                            for (int i = 0; i < user_uid_list.size(); i++) {
                                                                                user_mututal_list.add("0");
                                                                            }
                                                                        }


                                                                        boolean found = false;
                                                                        while (!found) {
                                                                            found = true;

                                                                            for (int i = 0; i < user_uid_list.size(); i++) {
                                                                                if (i > 0) {
                                                                                    if (Integer.valueOf(user_mututal_list.get(i)) > Integer.valueOf(user_mututal_list.get(i - 1))) {
                                                                                        String temp = user_mututal_list.get(i);
                                                                                        user_mututal_list.set(i, user_mututal_list.get(i - 1));
                                                                                        user_mututal_list.set(i - 1, temp);
                                                                                        String temp2 = user_uid_list.get(i);
                                                                                        user_uid_list.set(i, user_uid_list.get(i - 1));
                                                                                        user_uid_list.set(i - 1, temp2);
                                                                                        found = false;
                                                                                    }
                                                                                }

                                                                            }
                                                                        }


                                                                        for (int i = 0; i < user_uid_list.size(); i++) {
                                                                            Log.d(TAG, user_uid_list.get(i));
                                                                        }

                                                                        for (int i = 0; i < user_uid_list.size(); i++) {
                                                                            for (int z = 0; z < people_may_know_uid.size(); z++) {
                                                                                if (user_uid_list.get(i).equals(people_may_know_uid.get(z))) {
                                                                                    people_may_know_uid.remove(people_may_know_uid.get(z));
                                                                                    if (customAdapter_know != null) {
                                                                                        customAdapter_know.notifyDataSetChanged();
                                                                                    }
                                                                                    break;
                                                                                }
                                                                            }
                                                                        }


                                                                        if (customAdapter_friend_list != null) {
                                                                            if (get_activity_state())
                                                                            {
                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        try {

                                                                                            isDownloading_friend_list = false;


                                                                                            for (int z = 0; z < user_uid_list.size(); z++) {
                                                                                                temp_list.add(user_uid_list.get(z));
                                                                                            }


                                                                                            users_view.setAdapter(customAdapter_friend_list);
                                                                                            customAdapter_friend_list.notifyDataSetChanged();


                                                                                            int totalHeight = 0;
                                                                                            int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                            for (int z = 0; z < customAdapter_friend_list.getCount(); z++) {
                                                                                                View listItem = customAdapter_friend_list.getView(z, null, users_view);
                                                                                                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                totalHeight += listItem.getMeasuredHeight();
                                                                                            }

                                                                                            ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                            params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter_friend_list.getCount() - 1));
                                                                                            users_view.setLayoutParams(params);
                                                                                            users_view.requestLayout();

                                                                                            new Thread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    try {
                                                                                                        childEventListener = databaseReference.child("friend_requests").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                                                                                                            @Override
                                                                                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                                                                // New code

                                                                                                                try {
                                                                                                                    if (get_activity_state()) {
                                                                                                                        for (int i = 0; i < user_uid_list.size(); i++) {
                                                                                                                            if (dataSnapshot.getValue().equals(user_uid_list.get(i))) {
                                                                                                                                user_uid_list.remove(user_uid_list.get(i));
                                                                                                                                temp_list.clear();
                                                                                                                                for (int z = 0; z < user_uid_list.size(); z++) {
                                                                                                                                    temp_list.add(user_uid_list.get(z));
                                                                                                                                }

                                                                                                                                if (get_activity_state()) {
                                                                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                                                                        @Override
                                                                                                                                        public void run() {
                                                                                                                                            try {
                                                                                                                                                customAdapter_friend_list.notifyDataSetChanged();
                                                                                                                                                int totalHeight = 0;
                                                                                                                                                int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                                                for (int z = 0; z < customAdapter_friend_list.getCount(); z++) {
                                                                                                                                                    View listItem = customAdapter_friend_list.getView(z, null, users_view);
                                                                                                                                                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                                                    totalHeight += listItem.getMeasuredHeight();
                                                                                                                                                }

                                                                                                                                                ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                                                                                params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter_friend_list.getCount() - 1));
                                                                                                                                                users_view.setLayoutParams(params);
                                                                                                                                                users_view.requestLayout();
                                                                                                                                            } catch (Exception e) {
                                                                                                                                                e.printStackTrace();
                                                                                                                                            }

                                                                                                                                        }
                                                                                                                                    });
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                } catch (Exception e) {
                                                                                                                    e.printStackTrace();
                                                                                                                }

                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                                                                                try {
                                                                                                                    if (get_activity_state()) {
                                                                                                                        for (int i = 0; i < user_uid_list.size(); i++) {
                                                                                                                            if (dataSnapshot.getValue().equals(user_uid_list.get(i))) {
                                                                                                                                user_uid_list.remove(user_uid_list.get(i));
                                                                                                                                temp_list.clear();
                                                                                                                                for (int z = 0; z < user_uid_list.size(); z++) {
                                                                                                                                    temp_list.add(user_uid_list.get(z));
                                                                                                                                }

                                                                                                                                if (get_activity_state()) {
                                                                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                                                                        @Override
                                                                                                                                        public void run() {
                                                                                                                                            try {
                                                                                                                                                customAdapter_friend_list.notifyDataSetChanged();
                                                                                                                                                int totalHeight = 0;
                                                                                                                                                int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                                                for (int z = 0; z < customAdapter_friend_list.getCount(); z++) {
                                                                                                                                                    View listItem = customAdapter_friend_list.getView(z, null, users_view);
                                                                                                                                                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                                                    totalHeight += listItem.getMeasuredHeight();
                                                                                                                                                }

                                                                                                                                                ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                                                                                params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter_friend_list.getCount() - 1));
                                                                                                                                                users_view.setLayoutParams(params);
                                                                                                                                                users_view.requestLayout();
                                                                                                                                            } catch (Exception e) {
                                                                                                                                                e.printStackTrace();
                                                                                                                                            }

                                                                                                                                        }
                                                                                                                                    });
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                } catch (Exception e) {
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

                                                                                                        friends_child_event_listener = databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").addChildEventListener(new ChildEventListener() {
                                                                                                            @Override
                                                                                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                                                                try {

                                                                                                                    if (get_activity_state()) {
                                                                                                                        for (int i = 0; i < user_uid_list.size(); i++) {
                                                                                                                            if (user_uid_list.get(i).equals(dataSnapshot.getValue())) {
                                                                                                                                // user_uid_list.remove(user_uid_list.get(i));
                                                                                                                                final int i_new = i;
                                                                                                                                if (get_activity_state()) {
                                                                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                                                                        @Override
                                                                                                                                        public void run() {
                                                                                                                                            try {
                                                                                                                                                users_view.getChildAt(i_new).findViewById(R.id.custom_list_item_cancel_request_button).setVisibility(View.INVISIBLE);
                                                                                                                                                users_view.getChildAt(i_new).findViewById(R.id.custom_list_item_add_friend_button).setVisibility(View.INVISIBLE);
                                                                                                                                                users_view.getChildAt(i_new).findViewById(R.id.friend_ship_check_custom_user_list_item).setVisibility(View.VISIBLE);
                                                                                                                                            } catch (Exception e) {
                                                                                                                                                e.printStackTrace();
                                                                                                                                            }

                                                                                                                                        }
                                                                                                                                    });
                                                                                                                                }


                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                } catch (Exception e) {
                                                                                                                    e.printStackTrace();
                                                                                                                }

                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                                            }
                                                                                                        });

                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                    }
                                                                                                }
                                                                                            }).start();

                                                                                        } catch (Exception e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }

                                                                        } else {
                                                                            isDownloading_friend_list = false;
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    isDownloading_friend_list = false;
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                isDownloading_friend_list = false;
                                                                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                                                    if (get_activity_state()) {
                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    bottom_main_bar.setVisibility(View.INVISIBLE);
                                                                                    Toast.makeText(getActivity(), "Error, please try again", Toast.LENGTH_SHORT).show();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        });
                                                                    }

                                                                }

                                                            }
                                                        });

                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                isDownloading_friend_list = false;
                                                if (get_activity_state()) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                bottom_main_bar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(getActivity(), "Error, please try again", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });
                                                }

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();


                        } else {
                            isDownloading_friend_list = false;
                            if (temp_list != null) {
                                temp_list.clear();
                            }
                            if (get_activity_state()) {
                                Toast.makeText(getActivity(), "Fields can't be Empty!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    if (get_activity_state()) {
                        ((home_activity) getActivity()).show_snackbar("No Internet!", true);
                    }
                }
            }

        } catch (Exception e) {
            isDownloading_friend_list = false;
            e.printStackTrace();
        }

    }
*/
    class CustomAdapter_friend_list extends BaseAdapter {

        @Override
        public int getCount() {

            return all_friends_list_uids.size();

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
        public View getView( int i, View view, ViewGroup viewGroup) {
            final int req_ref = i;
            view = getLayoutInflater().inflate(R.layout.custom_user_list_item,null);


            final ImageView imageView =  view.findViewById(R.id.profile_pic_custom_user_list_item);
            final TextView name_view = view.findViewById(R.id.custom_user_list_item_name_text_name_view);
            final TextView mutual_view = view.findViewById(R.id.custom_user_list_item_mutual_view);
            final ImageButton add_friend_button = view.findViewById(R.id.custom_list_item_add_friend_button);
            final ImageButton cancel_request_button = view.findViewById(R.id.custom_list_item_cancel_request_button);
            final ProgressBar bar_friend_request_processing = view.findViewById(R.id.custom_user_list_item_progress_bar);
            final  TextView nick_view = view.findViewById(R.id.custom_users_nick_view);
            final TextView location_view = view.findViewById(R.id.custom_users_location_view);
            final  ImageView check_view =view.findViewById(R.id.friend_ship_check_custom_user_list_item);
            final View myView = view;




            try {


                if (get_activity_state() && req_ref < all_friends_list_uids.size()) {


                    check_view.setVisibility(View.INVISIBLE);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {


                                if (!all_friends_list_uids.get(req_ref).equals(mAuth.getCurrentUser().getUid()))
                                {
                                boolean is_friend = false;


                                for (DataSnapshot dataSnapshot : user_profile_all_data_snapshot.child(all_friends_list_uids.get(req_ref)).getChildren()) {
                                    if (dataSnapshot.getKey().equals("friends")) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                is_friend = true;
                                            }
                                        }


                                    }
                                }

                                if (is_friend) {
                                    if (is_activity_running) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {




                                                try {
                                                    if (req_ref < all_friends_list_uids.size()) {


                                    /////////////////////////////////////// new code //////////////////////////////////////////////////


                                                        /*
                                                        try {
                                                            imageView.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    if (get_activity_state() && req_ref < all_friends_list_uids.size()) {

                                                                        try {
                                                                            Intent intent = new Intent(profile_view_activity.this, profile_view_activity.class);
                                                                            intent.putExtra("curr_uid", all_friends_list_uids.get(req_ref));
                                                                          startActivity(intent);
                                                                        }catch (Exception e)
                                                                        {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }
                                                                }
                                                            });
                                                        }catch (Exception e)
                                                        {
                                                            e.printStackTrace();
                                                        }
                                                        */



                                                        myView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (get_activity_state() && req_ref < all_friends_list_uids.size()) {
                                                                    try {
                                                                        Intent intent = new Intent(profile_view_activity.this, profile_view_activity.class);
                                                                        intent.putExtra("curr_uid", all_friends_list_uids.get(req_ref));
                                                                       startActivity(intent);
                                                                    }catch (Exception e)
                                                                    {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        });

                                                        ////////////////////////////////////////////////////////////////////////////////



                                                        check_view.setVisibility(View.VISIBLE);
                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                        add_friend_button.setVisibility(View.INVISIBLE);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    }

                                } else {


                                    boolean already_sent = false;

                                    if (friend_request_data != null) {

                                        if (req_ref < all_friends_list_uids.size()) {
                                            for (DataSnapshot dataSnapshot : friend_request_data.getChildren()) {

                                                if (dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        if (dataSnapshot1.getValue().equals(all_friends_list_uids.get(req_ref))) {
                                                            already_sent = true;
                                                        }
                                                    }
                                                }


                                            }
                                        }
                                    }

                                    if (already_sent) {
                                        if (is_activity_running) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        if (req_ref < all_friends_list_uids.size()) {
                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                            add_friend_button.setVisibility(View.INVISIBLE);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                        }

                                    } else {


                                        boolean req_check_bool = false;
                                        if (friend_request_data != null) {
                                            for (DataSnapshot dataSnapshot : friend_request_data.getChildren()) {
                                                if (req_ref < all_friends_list_uids.size()) {
                                                    if (dataSnapshot.getKey().equals(all_friends_list_uids.get(req_ref))) {
                                                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                            if (dsp.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                req_check_bool = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (req_check_bool) {
                                            if (is_activity_running) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            if (req_ref < all_friends_list_uids.size()) {
                                                                cancel_request_button.setVisibility(View.VISIBLE);
                                                                add_friend_button.setVisibility(View.INVISIBLE);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });
                                            }

                                        } else {
                                            if (is_activity_running) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            if (req_ref < all_friends_list_uids.size()) {
                                                                cancel_request_button.setVisibility(View.INVISIBLE);
                                                                add_friend_button.setVisibility(View.VISIBLE);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });
                                            }

                                        }
                                    }


                                }
                            }
                            else {
                                    if (is_activity_running) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    if (req_ref < all_friends_list_uids.size()) {
                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                        add_friend_button.setVisibility(View.INVISIBLE);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    }
                                }

                                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                    for (DataSnapshot dataSnapshot : user_profile_all_data_snapshot.getChildren()) {

                                        if (dataSnapshot.getKey().toString().equals(all_friends_list_uids.get(req_ref))) {
                                            final StorageReference storageReference_one = firebaseStorage.getReference();
                                            if (mAuth != null && mAuth.getCurrentUser() != null) {

                                                if (req_ref < all_friends_list_uids.size()) {
                                                    if (is_activity_running) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    Glide.with(profile_view_activity.this.getApplicationContext())
                                                                            .using(new FirebaseImageLoader())
                                                                            .load(storageReference_one.child(all_friends_list_uids.get(req_ref))).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
                                                                }catch (Exception e)
                                                                {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });

                                                    }
                                                }






                                                /*
                                                StorageReference prof_reference_one = storageReference_one.child(all_friends_list_uids.get(req_ref));
                                                prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        if (req_ref < all_friends_list_uids.size()) {
                                                            if (is_activity_running) {
                                                               runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            Glide.with(getApplicationContext())
                                                                                    .using(new FirebaseImageLoader())
                                                                                    .load(storageReference_one.child(all_friends_list_uids.get(req_ref))).into(imageView);
                                                                        }catch (Exception e)
                                                                        {
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
                                                */
                                            }

                                            for (final DataSnapshot dsp : dataSnapshot.getChildren()) {

                                                if (dsp.getKey().toString().equals("full_name")) {
                                                    if (is_activity_running) {
                                                      runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (req_ref < all_friends_list_uids.size()) {
                                                                        name_view.setText(dsp.getValue().toString());
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });
                                                    }

                                                }
                                                if (dsp.getKey().equals("name")) {
                                                    if (is_activity_running) {
                                                       runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (req_ref < all_friends_list_uids.size() && !dsp.getValue().toString().equals("")) {
                                                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nick_view.getLayoutParams();
                                                                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                        nick_view.setLayoutParams(layoutParams);
                                                                        nick_view.setText("@ " + dsp.getValue().toString());
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });
                                                    }
                                                }
                                                if (dsp.getKey().equals("City")) {
                                                    if (is_activity_running) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (req_ref < all_friends_list_uids.size()) {
                                                                    try {
                                                                        location_view.setText(" " + dsp.getValue().toString());
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                                if (dsp.getKey().equals("friends")) {



                                                    if (personal_data_snap != null && personal_found) {
                                                        int mutual_count = 0;
                                                        for (DataSnapshot dataSnapshot1 : dsp.getChildren()) {
                                                            for (DataSnapshot dataSnapshot2 : personal_data_snap.getChildren()) {
                                                                if (dataSnapshot2.getValue().equals(dataSnapshot1.getValue())) {
                                                                    mutual_count = mutual_count + 1;
                                                                }
                                                            }
                                                        }
                                                        final int my_mutual = mutual_count;
                                                        if (is_activity_running) {
                                                         runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        if (req_ref < all_friends_list_uids.size() && my_mutual > 0) {
                                                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mutual_view.getLayoutParams();
                                                                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                            mutual_view.setLayoutParams(layoutParams);
                                                                            mutual_view.setText(getResources().getString(R.string.mutual_friends_string)+ String.valueOf(my_mutual));
                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }
                                                            });
                                                        }

                                                    }
                                                }
                                            }


                                        }
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }


                        }
                    }).start();


                    add_friend_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                    if (is_activity_running)
                                    {



                                        if (is_connected()) {

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {


                                                        if (is_activity_running) {

                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        if (req_ref < all_friends_list_uids.size()) {

                                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                                            add_friend_button.setVisibility(View.INVISIBLE);
                                                                            bar_friend_request_processing.setVisibility(View.VISIBLE);
                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }
                                                            });
                                                        }


                                                        DatabaseReference in_add_ref = databaseReference.child("friend_requests");
                                                        in_add_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                try {

                                                                    friend_request_data=dataSnapshot;
                                                                    if (get_activity_state() && req_ref < all_friends_list_uids.size()) {
                                                                        boolean add_search_found = false;
                                                                        if (dataSnapshot.hasChild(all_friends_list_uids.get(req_ref))) {

                                                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.child(all_friends_list_uids.get(req_ref)).getChildren()) {
                                                                                if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                                    add_search_found = true;
                                                                                }
                                                                            }


                                                                            if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildren()) {
                                                                                    if (dataSnapshot1.getValue().equals(all_friends_list_uids.get(req_ref))) {
                                                                                        add_search_found = true;
                                                                                    }
                                                                                }
                                                                            }



                                                                            if (!add_search_found) {
                                                                                DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");


                                                                                String key = friend_requests_reference.child(all_friends_list_uids.get(req_ref)).push().getKey();
                                                                                if (key != null) {



                                                                                    //  friend_requests_reference.child(user_uid_list.get(req_ref)).child(mAuth.getCurrentUser().getUid()).
                                                                                    friend_requests_reference.child(all_friends_list_uids.get(req_ref)).child(key).setValue(mAuth.getCurrentUser().getUid()).
                                                                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                    if (task.isSuccessful()) {
                                                                                                        if (get_activity_state()) {
                                                                                                           runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    try {
                                                                                                                        if (req_ref < all_friends_list_uids.size()) {
                                                                                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                                                                                                                            cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                            add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                            bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                                           // all_friends_list_uids.remove(all_friends_list_uids.get(req_ref));
                                                                                                                           // customAdapter_friend_list.notifyDataSetChanged();
                                                                                                                            /*
                                                                                                                            int totalHeight = 0;
                                                                                                                            int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                            for (int z = 0; z < customAdapter_friend_list.getCount(); z++) {
                                                                                                                                View listItem = customAdapter_friend_list.getView(z, null, users_view);
                                                                                                                                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                                totalHeight += listItem.getMeasuredHeight();
                                                                                                                            }

                                                                                                                            ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                                                            params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter_friend_list.getCount() - 1));
                                                                                                                            users_view.setLayoutParams(params);
                                                                                                                            users_view.requestLayout();
                                                                                                                            */
                                                                                                                        }
                                                                                                                    } catch (Exception e) {
                                                                                                                        e.printStackTrace();
                                                                                                                    }

                                                                                                                }
                                                                                                            });


                                                                                                            DatabaseReference friend_req_data_initial = databaseReference.child("friend_requests");
                                                                                                            friend_req_data_initial.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                    if (get_activity_state()) {
                                                                                                                        friend_request_data = dataSnapshot;
                                                                                                                    }
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                                                }
                                                                                                            });


                                                                                                        }
                                                                                                    } else {
                                                                                                        if (get_activity_state()) {
                                                                                                           runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    try {
                                                                                                                        if (req_ref < all_friends_list_uids.size()) {
                                                                                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                                            add_friend_button.setVisibility(View.VISIBLE);
                                                                                                                            bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                                        }
                                                                                                                    } catch (Exception e) {
                                                                                                                        e.printStackTrace();
                                                                                                                    }

                                                                                                                }
                                                                                                            });

                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                } else {
                                                                                    if (get_activity_state()) {
                                                                                       runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                try {
                                                                                                    if (req_ref < all_friends_list_uids.size()) {
                                                                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                        add_friend_button.setVisibility(View.VISIBLE);
                                                                                                        bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                } catch (Exception e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        });
                                                                                    }

                                                                                }
                                                                            } else {
                                                                                if (get_activity_state()) {
                                                                                   runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            try {
                                                                                                if (req_ref < all_friends_list_uids.size()) {
                                                                                                    try {
                                                                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                        add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                        bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                    }


                                                                                                }
                                                                                            } catch (Exception e) {
                                                                                                e.printStackTrace();
                                                                                            }

                                                                                        }
                                                                                    });
                                                                                }

                                                                            }


                                                                        } else {


                                                                            boolean my_found = false;

                                                                            if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildren()) {
                                                                                    if (dataSnapshot1.getValue().equals(all_friends_list_uids.get(req_ref))) {
                                                                                        my_found = true;
                                                                                    }
                                                                                }
                                                                            }

                                                                            if (!my_found)
                                                                            {


                                                                            DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");


                                                                            String key = friend_requests_reference.child(all_friends_list_uids.get(req_ref)).push().getKey();
                                                                            if (key != null) {


                                                                                //  friend_requests_reference.child(user_uid_list.get(req_ref)).child(mAuth.getCurrentUser().getUid()).
                                                                                friend_requests_reference.child(all_friends_list_uids.get(req_ref)).child(key).setValue(mAuth.getCurrentUser().getUid()).
                                                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                if (task.isSuccessful()) {
                                                                                                    if (get_activity_state()) {
                                                                                                        runOnUiThread(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                if (req_ref < all_friends_list_uids.size()) {
                                                                                                                    try {
                                                                                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                                                                                                                        cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                        add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                        bar_friend_request_processing.setVisibility(View.INVISIBLE);

                                                                                                                        /*
                                                                                                                        user_uid_list.remove(user_uid_list.get(req_ref));
                                                                                                                        customAdapter_friend_list.notifyDataSetChanged();
                                                                                                                        int totalHeight = 0;
                                                                                                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                        for (int z = 0; z < customAdapter_friend_list.getCount(); z++) {
                                                                                                                            View listItem = customAdapter_friend_list.getView(z, null, users_view);
                                                                                                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                            totalHeight += listItem.getMeasuredHeight();
                                                                                                                        }

                                                                                                                        ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                                                        params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter_friend_list.getCount() - 1));
                                                                                                                        users_view.setLayoutParams(params);
                                                                                                                        users_view.requestLayout();
                                                                                                                        */
                                                                                                                    } catch (Exception e) {
                                                                                                                        e.printStackTrace();
                                                                                                                    }

                                                                                                                }
                                                                                                            }
                                                                                                        });


                                                                                                        DatabaseReference friend_req_data_initial = databaseReference.child("friend_requests");
                                                                                                        friend_req_data_initial.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                            @Override
                                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                if (get_activity_state()) {
                                                                                                                    friend_request_data = dataSnapshot;
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                                            }
                                                                                                        });


                                                                                                    }
                                                                                                } else {
                                                                                                    if (get_activity_state()) {
                                                                                                        runOnUiThread(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                if (req_ref < all_friends_list_uids.size()) {
                                                                                                                    try {
                                                                                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                                        add_friend_button.setVisibility(View.VISIBLE);
                                                                                                                        bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                                    } catch (Exception e) {
                                                                                                                        e.printStackTrace();
                                                                                                                    }

                                                                                                                }
                                                                                                            }
                                                                                                        });

                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            } else {
                                                                                if (get_activity_state()) {
                                                                                    runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            if (req_ref < all_friends_list_uids.size()) {
                                                                                                try {
                                                                                                    cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                    add_friend_button.setVisibility(View.VISIBLE);
                                                                                                    bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                                                } catch (Exception e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }

                                                                            }
                                                                        }else {
                                                                                if (get_activity_state()) {
                                                                                        runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                try {
                                                                                                    if (req_ref < all_friends_list_uids.size()) {
                                                                                                        try {
                                                                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                            add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                            bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                        } catch (Exception e) {
                                                                                                            e.printStackTrace();
                                                                                                        }


                                                                                                    }
                                                                                                } catch (Exception e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        });
                                                                                    }

                                                                                }


                                                                        }

                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }


                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                if (get_activity_state()) {
                                                                   runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (req_ref < all_friends_list_uids.size()) {

                                                                                try {
                                                                                    cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                    add_friend_button.setVisibility(View.VISIBLE);
                                                                                    bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }


                                                                            }
                                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }).start();


                                        } else {
                                            if (get_activity_state()) {
                                               show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                            }
                                        }
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });



                    cancel_request_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (get_activity_state()) {


                                    if (is_connected()) {

                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                        bar_friend_request_processing.setVisibility(View.VISIBLE);


                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {

                                                    DatabaseReference initial_req_ref = databaseReference.child("friend_requests");

                                                    initial_req_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            try {
                                                                boolean search_cancel_found = false;

                                                                if (dataSnapshot.hasChild(all_friends_list_uids.get(req_ref))) {
                                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(all_friends_list_uids.get(req_ref)).getChildren()) {
                                                                        if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                            search_cancel_found = true;


                                                                            final DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");
                                                                            friend_requests_reference.child(all_friends_list_uids.get(req_ref)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    try {
                                                                                        if (get_activity_state()) {
                                                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                                                if (snapshot.getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                                                                                                    friend_requests_reference.child(all_friends_list_uids.get(req_ref)).child(snapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {

                                                                                                                if (get_activity_state()) {

                                                                                                                    runOnUiThread(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            if (req_ref < all_friends_list_uids.size()) {
                                                                                                                                try {
                                                                                                                                    cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                                                    add_friend_button.setVisibility(View.VISIBLE);
                                                                                                                                    bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                                                } catch (Exception e) {
                                                                                                                                    e.printStackTrace();
                                                                                                                                }

                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                }


                                                                                                                DatabaseReference friend_req_data_initial = databaseReference.child("friend_requests");
                                                                                                                friend_req_data_initial.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                    @Override
                                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                        if (get_activity_state()) {
                                                                                                                            friend_request_data = dataSnapshot;
                                                                                                                        }
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                                                    }
                                                                                                                });
                                                                                                            } else {
                                                                                                                if (get_activity_state()) {
                                                                                                                 runOnUiThread(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            if (req_ref < all_friends_list_uids.size()) {
                                                                                                                                try {
                                                                                                                                    add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                                    cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                                    bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                                                                                } catch (Exception e) {
                                                                                                                                    e.printStackTrace();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                }

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
                                                                                public void onCancelled(DatabaseError databaseError) {
                                                                                    if (get_activity_state()) {
                                                                                       runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                if (req_ref <all_friends_list_uids.size()) {
                                                                                                    try {
                                                                                                        cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                        bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                    }

                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }

                                                                                }
                                                                            });

                                                                        }
                                                                    }

                                                                    if (!search_cancel_found) {
                                                                        if (get_activity_state()) {
                                                                        runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (req_ref < all_friends_list_uids.size()) {
                                                                                        try {
                                                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                            add_friend_button.setVisibility(View.VISIBLE);
                                                                                            bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                        } catch (Exception e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                } else {
                                                                    if (get_activity_state()) {
                                                                     runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                if (req_ref < all_friends_list_uids.size()) {
                                                                                    try {
                                                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                        add_friend_button.setVisibility(View.VISIBLE);
                                                                                        bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }

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
                                                            if (get_activity_state()) {
                                                            runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (req_ref < all_friends_list_uids.size()) {
                                                                            try {
                                                                                cancel_request_button.setVisibility(View.VISIBLE);
                                                                                bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        }).start();


                                    } else {
                                        if (get_activity_state()) {
                                           show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                        }
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });


                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return view;
        }
    }

    private void show_friends_bar()
    {
        try {
            isDownloading_friend_list=true;
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) friends_list_bar.getLayoutParams();
            layoutParams.height= (LinearLayout.LayoutParams.WRAP_CONTENT);
           friends_list_bar.setLayoutParams(layoutParams);
            friends_list_bar.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void hide_friends_bar()
    {
        try {
            isDownloading_friend_list=false;
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) friends_list_bar.getLayoutParams();
            layoutParams.height= 0;
            friends_list_bar.setLayoutParams(layoutParams);
            friends_list_bar.setVisibility(View.INVISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    private void get_all_friends()
    {

        if (get_activity_state() && is_connected() && !isDownloading_friend_list) {





            all_friends_list_uids.clear();
            all_friends_list_names.clear();
            customAdapter_friend_list.notifyDataSetChanged();

            make_friends_list_to_zero();

            show_friends_bar();

            databaseReference.child("friend_requests").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friend_request_data=dataSnapshot;

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            user_profile_all_data_snapshot = dataSnapshot;

                            if (user_profile_all_data_snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                personal_found = true;
                                personal_data_snap = dataSnapshot;
                            }

                            if (dataSnapshot.hasChild((current_uid)) && dataSnapshot.child(current_uid).hasChild("friends")) {
                                user_profile_all_friends_snapshot = dataSnapshot.child(current_uid).child("friends");


                                for (DataSnapshot dataSnapshot1 : dataSnapshot.child(current_uid).child("friends").getChildren()) {


                                    if (user_profile_all_data_snapshot.child(dataSnapshot1.getValue().toString()).hasChild("full_name")) {
                                        all_friends_list_uids.add(dataSnapshot1.getValue().toString());
                                    }

                                }

                                boolean is_wrong = false;


                                while (!is_wrong) {
                                    is_wrong = true;
                                    for (int i = 1; i < all_friends_list_uids.size(); i++) {
                                        if (user_profile_all_data_snapshot.child(all_friends_list_uids.get(i - 1)).child("full_name").getValue().toString().compareTo(user_profile_all_data_snapshot.child(all_friends_list_uids.get(i)).child("full_name").getValue().toString()) > 0) {
                                            is_wrong = false;
                                            String temp = all_friends_list_uids.get(i);
                                            all_friends_list_uids.set(i, all_friends_list_uids.get(i - 1));
                                            all_friends_list_uids.set(i - 1, temp);

                                        }
                                    }
                                }

                                for (int i = 0; i < all_friends_list_uids.size(); i++) {
                                    all_friends_list_names.add(user_profile_all_data_snapshot.child(all_friends_list_uids.get(i)).child("full_name").getValue().toString());
                                }

                                hide_friends_bar();

                                users_view_all.setAdapter(customAdapter_friend_list);
                                customAdapter_friend_list.notifyDataSetChanged();

                              //wrap
                                make_friends_list_to_wrap();


                            } else {
                                if (get_activity_state()) {
                                    hide_friends_bar();
                                    show_no_friends_text_view();
                                    /*
                                    no_friends_yet_view.setVisibility(View.VISIBLE);
                                    */
                                    // no friends
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (get_activity_state()) {
                                hide_friends_bar();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (get_activity_state()) {
                        hide_friends_bar();
                    }
                }
            });












        }















    }









    private void get_profile_data()
    {




        if (!is_downloading_prof_data && is_connected() && get_activity_state()) {





            try {
                progressBar.setVisibility(View.VISIBLE);
                is_downloading_prof_data=true;
                try {
                    final StorageReference storageReference_one = firebaseStorage.getReference();

                    Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(storageReference_one.child(current_uid)).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.NONE).into(prof_view);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                databaseReference.child("users").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (is_activity_running) {

                            try {
                                progressBar.setVisibility(View.INVISIBLE);
                                is_downloading_prof_data = false;

                                if (dataSnapshot.hasChild("message_count"))
                                {
                                    try {
                                        String no_mess = String.valueOf(dataSnapshot.child("message_count").getChildrenCount());
                                        no_of_memes.setText(no_mess + " " +  getResources().getString(R.string.message_with_brackets));
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    no_of_memes.setText("0 " + getResources().getString(R.string.message_with_brackets));
                                }

                                if (dataSnapshot.hasChild("friends")) {
                                    no_of_friends.setText(String.valueOf(dataSnapshot.child("friends").getChildrenCount()) + " "+ getResources().getString(R.string.friends_with_brackets));
                                } else {
                                    no_of_friends.setText("0 "+ getResources().getString(R.string.friends_with_brackets));
                                }
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    if (dataSnapshot1.getKey().equals("full_name")) {
                                        full_name_view.setText(dataSnapshot1.getValue().toString());
                                        String f_name = dataSnapshot1.getValue().toString();
                                        if (f_name!=null) {
                                            meme_of_text_view.setText(getResources().getString(R.string.messages_of_string)+" " + f_name);
                                            friends_of_text_view.setText(getResources().getString(R.string.horrible_friends_of)+" " + f_name);
                                        }
                                    }
                                    if (dataSnapshot1.getKey().equals("name")) {
                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nick_view.getLayoutParams();
                                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        nick_view.setLayoutParams(layoutParams);
                                        nick_view.setText("@ " + dataSnapshot1.getValue().toString());
                                    }
                                    if (dataSnapshot1.getKey().equals("City")) {
                                        loc_view.setText(" " + dataSnapshot1.getValue().toString());
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            //////////////////////// new code with love button ///////////////////////////////




                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (get_activity_state()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            is_downloading_prof_data = false;
                        }
                    }
                });
            }catch (Exception e)
            {
                e.printStackTrace();
            }






        }




    }

    private void check_for_favourite()
    {



        if (get_activity_state()) {

            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final DataSnapshot snap = dataSnapshot;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                if (snap != null) {
                                    if (snap.hasChild("favourites")) {
                                        for (DataSnapshot dataSnapshot1 : snap.child("favourites").getChildren()) {
                                            if (dataSnapshot1.getKey().equals(current_uid)) {

                                                if (dataSnapshot1.getValue().equals(true)) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                love_button.setColorFilter(getResources().getColor(R.color.red_400));
                                                                love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                                int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                                love_button.setPadding(margin, margin, margin, margin);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });

                                                }

                                            }
                                        }

                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }







    }



    private void show_no_meme_text_view()
    {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_meme_found_text_view.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
       layoutParams.setMargins(0, 16, 0, 16);
        no_meme_found_text_view.setLayoutParams(layoutParams);
    }

    private void hide_no_meme_text_view()
    {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_meme_found_text_view.getLayoutParams();
        layoutParams.height = 0;
        layoutParams.setMargins(0, 0, 0, 0);
        no_meme_found_text_view.setLayoutParams(layoutParams);
    }

    private void show_no_friends_text_view()
    {

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_friends_yet_view.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(0, 16, 0, 0);
        no_friends_yet_view.setLayoutParams(layoutParams);
    }

    private void hide_no_friends_text_view()
    {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_friends_yet_view.getLayoutParams();
        layoutParams.height = 0;
        layoutParams.setMargins(0, 0, 0, 0);
        no_friends_yet_view.setLayoutParams(layoutParams);
    }













}
