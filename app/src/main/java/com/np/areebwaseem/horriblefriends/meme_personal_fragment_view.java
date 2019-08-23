package com.np.areebwaseem.horriblefriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by areebwaseem on 10/28/17.
 */

public class meme_personal_fragment_view extends Fragment {

    private static final String TAG = "meme_personal_fragment_";


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

    CustomAdapter customAdapter;
    ListView all_memes_list_view;

    ArrayList<String> all_friends_list;
    DataSnapshot all_memes_data_snapshot;
    ArrayList<String> memes_with_friends_list;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    Bitmap bitmap;
    TextView no_meme_found_text_view;







    boolean isDownloading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.personal_memes_fragment_view, container, false);
        context = this.getActivity();

        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();




        ////////////////// Layout Elements ////////////////////////

        all_memes_list_view= view.findViewById(R.id.meme_personal_list_view);
        swipeRefreshLayout = view.findViewById(R.id.personal_memes_swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progressBar_personal_memes_fragment);
        no_meme_found_text_view = view.findViewById(R.id.no_memes_found_text_view_personal);



        ////////////// Firebase Elements Initialization ////////////

        mAuth = FirebaseAuth.getInstance();
        databaseReference  = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        is_activity_running=true;
        isDownloading=false;



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

    public void get_all_memes()
    {




        if (get_activity_state()) {



            if (is_connected()) {


                try {
                    if (!isDownloading)
                    {



                    no_meme_found_text_view.setVisibility(View.INVISIBLE);


                    all_friends_list.clear();
                    memes_with_friends_list.clear();
                    if (customAdapter != null) {
                        customAdapter.notifyDataSetChanged();
                    }


                    make_list_to_zero();
                    isDownloading=true;
                    progressBar.setVisibility(View.VISIBLE);


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



                        /*

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            for (int i = 0; i < all_friends_list.size(); i++) {
                                if (dataSnapshot1.hasChild("to")) {
                                    if (dataSnapshot1.child("to").getValue().equals(all_friends_list.get(i))) {
                                        memes_with_friends_list.add(dataSnapshot1.getKey());
                                    }
                                }
                            }
                        }


                        if (memes_with_friends_list.size() > 0) {
                            progressBar.setVisibility(View.INVISIBLE);
                            all_memes_list_view.setAdapter(customAdapter);
                            customAdapter.notifyDataSetChanged();
                            make_list_to_wrap();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "No memes found", Toast.LENGTH_SHORT).show();
                            // no memes found
                        }
                        */


                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                if (dataSnapshot1.hasChild("to")) {
                                                    if (dataSnapshot1.child("to").getValue().equals(current_uid)) {
                                                        memes_with_friends_list.add(dataSnapshot1.getKey());
                                                    }
                                                }

                                            }
                                            isDownloading = false;
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
                                                            if (memes_with_friends_list.size() > 0) {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                Collections.reverse(memes_with_friends_list);
                                                                all_memes_list_view.setAdapter(customAdapter);
                                                                customAdapter.notifyDataSetChanged();

                                                                ///////// new code /////////////////////////////

                                                                if (get_activity_state()) {

                                                                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("new_message_notifications").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                        }
                                                                    });

                                                                }

                                                                make_list_to_wrap();
                                                            } else {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                no_meme_found_text_view.setVisibility(View.VISIBLE);
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
                                            error_bar();
                                            e.printStackTrace();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        if (get_activity_state()) {
                                            isDownloading=false;
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
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
                               error_bar();
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
                if (getActivity()!=null) {
                    ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
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
        }else {
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
            ((home_activity)getActivity()).set_bottom_meme();
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

                           to_image_view.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   if (get_activity_state() && current_uid!=null)
                                       try {
                                           Intent intent = new Intent(getActivity(),profile_view_activity.class);
                                           intent.putExtra("curr_uid", current_uid);
                                           getActivity().startActivity(intent);
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



                       try {
                           final StorageReference storageReference_one = firebaseStorage.getReference();
                           final String curr_uid = all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("to").getValue().toString();
                           Glide.with(getActivity().getApplicationContext())
                                   .using(new FirebaseImageLoader())
                                   .load(storageReference_one.child(curr_uid)).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(to_image_view);
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


                       long now_time= ((home_activity)getActivity()).get_time_now();
                       if (now_time!=0) {
                           long milli_sec = Long.parseLong(all_memes_data_snapshot.child(memes_with_friends_list.get(req_ref)).child("timestamp").getValue().toString());
                           // int hours   = (int) (((System.currentTimeMillis()-milli_sec)/ (1000*60*60)) % 24);
                          // int hours = (int) (((now_time - milli_sec)) / (1000 * 60 * 60));
                         //  int mins = (int) (((now_time - milli_sec) / (1000 * 60)) % 60);

                         //  int hours   = (int) (((now_time-milli_sec) / (1000*60*60)) % 24);
                           // int mins = (int) (((now_time - milli_sec) / (1000 * 60)) % 60);
                           int hours = (int) (((now_time - milli_sec)) / (1000 * 60 * 60));
                           int mins = (int) (((now_time-milli_sec) / (1000*60)) % 60);

                           if (hours >= 1) {
                               hours_ago_text_view.setText(String.valueOf(hours) + " " + getResources().getString(R.string.hours_ago));
                           } else {
                               //  int minutes = (int) (((System.currentTimeMillis()-milli_sec) / (1000*60) % 60));
                               hours_ago_text_view.setText(String.valueOf(mins) + " "+ getResources().getString(R.string.minutes_ago));
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


                           final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                           View mView = getActivity().getLayoutInflater().inflate(R.layout.share_dialog_custom_view,null);
                           ImageButton fb_share = mView.findViewById(R.id.fb_share_custom);
                           ImageButton tw_share = mView.findViewById(R.id.tw_share_custom);
                           ImageButton in_share = mView.findViewById(R.id.in_share_custom);
                           ImageButton wh_share = mView.findViewById(R.id.wh_share_custom);

                           builder.setView(mView);
                           final AlertDialog dialog= builder.create();
                           Window window = dialog.getWindow();
                           final int sdk = android.os.Build.VERSION.SDK_INT;
                           if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                               window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.layout_rounded_white) );
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
                                       ((home_activity) getActivity()).current_share_uid = memes_with_friends_list.get(req_ref);
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
                           if (get_activity_state()) {
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





