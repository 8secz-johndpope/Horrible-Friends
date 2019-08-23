package com.np.areebwaseem.horriblefriends;


import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.text.Layout;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.soundcloud.android.crop.Crop;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.ECField;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;


public class home_activity extends AppCompatActivity {

    private sectionsStatePageAdapter adapter;
    public ViewPager viewPager;
    private static final String TAG = "home_activity";
    int current_frag_index=0;
    FirebaseAuth mAuth;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    boolean isrunning=true;
    int request_invite=1002;
    BottomNavigationView bottomNavigationView;
    View parentLayout;
    FloatingActionButton floatingActionButton;
    ShareDialog shareDialog;
    boolean is_mess_vis;
    boolean is_frie_vis;
    boolean is_removing_token;



    CallbackManager callbackManager;

    int instagram_code=1101;
    int whatsapp_code= 1102;
    int twitter_code=1103;

    ConnectivityManager connMgr;
    NetworkInfo networkInfo;

    View bott_view_fr_req;
    Badge bott_view_fr_req_badge;

    View bott_view_mess_new;
    Badge bott_view_mess_new_badge;


    SharedPreferences sharedPreferences;

   public String current_share_uid="";

    long now = 0;

    private Timer timer;
    String current_uid;

    NTP_UTC_TIME client;

    int current_new_friend_requests;
    int current_new_mess_requests;

