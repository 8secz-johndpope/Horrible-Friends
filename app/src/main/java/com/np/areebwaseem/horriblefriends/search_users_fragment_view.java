package com.np.areebwaseem.horriblefriends;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.ScrollView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by areebwaseem on 10/28/17.
 */

public class search_users_fragment_view extends Fragment {
    private static final String TAG = "search_users_fragment_v";


private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    EditText search_user_edit_text;
    ListView users_view;
    ArrayList<String> user_uid_list;
    ArrayList<String> user_mututal_list;
    DataSnapshot all_users_uid_data;
    CustomAdapter customAdapter;
    FirebaseStorage firebaseStorage;
    ProgressBar bottom_main_bar;
    DataSnapshot friend_request_data;
    volatile boolean is_activity_running;
    DataSnapshot personal_data_snap;
    Context context;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    boolean personal_found=false;
    String current_uid="";
    ChildEventListener childEventListener;
    ChildEventListener friends_child_event_listener;
    CustomAdapter_know customAdapter_know;
    ArrayList<String> user_phone_list;
    ArrayList<String> people_may_know_name;
    ArrayList<String> people_may_know_mutual;
    ArrayList<String> people_may_know_uid;
    ArrayList<String> phone_no_uid_list;
    boolean may_downloading;
    boolean self_friends_snapshot_found=false;
    DataSnapshot all_user_data_for_may_know;
    DataSnapshot self_friends_snapshot;
    ListView people_you_may_know_list_view;
    ProgressBar may_know_progress_bar;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> temp_list;
    Button invite_friends_button;
    int request_invite=1002;
    TextView no_results;
    ScrollView main_scroll;

    ChildEventListener childEventListener_may;
    ChildEventListener friends_child_event_listener_may;


