package com.np.areebwaseem.horriblefriends;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class add_friend_activity extends AppCompatActivity {
    private static final String TAG = "add_friend_activity";
    private FirebaseAuth mAuth;
    int current_frag_index=0;
    DatabaseReference databaseReference;
    EditText search_user_edit_text;
    ListView users_view;
    ArrayList<String> user_uid_list;
    ArrayList<String> user_mututal_list;
    DataSnapshot all_users_uid_data;
    private sectionsStatePageAdapter adapter;
    public ViewPager viewPager;

   View parentLayout;
    FirebaseStorage firebaseStorage;
    ProgressBar bottom_main_bar;
    DataSnapshot friend_request_data;
    volatile boolean is_activity_running=true;
    DataSnapshot personal_data_snap;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_activity);
        parentLayout = findViewById(android.R.id.content);
        bottomNavigationView = findViewById(R.id.navigationBottomsecond);
        is_activity_running=true;


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

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_bar));
        adapter = new sectionsStatePageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.fragmentContainer2);
        setupViewPager(viewPager,"test_fragment");
        actionBar.setDisplayHomeAsUpEnabled(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.navigation_bot_search_friendss)
                {
                    setupViewPager(viewPager,"test_fragment");
                }
                else if (item.getItemId()==R.id.navigation_bot_all_friends)
                {

                    setupViewPager(viewPager,"all_friends");
                }
                item.setChecked(true);
                return false;
            }
        });





    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();

                return true;
default:
            return super.onOptionsItemSelected(item);
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
            if (fragmentType.equals("test_fragment")) {
                adapter.addFragment(new search_users_fragment_view(), fragmentType);

            } else if (fragmentType.equals("all_friends"))
            {
                 adapter.addFragment(new all_friends_fragment_view(), fragmentType);
            }
            else if (fragmentType.equals("bluetooth_fragment"))
            {
                //adapter.addFragment(new select_dev__new_frag_layout(), fragmentType);
            }
            else if (fragmentType.equals("home_new_fragment"))
            {
                //adapter.addFragment(new main_sunny_fragment_view(), fragmentType);
            }



            // adapter.addFragment(new invoice_fragment_view(), "invoice view");
            if (viewPager.getAdapter() == null) {
                viewPager.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                setViewPager(fragmentType);
            }
        } else if (check == true) {
            setViewPager(fragmentType);
            Log.d(TAG, fragmentType);
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
            viewPager.setCurrentItem(fragmentNumber);
            current_frag_index=fragmentNumber;
            // setActionBarStatus(fragmentType);
            Log.d(TAG, "Pager Setttttttttt");
            Log.d(TAG, "Fragment Type " + fragmentType);
            Log.d(TAG, String.valueOf(adapter.mFragmentTitleList.size()));
        }

    }

        public void set_bottom_search()
        {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bot_search_friendss);
        }
        public void set_bottom_users()
        {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bot_all_friends);
        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_activity_running = false;
    }

    public void show_snackbar(String txt, Boolean length)
    {
        if (is_activity_running) {
            if (parentLayout != null) {
                Snackbar mSnackbar;
                if (length) {
                    mSnackbar = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_LONG);
                } else {
                    mSnackbar = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_SHORT);
                }

                View mView = mSnackbar.getView();
                mView.setBackgroundColor(ContextCompat.getColor(add_friend_activity.this, R.color.colorPrimaryDark));
                TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
                mTextView.setTextColor(ContextCompat.getColor(add_friend_activity.this, R.color.white));
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




    /*

    private void getCurrentPhoto(String url)
    {


        if (mAuth!=null && mAuth.getCurrentUser()!=null) {
            if (mAuth.getCurrentUser().getPhotoUrl() != null) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        final ImageDownloader downloader = new ImageDownloader();

                        try {


                                profImage = downloader.execute(mAuth.getCurrentUser().getPhotoUrl().toString()).get();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profile_view.setImageBitmap(profImage);
                                }
                            });


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }


                };
                new Thread(runnable).start();
            }
        }

    }

*/








}