    ChildEventListener new_friend_requests_listener;
    ChildEventListener new_mess_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);
        parentLayout = findViewById(android.R.id.content);
        mAuth= FirebaseAuth.getInstance();

       is_mess_vis=false;
      is_frie_vis=false;
      is_removing_token=false;


        if (mAuth==null || mAuth.getCurrentUser()==null)
        {

            finish();
            return;
        }

        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            current_uid= mAuth.getCurrentUser().getUid();
        }
        current_new_mess_requests=0;
        current_new_friend_requests=0;


        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        isrunning=true;
   bottomNavigationView = findViewById(R.id.navigationBottom);
   floatingActionButton = findViewById(R.id.fab_write);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.smb_coin);
            NotificationChannel notificationChannel = new NotificationChannel(getResources().getString(R.string.default_notification_channel_id), "horrible channel",importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_600));
            notificationChannel.setSound(sound, null);


            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);



            ///////////////////////////////////////////
            int importance_mute = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel_muted = new NotificationChannel(getResources().getString(R.string.muted_notification_channel_id), "muted horrible channel",importance_mute);
            notificationChannel_muted.enableLights(true);
            notificationChannel_muted.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_600));
            notificationChannel_muted.setSound(null, null);


            NotificationManager notificationManager_mute = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager_mute.createNotificationChannel(notificationChannel_muted);

        }






        sharedPreferences = this.getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(home_activity.this, R.color.yellow_700));
        }



        client = new NTP_UTC_TIME();







           start_timer();



                 ////////////////////// Facebook Share ////////////////////////

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

                show_snackbar(getResources().getString(R.string.cant_open_facebook),false);

            }});



        //////////////////////////////////////////////////////////

        

        /*
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_layout, null);


       getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Toolbar parent =(Toolbar) mCustomView.getParent();
        parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0,0);
*/

     ActionBar actionBar = getSupportActionBar();
     /*
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.abs_layout, null);
        actionBar.setCustomView(customView);
       // android.support.v7.widget.Toolbar parent =( android.support.v7.widget.Toolbar) customView.getParent();
       // parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
       // parent.setContentInsetsAbsolute(0,0);

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffd41d")));
*/



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_bar));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        NavigationView navigationView = findViewById(R.id.nav_view_main);

        /*
        View mView = navigationView.getHeaderView(0);

        final TextView name_view = mView.findViewById(R.id.side_nav_name);
        final TextView email_view = mView.findViewById(R.id.side_nav_det);
        final ImageView side_prof_view = mView.findViewById(R.id.profile_pic_side_nav);

        side_prof_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_activity.this,profile_view_activity.class);
                intent.putExtra("curr_uid", mAuth.getCurrentUser().getUid());
              startActivity(intent);
            }
        });
        */


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_CONTACTS}, 1001);
            }
        }




        if (mAuth!=null && mAuth.getCurrentUser()!=null) {


            /*

            final StorageReference storageReference_one = firebaseStorage.getReference();
            if (mAuth != null && mAuth.getCurrentUser() != null) {

                StorageReference prof_reference_one = storageReference_one.child(mAuth.getCurrentUser().getUid());
                prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                            Glide.with(home_activity.this)
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference_one.child(mAuth.getCurrentUser().getUid())).into(side_prof_view);

                        // Got the download URL for 'users/me/profile.png'
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        // File not found
                    }
                });
            }
*/
            adapter = new sectionsStatePageAdapter(getSupportFragmentManager());
            viewPager = (ViewPager) findViewById(R.id.fragmentContainer);
            setupViewPager(viewPager,"home_fragment");




            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    is_mess_vis=false;
                    is_frie_vis=false;
                    if (item.getItemId()==R.id.navigation_bot_users)
                    {

                        setupViewPager(viewPager,"test_fragment");
                        is_frie_vis=true;
                        hide_frie_badge();
                    }
                    else if (item.getItemId()==R.id.navigation_bot_search)
                    {
                        setupViewPager(viewPager,"search_fragment");
                    }
                    else if (item.getItemId()==R.id.navigation_bot_home)
                    {
                        setupViewPager(viewPager,"home_fragment");
                    }
                    else if (item.getItemId()==R.id.navigation_bot_message)
                    {
                        setupViewPager(viewPager,"meme_fragment");
                        is_mess_vis=true;
                        hide_mess_mess_badge();
                    }
                    else if (item.getItemId()==R.id.navigation_bot_settings)
                    {
                        setupViewPager(viewPager,"settings_fragment");
                    }




                    item.setChecked(true);

                    return false;

                }
            });

           navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
               @Override
               public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                   DrawerLayout drawer =  findViewById(R.id.drawerLayout);

                   if (item.getItemId()==R.id.navigation_side_out)
                   {
                      // mAuth.signOut();

                      remove_token();
                   }
                   else if (item.getItemId()== R.id.navigation_side_users)
                   {
                      // setupViewPager(viewPager,"test_fragment");
                       bottomNavigationView.setSelectedItemId(R.id.navigation_bot_users);
                   }
                   else if (item.getItemId()== R.id.navigation_side_view_profile)
                   {
                       Intent intent = new Intent(home_activity.this,profile_view_activity.class);
                       intent.putExtra("curr_uid", mAuth.getCurrentUser().getUid());
                       startActivity(intent);
                   }

                   else if (item.getItemId()==R.id.navigation_side_search)
                   {
                      // setupViewPager(viewPager,"search_fragment");
                       bottomNavigationView.setSelectedItemId(R.id.navigation_bot_search);
                   }
                   else if (item.getItemId()==R.id.navigation_side_home)
                   {
                    //   setupViewPager(viewPager,"home_fragment");
                       bottomNavigationView.setSelectedItemId(R.id.navigation_bot_home);
                   }
                   else if (item.getItemId()== R.id.navigation_side_message)
                   {
                      // setupViewPager(viewPager,"meme_fragment");
                       bottomNavigationView.setSelectedItemId(R.id.navigation_bot_message);
                   }
                   else if (item.getItemId()== R.id.navigation_side_write)
                   {
                       Intent i= new Intent(home_activity.this,new_meme_select_user_activity.class);
                       startActivity(i);
                   }
                   else if (item.getItemId()==R.id.navigation_side_settings)
                   {
                       bottomNavigationView.setSelectedItemId(R.id.navigation_bot_settings);
                   }
                   else if (item.getItemId()==R.id.navigation_side_privacy)
                   {
                       Intent i= new Intent(home_activity.this,privacy_policy.class);
                       startActivity(i);
                   }
                   else if (item.getItemId()==R.id.navigation_side_terms)
                   {
                       Intent i= new Intent(home_activity.this,terms_of_use.class);
                       startActivity(i);
                   }




                   drawer.closeDrawer(GravityCompat.START);

                       return false;
               }
           });







           /*


            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (isrunning)
                    {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        if (dataSnapshot1.getKey().equals("full_name"))
                        {
                            //name_view.setText(dataSnapshot1.getValue().toString());
                        }
                        if (dataSnapshot1.getKey().equals("Email"))
                        {
                            //email_view.setText(dataSnapshot1.getValue().toString());
                        }
                    }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            */
        }



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(home_activity.this,new_meme_select_user_activity.class);
                startActivity(i);

            }
        });







        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null)
                {
                    sharedPreferences.edit().putString("token_set","false").apply();
                    finish();
                }
            }
        });

        if (bottomNavigationView!=null) {
            bott_view_fr_req = bottomNavigationView.findViewById(R.id.navigation_bot_users);
            bott_view_fr_req_badge = new QBadgeView(this).bindTarget(bott_view_fr_req);
            bott_view_fr_req_badge.hide(true);
        }

        if (bottomNavigationView!=null) {
            bott_view_mess_new = bottomNavigationView.findViewById(R.id.navigation_bot_message);
            bott_view_mess_new_badge = new QBadgeView(this).bindTarget(bott_view_mess_new);
            bott_view_mess_new_badge.hide(true);
        }

        Bundle bundle = getIntent().getExtras();

        if (bundle!=null && bundle.getString("type")!=null)
        {

            try {
                Log.d("Bundle value was",bundle.getString("type"));
                if (bundle.getString("type")!=null &&bundle.getString("type").equals("friendrequestnotification"))
                {
                    set_bottom_users();
                }
                else if (bundle.getString("type")!=null &&bundle.getString("type").equals("new_message_notification"))
                {
                    set_bottom_meme();
                }
                else if (bundle.getString("type")!=null &&bundle.getString("type").equals("fvourite_got_message_notification"))
                {
                    set_bottom_home();
                }
                else if (bundle.getString("type")!=null &&bundle.getString("type").equals("friendrequestacceptdnotification"))
                {
                    set_bottom_users();
                }
                else if (bundle.getString("type")!=null &&bundle.getString("type").equals("new_comment_notification"))
                {
                    set_bottom_home();
                    if (bundle.getString("mess_uid")!=null) {

                        String messag_id = bundle.getString("mess_uid");

                        Log.d(TAG, "String was: " + bundle.getString("mess_uid"));

                        Intent intent_one = new Intent(home_activity.this, comment_activity.class);
                        intent_one.putExtra("message_uid", messag_id);
                        startActivity(intent_one);

                    }

                }
            }catch (Exception e)
            {
                if (isrunning)
                {
                    Toast.makeText(getApplicationContext(),"Bundle Error",Toast.LENGTH_SHORT).show();
                }
                e.printStackTrace();
            }



        }






    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {

            Bundle bundle = intent.getExtras();


            try {
                if (bundle!=null && bundle.getString("type")!=null)
                {
                    Log.d("Bundle value was",bundle.getString("type"));
                    if (bundle.getString("type")!=null &&bundle.getString("type").equals("friendrequestnotification"))
                    {
                        set_bottom_users();
                    }
                    else if (bundle.getString("type")!=null &&bundle.getString("type").equals("new_message_notification"))
                    {
                        set_bottom_meme();
                    }
                    else if (bundle.getString("type")!=null &&bundle.getString("type").equals("fvourite_got_message_notification"))
                    {
                        set_bottom_home();

                    }
                    else if (bundle.getString("type")!=null &&bundle.getString("type").equals("friendrequestacceptdnotification"))
                    {
                        set_bottom_users();
                    }

                    else if (bundle.getString("type")!=null &&bundle.getString("type").equals("new_comment_notification"))
                    {
                        set_bottom_home();
                        if (bundle.getString("mess_uid")!=null) {

                            String messag_id = bundle.getString("mess_uid");

                            Log.d(TAG, "String was: " + bundle.getString("mess_uid"));

                            Intent intent_one = new Intent(home_activity.this, comment_activity.class);
                            intent_one.putExtra("message_uid", messag_id);
                            startActivity(intent_one);

                        }

                    }


                }
            }catch (Exception e)
            {
                if (isrunning)
                {
                    Toast.makeText(getApplicationContext(),"Bundle Error",Toast.LENGTH_SHORT).show();
                }
                e.printStackTrace();
            }





            /*
            if (bundle.getString("value")!=null)
            {
                Toast.makeText(getApplicationContext(),"Yeah",Toast.LENGTH_SHORT).show();
                Log.d(TAG, bundle.getString("value"));
            }
            */

            /*
            for (String key : getIntent().getExtras().keySet()) {

                String value = getIntent().getExtras().getString(key);
                   if (value!=null)
                   {
                       Toast.makeText(getApplicationContext(),key,Toast.LENGTH_SHORT).show();
                       set_bottom_users();
                   }

            }
            */



        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        ////////////////////// Facebook Share ////////////////////////

        callbackManager.onActivityResult(requestCode, resultCode, data);

        ///////////////////////////////////////////////////////////



        if (requestCode== request_invite)
        {

            if (resultCode == Activity.RESULT_OK)
            {
                //  String [] ids = AppInviteInvitation.getInvitationIds(requestCode,data);


            }
            else {
                Toast.makeText(this, getResources().getString(R.string.inv_not_sent),Toast.LENGTH_SHORT).show();
            }

        }


       // || resultCode==instagram_code || resultCode==twitter_code



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

    /*

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_options_menu, menu);
        return true;
    }
*/
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()) {
            case R.id.invite_friends_item:
                Intent intent = new AppInviteInvitation.IntentBuilder("Invite Friends")
                        .setMessage("Hey i am on Horrible friends, follow this link to check it out!")
                        .setDeepLink(Uri.parse("www.google.com"))
                        .setCallToActionText("Invitation")
                        .build();
                startActivityForResult(intent,request_invite );



            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            /*
            if (adapter!=null)
            {
                if (adapter.mFragmentTitleList.size()>=1) {
                    if (current_frag_index>0) {
                        setViewPager(adapter.mFragmentTitleList.get(current_frag_index - 1));
                    }

                }


            }
            */

            moveTaskToBack(true);


            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop_timer();
        isrunning=false;
        if (new_mess_listener!=null)
        {
            if (current_uid!=null) {
                current_new_mess_requests = 0;
                databaseReference.child("users").child(current_uid).child("new_message_notifications").removeEventListener(new_mess_listener);
                new_mess_listener = null;
            }
        }
        if (new_friend_requests_listener!=null)
        {
            if (current_uid!=null) {
                current_new_friend_requests = 0;
                databaseReference.child("users").child(current_uid).child("new_friend_notifications").removeEventListener(new_friend_requests_listener);
                new_friend_requests_listener = null;
            }
        }
    }

    public void setupViewPager(ViewPager viewPager, String fragmentType) {
        boolean check = false;

        for (int i = 0; i < adapter.mFragmentTitleList.size(); i++) {
            if (adapter.mFragmentTitleList.get(i).equals(fragmentType)) {
                check = true;
            }
        }
        if (check == false) {
            Log.d(TAG, "Value was False");


            try {
                if (fragmentType.equals("test_fragment")) {

                    adapter.addFragment(new friends_users_fragment_view(), fragmentType);

                }
                else if (fragmentType.equals("search_fragment")) {

                    adapter.addFragment(new search_users_fragment_view(), fragmentType);

                }
                else if (fragmentType.equals("home_fragment"))
                {

                    adapter.addFragment(new meme_home_fragment_view(),fragmentType );

                }
                else if (fragmentType.equals("meme_fragment"))
                {

                    adapter.addFragment(new meme_personal_fragment_view(),fragmentType);

                }
                else if (fragmentType.equals("settings_fragment"))
                {

                    adapter.addFragment(new settings_fragment_view(),fragmentType);

                }




                // adapter.addFragment(new invoice_fragment_view(), "invoice view");
                if (viewPager.getAdapter() == null) {
                    viewPager.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                    setViewPager(fragmentType);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        } else if (check == true) {
            setViewPager(fragmentType);
            Log.d(TAG, fragmentType);
        }
    }

    public void set_bottom_users()
    {
        try {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bot_users);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void setViewPager(String fragmentType) {
        int fragmentNumber = 0;
        boolean check = false;
        for (int i = 0; i < adapter.mFragmentTitleList.size(); i++) {
            if (fragmentType.equals(adapter.mFragmentTitleList.get(i))) {
                fragmentNumber = i;
                check = true;
            }
        }
        if (check == true) {


            try {


                viewPager.setCurrentItem(fragmentNumber);
                current_frag_index=fragmentNumber;


                if (fragmentType.equals("home_fragment"))
                {
                    try {
                        ((meme_home_fragment_view)adapter.getItem(viewPager.getCurrentItem())).get_all_memes();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else if (fragmentType.equals("meme_fragment"))
                {
                    try {
                        ((meme_personal_fragment_view)adapter.getItem(viewPager.getCurrentItem())).get_all_memes();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (fragmentType.equals("test_fragment"))
                {
                    try {
                        ((friends_users_fragment_view)adapter.getItem(viewPager.getCurrentItem())).get_friends_data();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                // setActionBarStatus(fragmentType);
                Log.d(TAG, "Pager Setttttttttt");
                Log.d(TAG, "Fragment Type " + fragmentType);
                Log.d(TAG, String.valueOf(adapter.mFragmentTitleList.size()));


            }catch (Exception e)
            {
                e.printStackTrace();
            }




        }

    }
    public void set_bottom_settings()
    {
        try {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bot_settings);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void set_bottom_search()
    {
        try {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bot_search);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void set_bottom_home()
    {
        try {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bot_home);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void set_bottom_meme()
    {
        try {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bot_message);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void show_snackbar(String txt, Boolean length)
    {
        if (isrunning) {
            if (parentLayout != null) {
                Snackbar mSnackbar;
                if (length) {
                    mSnackbar = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_LONG);
                } else {
                    mSnackbar = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_SHORT);
                }

                View mView = mSnackbar.getView();
                mView.setBackgroundColor(ContextCompat.getColor(home_activity.this, R.color.colorPrimaryDark));
                TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
                mTextView.setTextColor(ContextCompat.getColor(home_activity.this, R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                mSnackbar.show();

                // Snackbar.make(parentLayout, "Please wait!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
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

    private void perform_share()
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
            builder = new AlertDialog.Builder(home_activity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(home_activity.this);

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


    @Override
    protected void onPause() {
        super.onPause();
        if (new_mess_listener!=null && current_uid!=null)
        {
            current_new_mess_requests=0;
            databaseReference.child("users").child(current_uid).child("new_message_notifications").removeEventListener(new_mess_listener);
            new_mess_listener=null;
        }
        if (new_friend_requests_listener!=null && current_uid!=null)
        {
            current_new_friend_requests=0;
            databaseReference.child("users").child(current_uid).child("new_friend_notifications").removeEventListener(new_friend_requests_listener);
            new_friend_requests_listener=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();



        start_timer();
        check_token();

        if (bottomNavigationView!=null ) {
            if (bott_view_fr_req==null || bott_view_fr_req_badge==null) {
                bott_view_fr_req = bottomNavigationView.findViewById(R.id.navigation_bot_all_friends);
                if (bott_view_fr_req_badge!=null)
                {
                    bott_view_fr_req_badge.hide(true);
                }
                bott_view_fr_req_badge = new QBadgeView(this).bindTarget(bott_view_fr_req);
                bott_view_fr_req_badge.hide(true);
            }


            if (bott_view_mess_new==null || bott_view_mess_new_badge==null) {
                bott_view_mess_new = bottomNavigationView.findViewById(R.id.navigation_bot_message);
                if (bott_view_mess_new_badge!=null)
                {
                    bott_view_mess_new_badge.hide(true);
                }
                bott_view_mess_new_badge = new QBadgeView(this).bindTarget(bott_view_mess_new);
                bott_view_mess_new_badge.hide(true);
            }


        }
        if (mAuth==null || mAuth.getCurrentUser()==null)
        {
            finish();
        }
        else {
            if (new_friend_requests_listener ==null && databaseReference!=null && is_connected() && current_uid!=null)
            {
                new_friend_requests_listener = databaseReference.child("users").child(current_uid).child("new_friend_notifications").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (isrunning) {
                            current_new_friend_requests = current_new_friend_requests + 1;
                            //bott_view_fr_req_badge.setBadgeNumber(current_new_friend_requests);
                            show_frie_badge(current_new_friend_requests);
                            // bott_view_fr_req_badge.hide(false);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        if (isrunning) {
                            if (current_new_friend_requests > 0) {
                                current_new_friend_requests = current_new_friend_requests - 1;
                                // bott_view_fr_req_badge.setBadgeNumber(current_new_friend_requests);
                                show_frie_badge(current_new_friend_requests);
                                // bott_view_fr_req_badge.hide(false);
                                if (current_new_friend_requests == 0) {
                                    hide_frie_badge();
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            if (new_mess_listener==null && databaseReference!=null && is_connected() && current_uid!=null)
            {
                new_mess_listener = databaseReference.child("users").child(current_uid).child("new_message_notifications").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (isrunning) {
                            Log.d(TAG, "Added");
                            current_new_mess_requests = current_new_mess_requests + 1;
                            show_mess_badge(current_new_mess_requests);
                        }

                       // bott_view_mess_new_badge.hide(false);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        if (isrunning) {

                            if (current_new_mess_requests > 0) {
                                current_new_mess_requests = current_new_mess_requests - 1;
                                show_mess_badge(current_new_mess_requests);

                                if (current_new_mess_requests == 0) {
                                    hide_mess_mess_badge();
                                }
                            }

                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
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


    private void store_token()
    {
        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            /*
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful())
                    {

                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue(task.getResult().getToken()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sharedPreferences.edit().putString("token_set","true").apply();
                            }
                        });
                    }else {

                    }
                }

            });
            */

            if (is_connected()) {

                String curr_token = FirebaseInstanceId.getInstance().getToken();
                if (curr_token != null) {
                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue(curr_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sharedPreferences.edit().putString("token_set", "true").apply();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Null token", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    private void remove_token()
    {

        if (!is_removing_token) {
            is_removing_token=true;
            if (mAuth != null && mAuth.getCurrentUser() != null) {
                if (is_connected()) {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (isrunning) {
                                if (mAuth != null && mAuth.getCurrentUser() != null && dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {


                                    if (isrunning && is_connected()) {
                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                is_removing_token=false;
                                                if (task.isSuccessful()) {
                                                    sharedPreferences.edit().putString("token_set", "false").apply();
                                                    mAuth.signOut();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        is_removing_token=false;
                                        show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                    }
                                } else {
                                    is_removing_token=false;
                                    sharedPreferences.edit().putString("token_set", "false").apply();
                                    mAuth.signOut();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            is_removing_token=false;
                            if (isrunning) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    is_removing_token=false;
                    if (isrunning) {
                        show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                    }
                }
            } else {
                is_removing_token = false;
            }
        }
        else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
        }

    }


    private Badge addBadgeAt(int position, int number) {
        // add badge
        return new QBadgeView(this)
                .setBadgeNumber(number)
                .setGravityOffset(12, 2, true)
                .bindTarget(bottomNavigationView.getChildAt(position))
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
                            Toast.makeText(home_activity.this, "bla", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean is_connected()
    {
        if (isrunning) {
            connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }



    private void show_mess_badge(int badge_number)
    {
        if (bott_view_mess_new_badge!=null) {
            bott_view_mess_new_badge.setBadgeNumber(badge_number);
        }
    }
    private void hide_mess_mess_badge()
    {
        if (bott_view_mess_new_badge!=null) {
            bott_view_mess_new_badge.hide(true);
        }
    }

    private void show_frie_badge(int badge_number)
    {
        if (bott_view_fr_req_badge!=null) {
            bott_view_fr_req_badge.setBadgeNumber(badge_number);
        }
    }
    private void hide_frie_badge()
    {
        if (bott_view_fr_req_badge!=null) {
            bott_view_fr_req_badge.hide(true);
        }
    }

    private void check_token()
    {

        if (sharedPreferences!=null)

        {

            if (sharedPreferences.getString("token_set", "abc").equals("false")) {
                store_token();
            } else {
                if (is_connected()) {
                    if (databaseReference != null) {
                        if (current_uid != null) {
                            databaseReference.child("users").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (isrunning) {
                                        if (dataSnapshot.hasChild("token")) {
                                            if (!dataSnapshot.child("token").getValue().toString().equals("")) {
                                                String curr_token = FirebaseInstanceId.getInstance().getToken();
                                                if (curr_token != null) {
                                                    if (!dataSnapshot.child("token").getValue().toString().equals(curr_token)) {
                                                        store_token();
                                                    }
                                                }
                                            } else {
                                                store_token();
                                            }
                                        } else {
                                            store_token();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }
        }
        else {
            sharedPreferences = this.getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);

        }






    }







}
