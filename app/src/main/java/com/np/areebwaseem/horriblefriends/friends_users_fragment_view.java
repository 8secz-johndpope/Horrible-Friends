package com.np.areebwaseem.horriblefriends;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by areebwaseem on 10/28/17.
 */

public class friends_users_fragment_view extends Fragment {
    private static final String TAG = "friends_users_fragment_";


    Context context;
    int write_meme_request_code = 1001;

    int request_invite = 1002;
    FloatingActionButton add_friend_fab;
    volatile boolean is_activity_running;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    TextView number_of_requests;
    ListView friend_requests_list_view;

    SwipeRefreshLayout swipeRefreshLayout;
    CustomAdapter_requests customAdapter_requests;


    ArrayList<String> friend_requests_uid_list;




    private FirebaseFunctions mFunctions;

    DataSnapshot friend_requests_data;

    DataSnapshot friend_request_data;
    DataSnapshot all_users_uid_data;


    DataSnapshot self_friends_req_snapshot;
    boolean self_friends_req_snapshot_found=false;
    String current_uid="";


    ProgressBar requests_progress_bar;

    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    ChildEventListener requests_listener;



    boolean request_downloading;
    ChildEventListener valueEventListener_all;
    String current_uid_all="";
    boolean isDownloading_all;
    ProgressBar bottom_main_bar_all;
    DataSnapshot friend_request_data_all;
    DataSnapshot all_user_data_all;
    volatile boolean is_activity_running_all;
    DataSnapshot personal_data_snap_all;
    DataSnapshot personal_friends_data_all;
    Context context_all;
    SwipeRefreshLayout swipeRefreshLayout_all;
    ListView users_view_all;
    ArrayList<String> user_uid_list_all;
    ArrayList<String> user_name_list_all;
    DataSnapshot all_users_uid_dataa_all;
   CustomAdapter_all customAdapter_all;
   TextView no_new_requests;
   TextView no_friends_yet_view;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment_layout, container, false);
        context = this.getActivity();

        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();


        request_downloading=false;
        isDownloading_all=false;

        ////////////////// Layout Elements ////////////////////////

        add_friend_fab = view.findViewById(R.id.fab);
        number_of_requests = view.findViewById(R.id.total_friend_requests_text_view);
        friend_requests_list_view = view.findViewById(R.id.friend_requests_list_view);
        no_friends_yet_view = view.findViewById(R.id.no_friends_yet_text_view);
        no_new_requests = view.findViewById(R.id.no_new_requests_view);
        mFunctions = FirebaseFunctions.getInstance();

        swipeRefreshLayout = view.findViewById(R.id.users_fragment_swipe_to_fresh);


        requests_progress_bar = view.findViewById(R.id.progressBar_friend_requests_layout);

        users_view_all = view.findViewById(R.id.all_user_list_view);
        bottom_main_bar_all  = view.findViewById(R.id.progressBar_may_know_layout);





        customAdapter_requests = new CustomAdapter_requests();


        //////////////// Arraylists Initialization //////////////////////
        friend_requests_uid_list = new ArrayList<>();

        user_uid_list_all = new ArrayList<>();
        user_name_list_all = new ArrayList<>();
        customAdapter_all = new CustomAdapter_all();


        ////////////// Firebase Elements Initialization ////////////

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        is_activity_running = true;
        is_activity_running_all=true;


        add_friend_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });






        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connMgr.getActiveNetworkInfo();
                if (getActivity()!=null) {
                    if (networkInfo != null && networkInfo.isConnected()) {
                        get_all_requests();
                     performSearch();
                    } else {
                        ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                    }
                }
            }
        });



        if (networkInfo!=null && networkInfo.isConnected()) {

            get_all_requests();
            performSearch();

        }

        else {
            if (getActivity()!=null) {
                ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
            }
        }

        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            current_uid= mAuth.getCurrentUser().getUid();
        }

        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            current_uid_all=mAuth.getCurrentUser().getUid();
        }

        return view;
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
            ((home_activity) getActivity()).set_bottom_users();
        }

    }

    public void  get_friends_data()
    {
        if (getActivity()!=null) {
            try {
                connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo!=null && networkInfo.isConnected()) {

                    if (!request_downloading) {

                        get_all_requests();

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        is_activity_running = false;
        is_activity_running_all = false;

        if (requests_listener!=null)
        {
            try {
                databaseReference.child("friend_requests").child(current_uid).removeEventListener(requests_listener);
                requests_listener=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }



        if (valueEventListener_all!=null)
        {
            try {
                databaseReference.child("users").child(current_uid_all).child("friends").removeEventListener(valueEventListener_all);
                valueEventListener_all=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (requests_listener!=null)
        {
            try {
                databaseReference.child("friend_requests").child(current_uid).removeEventListener(requests_listener);
                requests_listener=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }



        if (valueEventListener_all!=null)
        {
            try {
                databaseReference.child("users").child(current_uid_all).child("friends").removeEventListener(valueEventListener_all);
                valueEventListener_all=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }





    }

    private boolean get_activity_state() {
        if (getActivity()!=null && mAuth != null && mAuth.getCurrentUser() != null && is_activity_running ) {
            return true;
        } else {
            return false;
        }
    }

    private void get_all_requests() {
        try {

        if (get_activity_state()) {

            if (!request_downloading) {
                request_downloading = true;

                if (friend_requests_uid_list != null) {
                    friend_requests_uid_list.clear();
                    customAdapter_requests.notifyDataSetChanged();
                    int totalHeight = 0;
                    int desiredWidth = View.MeasureSpec.makeMeasureSpec(friend_requests_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                    for (int z = 0; z < customAdapter_requests.getCount(); z++) {
                        View listItem = customAdapter_requests.getView(z, null, friend_requests_list_view);
                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                        totalHeight += listItem.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params = friend_requests_list_view.getLayoutParams();
                    params.height = totalHeight + (friend_requests_list_view.getDividerHeight() * (customAdapter_requests.getCount() - 1));
                    friend_requests_list_view.setLayoutParams(params);
                    friend_requests_list_view.requestLayout();

                }

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_new_requests.getLayoutParams();
                layoutParams.height = 0;
                no_new_requests.setLayoutParams(layoutParams);

                if (requests_listener != null) {
                    databaseReference.child("friend_requests").child(mAuth.getCurrentUser().getUid()).removeEventListener(requests_listener);
                }

                show_req_bar();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            DatabaseReference search_reference = databaseReference.child("users");
                            search_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {

                                        if (get_activity_state()) {
                                            self_friends_req_snapshot_found = false;

                                            all_users_uid_data = dataSnapshot;
                                            for (DataSnapshot self_snap : all_users_uid_data.child(mAuth.getCurrentUser().getUid()).getChildren()) {
                                                if (self_snap.getKey().equals("friends")) {
                                                    self_friends_req_snapshot_found = true;
                                                    self_friends_req_snapshot = self_snap;
                                                }
                                            }


                                            DatabaseReference requests_reference = databaseReference.child("friend_requests");

                                            requests_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    try {
                                                        if (get_activity_state()) {

                                                            friend_requests_data = dataSnapshot;
                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                if (snapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {

                                                                    for (DataSnapshot dsp : snapshot.getChildren()) {
                                                                        friend_requests_uid_list.add(dsp.getValue().toString());
                                                                    }
                                                                    break;
                                                                }
                                                            }


                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {

                                                                        hide_req_bar();

                                                                        if (friend_requests_uid_list.size() == 0) {

                                                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_new_requests.getLayoutParams();
                                                                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                            no_new_requests.setLayoutParams(layoutParams);
                                                                        }

                                                                        friend_requests_list_view.setAdapter(customAdapter_requests);

                                                                        /////////////////////////// new code //////////////////////////////////////
                                                                        if (get_activity_state()) {

                                                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("new_friend_notifications").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                }
                                                                            });

                                                                        }

                                                                        int totalHeight = 0;
                                                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(friend_requests_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                        for (int z = 0; z < customAdapter_requests.getCount(); z++) {
                                                                            View listItem = customAdapter_requests.getView(z, null, friend_requests_list_view);
                                                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                            totalHeight += listItem.getMeasuredHeight();
                                                                        }

                                                                        ViewGroup.LayoutParams params = friend_requests_list_view.getLayoutParams();
                                                                        params.height = totalHeight + (friend_requests_list_view.getDividerHeight() * (customAdapter_requests.getCount() - 1));
                                                                        friend_requests_list_view.setLayoutParams(params);
                                                                        friend_requests_list_view.requestLayout();

                                                                        request_downloading = false;


                                                                        new Thread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {

                                                                                    requests_listener = databaseReference.child("friend_requests").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                                                                                        @Override
                                                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                                                            try {
                                                                                            if (get_activity_state()) {
                                                                                                if (friend_requests_uid_list != null && friend_requests_uid_list.size() > 0) {
                                                                                                    for (int i = 0; i < friend_requests_uid_list.size(); i++) {
                                                                                                        if (dataSnapshot.getValue().equals(friend_requests_uid_list.get(i))) {

                                                                                                            final int req = i;

                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    try {
                                                                                                                        friend_requests_uid_list.remove(friend_requests_uid_list.get(req));
                                                                                                                        customAdapter_requests.notifyDataSetChanged();

                                                                                                                        int totalHeight = 0;
                                                                                                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(friend_requests_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                        for (int z = 0; z < customAdapter_requests.getCount(); z++) {
                                                                                                                            View listItem = customAdapter_requests.getView(z, null, friend_requests_list_view);
                                                                                                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                            totalHeight += listItem.getMeasuredHeight();
                                                                                                                        }

                                                                                                                        ViewGroup.LayoutParams params = friend_requests_list_view.getLayoutParams();
                                                                                                                        params.height = totalHeight + (friend_requests_list_view.getDividerHeight() * (customAdapter_requests.getCount() - 1));
                                                                                                                        friend_requests_list_view.setLayoutParams(params);
                                                                                                                        friend_requests_list_view.requestLayout();
                                                                                                                        if (friend_requests_uid_list.size() == 0) {
                                                                                                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_new_requests.getLayoutParams();
                                                                                                                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                                                                            no_new_requests.setLayoutParams(layoutParams);
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
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    request_downloading = false;
                                                    if (get_activity_state()) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    Toast.makeText(getActivity(),getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                                    hide_req_bar();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });

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
                                    request_downloading = false;
                                    if (get_activity_state()) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hide_req_bar();
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


            }


        }
    }catch (Exception e)
        {
            request_downloading=false;
            e.printStackTrace();
        }


    }





    class CustomAdapter_requests extends BaseAdapter {

        @Override
        public int getCount() {
            return friend_requests_uid_list.size();
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
            view = getLayoutInflater().inflate(R.layout.custom_requests_user_list_item,null);
            final TextView name_view = view.findViewById(R.id.custom_request_user_list_item_name_text_name_view);
            final TextView mutual_view = view.findViewById(R.id.custom_request_user_list_item_mutual_view);
            final ImageView prof_image = view.findViewById(R.id.profile_pic_custom_request_user_list_item);
            final ImageButton accept_button = view.findViewById(R.id.custom_list_item_accept_friend_button);
            final ImageButton deny_button = view.findViewById(R.id.custom_list_item_deny_friend_button);
            final ProgressBar accept_progress_bar = view.findViewById(R.id.custom_request_user_list_item_progress_bar);
            final TextView nick_request_view = view.findViewById(R.id.request_users_nick_view);
            final TextView location_request_view = view.findViewById(R.id.request_users_location_view);
            Log.d(TAG, "View made");

            final int req_ref = i;


            if (get_activity_state() && req_ref<friend_requests_uid_list.size()) {


                accept_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                        if (get_activity_state()) {
                            connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            networkInfo = connMgr.getActiveNetworkInfo();

                            if (networkInfo != null && networkInfo.isConnected()) {

                                accept_progress_bar.setVisibility(View.VISIBLE);
                                accept_button.setVisibility(View.INVISIBLE);
                                deny_button.setVisibility(View.INVISIBLE);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            DatabaseReference self_reference = databaseReference.child("users");

                                            String key = self_reference.child(mAuth.getCurrentUser().getUid()).child("friends").push().getKey();

                                            if (key != null) {


                                                self_reference.child(mAuth.getCurrentUser().getUid()).child("friends").child(key).setValue(friend_requests_uid_list.get(req_ref)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull final Task<Void> task) {

                                                        try {


                                                        if (get_activity_state()) {
                                                            if (task.isSuccessful())  // Success
                                                            {


                                                                /*
                                                                getActivity().runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            performSearch();
                                                                            if (req_ref < friend_requests_uid_list.size()) {
                                                                                accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                accept_button.setVisibility(View.INVISIBLE);
                                                                                deny_button.setVisibility(View.INVISIBLE);
                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }
                                                                });
                                                                */


                                                                final DatabaseReference friend_req_reference = databaseReference.child("friend_requests");

                                                                friend_req_reference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) { //Success
                                                                        try {

                                                                            if (get_activity_state()) {
                                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                                    if (snapshot.getValue().equals(friend_requests_uid_list.get(req_ref))) {

                                                                                        final String new_id = friend_requests_uid_list.get(req_ref);
                                                                                        friend_req_reference.child(mAuth.getCurrentUser().getUid()).child(snapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull final Task<Void> task) {
                                                                                                if (task.isSuccessful()) //Success
                                                                                                {
                                                                                                    if (getActivity() != null) {
                                                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {


                                                                                                                try {
                                                                                                                    if (mFunctions != null && personal_data_snap_all != null && personal_data_snap_all.hasChild("full_name")) {
                                                                                                                        Map<String, Object> data = new HashMap<>();
                                                                                                                        data.put("text", new_id);
                                                                                                                        data.put("name", personal_data_snap_all.child("full_name").getValue().toString());
                                                                                                                        mFunctions.getHttpsCallable("accept_request")
                                                                                                                                .call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                                                                                    }
                                                                                                                        });
                                                                                                                    }
                                                                                                                }catch (Exception e)
                                                                                                                {
                                                                                                                    e.printStackTrace();
                                                                                                                }



                                                                                                                try {
                                                                                                                    performSearch();
                                                                                                                    if (req_ref < friend_requests_uid_list.size()) {
                                                                                                                        accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                                                        accept_button.setVisibility(View.INVISIBLE);
                                                                                                                        deny_button.setVisibility(View.INVISIBLE);
                                                                                                                    }
                                                                                                                } catch (Exception e) {
                                                                                                                    e.printStackTrace();
                                                                                                                }

                                                                                                            }
                                                                                                        });
                                                                                                    }


                                                                                                } else { // Error
                                                                                                    if (getActivity() != null) {

                                                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                try {
                                                                                                                    if (req_ref < friend_requests_uid_list.size()) {

                                                                                                                        accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                                                        accept_button.setVisibility(View.VISIBLE);
                                                                                                                        deny_button.setVisibility(View.INVISIBLE);
                                                                                                                    }
                                                                                                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
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
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) { // Error
                                                                        if (get_activity_state()) {
                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {

                                                                                    try {
                                                                                        if (req_ref < friend_requests_uid_list.size()) {

                                                                                            accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                            accept_button.setVisibility(View.VISIBLE);
                                                                                            deny_button.setVisibility(View.INVISIBLE);
                                                                                        }
                                                                                        Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                }
                                                                            });
                                                                        }

                                                                    }
                                                                });










                                                                /*





                                                                DatabaseReference other_reference = databaseReference.child("users");
                                                                String second_key = other_reference.child(friend_requests_uid_list.get(req_ref)).child("friends").push().getKey();
                                                                if (second_key != null) {
                                                                    other_reference.child(friend_requests_uid_list.get(req_ref)).child("friends").child(second_key).setValue(mAuth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (get_activity_state()) {
                                                                                if (task.isSuccessful()) // Success
                                                                                {
                                                                                    final DatabaseReference friend_req_reference = databaseReference.child("friend_requests");

                                                                                    friend_req_reference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) { //Success
                                                                                            try {

                                                                                                if (get_activity_state()) {
                                                                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                                                        if (snapshot.getValue().equals(friend_requests_uid_list.get(req_ref))) {
                                                                                                            friend_req_reference.child(mAuth.getCurrentUser().getUid()).child(snapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull final Task<Void> task) {
                                                                                                                    if (task.isSuccessful()) //Success
                                                                                                                    {
                                                                                                                        if (getActivity() != null) {
                                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                                @Override
                                                                                                                                public void run() {
                                                                                                                                    try {
                                                                                                                                        performSearch();
                                                                                                                                        if (req_ref < friend_requests_uid_list.size()) {
                                                                                                                                            accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                                                                            accept_button.setVisibility(View.INVISIBLE);
                                                                                                                                            deny_button.setVisibility(View.INVISIBLE);
                                                                                                                                        }
                                                                                                                                    } catch (Exception e) {
                                                                                                                                        e.printStackTrace();
                                                                                                                                    }

                                                                                                                                }
                                                                                                                            });
                                                                                                                        }


                                                                                                                    } else { // Error
                                                                                                                        if (getActivity() != null) {

                                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                                @Override
                                                                                                                                public void run() {
                                                                                                                                    try {
                                                                                                                                        if (req_ref < friend_requests_uid_list.size()) {

                                                                                                                                            accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                                                                            accept_button.setVisibility(View.VISIBLE);
                                                                                                                                            deny_button.setVisibility(View.INVISIBLE);
                                                                                                                                        }
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
                                                                                            } catch (Exception e) {
                                                                                                e.printStackTrace();
                                                                                            }

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) { // Error
                                                                                            if (get_activity_state()) {
                                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {

                                                                                                        try {
                                                                                                            if (req_ref < friend_requests_uid_list.size()) {

                                                                                                                accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                                                accept_button.setVisibility(View.VISIBLE);
                                                                                                                deny_button.setVisibility(View.INVISIBLE);
                                                                                                            }
                                                                                                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                                                                                        } catch (Exception e) {
                                                                                                            e.printStackTrace();
                                                                                                        }

                                                                                                    }
                                                                                                });
                                                                                            }

                                                                                        }
                                                                                    });
                                                                                } else { //Error
                                                                                    if (getActivity()!=null) {
                                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                try {
                                                                                                    if (req_ref < friend_requests_uid_list.size()) {

                                                                                                        accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                                        accept_button.setVisibility(View.VISIBLE);
                                                                                                        deny_button.setVisibility(View.INVISIBLE);
                                                                                                    }
                                                                                                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                                                                                } catch (Exception e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });


                                                                } else {  // Error
                                                                    if (get_activity_state()) {

                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    if (req_ref < friend_requests_uid_list.size()) {

                                                                                        accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                        accept_button.setVisibility(View.VISIBLE);
                                                                                        deny_button.setVisibility(View.INVISIBLE);
                                                                                    }
                                                                                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        });
                                                                    }


                                                                }
                                                                */


                                                            }
                                                            else {
                                                                if (get_activity_state()) {

                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            try {
                                                                                if (req_ref < friend_requests_uid_list.size()) {

                                                                                    accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                    accept_button.setVisibility(View.VISIBLE);
                                                                                    deny_button.setVisibility(View.INVISIBLE);
                                                                                }
                                                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
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
                                                });

                                            } else {
                                                if (get_activity_state()) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }


                                                        }
                                                    });
                                                }

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();


                            } else {
                                if (getActivity()!=null) {
                                    ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                }
                            }
                        }
                    }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });


                deny_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                        if (get_activity_state()) {
                            connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            networkInfo = connMgr.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                accept_progress_bar.setVisibility(View.VISIBLE);
                                accept_button.setVisibility(View.INVISIBLE);
                                deny_button.setVisibility(View.INVISIBLE);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            final DatabaseReference friend_req_reference = databaseReference.child("friend_requests");

                                            friend_req_reference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) { //Success
                                                    try {

                                                        if (get_activity_state()) {
                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                if (snapshot.getValue().equals(friend_requests_uid_list.get(req_ref))) {
                                                                    friend_req_reference.child(mAuth.getCurrentUser().getUid()).child(snapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) //Success
                                                                            {
                                                                                if (getActivity() != null) {
                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            try {
                                                                                                if (req_ref < friend_requests_uid_list.size()) {
                                                                                                    accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                                    accept_button.setVisibility(View.INVISIBLE);
                                                                                                    deny_button.setVisibility(View.INVISIBLE);
                                                                                                }
                                                                                            } catch (Exception e) {
                                                                                                e.printStackTrace();
                                                                                            }

                                                                                        }
                                                                                    });
                                                                                }


                                                                            } else { // Error
                                                                                if (getActivity() != null) {
                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            try {
                                                                                                if (req_ref < friend_requests_uid_list.size()) {

                                                                                                    accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                                                    accept_button.setVisibility(View.VISIBLE);
                                                                                                    deny_button.setVisibility(View.INVISIBLE);
                                                                                                }
                                                                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
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
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) { // Error
                                                    if (get_activity_state()) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (req_ref < friend_requests_uid_list.size()) {
                                                                        accept_progress_bar.setVisibility(View.INVISIBLE);
                                                                        accept_button.setVisibility(View.VISIBLE);
                                                                        deny_button.setVisibility(View.INVISIBLE);
                                                                    }
                                                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
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
                                if (get_activity_state()) {
                                    ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                }
                            }
                        }
                    }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });


                if (get_activity_state()) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            for (DataSnapshot dataSnapshot : all_users_uid_data.getChildren()) {

                                if (req_ref < friend_requests_uid_list.size()) {


                                    if (dataSnapshot.getKey().toString().equals(friend_requests_uid_list.get(req_ref))) {
                                        final StorageReference storageReference_one = firebaseStorage.getReference();
                                        if (mAuth != null && mAuth.getCurrentUser() != null) {

                                            /*
                                            StorageReference prof_reference_one = storageReference_one.child(friend_requests_uid_list.get(req_ref));
                                            prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                */
                                                    try {

                                                        if (req_ref < friend_requests_uid_list.size()) {
                                                            if (getActivity() != null) {
                                                                getActivity().runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            Glide.with(getActivity().getApplicationContext())
                                                                                    .using(new FirebaseImageLoader())
                                                                                    .load(storageReference_one.child(friend_requests_uid_list.get(req_ref))).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.NONE).into(prof_image);
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

                                                    /*
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
                                                if (get_activity_state()) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (req_ref < friend_requests_uid_list.size()) {
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
                                                if (get_activity_state()) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (req_ref < friend_requests_uid_list.size() && !dsp.getValue().toString().equals("")) {
                                                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nick_request_view.getLayoutParams();
                                                                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                    nick_request_view.setLayoutParams(layoutParams);
                                                                    nick_request_view.setText("@ " + dsp.getValue().toString());
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                            if (dsp.getKey().equals("City")) {
                                                if (get_activity_state()) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (req_ref < friend_requests_uid_list.size()) {
                                                                    location_request_view.setText(" " + dsp.getValue().toString());
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });
                                                }
                                            }
                                            if (dsp.getKey().equals("friends")) {

                                                if (self_friends_req_snapshot != null && self_friends_req_snapshot_found) {

                                                    int mutual_count = 0;
                                                    for (DataSnapshot dataSnapshot1 : dsp.getChildren()) {
                                                        for (DataSnapshot dataSnapshot2 : self_friends_req_snapshot.getChildren()) {
                                                            if (dataSnapshot2.getValue().equals(dataSnapshot1.getValue())) {
                                                                mutual_count = mutual_count + 1;
                                                            }
                                                        }
                                                    }
                                                    final int my_mutual = mutual_count;
                                                    if (get_activity_state()) {

                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (req_ref < friend_requests_uid_list.size() && my_mutual > 0) {
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
                            }
                        }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }


            }


            return view;
        }
    }

    private void show_req_bar()
    {
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) requests_progress_bar.getLayoutParams();
            layoutParams.height= (LinearLayout.LayoutParams.WRAP_CONTENT);
            requests_progress_bar.setLayoutParams(layoutParams);
            requests_progress_bar.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void hide_req_bar()
    {
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) requests_progress_bar.getLayoutParams();
            layoutParams.height= 0;
            requests_progress_bar.setLayoutParams(layoutParams);
            requests_progress_bar.setVisibility(View.INVISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void hide_all_bar()
    {
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) bottom_main_bar_all.getLayoutParams();
            layoutParams.height= 0;
            bottom_main_bar_all.setLayoutParams(layoutParams);
            bottom_main_bar_all.setVisibility(View.INVISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void show_all_bar()
    {
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) bottom_main_bar_all.getLayoutParams();
            layoutParams.height= (LinearLayout.LayoutParams.WRAP_CONTENT);
            bottom_main_bar_all.setLayoutParams(layoutParams);
            bottom_main_bar_all.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
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
                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").removeEventListener(valueEventListener_all);
            }

            if (user_name_list_all != null) {
                user_name_list_all.clear();
                if (customAdapter_all != null) {
                    customAdapter_all.notifyDataSetChanged();
                }
            }

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_friends_yet_view.getLayoutParams();
            layoutParams.height = 0;
            no_friends_yet_view.setLayoutParams(layoutParams);


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
                                            if (dataSnapshot1.getKey().equals(mAuth.getCurrentUser().getUid())) {
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

                                                getActivity().runOnUiThread(new Runnable() {
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
                                                                        valueEventListener_all = databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").addChildEventListener(new ChildEventListener() {
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
                                                                                                        getActivity().runOnUiThread(new Runnable() {
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
                                                                                                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_friends_yet_view.getLayoutParams();
                                                                                                                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                                                                        no_friends_yet_view.setLayoutParams(layoutParams);
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
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            hide_all_bar();
                                                            try {
                                                                //Toast.makeText(getActivity(), "No horrible friends to show!", Toast.LENGTH_SHORT).show();
                                                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_friends_yet_view.getLayoutParams();
                                                                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                no_friends_yet_view.setLayoutParams(layoutParams);

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
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hide_all_bar();
                                                        try {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
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
                                if (getActivity() != null) {
                                    isDownloading_all = false;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(getActivity(),profile_view_activity.class);
                        intent.putExtra("curr_uid", user_uid_list_all.get(req_ref));
                       getActivity().startActivity(intent);
                    }
                });

                /*


                boolean is_friend = false;
                for (DataSnapshot dataSnapshot: all_users_uid_data.child(user_uid_list.get(req_ref)).getChildren())
                {
                    if (dataSnapshot.getKey().equals("friends"))
                    {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {
                            if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid()))
                            {
                                is_friend=true;
                            }
                        }


                    }
                }

                if (is_friend)
                {
                    cancel_request_button.setVisibility(View.INVISIBLE);
                    add_friend_button.setVisibility(View.INVISIBLE);
                }
                else {


                    boolean already_sent= false;

                    if (friend_request_data!=null) {
                        for (DataSnapshot dataSnapshot : friend_request_data.getChildren()) {
                            if (dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    if (dataSnapshot1.getValue().equals(user_uid_list.get(req_ref))) {
                                        already_sent = true;
                                    }
                                }
                            }


                        }
                    }

                    if (already_sent)
                    {
                        cancel_request_button.setVisibility(View.INVISIBLE);
                        add_friend_button.setVisibility(View.INVISIBLE);
                    }
                    else {


                        boolean req_check_bool = false;
                        if (friend_request_data!=null) {
                            for (DataSnapshot dataSnapshot : friend_request_data.getChildren()) {
                                if (dataSnapshot.getKey().equals(user_uid_list.get(req_ref))) {
                                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                        if (dsp.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                            req_check_bool = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (req_check_bool) {
                            cancel_request_button.setVisibility(View.VISIBLE);
                            add_friend_button.setVisibility(View.INVISIBLE);
                        } else {
                            cancel_request_button.setVisibility(View.INVISIBLE);
                            add_friend_button.setVisibility(View.VISIBLE);
                        }
                    }


                }

*/


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
                                 getActivity().runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         try {
                                             love_button.setColorFilter(getContext().getResources().getColor(R.color.red_400));
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
                                                        love_button.setColorFilter(getContext().getResources().getColor(R.color.red_400));
                                                        love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                        int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                        love_button.setPadding(margin,margin,margin,margin);
                                                    }
                                                    else {

                                                        Toast.makeText(getActivity(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                                        love_button.setColorFilter(getContext().getResources().getColor(R.color.colorPrimaryDark));
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
                                                        love_button.setColorFilter(getContext().getResources().getColor(R.color.colorPrimaryDark));
                                                        love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button));
                                                        int margin = getResources().getDimensionPixelSize(R.dimen._8sdp);
                                                        love_button.setPadding(margin,margin,margin,margin);
                                                    }
                                                    else {
                                                        Toast.makeText(getActivity(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                                        love_button.setColorFilter(getContext().getResources().getColor(R.color.red_400));
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
                                                        love_button.setColorFilter(getContext().getResources().getColor(R.color.red_400));
                                                        love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button_red));
                                                        int margin = getResources().getDimensionPixelSize(R.dimen._6sdp);
                                                        love_button.setPadding(margin,margin,margin,margin);
                                                    }
                                                    else {
                                                        Toast.makeText(getActivity(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                                        love_button.setColorFilter(getContext().getResources().getColor(R.color.colorPrimaryDark));
                                                        love_button.setBackground(getResources().getDrawable(R.drawable.circular_image_button));
                                                        int margin = getResources().getDimensionPixelSize(R.dimen._8sdp);
                                                        love_button.setPadding(margin,margin,margin,margin);

                                                    }
                                                }
                                            });
                                        }

                                }
                                else {
                                    Toast.makeText(getActivity(),getResources().getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Toast.makeText(getActivity(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                cancel_request_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try {



                        final AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getActivity());
                        }
                        builder.setTitle(getResources().getString(R.string.confirm_string))
                                .setMessage(getResources().getString(R.string.sure_want_to_unfriend_string))
                                .setPositiveButton(getResources().getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        if (get_activity_state()) {
                                            connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                            networkInfo = connMgr.getActiveNetworkInfo();
                                            if (networkInfo != null && networkInfo.isConnected()) {
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
                                                                                            if (dataSnapshot5.getValue().equals(user_uid_list_all.get(req_ref))) {
                                                                                                DatabaseReference fresh_ref = databaseReference.child("users");
                                                                                                fresh_ref.child(mAuth.getCurrentUser().getUid()).child("friends").child(dataSnapshot5.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            if (getActivity() != null) {
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
                                                                                                            if (getActivity() != null) {
                                                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                                                    @Override
                                                                                                                    public void run() {
                                                                                                                        try {
                                                                                                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
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
                                                        if (req_ref < user_uid_list_all.size()) {
                                                            all_buttons.setVisibility(View.VISIBLE);

                                                        }
                                                        hide_all_bar();
                                                        if (get_activity_state()) {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                if (get_activity_state()) {
                                                    ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                                }
                                            }
                                        }


                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.no_string), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                        dialog.dismiss();
                                    }
                                }).show();


                    }catch (Exception e)
                        {
                            if (get_activity_state()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                            }
                            e.printStackTrace();
                        }






                    }
                });




                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running_all) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                            for (DataSnapshot dataSnapshot : all_users_uid_dataa_all.getChildren()) {

                                if (dataSnapshot.getKey().toString().equals(user_uid_list_all.get(req_ref))) {
                                    final StorageReference storageReference_one = firebaseStorage.getReference();
                                    if (mAuth != null && mAuth.getCurrentUser() != null) {

                                        /*
                                        StorageReference prof_reference_one = storageReference_one.child(user_uid_list_all.get(req_ref));
                                        prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                */
                                                if (req_ref < user_uid_list_all.size()) {
                                                    if (getActivity() != null) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                imageView.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if (req_ref < user_uid_list_all.size() && get_activity_state())
                                                                            try {
                                                                                Intent intent = new Intent(getActivity(),profile_view_activity.class);
                                                                                intent.putExtra("curr_uid", user_uid_list_all.get(req_ref));
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





                                                                try {
                                                                    Glide.with(getActivity().getApplicationContext())
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
                                            getActivity().runOnUiThread(new Runnable() {
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
                                            getActivity().runOnUiThread(new Runnable() {
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
                                            getActivity().runOnUiThread(new Runnable() {
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

                                                getActivity().runOnUiThread(new Runnable() {
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
                        Intent intent = new Intent(getActivity(),write_meme_activity.class);
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

















}