    boolean isDownloading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_friends_fragment_layout, container, false);
        context = this.getActivity();

        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();




        ////////////////// Layout Elements ////////////////////////


        search_user_edit_text = view.findViewById(R.id.search_user_edit_text);
        users_view = view.findViewById(R.id.searched_user_list_view);
        bottom_main_bar  = view.findViewById(R.id.add_friend_bottom_progress_bar);
        people_you_may_know_list_view = view.findViewById(R.id.people_you_may_know_list_view_search);
        may_know_progress_bar = view.findViewById(R.id.progressBar_may_know_layout_search);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_search_users_fragment);
        invite_friends_button = view.findViewById(R.id.invite_friends_button_search);
        no_results= view.findViewById(R.id.no_results_search_view);
        main_scroll = view.findViewById(R.id.search_users_fragment_scroll_view);

        ////////////// Firebase Elements Initialization ////////////



        mAuth = FirebaseAuth.getInstance();
        databaseReference  = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        is_activity_running=true;

        may_downloading=false;
        isDownloading=false;


        //////////////// Set progress bar tint ////////////////////////////////


        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Drawable wrapDrawable = DrawableCompat.wrap(may_know_progress_bar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimaryDark));
            may_know_progress_bar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            may_know_progress_bar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Drawable wrapDrawable = DrawableCompat.wrap(bottom_main_bar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimaryDark));
            bottom_main_bar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            bottom_main_bar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }
        */

        ///////////// Arraylist/ Adapter Initialization /////////////////////


        user_uid_list = new ArrayList<>();
        user_mututal_list = new ArrayList<>();
        customAdapter = new CustomAdapter();
        customAdapter_know = new CustomAdapter_know();
        user_phone_list= new ArrayList<>();
        people_may_know_mutual = new ArrayList<>();
        people_may_know_name = new ArrayList<>();
        people_may_know_uid= new ArrayList<>();
        phone_no_uid_list = new ArrayList<>();
        temp_list= new ArrayList<>();




        search_user_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {


                    if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {
                         performSearch();
                    }

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_user_edit_text.getApplicationWindowToken(), 0);
                    return true;
                } else {
                    return false;
                }
            }
        });



        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            current_uid= mAuth.getCurrentUser().getUid();

        }

        get_people_you_may_know();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                if (get_activity_state()) {
                    get_people_you_may_know();
                    search_user_edit_text.setText("");
                }
            }
        });

        invite_friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (get_activity_state())
                {
                    Intent intent = new AppInviteInvitation.IntentBuilder("Invite Friends")
                            .setMessage(getResources().getString(R.string.hey_i_am_on_horrible_check_it))
                            .setDeepLink(Uri.parse("www.google.com"))
                            .setCallToActionText("Invitation")
                            .build();
                    getActivity().startActivityForResult(intent,request_invite );
                }
            }
        });

        ///////////////////// new code /////////////////////////////////////



        try {
            main_scroll.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        if (search_user_edit_text.hasFocus())
                        {
                            search_user_edit_text.clearFocus();
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }



                    return false;
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
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
            ((home_activity)getActivity()).set_bottom_search();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        is_activity_running = false;
        if (childEventListener!=null)
        {
            try {
                databaseReference.child("friend_requests").child(current_uid).removeEventListener(childEventListener);
                childEventListener=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        if (friends_child_event_listener!=null)
        {
            try {
                databaseReference.child("users").child(current_uid).child("friends").removeEventListener(friends_child_event_listener);
                friends_child_event_listener=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        if (childEventListener_may!=null)
        {
            try {
                databaseReference.child("friend_requests").child(current_uid).removeEventListener(childEventListener_may);
                childEventListener_may=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        if (friends_child_event_listener_may!=null)
        {
            try {
                databaseReference.child("users").child(current_uid).child("friends").removeEventListener(friends_child_event_listener_may);
                friends_child_event_listener_may=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (childEventListener!=null)
        {
            try {
                databaseReference.child("friend_requests").child(current_uid).removeEventListener(childEventListener);
                childEventListener=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        if (friends_child_event_listener!=null)
        {
            try {
                databaseReference.child("users").child(current_uid).child("friends").removeEventListener(friends_child_event_listener);
                friends_child_event_listener=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        if (childEventListener_may!=null)
        {
            try {
                databaseReference.child("friend_requests").child(current_uid).removeEventListener(childEventListener_may);
                childEventListener_may=null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        if (friends_child_event_listener_may!=null)
        {

            try {
                databaseReference.child("users").child(current_uid).child("friends").removeEventListener(friends_child_event_listener_may);
                friends_child_event_listener_may=null;
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

    private void performSearch() {


        try {

            if (get_activity_state()) {


                connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connMgr.getActiveNetworkInfo();


                if (networkInfo != null && networkInfo.isConnected()) {

                    if (!isDownloading) {
                        isDownloading = true;

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
                                if (customAdapter != null) {
                                    customAdapter.notifyDataSetChanged();
                                }
                            }
                            if (temp_list != null) {
                                temp_list.clear();
                            }
                            if (user_mututal_list != null) {
                                user_mututal_list.clear();
                                if (customAdapter != null) {
                                    customAdapter.notifyDataSetChanged();
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


                                                                        if (customAdapter != null) {
                                                                            if (get_activity_state())
                                                                            {
                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {

                                                                                        isDownloading = false;


                                                                                        for (int z = 0; z < user_uid_list.size(); z++) {
                                                                                            temp_list.add(user_uid_list.get(z));
                                                                                        }


                                                                                        users_view.setAdapter(customAdapter);
                                                                                        customAdapter.notifyDataSetChanged();


                                                                                        int totalHeight = 0;
                                                                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                        for (int z = 0; z < customAdapter.getCount(); z++) {
                                                                                            View listItem = customAdapter.getView(z, null, users_view);
                                                                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                            totalHeight += listItem.getMeasuredHeight();
                                                                                        }

                                                                                        ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                        params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter.getCount() - 1));
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
                                                                                                                                            customAdapter.notifyDataSetChanged();
                                                                                                                                            int totalHeight = 0;
                                                                                                                                            int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                                            for (int z = 0; z < customAdapter.getCount(); z++) {
                                                                                                                                                View listItem = customAdapter.getView(z, null, users_view);
                                                                                                                                                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                                                totalHeight += listItem.getMeasuredHeight();
                                                                                                                                            }

                                                                                                                                            ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                                                                            params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter.getCount() - 1));
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
                                                                                                                                            customAdapter.notifyDataSetChanged();
                                                                                                                                            int totalHeight = 0;
                                                                                                                                            int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                                            for (int z = 0; z < customAdapter.getCount(); z++) {
                                                                                                                                                View listItem = customAdapter.getView(z, null, users_view);
                                                                                                                                                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                                                totalHeight += listItem.getMeasuredHeight();
                                                                                                                                            }

                                                                                                                                            ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                                                                            params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter.getCount() - 1));
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

                                                                                        /*
                                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                customAdapter.notifyDataSetChanged();
                                                                                            }
                                                                                        });
                                                                                        */

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
                                                                            isDownloading = false;
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    isDownloading = false;
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                isDownloading = false;
                                                                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                                                    if (get_activity_state()) {
                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    bottom_main_bar.setVisibility(View.INVISIBLE);
                                                                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                isDownloading = false;
                                                if (get_activity_state()) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                bottom_main_bar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                            isDownloading = false;
                            if (temp_list != null) {
                                temp_list.clear();
                            }
                            if (get_activity_state()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.fields_cant_be_empty), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    if (get_activity_state()) {
                        ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                    }
                }
            }

        } catch (Exception e) {
            isDownloading = false;
            e.printStackTrace();
        }

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return user_uid_list.size();

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


                  if (get_activity_state() && req_ref < user_uid_list.size()) {


                      check_view.setVisibility(View.INVISIBLE);


                      new Thread(new Runnable() {
                          @Override
                          public void run() {

                              try {


                              boolean is_friend = false;


                              for (DataSnapshot dataSnapshot : all_users_uid_data.child(user_uid_list.get(req_ref)).getChildren()) {
                                  if (dataSnapshot.getKey().equals("friends")) {
                                      for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                          if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                              is_friend = true;
                                          }
                                      }


                                  }
                              }

                              if (is_friend) {
                                  if (getActivity()!=null) {
                                      getActivity().runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              try {
                                                  if (req_ref < user_uid_list.size()) {

                                                      ///////////////// new code ///////////////////////
                                                      /*
                                                      try {
                                                          imageView.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View v) {
                                                                  if (get_activity_state() && req_ref < user_uid_list.size()) {

                                                                      try {
                                                                          Intent intent = new Intent(getActivity(), profile_view_activity.class);
                                                                          intent.putExtra("curr_uid", user_uid_list.get(req_ref));
                                                                          getActivity().startActivity(intent);
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
                                                              if (get_activity_state() && req_ref < user_uid_list.size()) {
                                                                  try {
                                                                      Intent intent = new Intent(getActivity(), profile_view_activity.class);
                                                                      intent.putExtra("curr_uid", user_uid_list.get(req_ref));
                                                                      getActivity().startActivity(intent);
                                                                  }catch (Exception e)
                                                                  {
                                                                      e.printStackTrace();
                                                                  }

                                                              }
                                                          }
                                                      });

                                                      /////////////////////////////////////////////////


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
                                      if (req_ref < user_uid_list.size()) {
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
                                  }

                                  if (already_sent) {
                                      if (getActivity()!=null) {
                                          getActivity().runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  try {
                                                      if (req_ref < user_uid_list.size()) {
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
                                              if (req_ref < user_uid_list.size()) {
                                                  if (dataSnapshot.getKey().equals(user_uid_list.get(req_ref))) {
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
                                          if (getActivity()!=null) {
                                              getActivity().runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      try {
                                                          if (req_ref < user_uid_list.size()) {
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
                                          if (getActivity()!=null) {
                                              getActivity().runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      try {
                                                          if (req_ref < user_uid_list.size()) {
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

                              if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                  for (DataSnapshot dataSnapshot : all_users_uid_data.getChildren()) {

                                      if (dataSnapshot.getKey().toString().equals(user_uid_list.get(req_ref))) {
                                          final StorageReference storageReference_one = firebaseStorage.getReference();
                                          if (mAuth != null && mAuth.getCurrentUser() != null) {

                                              /*
                                              StorageReference prof_reference_one = storageReference_one.child(user_uid_list.get(req_ref));
                                              prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                  @Override
                                                  public void onSuccess(Uri uri) {
                                                  */
                                                      if (req_ref < user_uid_list.size()) {
                                                          if (getActivity() != null) {
                                                              getActivity().runOnUiThread(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      try {

                                                                      //////////////// new code ////////////////




                                                                          Glide.with(getActivity().getApplicationContext())
                                                                                  .using(new FirebaseImageLoader())
                                                                                  .load(storageReference_one.child(user_uid_list.get(req_ref))).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.asset_four).into(imageView);

                                                                      }catch (Exception e)
                                                                      {
                                                                          e.printStackTrace();
                                                                      }
                                                                  }
                                                              });

                                                          }
                                                      }

                                                      // Got the download URL for 'users/me/profile.png'

                                              /*
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
                                                  if (getActivity()!=null) {
                                                      getActivity().runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              try {
                                                                  if (req_ref < user_uid_list.size()) {
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
                                                  if (getActivity()!=null) {
                                                      getActivity().runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              try {
                                                                  if (req_ref < user_uid_list.size() && !dsp.getValue().toString().equals("")) {
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
                                                  if (getActivity()!=null) {
                                                      getActivity().runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              if (req_ref < user_uid_list.size()) {
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
                                                      if (getActivity()!=null) {
                                                          getActivity().runOnUiThread(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                  try {
                                                                      if (req_ref < user_uid_list.size() && my_mutual > 0) {
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


                      add_friend_button.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              try {
                              if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                  if (getActivity()!=null)
                                  {

                                  connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                  networkInfo = connMgr.getActiveNetworkInfo();

                                  if (networkInfo != null && networkInfo.isConnected()) {

                                      new Thread(new Runnable() {
                                          @Override
                                          public void run() {
                                              try {


                                                  if (getActivity() != null) {

                                                      getActivity().runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              try {
                                                                  if (req_ref < user_uid_list.size()) {

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

                                                              if (get_activity_state() && req_ref < user_uid_list.size()) {
                                                                  boolean add_search_found = false;
                                                                  if (dataSnapshot.hasChild(user_uid_list.get(req_ref))) {

                                                                      for (DataSnapshot dataSnapshot1 : dataSnapshot.child(user_uid_list.get(req_ref)).getChildren()) {
                                                                          if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                              add_search_found = true;
                                                                          }
                                                                      }

                                                                      if (!add_search_found) {
                                                                          DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");


                                                                          String key = friend_requests_reference.child(user_uid_list.get(req_ref)).push().getKey();
                                                                          if (key != null) {


                             /*
                             Map mWayPointsMap = new HashMap();
                             mWayPointsMap.put(key, mAuth.getCurrentUser().getUid());

*/
                                                                              //  friend_requests_reference.child(user_uid_list.get(req_ref)).child(mAuth.getCurrentUser().getUid()).
                                                                              friend_requests_reference.child(user_uid_list.get(req_ref)).child(key).setValue(mAuth.getCurrentUser().getUid()).
                                                                                      addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                          @Override
                                                                                          public void onComplete(@NonNull Task<Void> task) {

                                                                                              if (task.isSuccessful()) {
                                                                                                  if (get_activity_state()) {
                                                                                                      getActivity().runOnUiThread(new Runnable() {
                                                                                                          @Override
                                                                                                          public void run() {
                                                                                                              try {
                                                                                                                  if (req_ref < user_uid_list.size()) {
                                                                                                                      Toast.makeText(getActivity(), getResources().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                                                                                                                      cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                      add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                      bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                                      user_uid_list.remove(user_uid_list.get(req_ref));
                                                                                                                      customAdapter.notifyDataSetChanged();
                                                                                                                      int totalHeight = 0;
                                                                                                                      int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                      for (int z = 0; z < customAdapter.getCount(); z++) {
                                                                                                                          View listItem = customAdapter.getView(z, null, users_view);
                                                                                                                          listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                          totalHeight += listItem.getMeasuredHeight();
                                                                                                                      }

                                                                                                                      ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                                                      params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter.getCount() - 1));
                                                                                                                      users_view.setLayoutParams(params);
                                                                                                                      users_view.requestLayout();
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
                                                                                                      getActivity().runOnUiThread(new Runnable() {
                                                                                                          @Override
                                                                                                          public void run() {
                                                                                                              try {
                                                                                                                  if (req_ref < user_uid_list.size()) {
                                                                                                                      Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                                                  getActivity().runOnUiThread(new Runnable() {
                                                                                      @Override
                                                                                      public void run() {
                                                                                          try {
                                                                                              if (req_ref < user_uid_list.size()) {
                                                                                                  cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                  add_friend_button.setVisibility(View.VISIBLE);
                                                                                                  bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                  Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                                              getActivity().runOnUiThread(new Runnable() {
                                                                                  @Override
                                                                                  public void run() {
                                                                                      try {
                                                                                          if (req_ref < user_uid_list.size()) {
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

                                                                      DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");


                                                                      String key = friend_requests_reference.child(user_uid_list.get(req_ref)).push().getKey();
                                                                      if (key != null) {


                             /*
                             Map mWayPointsMap = new HashMap();
                             mWayPointsMap.put(key, mAuth.getCurrentUser().getUid());

*/
                                                                          //  friend_requests_reference.child(user_uid_list.get(req_ref)).child(mAuth.getCurrentUser().getUid()).
                                                                          friend_requests_reference.child(user_uid_list.get(req_ref)).child(key).setValue(mAuth.getCurrentUser().getUid()).
                                                                                  addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                      @Override
                                                                                      public void onComplete(@NonNull Task<Void> task) {

                                                                                          if (task.isSuccessful()) {
                                                                                              if (get_activity_state()) {
                                                                                                  getActivity().runOnUiThread(new Runnable() {
                                                                                                      @Override
                                                                                                      public void run() {
                                                                                                          if (req_ref < user_uid_list.size()) {
                                                                                                              try {
                                                                                                                  Toast.makeText(getActivity(), getResources().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                                                                                                                  cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                  add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                  bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                                  user_uid_list.remove(user_uid_list.get(req_ref));
                                                                                                                  customAdapter.notifyDataSetChanged();
                                                                                                                  int totalHeight = 0;
                                                                                                                  int desiredWidth = View.MeasureSpec.makeMeasureSpec(users_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                  for (int z = 0; z < customAdapter.getCount(); z++) {
                                                                                                                      View listItem = customAdapter.getView(z, null, users_view);
                                                                                                                      listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                      totalHeight += listItem.getMeasuredHeight();
                                                                                                                  }

                                                                                                                  ViewGroup.LayoutParams params = users_view.getLayoutParams();
                                                                                                                  params.height = totalHeight + (users_view.getDividerHeight() * (customAdapter.getCount() - 1));
                                                                                                                  users_view.setLayoutParams(params);
                                                                                                                  users_view.requestLayout();
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
                                                                                                  getActivity().runOnUiThread(new Runnable() {
                                                                                                      @Override
                                                                                                      public void run() {
                                                                                                          if (req_ref < user_uid_list.size()) {
                                                                                                              try {
                                                                                                                  Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                                              getActivity().runOnUiThread(new Runnable() {
                                                                                  @Override
                                                                                  public void run() {
                                                                                      if (req_ref < user_uid_list.size()) {
                                                                                          try {
                                                                                              cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                              add_friend_button.setVisibility(View.VISIBLE);
                                                                                              bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                              Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                                          } catch (Exception e) {
                                                                                              e.printStackTrace();
                                                                                          }

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
                                                              getActivity().runOnUiThread(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      if (req_ref < user_uid_list.size()) {

                                                                          try {
                                                                              cancel_request_button.setVisibility(View.INVISIBLE);
                                                                              add_friend_button.setVisibility(View.VISIBLE);
                                                                              bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                          } catch (Exception e) {
                                                                              e.printStackTrace();
                                                                          }


                                                                      }
                                                                      Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                  connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                  networkInfo = connMgr.getActiveNetworkInfo();

                                  if (networkInfo != null && networkInfo.isConnected()) {

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

                                                          if (dataSnapshot.hasChild(user_uid_list.get(req_ref))) {
                                                              for (DataSnapshot dataSnapshot1 : dataSnapshot.child(user_uid_list.get(req_ref)).getChildren()) {
                                                                  if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                      search_cancel_found = true;


                                                                      final DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");
                                                                      friend_requests_reference.child(user_uid_list.get(req_ref)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                          @Override
                                                                          public void onDataChange(DataSnapshot dataSnapshot) {
                                                                              try {
                                                                              if (get_activity_state()) {
                                                                                  for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                                      if (snapshot.getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                                                                                          friend_requests_reference.child(user_uid_list.get(req_ref)).child(snapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                              @Override
                                                                                              public void onComplete(@NonNull Task<Void> task) {
                                                                                                  if (task.isSuccessful()) {

                                                                                                      if (get_activity_state()) {

                                                                                                          getActivity().runOnUiThread(new Runnable() {
                                                                                                              @Override
                                                                                                              public void run() {
                                                                                                                  if (req_ref < user_uid_list.size()) {
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
                                                                                                          getActivity().runOnUiThread(new Runnable() {
                                                                                                              @Override
                                                                                                              public void run() {
                                                                                                                  if (req_ref < user_uid_list.size()) {
                                                                                                                      try {
                                                                                                                          add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                          cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                          bar_friend_request_processing.setVisibility(View.INVISIBLE);
                                                                                                                          Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                                                  getActivity().runOnUiThread(new Runnable() {
                                                                                      @Override
                                                                                      public void run() {
                                                                                          if (req_ref < user_uid_list.size()) {
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
                                                                      getActivity().runOnUiThread(new Runnable() {
                                                                          @Override
                                                                          public void run() {
                                                                              if (req_ref < user_uid_list.size()) {
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
                                                                  getActivity().runOnUiThread(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                          if (req_ref < user_uid_list.size()) {
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
                                                              getActivity().runOnUiThread(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      if (req_ref < user_uid_list.size()) {
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


                  }

              }catch (Exception e)
              {
                  e.printStackTrace();
              }

            return view;
        }
    }
    public class ImageDownloader extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            }catch (MalformedURLException e)
            {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void get_people_you_may_know() {


        if (get_activity_state())

        {
            try {
            if (!may_downloading) {
                may_downloading = true;


                if (user_phone_list != null) {
                    user_phone_list.clear();
                }
                if (people_may_know_uid != null) {
                    people_may_know_uid.clear();
                    customAdapter_know.notifyDataSetChanged();
                }
                if (people_may_know_name != null) {
                    people_may_know_name.clear();
                    customAdapter_know.notifyDataSetChanged();
                }
                if (people_may_know_mutual != null) {
                    people_may_know_mutual.clear();
                    customAdapter_know.notifyDataSetChanged();
                }
                if (phone_no_uid_list != null) {
                    phone_no_uid_list.clear();
                    customAdapter_know.notifyDataSetChanged();
                }

                show_may_bar();

                if (childEventListener_may != null) {
                    databaseReference.child("friend_requests").child(mAuth.getCurrentUser().getUid()).removeEventListener(childEventListener_may);
                }
                if (friends_child_event_listener_may != null) {
                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").removeEventListener(friends_child_event_listener_may);
                }

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_results.getLayoutParams();
                layoutParams.height = 0;
                no_results.setLayoutParams(layoutParams);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ////////////////////////////////////////////////////////////////////////////////
                        try {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {
                                    // this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_CONTACTS}, 1001);
                                    ContentResolver cr = getActivity().getContentResolver();
                                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                                            null, null, null, null);

                                    if ((cur != null ? cur.getCount() : 0) > 0) {
                                        while (cur != null && cur.moveToNext()) {
                                            String id = cur.getString(
                                                    cur.getColumnIndex(ContactsContract.Contacts._ID));
                                            String name = cur.getString(cur.getColumnIndex(
                                                    ContactsContract.Contacts.DISPLAY_NAME));

                                            if (cur.getInt(cur.getColumnIndex(
                                                    ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                                Cursor pCur = cr.query(
                                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                        null,
                                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                                        new String[]{id}, null);

                                                while (pCur.moveToNext()) {
                                                    String phoneNo = pCur.getString(pCur.getColumnIndex(
                                                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                                                    if (phoneNo != null) {
                                                        boolean found = false;
                                                        for (int i = 0; i < user_phone_list.size(); i++) {
                                                            if (user_phone_list.get(i).equals(phoneNo)) {
                                                                found = true;
                                                            }
                                                        }
                                                        if (!found) {
                                                            user_phone_list.add(phoneNo);
                                                        }
                                                    }
                                                    Log.i(TAG, "Name: " + name);
                                                    Log.i(TAG, "Phone Number: " + phoneNo);


                                                }
                                                pCur.close();
                                            }
                                        }
                                    }
                                    if (cur != null) {
                                        cur.close();
                                    }

                                }
                            } else {

                                ContentResolver cr = getActivity().getContentResolver();
                                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                                        null, null, null, null);

                                if ((cur != null ? cur.getCount() : 0) > 0) {
                                    while (cur != null && cur.moveToNext()) {
                                        String id = cur.getString(
                                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                                        String name = cur.getString(cur.getColumnIndex(
                                                ContactsContract.Contacts.DISPLAY_NAME));

                                        if (cur.getInt(cur.getColumnIndex(
                                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                            Cursor pCur = cr.query(
                                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                    null,
                                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                                    new String[]{id}, null);

                                            while (pCur.moveToNext()) {
                                                String phoneNo = pCur.getString(pCur.getColumnIndex(
                                                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                                                if (phoneNo != null) {
                                                    boolean found = false;
                                                    for (int i = 0; i < user_phone_list.size(); i++) {
                                                        if (user_phone_list.get(i).equals(phoneNo)) {
                                                            found = true;
                                                        }
                                                    }
                                                    if (!found) {
                                                        user_phone_list.add(phoneNo);
                                                    }
                                                }
                                                Log.i(TAG, "Name: " + name);
                                                Log.i(TAG, "Phone Number: " + phoneNo);


                                            }
                                            pCur.close();
                                        }
                                    }
                                }
                                if (cur != null) {
                                    cur.close();
                                }
                            }

                            if (get_activity_state()) {


                                DatabaseReference request_ref = databaseReference.child("friend_requests");
                                request_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        try {
                                            friend_request_data = dataSnapshot;
                                            DatabaseReference user_ref = databaseReference.child("users");
                                            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    try {


                                                        if (get_activity_state()) {
                                                            self_friends_snapshot_found = false;
                                                            all_user_data_for_may_know = dataSnapshot;
                                                            for (DataSnapshot self_snap : all_user_data_for_may_know.child(mAuth.getCurrentUser().getUid()).getChildren()) {
                                                                if (self_snap.getKey().equals("friends")) {
                                                                    self_friends_snapshot_found = true;
                                                                    self_friends_snapshot = self_snap;
                                                                }
                                                            }
                                                            if (self_friends_snapshot != null && self_friends_snapshot_found) {

                                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                                    boolean my_check = false;
                                                                    for (DataSnapshot dsp : self_friends_snapshot.getChildren()) {
                                                                        if (dsp.getValue().equals(dataSnapshot1.getKey())) {
                                                                            my_check = true;
                                                                        }
                                                                    }
                                                                    if (!my_check) {

                                                                        if (!dataSnapshot1.getKey().equals(mAuth.getCurrentUser().getUid())) {

                                                                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {

                                                                                if (dataSnapshot2.getKey().equals("phone")) {
                                                                                    StringBuilder my_str = new StringBuilder(dataSnapshot2.getValue().toString());
                                                                                    for (int i = 0; i < my_str.length(); i++) {
                                                                                        if (my_str.charAt(0) == ' ') {

                                                                                            my_str.deleteCharAt(0);
                                                                                            break;
                                                                                        } else {
                                                                                            my_str.deleteCharAt(0);
                                                                                        }

                                                                                    }
                                                                                    if (!dataSnapshot2.getValue().equals("")) {
                                                                                        for (int i = 0; i < user_phone_list.size(); i++) {
                                                                                            if (user_phone_list.get(i).equals(dataSnapshot2.getValue()) || dataSnapshot2.getValue().toString().contains(user_phone_list.get(i)) || user_phone_list.get(i).contains(dataSnapshot2.getValue().toString()) || user_phone_list.get(i).contains(my_str.toString())) {
                                                                                                phone_no_uid_list.add(dataSnapshot1.getKey());
                                                                                            }

                                                                                        }

                                                                                    }
                                                                                }

                                                                                if (self_friends_snapshot != null && self_friends_snapshot_found) {
                                                                                    int mutual_count = 0;

                                                                                    if (dataSnapshot2.getKey().equals("friends")) {


                                                                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                                                                            for (DataSnapshot dataSnapshot4 : self_friends_snapshot.getChildren()) {
                                                                                                if (dataSnapshot4.getValue().equals(dataSnapshot3.getValue())) {
                                                                                                    mutual_count = mutual_count + 1;
                                                                                                }
                                                                                            }


                                                                                        }
                                                                                        if (mutual_count > 0) {
                                                                                            //  people_may_know_name.add(dataSnapshot1.child("full_name").getValue().toString());
                                                                                            people_may_know_uid.add(dataSnapshot1.getKey());
                                                                                            people_may_know_mutual.add(String.valueOf(mutual_count));
                                                                                        }


                                                                                    }


                                                                                }


                                                                            }
                                                                        }

                                                                    }
                                                                }
                                                            } else {

                                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                                                                    if (!dataSnapshot1.getKey().equals(mAuth.getCurrentUser().getUid())) {

                                                                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {

                                                                            if (dataSnapshot2.getKey().equals("phone")) {
                                                                                StringBuilder my_str = new StringBuilder(dataSnapshot2.getValue().toString());
                                                                                for (int i = 0; i < my_str.length(); i++) {
                                                                                    if (my_str.charAt(0) == ' ') {

                                                                                        my_str.deleteCharAt(0);
                                                                                        break;
                                                                                    } else {
                                                                                        my_str.deleteCharAt(0);
                                                                                    }

                                                                                }


                                                                                if (!dataSnapshot2.getValue().equals("")) {
                                                                                    for (int i = 0; i < user_phone_list.size(); i++) {
                                                                                        if (user_phone_list.get(i).equals(dataSnapshot2.getValue()) || dataSnapshot2.getValue().toString().contains(user_phone_list.get(i)) || user_phone_list.get(i).contains(dataSnapshot2.getValue().toString()) || user_phone_list.get(i).contains(my_str.toString())) {
                                                                                            phone_no_uid_list.add(dataSnapshot1.getKey());
                                                                                        }

                                                                                    }

                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }


                                                            }
                                                            if (people_may_know_uid.size() > 0) {

                                                                for (int i = 0; i < phone_no_uid_list.size(); i++) {
                                                                    for (int z = 0; z < people_may_know_uid.size(); z++) {
                                                                        if (people_may_know_uid.get(z).equals(phone_no_uid_list.get(i))) {
                                                                            phone_no_uid_list.remove(phone_no_uid_list.get(i));

                                                                            /////////// new code /////////////
                                                                            i=i-1;
                                                                            ////////////////////////////////


                                                                        }
                                                                    }
                                                                }

                                                                if (phone_no_uid_list.size() > 0) {
                                                                    for (int i = 0; i < phone_no_uid_list.size(); i++) {
                                                                        people_may_know_uid.add(phone_no_uid_list.get(i));
                                                                        people_may_know_mutual.add("0");
                                                                    }
                                                                }
                                                            } else {
                                                                for (int i = 0; i < phone_no_uid_list.size(); i++) {
                                                                    people_may_know_uid.add(phone_no_uid_list.get(i));
                                                                    people_may_know_mutual.add("0");
                                                                }
                                                            }


                                                            boolean found = false;
                                                            while (!found) {
                                                                found = true;

                                                                for (int i = 0; i < people_may_know_uid.size(); i++) {
                                                                    if (i > 0) {
                                                                        if (Integer.valueOf(people_may_know_mutual.get(i)) > Integer.valueOf(people_may_know_mutual.get(i - 1))) {
                                                                            String temp = people_may_know_mutual.get(i);
                                                                            people_may_know_mutual.set(i, people_may_know_mutual.get(i - 1));
                                                                            people_may_know_mutual.set(i - 1, temp);
                                                                            String temp2 = people_may_know_uid.get(i);
                                                                            people_may_know_uid.set(i, people_may_know_uid.get(i - 1));
                                                                            people_may_know_uid.set(i - 1, temp2);
                                                                            found = false;
                                                                        }
                                                                    }

                                                                }
                                                            }

////////////////// Eliminating already received requests from may know //////////////////////////////

                                                            if (friend_request_data != null) {

                                                                for (int i = 0; i < people_may_know_uid.size(); i++) {


                                                                    for (DataSnapshot dataSnapshot1 : friend_request_data.getChildren()) {
                                                                        if (dataSnapshot1.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                                                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                                                                if (dataSnapshot2.getValue().equals(people_may_know_uid.get(i))) {
                                                                                    people_may_know_uid.remove(people_may_know_uid.get(i));

                                                                                    ////////// new code ////////////
                                                                                    i=i-1;
                                                                                    ///////////////////////////////

                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }


                                                                for (DataSnapshot dataSnapshot1 : friend_request_data.getChildren()) {
                                                                    for (int i = 0; i < people_may_know_uid.size(); i++) {
                                                                        if (dataSnapshot1.getKey().equals(people_may_know_uid.get(i))) {
                                                                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                                                                if (dataSnapshot2.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                                    people_may_know_uid.remove(people_may_know_uid.get(i));

                                                                                    ////////////// new code //////////////////
                                                                                    i=i-1;
                                                                                    ////////////////////////////////////////
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            if (temp_list != null) {
                                                                for (int i = 0; i < temp_list.size(); i++) {
                                                                    for (int z = 0; z < people_may_know_uid.size(); z++) {
                                                                        if (people_may_know_uid.get(z).equals(temp_list.get(i))) {
                                                                            people_may_know_uid.remove(people_may_know_uid.get(z));
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }


                                                            Set<String> hs = new HashSet<>();
                                                            hs.addAll(people_may_know_uid);
                                                            people_may_know_uid.clear();
                                                            people_may_know_uid.addAll(hs);

                                                            if (get_activity_state())
                                                            {


                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {

                                                                        hide_may_bar();

                                                                        if (people_may_know_uid.size() == 0) {
                                                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) no_results.getLayoutParams();
                                                                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                            no_results.setLayoutParams(layoutParams);

                                                                        }

                                                                        people_you_may_know_list_view.setAdapter(customAdapter_know);
                                                                        int totalHeight = 0;
                                                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(people_you_may_know_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                        for (int z = 0; z < customAdapter_know.getCount(); z++) {
                                                                            View listItem = customAdapter_know.getView(z, null, people_you_may_know_list_view);
                                                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                            totalHeight += listItem.getMeasuredHeight();
                                                                        }

                                                                        ViewGroup.LayoutParams params = people_you_may_know_list_view.getLayoutParams();
                                                                        params.height = totalHeight + (people_you_may_know_list_view.getDividerHeight() * (customAdapter_know.getCount() - 1));
                                                                        people_you_may_know_list_view.setLayoutParams(params);
                                                                        people_you_may_know_list_view.requestLayout();

                                                                        may_downloading = false;


                                                                        new Thread(new Runnable() {
                                                                            @Override
                                                                            public void run() {

                                                                                try {

                                                                                    childEventListener_may = databaseReference.child("friend_requests").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                                                                                        @Override
                                                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                                            // New code
                                                                                            try {

                                                                                                if (get_activity_state()) {
                                                                                                    for (int i = 0; i < people_may_know_uid.size(); i++) {
                                                                                                        if (dataSnapshot.getValue().equals(people_may_know_uid.get(i))) {
                                                                                                            people_may_know_uid.remove(people_may_know_uid.get(i));
                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    try {
                                                                                                                        customAdapter_know.notifyDataSetChanged();
                                                                                                                        int totalHeight = 0;
                                                                                                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(people_you_may_know_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                        for (int z = 0; z < customAdapter_know.getCount(); z++) {
                                                                                                                            View listItem = customAdapter_know.getView(z, null, people_you_may_know_list_view);
                                                                                                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                            totalHeight += listItem.getMeasuredHeight();
                                                                                                                        }

                                                                                                                        ViewGroup.LayoutParams params = people_you_may_know_list_view.getLayoutParams();
                                                                                                                        params.height = totalHeight + (people_you_may_know_list_view.getDividerHeight() * (customAdapter_know.getCount() - 1));
                                                                                                                        people_you_may_know_list_view.setLayoutParams(params);
                                                                                                                        people_you_may_know_list_view.requestLayout();
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
                                                                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onChildRemoved(DataSnapshot dataSnapshot) {


                                                                                            try {


                                                                                                if (get_activity_state()) {
                                                                                                    for (int i = 0; i < people_may_know_uid.size(); i++) {
                                                                                                        if (dataSnapshot.getValue().equals(people_may_know_uid.get(i))) {
                                                                                                            people_may_know_uid.remove(people_may_know_uid.get(i));
                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    try {
                                                                                                                        customAdapter_know.notifyDataSetChanged();
                                                                                                                        int totalHeight = 0;
                                                                                                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(people_you_may_know_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                        for (int z = 0; z < customAdapter_know.getCount(); z++) {
                                                                                                                            View listItem = customAdapter_know.getView(z, null, people_you_may_know_list_view);
                                                                                                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                            totalHeight += listItem.getMeasuredHeight();
                                                                                                                        }

                                                                                                                        ViewGroup.LayoutParams params = people_you_may_know_list_view.getLayoutParams();
                                                                                                                        params.height = totalHeight + (people_you_may_know_list_view.getDividerHeight() * (customAdapter_know.getCount() - 1));
                                                                                                                        people_you_may_know_list_view.setLayoutParams(params);
                                                                                                                        people_you_may_know_list_view.requestLayout();
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
                                                                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });


                                                                                    friends_child_event_listener_may = databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").addChildEventListener(new ChildEventListener() {
                                                                                        @Override
                                                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                                                            try {

                                                                                                if (get_activity_state()) {
                                                                                                    for (int i = 0; i < people_may_know_uid.size(); i++) {
                                                                                                        if (people_may_know_uid.get(i).equals(dataSnapshot.getValue())) {
                                                                                                            people_may_know_uid.remove(people_may_know_uid.get(i));
                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    try {
                                                                                                                        customAdapter_know.notifyDataSetChanged();
                                                                                                                        int totalHeight = 0;
                                                                                                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(people_you_may_know_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                        for (int z = 0; z < customAdapter_know.getCount(); z++) {
                                                                                                                            View listItem = customAdapter_know.getView(z, null, people_you_may_know_list_view);
                                                                                                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                            totalHeight += listItem.getMeasuredHeight();
                                                                                                                        }

                                                                                                                        ViewGroup.LayoutParams params = people_you_may_know_list_view.getLayoutParams();
                                                                                                                        params.height = totalHeight + (people_you_may_know_list_view.getDividerHeight() * (customAdapter_know.getCount() - 1));
                                                                                                                        people_you_may_know_list_view.setLayoutParams(params);
                                                                                                                        people_you_may_know_list_view.requestLayout();
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
                                                                        may_downloading = false;
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            });
                                                        }


                                                        }
                                                    } catch (Exception e) {
                                                        may_downloading = false;
                                                        if (get_activity_state()) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    hide_may_bar();
                                                                }
                                                            });

                                                        }
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    may_downloading = false;
                                                    if (get_activity_state()) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                hide_may_bar();
                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        may_downloading = false;
                                        if (get_activity_state()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hide_may_bar();
                                                }
                                            });

                                        }
                                    }
                                });


                            }


                            //////////////////////////////////////////////////////////////////////////////////

                        } catch (Exception e) {
                            may_downloading = false;
                            if (get_activity_state()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hide_may_bar();
                                    }
                                });

                            }
                            e.printStackTrace();
                        }
                    }
                }).start();

            }


        }catch (Exception e)
            {
                may_downloading=false;
                hide_may_bar();
                e.printStackTrace();
            }
    }
    }
    private void show_may_bar()
    {
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) may_know_progress_bar.getLayoutParams();
            layoutParams.height= (LinearLayout.LayoutParams.WRAP_CONTENT);
            may_know_progress_bar.setLayoutParams(layoutParams);
            may_know_progress_bar.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void hide_may_bar()
    {
        try {
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) may_know_progress_bar.getLayoutParams();
            layoutParams.height= 0;
            may_know_progress_bar.setLayoutParams(layoutParams);
            may_know_progress_bar.setVisibility(View.INVISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    class CustomAdapter_know extends BaseAdapter {

        @Override
        public int getCount() {
            return people_may_know_uid.size();
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
            view = getLayoutInflater().inflate(R.layout.custom_user_people_you_may_know_list_item,null);
            final ImageView imageView = view.findViewById(R.id.profile_pic_custom_user_may_know_list_item);
            final TextView may_name = view.findViewById(R.id.custom_user_list__may_know_item_name_text_name_view);
            final TextView may_mutual = view.findViewById(R.id.custom_user_may_know_list_item_mutual_view);
            final ImageButton add_friend_button = view.findViewById(R.id.custom_list_may_know_item_add_friend_button);
            final ImageButton cancel_request_button = view.findViewById(R.id.custom_list_may_know_item_cancel_request_button);
            final TextView may_location = view.findViewById(R.id.may_users_location_view);
            final TextView may_nick = view.findViewById(R.id.may_users_nick_view);
            final ProgressBar may_Bar = view.findViewById(R.id.custom_user_may_know_list_item_progress_bar);
            final int req_ref=i;




            try {


                if (get_activity_state() && req_ref < people_may_know_uid.size()) {

/*
                boolean is_friend = false;
                for (DataSnapshot dataSnapshot: all_user_data_for_may_know.child(people_may_know_uid.get(req_ref)).getChildren())
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

*/


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                boolean already_sent = false;

                                if (friend_request_data != null) {



                                    for (DataSnapshot dataSnapshot : friend_request_data.getChildren()) {
                                        if (dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                if (dataSnapshot1.getValue().equals(people_may_know_uid.get(req_ref))) {
                                                    already_sent = true;
                                                }
                                            }
                                        }
                                    }
                                }

                                if (already_sent) {

                                    if (get_activity_state()) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    if (req_ref < people_may_know_uid.size()) {
                                                        try {
                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                            add_friend_button.setVisibility(View.INVISIBLE);
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

                                } else {


                                    boolean req_check_bool = false;
                                    if (friend_request_data != null) {
                                        for (DataSnapshot dataSnapshot : friend_request_data.getChildren()) {
                                            if (dataSnapshot.getKey().equals(people_may_know_uid.get(req_ref))) {
                                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                    if (dsp.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                        req_check_bool = true;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (req_check_bool) {
                                        if (get_activity_state()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (req_ref < people_may_know_uid.size()) {
                                                        try {
                                                            cancel_request_button.setVisibility(View.VISIBLE);
                                                            add_friend_button.setVisibility(View.INVISIBLE);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }
                                            });
                                        }

                                    } else {
                                        if (get_activity_state()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (req_ref < people_may_know_uid.size()) {
                                                        try {
                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                            add_friend_button.setVisibility(View.VISIBLE);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }
                                            });
                                        }

                                    }
                                }


                                //   }


                                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {

                                    for (DataSnapshot dataSnapshot : all_user_data_for_may_know.getChildren()) {

                                        if (dataSnapshot.getKey().toString().equals(people_may_know_uid.get(req_ref))) {
                                            final StorageReference storageReference_one = firebaseStorage.getReference();
                                            if (mAuth != null && mAuth.getCurrentUser() != null) {

                                                /*
                                                StorageReference prof_reference_one = storageReference_one.child(people_may_know_uid.get(req_ref));
                                                prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                    */
                                                        if (req_ref < people_may_know_uid.size()) {
                                                            if (getActivity() != null) {
                                                                getActivity().runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            Glide.with(getActivity().getApplicationContext())
                                                                                    .using(new FirebaseImageLoader())
                                                                                    .load(storageReference_one.child(people_may_know_uid.get(req_ref))).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);

                                                                        }catch (Exception e)
                                                                        {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }
                                                                });

                                                            }
                                                        }

                                                        ////////////// new code ////////////////////

                                                        // Got the download URL for 'users/me/profile.png'

                                                    /*
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

                                                                if (req_ref < people_may_know_uid.size()) {
                                                                    try {
                                                                        may_name.setText(dsp.getValue().toString());
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

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
                                                                if (req_ref < people_may_know_uid.size() && !dsp.getValue().toString().equals("")) {
                                                                    try {
                                                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) may_nick.getLayoutParams();
                                                                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                        may_nick.setLayoutParams(layoutParams);
                                                                        may_nick.setText("@ " + dsp.getValue().toString());
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

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
                                                                if (req_ref < people_may_know_uid.size()) {
                                                                    try {
                                                                        may_location.setText(" " + dsp.getValue().toString());
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                                if (dsp.getKey().equals("friends")) {
                                                    if (self_friends_snapshot != null && self_friends_snapshot_found) {
                                                        int mutual_count = 0;
                                                        for (DataSnapshot dataSnapshot1 : dsp.getChildren()) {
                                                            for (DataSnapshot dataSnapshot2 : self_friends_snapshot.getChildren()) {
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
                                                                    if (req_ref < people_may_know_uid.size() && my_mutual > 0) {
                                                                        try {
                                                                            may_mutual.setText(getResources().getString(R.string.mutual_friends_string) + String.valueOf(my_mutual));
                                                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) may_mutual.getLayoutParams();
                                                                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                                            may_mutual.setLayoutParams(layoutParams);
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
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
                            if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running) {
                                connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                networkInfo = connMgr.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.isConnected()) {

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {


                                            if (get_activity_state()) {


                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (req_ref < people_may_know_uid.size()) {
                                                            try {
                                                                cancel_request_button.setVisibility(View.INVISIBLE);
                                                                add_friend_button.setVisibility(View.INVISIBLE);
                                                                may_Bar.setVisibility(View.VISIBLE);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    }
                                                });
                                            }

                                            try {


                                                DatabaseReference in_add_ref = databaseReference.child("friend_requests");

                                                in_add_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        try {
                                                        if (get_activity_state()) {
                                                            boolean may_add_found = false;
                                                            if (req_ref < people_may_know_uid.size()) {
                                                                if (dataSnapshot.hasChild(people_may_know_uid.get(req_ref))) {
                                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(people_may_know_uid.get(req_ref)).getChildren()) {
                                                                        if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                            may_add_found = true;
                                                                        }
                                                                    }
                                                                    if (!may_add_found) {
                                                                        DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");

                                                                        String key = friend_requests_reference.child(people_may_know_uid.get(req_ref)).push().getKey();
                                                                        if (key != null) {


                             /*
                             Map mWayPointsMap = new HashMap();
                             mWayPointsMap.put(key, mAuth.getCurrentUser().getUid());

*/
                                                                            //  friend_requests_reference.child(user_uid_list.get(req_ref)).child(mAuth.getCurrentUser().getUid()).
                                                                            friend_requests_reference.child(people_may_know_uid.get(req_ref)).child(key).setValue(mAuth.getCurrentUser().getUid()).
                                                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull final Task<Void> task) {

                                                                                            if (task.isSuccessful()) {
                                                                                                if (get_activity_state()) {

                                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                                        @Override
                                                                                                        public void run() {
                                                                                                            if (req_ref < people_may_know_uid.size()) {
                                                                                                                try {
                                                                                                                    Toast.makeText(getActivity(), getResources().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                                                                                                                    cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                    add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                    may_Bar.setVisibility(View.INVISIBLE);
                                                                                                                    people_may_know_uid.remove(people_may_know_uid.get(req_ref));
                                                                                                                    customAdapter_know.notifyDataSetChanged();
                                                                                                                    int totalHeight = 0;
                                                                                                                    int desiredWidth = View.MeasureSpec.makeMeasureSpec(people_you_may_know_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                    for (int z = 0; z < customAdapter_know.getCount(); z++) {
                                                                                                                        View listItem = customAdapter_know.getView(z, null, people_you_may_know_list_view);
                                                                                                                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                        totalHeight += listItem.getMeasuredHeight();
                                                                                                                    }

                                                                                                                    ViewGroup.LayoutParams params = people_you_may_know_list_view.getLayoutParams();
                                                                                                                    params.height = totalHeight + (people_you_may_know_list_view.getDividerHeight() * (customAdapter_know.getCount() - 1));
                                                                                                                    people_you_may_know_list_view.setLayoutParams(params);
                                                                                                                    people_you_may_know_list_view.requestLayout();
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
                                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                                        @Override
                                                                                                        public void run() {
                                                                                                            try {
                                                                                                                if (req_ref < people_may_know_uid.size()) {

                                                                                                                    cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                                    add_friend_button.setVisibility(View.VISIBLE);
                                                                                                                    may_Bar.setVisibility(View.INVISIBLE);
                                                                                                                }
                                                                                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        if (get_activity_state()) {
                                                                                            try {
                                                                                                cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                may_Bar.setVisibility(View.INVISIBLE);
                                                                                                add_friend_button.setVisibility(View.VISIBLE);
                                                                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();

                                                                                            } catch (Exception e) {
                                                                                                e.printStackTrace();
                                                                                            }

                                                                                        }
                                                                                    }
                                                                                });
                                                                            }

                                                                        }
                                                                    } else {


                                                                        if (req_ref < people_may_know_uid.size()) {

                                                                            if (get_activity_state()) {
                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        try {
                                                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                            add_friend_button.setVisibility(View.INVISIBLE);
                                                                                            may_Bar.setVisibility(View.INVISIBLE);
                                                                                        } catch (Exception e) {
                                                                                            e.printStackTrace();
                                                                                        }

                                                                                    }
                                                                                });
                                                                            }

                                                                        }


                                                                    }


                                                                } else {
                                                                    DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");

                                                                    String key = friend_requests_reference.child(people_may_know_uid.get(req_ref)).push().getKey();
                                                                    if (key != null) {

                                                                        if (get_activity_state()) {

                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {
                                                                                        if (req_ref < people_may_know_uid.size()) {

                                                                                            add_friend_button.setVisibility(View.INVISIBLE);
                                                                                            may_Bar.setVisibility(View.VISIBLE);
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                }
                                                                            });
                                                                        }

                             /*
                             Map mWayPointsMap = new HashMap();
                             mWayPointsMap.put(key, mAuth.getCurrentUser().getUid());

*/
                                                                        //  friend_requests_reference.child(user_uid_list.get(req_ref)).child(mAuth.getCurrentUser().getUid()).
                                                                        friend_requests_reference.child(people_may_know_uid.get(req_ref)).child(key).setValue(mAuth.getCurrentUser().getUid()).
                                                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()) {
                                                                                            if (get_activity_state()) {

                                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        try {
                                                                                                            if (req_ref < people_may_know_uid.size()) {
                                                                                                                Toast.makeText(getActivity(), getResources().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                                                                                                                cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                may_Bar.setVisibility(View.INVISIBLE);
                                                                                                                people_may_know_uid.remove(people_may_know_uid.get(req_ref));
                                                                                                                customAdapter_know.notifyDataSetChanged();
                                                                                                                int totalHeight = 0;
                                                                                                                int desiredWidth = View.MeasureSpec.makeMeasureSpec(people_you_may_know_list_view.getWidth(), View.MeasureSpec.AT_MOST);
                                                                                                                for (int z = 0; z < customAdapter_know.getCount(); z++) {
                                                                                                                    View listItem = customAdapter_know.getView(z, null, people_you_may_know_list_view);
                                                                                                                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                                                                    totalHeight += listItem.getMeasuredHeight();
                                                                                                                }

                                                                                                                ViewGroup.LayoutParams params = people_you_may_know_list_view.getLayoutParams();
                                                                                                                params.height = totalHeight + (people_you_may_know_list_view.getDividerHeight() * (customAdapter_know.getCount() - 1));
                                                                                                                people_you_may_know_list_view.setLayoutParams(params);
                                                                                                                people_you_may_know_list_view.requestLayout();
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
                                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        try {
                                                                                                            if (req_ref < people_may_know_uid.size()) {

                                                                                                                cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                                add_friend_button.setVisibility(View.VISIBLE);
                                                                                                                may_Bar.setVisibility(View.INVISIBLE);
                                                                                                            }
                                                                                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {
                                                                                        Toast.makeText(getActivity(),getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                    }catch (Exception e)
                                                        {
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
                                                                        if (req_ref < people_may_know_uid.size()) {

                                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                                            add_friend_button.setVisibility(View.VISIBLE);
                                                                            may_Bar.setVisibility(View.INVISIBLE);
                                                                        }
                                                                        Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }
                                                            });
                                                        }


                                                    }
                                                });

                                            }catch (Exception e)
                                            {
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
                        }
                    });

                    cancel_request_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                            if (get_activity_state()) {

                                connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                networkInfo = connMgr.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.isConnected()) {

                                    cancel_request_button.setVisibility(View.INVISIBLE);
                                    may_Bar.setVisibility(View.VISIBLE);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {


                                                DatabaseReference req_initial_ref = databaseReference.child("friend_requests");
                                                req_initial_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        try {
                                                            boolean mutual_cancel_found = false;

                                                            if (get_activity_state()) {
                                                                if (dataSnapshot.hasChild(people_may_know_uid.get(req_ref))) {

                                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(people_may_know_uid.get(req_ref)).getChildren()) {

                                                                        if (dataSnapshot1.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                                                            mutual_cancel_found = true;
                                                                            final DatabaseReference friend_requests_reference = databaseReference.child("friend_requests");

                                                                            friend_requests_reference.child(people_may_know_uid.get(req_ref)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    try {
                                                                                        if (get_activity_state()) {
                                                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                                                if (snapshot.getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                                                                                                    friend_requests_reference.child(people_may_know_uid.get(req_ref)).child(snapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {

                                                                                                                if (get_activity_state()) {

                                                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            try {
                                                                                                                                if (req_ref < people_may_know_uid.size()) {
                                                                                                                                    cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                                                    add_friend_button.setVisibility(View.VISIBLE);
                                                                                                                                    may_Bar.setVisibility(View.INVISIBLE);
                                                                                                                                }
                                                                                                                            } catch (Exception e) {
                                                                                                                                e.printStackTrace();
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
                                                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            try {
                                                                                                                                if (req_ref < people_may_know_uid.size()) {
                                                                                                                                    add_friend_button.setVisibility(View.INVISIBLE);
                                                                                                                                    cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                                                    may_Bar.setVisibility(View.INVISIBLE);
                                                                                                                                }
                                                                                                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                                                                                public void onCancelled(DatabaseError databaseError) {
                                                                                    if (get_activity_state()) {
                                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                try {
                                                                                                    if (req_ref < people_may_know_uid.size()) {
                                                                                                        cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                        may_Bar.setVisibility(View.INVISIBLE);
                                                                                                    }
                                                                                                } catch (Exception e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        });
                                                                                    }

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                    if (!mutual_cancel_found) {
                                                                        if (get_activity_state()) {
                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {
                                                                                        if (req_ref < people_may_know_uid.size()) {
                                                                                            add_friend_button.setVisibility(View.VISIBLE);
                                                                                            cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                            may_Bar.setVisibility(View.INVISIBLE);
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
                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    if (req_ref < people_may_know_uid.size()) {
                                                                                        add_friend_button.setVisibility(View.VISIBLE);
                                                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                        may_Bar.setVisibility(View.INVISIBLE);
                                                                                    }
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
                                                        if (get_activity_state()) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        if (req_ref < people_may_know_uid.size()) {
                                                                            cancel_request_button.setVisibility(View.VISIBLE);
                                                                            may_Bar.setVisibility(View.INVISIBLE);
                                                                        }
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
                                    try {
                                        if (get_activity_state()) {
                                            ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
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

















}
