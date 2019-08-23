package com.np.areebwaseem.horriblefriends;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.android.gms.appinvite.AppInviteInvitation;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.spec.ECField;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by areebwaseem on 10/28/17.
 */

public class meme_home_fragment_view extends Fragment {

    private static final String TAG = "meme_home_fragment_view";


    private FirebaseAuth mAuth;

    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    ProgressBar bottom_main_bar;
    DataSnapshot friend_request_data;
    volatile boolean is_activity_running;
    Context context;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    String current_uid="";
    String curr_naame;

    boolean should_down;

    CustomAdapter customAdapter;
    CustomAdapter_default customAdapter_default;
    ListView all_memes_list_view;

    ArrayList<String> all_friends_list;
    DataSnapshot all_memes_data_snapshot;
    ArrayList<String> memes_with_friends_list;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    boolean isDownloading;
    TextView no_meme_found_text_view;



    private FirebaseFunctions mFunctions;
    ArrayList<String> new_array;
    ListView default_view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_memes_fragment_view, container, false);
        context = this.getActivity();

        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        mFunctions = FirebaseFunctions.getInstance();


        ////////////////// Layout Elements ////////////////////////

        all_memes_list_view= view.findViewById(R.id.meme_home_list_view);
        swipeRefreshLayout = view.findViewById(R.id.home_memes_swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progressBar_home_memes_fragment);
        no_meme_found_text_view = view.findViewById(R.id.no_memes_found_text_view_home);

        default_view = view.findViewById(R.id.meme_home_default_list_view);



        /*
        Congratulations you have just joined Horrible Friends. From now you will hate all your friends indistinctly.
                Start sabotaging your friendship right now: go to "search" page and invite your worst friends to join your circle.
        Everything you will write on Horrible Friends will be protected with anonymity. Finally your friends can enjoy your complete sincerity.
        The messages you receive will appear also on the newsfeed of your friends. Their comments will be exquisitely anonymous as well.
        All the messages will be automatically destroyed after 12h, do not look for a way out, you cannot escape before.
                Sick about too much horrible messages? Actualy de-activating your profile the messages will disappear with you.
        Warning. Trolling up your friends with anonymous message can cause addiction an can make the world a worst place.

*/
        new_array = new ArrayList<>();
        new_array.add(getResources().getString(R.string.first_message));
        new_array.add(getResources().getString(R.string.second_message));
        new_array.add(getResources().getString(R.string.third_message));
        new_array.add(getResources().getString(R.string.fourth_message));
        new_array.add(getResources().getString(R.string.fifth_message));
        new_array.add(getResources().getString(R.string.sixth_message));
        new_array.add(getResources().getString(R.string.seventh_message));

        /*
        new_array.add("Everything you will write on Horrible Friends will be protected with anonymity. Finally your friends can enjoy your complete sincerity.");
        new_array.add("The messages you receive will also appear in the news feed of your friends. Their comments will be exquisitely anonymous as well.");
        new_array.add("All messages will be automatically destroyed after 12h, do not look for a way out, you cannot escape before.");
        new_array.add(" Sick of too many horrible messages? De-activating your profile, the messages will disappear with you.");
        new_array.add("Warning. Trolling up your friends with anonymous message can cause addiction and can make the world a worst place.");
        */




        should_down=false;

        isDownloading=false;

        /////////////////// Firebase Elements Initialization ////////////

        mAuth = FirebaseAuth.getInstance();
        databaseReference  = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        is_activity_running=true;
        customAdapter_default = new CustomAdapter_default();



        ///////////// Arraylist/ Adapter Initialization /////////////////////

       all_friends_list = new ArrayList<>();
       memes_with_friends_list= new ArrayList<>();
       customAdapter = new CustomAdapter();




        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            current_uid= mAuth.getCurrentUser().getUid();

        }


        clear_cache();




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                if (get_activity_state()) {
                    clear_cache();
                    get_all_memes();
                }
            }
        });

        get_all_memes();




        return view;
    }


    public void get_deafult_memes()
    {

        try {
            if (current_uid!=null && get_activity_state())
            {
                make_default_list_to_zero();
                databaseReference.child("users").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (should_down) {
                            should_down=false;
                            try {
                                if (get_activity_state()) {
                                    if (dataSnapshot.hasChild("full_name")) {
                                        curr_naame = dataSnapshot.child("full_name").getValue().toString();

                                    }
                                    if (default_view != null && customAdapter_default != null) {
                                        default_view.setAdapter(customAdapter_default);
                                    }
                                    make_list_to_zero();
                                    make_default_list_to_wrap();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }








    }






    public void get_all_memes() {

    try {

        if (get_activity_state()) {


            if (is_connected() ) {


                if ( !isDownloading)
                {

                    no_meme_found_text_view.setVisibility(View.INVISIBLE);
                isDownloading = true;
                should_down=false;

                all_friends_list.clear();
                memes_with_friends_list.clear();

                if (customAdapter != null) {
                    customAdapter.notifyDataSetChanged();
                }


                make_list_to_zero();
                make_default_list_to_zero();
                if (customAdapter_default!=null)
                {
                    customAdapter_default.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DatabaseReference all_friends_reference = databaseReference.child("users").child(mAuth.getCurrentUser().getUid());
                            all_friends_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                    if (get_activity_state()) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (dataSnapshot1.getKey().equals("friends")) {
                                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                                    all_friends_list.add(dataSnapshot2.getValue().toString());
                                                }
                                            }
                                        }
                                        if (all_friends_list.size() > 0) {

                                            DatabaseReference messages_reference = databaseReference.child("messages");
                                            messages_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    try {
                                                    if (get_activity_state()) {

                                                        all_memes_data_snapshot = dataSnapshot;

                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                            if (current_uid != null && dataSnapshot1.hasChild("to") && dataSnapshot1.child("to").getValue().toString().equals(current_uid)) {
                                                                memes_with_friends_list.add(dataSnapshot1.getKey());
                                                            } else {
                                                                for (int i = 0; i < all_friends_list.size(); i++) {
                                                                    if (dataSnapshot1.hasChild("to")) {
                                                                        if (dataSnapshot1.child("to").getValue().equals(all_friends_list.get(i))) {
                                                                            memes_with_friends_list.add(dataSnapshot1.getKey());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        isDownloading = false;





                                                        if (memes_with_friends_list.size() > 0) {
                                                            if (getActivity()!=null) {

                                                                boolean is_true=false;

                                                                while(!is_true)
                                                                {
                                                                    boolean check =false;
                                                                    for (int i=1;i<memes_with_friends_list.size();i++)
                                                                    {
                                                                        try {
                                                                            if (Long.parseLong(all_memes_data_snapshot.child(memes_with_friends_list.get(i)).child("timestamp").getValue().toString()) < Long.parseLong(all_memes_data_snapshot.child(memes_with_friends_list.get(i - 1)).child("timestamp").getValue().toString())) {
                                                                                String a = memes_with_friends_list.get(i - 1);
                                                                                memes_with_friends_list.set(i - 1, memes_with_friends_list.get(i));
                                                                                memes_with_friends_list.set(i, a);
                                                                                check = true;
                                                                            }
                                                                        }catch (Exception e)
                                                                        {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }

                                                                    if (!check)
                                                                    {
                                                                        is_true=true;
                                                                    }
                                                                }


                                                                getActivity().runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                           Collections.reverse(memes_with_friends_list);
                                                                            all_memes_list_view.setAdapter(customAdapter);
                                                                            customAdapter.notifyDataSetChanged();
                                                                            make_list_to_wrap();
                                                                            should_down=false;
                                                                        } catch (Exception e) {
                                                                            isDownloading = false;
                                                                            e.printStackTrace();
                                                                        }

                                                                    }
                                                                });
                                                            }


                                                        } else {
                                                            if (getActivity()!=null) {

                                                                getActivity().runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            // Toast.makeText(getActivity(), "No memes found", Toast.LENGTH_SHORT).show();
                                                                            should_down=true;
                                                                            get_deafult_memes();
                                                                            no_meme_found_text_view.setVisibility(View.VISIBLE);
                                                                        } catch (Exception e) {
                                                                            isDownloading = false;
                                                                            e.printStackTrace();
                                                                        }

                                                                        // no memes found
                                                                    }
                                                                });
                                                            }


                                                        }
                                                    }
                                                }catch (Exception e)
                                                    {
                                                        error_bar();
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    if (get_activity_state()) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    isDownloading = false;
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }


                                                                //Error
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        } else {
                                            if (get_activity_state()) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        isDownloading = false;
                                                        try {

                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            // Toast.makeText(getActivity(), "No memes found", Toast.LENGTH_SHORT).show();
                                                            should_down=true;
                                                           get_deafult_memes();
                                                            no_meme_found_text_view.setVisibility(View.VISIBLE);
                                                            // No friends so no memes!
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
                                        error_bar();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    if (get_activity_state()) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                isDownloading = false;
                                                try {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                    // Error
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                    }


                                }
                            });
                        } catch (Exception e) {
                            error_bar();
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
            else {
                    Log.d(TAG, "is downloading");
                }


            } else {

                try {
                    if (getActivity() != null) {
                        ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }
        else {
            isDownloading=false;
          Log.d(TAG,"null activity");
        }
    }
    catch (Exception e)
    {
        isDownloading=false;
        e.printStackTrace();
    }

    }


    private void make_list_to_wrap() {
        if (getActivity()!=null) {


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        if (get_activity_state() && all_memes_list_view != null) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        }


    }


    private void make_list_to_zero()
    {

        if (getActivity()!=null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (get_activity_state() && all_memes_list_view != null) {
                            ViewGroup.LayoutParams params = all_memes_list_view.getLayoutParams();
                            params.height = 0;
                            all_memes_list_view.setLayoutParams(params);
                            all_memes_list_view.requestLayout();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

        }

    }


    private void make_default_list_to_wrap() {
        if (getActivity()!=null) {


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        if (get_activity_state() && default_view!= null) {
                            int totalHeight = 0;
                            int desiredWidth = View.MeasureSpec.makeMeasureSpec(default_view.getWidth(), View.MeasureSpec.AT_MOST);
                            for (int z = 0; z < customAdapter_default.getCount(); z++) {
                                View listItem = customAdapter_default.getView(z, null, default_view);
                                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                totalHeight += listItem.getMeasuredHeight();
                            }

                            ViewGroup.LayoutParams params = default_view.getLayoutParams();
                            params.height = totalHeight + (default_view.getDividerHeight() * (customAdapter_default.getCount() - 1));
                            default_view.setLayoutParams(params);
                            default_view.requestLayout();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        }


    }


    private void make_default_list_to_zero()
    {

        if (getActivity()!=null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (get_activity_state() && default_view != null) {
                            ViewGroup.LayoutParams params = default_view.getLayoutParams();
                            params.height = 0;
                            default_view.setLayoutParams(params);
                            default_view.requestLayout();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

        }

    }


    private boolean is_connected()
    {
        if (getActivity()!=null) {
            connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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



    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        Log.d(TAG, "First Resume");
        if (visible && isResumed())
        {

            Log.d(TAG, "First Resume with visible");
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume

            onResume();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Second Resume");
        if (!getUserVisibleHint()) {
            Log.d(TAG, "Second Resume Inside");
            return;
        }
        if (getActivity()!=null) {
            ((home_activity)getActivity()).set_bottom_home();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        is_activity_running = false;

    }


    private boolean get_activity_state() {

        if (getActivity()!=null && mAuth != null && mAuth.getCurrentUser() != null && is_activity_running ) {
            return true;
        } else {
            return false;
        }

    }


    private void show_may_bar()
    {
        /*
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) may_know_progress_bar.getLayoutParams();
            layoutParams.height= (LinearLayout.LayoutParams.WRAP_CONTENT);
            may_know_progress_bar.setLayoutParams(layoutParams);
            may_know_progress_bar.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        */

    }

    private void hide_may_bar()
    {
        /*
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) may_know_progress_bar.getLayoutParams();
            layoutParams.height= 0;
            may_know_progress_bar.setLayoutParams(layoutParams);
            may_know_progress_bar.setVisibility(View.INVISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        */

    }
    private void error_bar()
    {

        if (getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (getActivity() != null) {
                            isDownloading = false;
                            if (progressBar != null) {
                                progressBar.setVisibility(View.INVISIBLE);
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

                   try {
                       to_text_view.setText(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("to_name").getValue().toString());
                   }catch (Exception e)
                   {
                       e.printStackTrace();
                   }




                   try {
                       final StorageReference storageReference_one = firebaseStorage.getReference();
                       final String curr_uid = all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("to").getValue().toString();

                      // StorageReference prof_reference_one = storageReference_one.child(curr_uid);

                       if (getActivity()!=null) {

                           try {

                               to_image_view.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       if (get_activity_state() && curr_uid!=null)
                                           try {
                                               Intent intent = new Intent(getActivity(),profile_view_activity.class);
                                               intent.putExtra("curr_uid", curr_uid);
                                               getActivity().startActivity(intent);
                                           }catch (Exception e)
                                           {
                                               if (getActivity()!=null)
                                               {
                                                   Toast.makeText(getActivity(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                                               }
                                               e.printStackTrace();
                                           }
                                   }
                               });
                           }catch (Exception e)
                           {
                               e.printStackTrace();
                           }


                           Glide.with(getActivity().getApplicationContext())
                                   .using(new FirebaseImageLoader())
                                   .load(storageReference_one.child(curr_uid)).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(to_image_view);
                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }




                   /*
                   try {
                       final StorageReference storageReference_one = firebaseStorage.getReference();


                       if (is_connected() && mAuth != null && mAuth.getCurrentUser() != null) {

                           final String curr_uid = all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("to").getValue().toString();

                           StorageReference prof_reference_one = storageReference_one.child(curr_uid);
                           prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {

                                   try {
                                       if (req_ref < memes_with_friends_list.size()) {
                                           if (get_activity_state() && getActivity() != null) {
                                               getActivity().runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       try {
                                                           Glide.with(getActivity().getApplicationContext())
                                                                   .using(new FirebaseImageLoader())
                                                                   .load(storageReference_one.child(curr_uid)).into(to_image_view);
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


                                   // Got the download URL for 'users/me/profile.png'
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception exception) {

                                   // File not found
                               }
                           });
                       }
                   } catch (Exception e)
                   {
                       e.printStackTrace();
                   }
                   */




                   try {
                       meme_text_view.setText(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("message").getValue().toString());
                   }catch (Exception e)
                   {
                       e.printStackTrace();
                   }




                   try {

                       if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("timestamp"))

                       {

                           if (getActivity()!=null) {

                               long now_time = ((home_activity) getActivity()).get_time_now();

                               if (now_time != 0) {

                                   long milli_sec = Long.parseLong(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("timestamp").getValue().toString());
                                  // Log.d(TAG,String.valueOf(milli_sec));
                                   // int hours   = (int) (((System.currentTimeMillis()-milli_sec)/ (1000*60*60)) % 24);
                                 //  int hours = (int) ((((now_time - milli_sec)) / (1000 * 60 * 60))%24);
                                  // int hours   = (int) (((now_time-milli_sec) / (1000*60*60)) % 24);
                                 //  int hours = (int) (((now_time - milli_sec)) / (1000 * 60 * 60));
                                  // int hours   = (int) (((now_time-milli_sec) / (1000*60*60)) % 24);
                                  // int mins = (int) (((now_time - milli_sec) / (1000 * 60)) % 60);

                                  // int hours   = (int) (((now_time-milli_sec) / (1000*60*60)) % 24);

                                   int hours = (int) (((now_time - milli_sec)) / (1000 * 60 * 60));
                                   int mins = (int) (((now_time-milli_sec) / (1000*60)) % 60);



                                   if (hours >= 1) {
                                       hours_ago_text_view.setText(String.valueOf(hours) + " "+ getResources().getString(R.string.hours_ago));
                                   } else {
                                       //  int minutes = (int) (((System.currentTimeMillis()-milli_sec) / (1000*60) % 60));
                                       hours_ago_text_view.setText(String.valueOf(mins) + " " + getResources().getString(R.string.minutes_ago));
                                   }
                                   // Log.d(TAG, String.valueOf(System.currentTimeMillis()));
                               }
                           }




                       }

                   }catch (Exception e)
                   {
                       e.printStackTrace();
                   }

                   try {
                       if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("shares")) {
                           no_of_shares.setVisibility(View.VISIBLE);
                           no_of_shares.setText(String.valueOf(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("shares").getChildrenCount()));
                       }
                   }catch (Exception e)
                   {
                       e.printStackTrace();
                   }


                   try {

                       if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("comments")) {
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
                           try {
                           //  View myView = getViewByPosition(req_ref, all_memes_list_view);
                           View myView = share_part_constraint_layout;

                           final Bitmap my_bit = getBitmapFromView(myView);


                           final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                           View mView = getActivity().getLayoutInflater().inflate(R.layout.share_dialog_custom_view, null);
                           ImageButton fb_share = mView.findViewById(R.id.fb_share_custom);
                           ImageButton tw_share = mView.findViewById(R.id.tw_share_custom);
                           ImageButton in_share = mView.findViewById(R.id.in_share_custom);
                           ImageButton wh_share = mView.findViewById(R.id.wh_share_custom);

                           builder.setView(mView);
                           final AlertDialog dialog = builder.create();
                           Window window = dialog.getWindow();
                           final int sdk = android.os.Build.VERSION.SDK_INT;
                           if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                               window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.layout_rounded_white));
                           } else {
                               window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.layout_rounded_white));
                           }

                               ((home_activity) getActivity()).current_share_uid = memes_with_friends_list.get(req_ref);


                           dialog.show();

                           fb_share.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   try {
                                       dialog.dismiss();
                                       ((home_activity) getActivity()).share_facebook(my_bit);
                                   }catch (Exception e)
                                   {
                                       if (getActivity()!=null)
                                       {
                                           Toast.makeText(getActivity(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
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
                                     //  ((home_activity) getActivity()).current_share_uid = memes_with_friends_list.get(req_ref);
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
                                   if (getActivity()!=null) {
                                       ((home_activity) getActivity()).share_with_twitter(my_bit);
                                   }
                                   else {
                                       Toast.makeText(getActivity(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
                           in_share.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {

                                   /*
                                   try {
                                       dialog.dismiss();
                                    //   ((home_activity) getActivity()).current_share_uid = memes_with_friends_list.get(req_ref);
                                       ((home_activity) getActivity()).createInstagramIntent(my_bit);
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
                                   if (getActivity()!=null) {
                                       ((home_activity) getActivity()).createInstagramIntent(my_bit);
                                   }
                                   else {
                                       Toast.makeText(getActivity(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                   }

                               }
                           });
                           wh_share.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   /*
                                   try {

                                       dialog.dismiss();
                                   //    ((home_activity) getActivity()).current_share_uid = memes_with_friends_list.get(req_ref);


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
                                   if (getActivity()!=null) {
                                       ((home_activity) getActivity()).share_with_whatsapp(my_bit);
                                   }
                                   else {
                                       Toast.makeText(getActivity(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
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
                               if (getActivity()!=null) {
                                   Intent intent = new Intent(getActivity(), comment_activity.class);
                                   intent.putExtra("message_uid", memes_with_friends_list.get(req_ref));
                                   getActivity().startActivity(intent);
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

    public View getViewByPosition(int pos, ListView listView) {

            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition ) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
    }





    class CustomAdapter_default extends BaseAdapter {

        @Override
        public int getCount() {
            return  new_array.size();
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


            if (get_activity_state() && req_ref<new_array.size()) {


                try {

                    try {
                        if (curr_naame!=null) {
                            to_text_view.setText(curr_naame);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        if (req_ref < new_array.size()) {
                            meme_text_view.setText(new_array.get(req_ref));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                    try {
                        if (current_uid != null) {
                            final StorageReference storageReference_one = firebaseStorage.getReference();
                            final String curr_uid = current_uid;

                            // StorageReference prof_reference_one = storageReference_one.child(curr_uid);

                            if (getActivity() != null) {

                                try {

                                    to_image_view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (get_activity_state() && curr_uid!=null)
                                                try {
                                                    Intent intent = new Intent(getActivity(),profile_view_activity.class);
                                                    intent.putExtra("curr_uid", curr_uid);
                                                    getActivity().startActivity(intent);
                                                }catch (Exception e)
                                                {
                                                    if (getActivity()!=null)
                                                    {
                                                        Toast.makeText(getActivity(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                                                    }
                                                    e.printStackTrace();
                                                }
                                        }
                                    });
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }


                                Glide.with(getActivity().getApplicationContext())
                                        .using(new FirebaseImageLoader())
                                        .load(storageReference_one.child(curr_uid)).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(to_image_view);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }




                   /*
                   try {
                       final StorageReference storageReference_one = firebaseStorage.getReference();


                       if (is_connected() && mAuth != null && mAuth.getCurrentUser() != null) {

                           final String curr_uid = all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("to").getValue().toString();

                           StorageReference prof_reference_one = storageReference_one.child(curr_uid);
                           prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {

                                   try {
                                       if (req_ref < memes_with_friends_list.size()) {
                                           if (get_activity_state() && getActivity() != null) {
                                               getActivity().runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       try {
                                                           Glide.with(getActivity().getApplicationContext())
                                                                   .using(new FirebaseImageLoader())
                                                                   .load(storageReference_one.child(curr_uid)).into(to_image_view);
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


                                   // Got the download URL for 'users/me/profile.png'
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception exception) {

                                   // File not found
                               }
                           });
                       }
                   } catch (Exception e)
                   {
                       e.printStackTrace();
                   }
                   */




                   /*

                    try {
                        meme_text_view.setText(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("message").getValue().toString());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }




                    try {

                        if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("timestamp"))

                        {

                            if (getActivity()!=null) {

                                long now_time = ((home_activity) getActivity()).get_time_now();

                                if (now_time != 0) {

                                    long milli_sec = Long.parseLong(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("timestamp").getValue().toString());
                                    // int hours   = (int) (((System.currentTimeMillis()-milli_sec)/ (1000*60*60)) % 24);
                                    int hours = (int) (((now_time - milli_sec)) / (1000 * 60 * 60));
                                    int mins = (int) (((now_time - milli_sec) / (1000 * 60)) % 60);


                                    if (hours > 1) {
                                        hours_ago_text_view.setText(String.valueOf(hours) + " hours ago");
                                    } else {
                                        //  int minutes = (int) (((System.currentTimeMillis()-milli_sec) / (1000*60) % 60));
                                        hours_ago_text_view.setText(String.valueOf(mins) + " minutes ago");
                                    }
                                    // Log.d(TAG, String.valueOf(System.currentTimeMillis()));
                                }
                            }




                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    try {
                        if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("shares")) {
                            no_of_shares.setVisibility(View.VISIBLE);
                            no_of_shares.setText(String.valueOf(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("shares").getChildrenCount()));
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                    try {

                        if (all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).hasChild("comments")) {
                            no_of_comments.setVisibility(View.VISIBLE);
                            no_of_comments.setText(String.valueOf(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("comments").getChildrenCount()));
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

*/

            }












            return view;
        }
    }

    private void clear_cache()
    {
        if (getActivity()!=null)
        {

            /*
            try {
                Glide.get(getActivity().getApplicationContext()).clearMemory();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            */


            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Glide.get(getActivity().getApplicationContext()).clearDiskCache();
                        }catch (Exception e)
                        {
                            //Toast.makeText(getContext(),"Error1",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }



                    }
                }).start();

            }catch (Exception e)
            {
                //Toast.makeText(getContext(),"Error2",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }




















}
