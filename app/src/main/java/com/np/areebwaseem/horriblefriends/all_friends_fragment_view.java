package com.np.areebwaseem.horriblefriends;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import java.util.ArrayList;

/**
 * Created by areebwaseem on 10/28/17.
 */

public class all_friends_fragment_view extends Fragment {
    private static final String TAG = "all_friends_fragment_vi";


private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    ListView users_view_all;
    ArrayList<String> user_uid_list_all;
    ArrayList<String> user_name_list_all;
    DataSnapshot all_users_uid_dataa_all;
    CustomAdapter_all customAdapter_all;
    FirebaseStorage firebaseStorage;
    ProgressBar bottom_main_bar_all;
    DataSnapshot friend_request_data_all;
    DataSnapshot all_user_data_all;
    volatile boolean is_activity_running_all;
    DataSnapshot personal_data_snap_all;
    DataSnapshot personal_friends_data_all;
    Context context_all;
    SwipeRefreshLayout swipeRefreshLayout_all;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    ChildEventListener valueEventListener_all;
    String current_uid_all="";
    boolean isDownloading_all=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.all_users_fragment_view, container, false);
        context_all = this.getActivity();
        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();



        ////////////////// Layout Elements ////////////////////////



        users_view_all = view.findViewById(R.id.all_user_list_view);
        bottom_main_bar_all  = view.findViewById(R.id.all_friend_bottom_progress_bar);
        swipeRefreshLayout_all = view.findViewById(R.id.all_swipe_to_refresh);

       ///////////////// Fire base Elements Initialization ///////////////////

        mAuth = FirebaseAuth.getInstance();
        databaseReference  = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        is_activity_running_all=true;


        ///////////// Arraylist/ Adapter Initialization /////////////////////


        user_uid_list_all = new ArrayList<>();
        user_name_list_all = new ArrayList<>();
        customAdapter_all = new CustomAdapter_all();

        if (networkInfo!=null && networkInfo.isConnected()) {
            performSearch();
        }
        else {
            ((add_friend_activity) getActivity()).show_snackbar("No Internet!", true);
        }


        swipeRefreshLayout_all.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (get_activity_state()) {
                    swipeRefreshLayout_all.setRefreshing(false);
                    connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo!=null && networkInfo.isConnected()) {
                        performSearch();
                    }
                    else {
                        ((add_friend_activity) getActivity()).show_snackbar("No Internet!", true);
                    }
                }
            }
        });

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
            ((add_friend_activity)getActivity()).set_bottom_users();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        is_activity_running_all = false;
        if (valueEventListener_all!=null)
        {
            databaseReference.child("users").child(current_uid_all).child("friends").removeEventListener(valueEventListener_all);
        }
    }


    private boolean get_activity_state() {
        if (getActivity()!=null && mAuth != null && mAuth.getCurrentUser() != null && is_activity_running_all ) {
            return true;
        } else {
            return false;
        }
    }

    private void performSearch()
    {


        if (!isDownloading_all) {
            isDownloading_all=true;
            bottom_main_bar_all.setVisibility(View.VISIBLE);

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

            new Thread(new Runnable() {
                @Override
                public void run() {

                    DatabaseReference databaseReference1 = databaseReference.child("users");
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
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
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            isDownloading_all=false;
                                            bottom_main_bar_all.setVisibility(View.INVISIBLE);
                                            users_view_all.setAdapter(customAdapter_all);
                                            customAdapter_all.notifyDataSetChanged();


                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    valueEventListener_all = databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").addChildEventListener(new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                        }

                                                        @Override
                                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                        }

                                                        @Override
                                                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                            if (user_uid_list_all != null && user_uid_list_all.size() > 0) {
                                                                for (int i = 0; i < user_uid_list_all.size(); i++) {
                                                                    if (user_uid_list_all.get(i).equals(dataSnapshot.getValue())) {
                                                                        user_uid_list_all.remove(user_uid_list_all.get(i));
                                                                        if (customAdapter_all != null) {
                                                                            if (get_activity_state()) {
                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        customAdapter_all.notifyDataSetChanged();
                                                                                    }
                                                                                });

                                                                            }
                                                                        }
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
                                            }).start();


                                        }
                                    });

                                } else {
                                    isDownloading_all=false;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            bottom_main_bar_all.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getActivity(), "No horrible friends to show!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                            } else {
                                isDownloading_all=false;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bottom_main_bar_all.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "No data!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (getActivity() != null) {
                                isDownloading_all=false;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    });


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
            final Button add_friend_button = view.findViewById(R.id.all_custom_list_item_add_friend_button);
            final Button cancel_request_button = view.findViewById(R.id.all_custom_list_item_cancel_request_button);
            final ProgressBar bar_friend_request_processing = view.findViewById(R.id.all_custom_user_list_item_progress_bar);




            if (get_activity_state() && req_ref<user_uid_list_all.size()) {



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






                cancel_request_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (get_activity_state()) {
                            connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            networkInfo = connMgr.getActiveNetworkInfo();
                            if (networkInfo!=null && networkInfo.isConnected()) {
                                final DatabaseReference delete_reference = databaseReference.child("users");
                                cancel_request_button.setVisibility(View.INVISIBLE);
                                bottom_main_bar_all.setVisibility(View.VISIBLE);
                                delete_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        if (get_activity_state()) {



                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
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
                                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                                @Override
                                                                                                                                public void run() {
                                                                                                                                    if (req_ref < user_uid_list_all.size()) {
                                                                                                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                                                                                                    }
                                                                                                                                    bottom_main_bar_all.setVisibility(View.INVISIBLE);
                                                                                                                                }
                                                                                                                            });


                                                                                                                        } else {
                                                                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                                                                @Override
                                                                                                                                public void run() {

                                                                                                                                        bottom_main_bar_all.setVisibility(View.INVISIBLE);
                                                                                                                                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            });

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
                                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                if (req_ref < user_uid_list_all.size()) {
                                                                                                    cancel_request_button.setVisibility(View.VISIBLE);
                                                                                                }
                                                                                                bottom_main_bar_all.setVisibility(View.INVISIBLE);
                                                                                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });

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
                                            }).start();




                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        if (req_ref < user_uid_list_all.size()) {
                                            cancel_request_button.setVisibility(View.VISIBLE);

                                        }
                                        bottom_main_bar_all.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                ((add_friend_activity) getActivity()).show_snackbar("No Internet!", true);
                            }
                        }
                    }
                });




                if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running_all) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (DataSnapshot dataSnapshot : all_users_uid_dataa_all.getChildren()) {

                                if (dataSnapshot.getKey().toString().equals(user_uid_list_all.get(req_ref))) {
                                    final StorageReference storageReference_one = firebaseStorage.getReference();
                                    if (mAuth != null && mAuth.getCurrentUser() != null) {

                                        StorageReference prof_reference_one = storageReference_one.child(user_uid_list_all.get(req_ref));
                                        prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                if (req_ref < user_uid_list_all.size()) {
                                                    if (getActivity()!=null) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Glide.with(getActivity().getApplicationContext())
                                                                        .using(new FirebaseImageLoader())
                                                                        .load(storageReference_one.child(user_uid_list_all.get(req_ref))).into(imageView);
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

                                    for (final DataSnapshot dsp : dataSnapshot.getChildren()) {

                                        if (dsp.getKey().toString().equals("full_name")) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (req_ref < user_uid_list_all.size()) {
                                                        name_view.setText(dsp.getValue().toString());
                                                    }
                                                }
                                            });

                                        }
                                        if (dsp.getKey().equals("friends"))
                                        {
                                            if (personal_friends_data_all!=null) {
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
                                                        if (req_ref < user_uid_list_all.size()) {
                                                            mutual_view.setText("Mutual Friends: " + String.valueOf(my_mutual));
                                                        }
                                                    }
                                                });


                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }).start();


                }
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
















}
