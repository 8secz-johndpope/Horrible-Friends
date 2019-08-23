package com.np.areebwaseem.horriblefriends;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;


import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;


import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Button;
import android.widget.CompoundButton;


import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by areebwaseem on 10/28/17.
 */

public class settings_fragment_view extends Fragment {

    private static final String TAG = "settings_fragment_view";


    private FirebaseAuth mAuth;

    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;

    volatile boolean is_activity_running;
    Context context;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    String current_uid="";
    ProgressBar progressBar;
    Switch friend_request_switch;
    Switch new_message_switch;
    Switch new_comment_switch;
    Switch message_favourite_switch;
    String current_name="";
    String current_nick="";
    String current_city="";
    String current_phone="";
    Button sele_camera;
    Button select_image;
    Uri imageUri;

    boolean is_deleting;
    boolean is_suspending;

    DataSnapshot personal_data_snap;

    ConstraintLayout delete_account_button;
    ConstraintLayout suspend_account_button;
    ConstraintLayout log_out_button;
    ConstraintLayout change_name_button;
    ConstraintLayout change_nick_button;
    ConstraintLayout change_mobile_button;
    ConstraintLayout change_city_button;
    ConstraintLayout change_password_button;

    String my_selected_city_code="";
    TextView name_view;
    TextView nick_view;
    TextView loc_view;
    TextView phone_view;
    TextView email_view;
    DataSnapshot personal_auth;
    CircleImageView prof_image_view;
    ContentValues values;
    boolean new_pic_selected;
    Button resend_email;
    Button update_email;
    ConstraintLayout un_veri_layout;
    TextView veri_em_text_view;


    private FirebaseFunctions mFunctions;

    SwipeRefreshLayout swipeRefreshLayout;


SharedPreferences sharedPreferences;



    boolean isDownloading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_fragment_layout, container, false);
        context = this.getActivity();

        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        new_pic_selected=false;
        is_deleting=false;


        is_activity_running=true;
        sharedPreferences = getActivity().getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);


        ////////////////// Layout Elements ////////////////////////

        friend_request_switch = view.findViewById(R.id.switch1);
        new_message_switch = view.findViewById(R.id.switch2);
        new_comment_switch = view.findViewById(R.id.switch3);
        message_favourite_switch = view.findViewById(R.id.switch4);
        delete_account_button = view.findViewById(R.id.settings_fragment_delete_account_button);
        suspend_account_button = view.findViewById(R.id.settings_fragment_suspend_account_button);
        log_out_button = view.findViewById(R.id.settings_fragment_logout_button);
        change_name_button = view.findViewById(R.id.settings_fragment_edit_name_view);
        change_nick_button = view.findViewById(R.id.settings_fragment_edit_nick_view);
        change_city_button = view.findViewById(R.id.settings_fragment_edit_city_view);
        change_mobile_button = view.findViewById(R.id.settings_fragment_edit_mobile_view);
        change_password_button = view.findViewById(R.id.settings_fragment_edit_password_view);
        name_view = view.findViewById(R.id.settings_fragment_name_text_view);
        nick_view = view.findViewById(R.id.settings_fragment_nick_view);
        loc_view = view.findViewById(R.id.settings_fragment_location_view);
        phone_view = view.findViewById(R.id.settings_fragment_phone_view);
        email_view = view.findViewById(R.id.settings_fragment_email_view);
        swipeRefreshLayout = view.findViewById(R.id.settings_swipe_refresh);
        progressBar = view.findViewById(R.id.swipe_refresh_progres_bar);
        prof_image_view = view.findViewById(R.id.profile_pic_settings_fragment);
        sele_camera = view.findViewById(R.id.sele_camera_settings);
        select_image = view.findViewById(R.id.sele_gallery_settings);
        resend_email = view.findViewById(R.id.resend_email_button_under_cons_layout);
        update_email = view.findViewById(R.id.update_email_under_cons_layout);
        un_veri_layout = view.findViewById(R.id.veri_email_cons_layout);
        veri_em_text_view = view.findViewById(R.id.verify_email_text_view);



        my_selected_city_code= "+1";





        ////////////// Firebase Elements Initialization ////////////

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        databaseReference  = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        isDownloading=false;
        is_suspending=false;



        ///////////// Arraylist/ Adapter Initialization /////////////////////





        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            current_uid= mAuth.getCurrentUser().getUid();
        }

        if (is_connected())
        {
            try {
                final StorageReference storageReference_one = firebaseStorage.getReference();
                Glide.with(getActivity())
                        .using(new FirebaseImageLoader())
                        .load(storageReference_one.child(current_uid)).error(R.drawable.asset_four).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(prof_image_view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        friend_request_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (get_activity_state() && is_connected()) {
                    if (isChecked) {

                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").child("friend_requests").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").child("friend_requests").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    if (getActivity()!=null)
                    {
                        ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                    }
                }
            }
        });

        new_message_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (get_activity_state() && is_connected()) {
                    if (isChecked) {
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").child("new_message").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").child("new_message").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    if (getActivity()!=null)
                    {
                        ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                    }
                }
            }
        });

        change_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_change_name_alert();
            }
        });

        change_nick_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_change_nick_alert();
            }
        });

        change_mobile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_change_phone_alert();
            }
        });

        change_city_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_change_city_alert();
            }
        });

        change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               check_for_auth_type();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                if (!isDownloading)
                {
                    try {
                        final StorageReference storageReference_one = firebaseStorage.getReference();
                        Glide.with(getActivity())
                                .using(new FirebaseImageLoader())
                                .load(storageReference_one.child(current_uid)).error(R.drawable.asset_four).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(prof_image_view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    remove_email_view();
                    remove_location();
                    remove_nick();
                    remove_name();
                    remove_phone_view();
                    get_profile_data();
                }
            }
        });










        new_comment_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (get_activity_state() && is_connected()) {
                    if (isChecked) {
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").child("new_comment").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").child("new_comment").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else {
                    if (getActivity()!=null)
                    {
                        ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                    }
                }
            }
        });

        message_favourite_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (get_activity_state() && is_connected()) {
                    if (isChecked) {
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").child("favourite_message").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").child("favourite_message").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else {
                    if (getActivity() != null) {
                        ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                    }
                }
            }
        });

        delete_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (current_uid!=null && !is_deleting) {



                    final AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle(getResources().getString(R.string.confirm_string))
                            .setMessage(getResources().getString(R.string.are_you_sure_want_to_delete))
                            .setPositiveButton(getResources().getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (get_activity_state() && is_connected()) {
                                        // continue with delete
                                        is_deleting = true;

                                        delete_account_button.setClickable(false);
                                        progressBar.setVisibility(View.VISIBLE);
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("text", current_uid);


                                        mFunctions.getHttpsCallable("delete_user")
                                                .call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                if (task.isSuccessful()) {

                                                    // Log.d(TAG, task.getResult().getData().toString());
                                                    sharedPreferences.edit().putString("token_set", "false").apply();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.success_string), Toast.LENGTH_SHORT).show();
                                                    is_deleting = false;
                                                    mAuth.signOut();

                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    delete_account_button.setClickable(true);
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                    is_deleting = false;
                                                }
                                            }
                                        });
                                    }else {
                                        if (getActivity()!=null)
                                        {
                                            ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.no_string), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            }).show();



                    /*
                    delete_user(current_uid)
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Exception e = task.getException();
                                        if (e instanceof FirebaseFunctionsException) {
                                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                            FirebaseFunctionsException.Code code = ffe.getCode();
                                            Object details = ffe.getDetails();
                                        }

                                        // ...
                                    }
                                    else {
                                        Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });
                            */
                }

            }
        });

        suspend_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (get_activity_state() && is_connected())
                {

                if (current_uid != null && !is_suspending) {
                    final AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle(getResources().getString(R.string.confirm_string))
                            .setMessage(getResources().getString(R.string.are_you_sure_suspend_account_string))
                            .setPositiveButton(getResources().getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete

                                    dialog.dismiss();
                                    if (get_activity_state() && is_connected())

                                    {

                                        suspend_account_button.setClickable(false);
                                        is_suspending = true;

                                        Map<String, Object> taskMap = new HashMap<>();
                                        taskMap.put("token", "");
                                        taskMap.put("is_suspended", true);
                                        databaseReference.child("users_auth").child(mAuth.getCurrentUser().getUid()).child("is_suspended").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    sharedPreferences.edit().putString("token_set", "false").apply();
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.account_suspended), Toast.LENGTH_SHORT).show();
                                                    is_suspending = false;
                                                    mAuth.signOut();

                                                } else {
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                    suspend_account_button.setClickable(true);
                                                    is_suspending = false;
                                                }
                                            }
                                        });
                                    }else {
                                        if (getActivity()!=null)
                                        {
                                            ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                                        }

                                    }
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.no_string), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            }).show();

                }
            }
            else {
                    if (getActivity()!=null)
                    {
                        ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                    }
                }

            }
        });

        sele_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check_storage_permission()) {
                    values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getActivity().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 3);
                }
                else {
                    if (getActivity()!=null) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.grant_storage_permission_string), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);

            }
        });

        log_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_token();
            }
        });

        get_profile_data();






        return view;
    }

    private void get_profile_data(){


        if (is_connected() && current_uid!=null && !isDownloading && get_activity_state())
        {
            check_verification();
            progressBar.setVisibility(View.VISIBLE);
            isDownloading=true;
            databaseReference.child("users_auth").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    personal_auth=dataSnapshot;
                    databaseReference.child("users").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            isDownloading=false;
                            if (get_activity_state() && dataSnapshot!=null)
                            {
                                personal_data_snap=dataSnapshot;
                                if (dataSnapshot.hasChild("notifications"))
                                {
                                    if (dataSnapshot.child("notifications").hasChild("friend_requests"))
                                    {
                                        try {
                                            boolean is_request = (Boolean) dataSnapshot.child("notifications").child("friend_requests").getValue();

                                            if  (is_request)
                                            {

                                                friend_request_switch.setChecked(true);
                                            }
                                            else {

                                                friend_request_switch.setChecked(false);

                                            }
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                    else {

                                        friend_request_switch.setChecked(false);

                                    }
                                    if (dataSnapshot.child("notifications").hasChild("new_message"))
                                    {

                                        try {
                                            boolean is_request = (Boolean) dataSnapshot.child("notifications").child("new_message").getValue();

                                            if  (is_request)
                                            {
                                                new_message_switch.setChecked(true);
                                            }
                                            else {
                                                new_message_switch.setChecked(false);
                                            }
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }



                                    }
                                    else {
                                        new_message_switch.setChecked(false);
                                    }
                                    if (dataSnapshot.child("notifications").hasChild("favourite_message"))
                                    {

                                        try {
                                            boolean is_request = (Boolean) dataSnapshot.child("notifications").child("favourite_message").getValue();

                                            if  (is_request)
                                            {
                                                message_favourite_switch.setChecked(true);
                                            }
                                            else {
                                                message_favourite_switch.setChecked(false);
                                            }
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                    else {
                                        message_favourite_switch.setChecked(false);
                                    }
                                    if (dataSnapshot.child("notifications").hasChild("new_comment"))
                                    {

                                        try {
                                            boolean is_request = (Boolean) dataSnapshot.child("notifications").child("new_comment").getValue();

                                            if  (is_request)
                                            {
                                                new_comment_switch.setChecked(true);
                                            }
                                            else {
                                                new_message_switch.setChecked(false);
                                            }
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                    else {
                                        new_message_switch.setChecked(false);
                                    }


                                }
                                else {
                                    // all buttons off
                                }
                                if (dataSnapshot.hasChild("full_name") && !dataSnapshot.child("full_name").getValue().toString().trim().equals(""))
                                {
                                    set_name(dataSnapshot.child("full_name").getValue().toString());
                                }
                                if (dataSnapshot.hasChild("name") && !dataSnapshot.child("name").getValue().toString().trim().equals(""))
                                {
                                    set_nick(dataSnapshot.child("name").getValue().toString());
                                }
                                if (dataSnapshot.hasChild("City") && !dataSnapshot.child("City").getValue().toString().trim().equals(""))
                                {
                                    set_location(dataSnapshot.child("City").getValue().toString());
                                }
                                if (dataSnapshot.hasChild("Email") && !dataSnapshot.child("Email").getValue().toString().trim().equals(""))
                                {
                                    set_email_view(dataSnapshot.child("Email").getValue().toString());
                                }
                                if (dataSnapshot.hasChild("phone") && !dataSnapshot.child("phone").getValue().toString().trim().equals(""))
                                {
                                    set_phone_view(dataSnapshot.child("phone").getValue().toString());
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                isDownloading=false;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (get_activity_state()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                isDownloading=false;
                                Toast.makeText(getActivity(), getResources().getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (get_activity_state()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        isDownloading=false;
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else {
            if (get_activity_state()) {
                if (!is_connected()) {

                    ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                }
            }
            progressBar.setVisibility(View.INVISIBLE);
            isDownloading=false;
           // Toast.makeText(getActivity(),"null",Toast.LENGTH_SHORT).show();
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
        if (requestCode == 2 && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();



                /*
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                */

            //   new_pic_selected=true;

            try {


                    /*
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), selectedImage);
                    //  Bitmap rotatedBitmap = rotateImage(thumbnail, -90);
                    // profile_view.setImageBitmap(rotatedBitmap);
                    // String imageurl = getRealPathFromURI(imageUri);
                    // profile_view.setImageURI(imageurl);
                    ExifInterface ei = new ExifInterface(getRealPathFromURI(selectedImage));
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(thumbnail, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(thumbnail, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(thumbnail, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = thumbnail;
                    }
                    */
                //
                // profile_view.setImageBitmap(rotatedBitmap);
                Uri myUri = Uri.fromFile(new File(getActivity().getCacheDir(),"cropped"));

                Crop.of(selectedImage, myUri).asSquare().start(getActivity(),settings_fragment_view.this);
                //  profile_view.setImageURI(Crop.getOutput(data));


            } catch (Exception e) {
                // profile_view.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                e.printStackTrace();
            }













        }

        else if (requestCode==Crop.REQUEST_CROP)
        {
            handleCrop(resultCode,data);
        }



        /*
        else   if (requestCode == 1000) {
            if(resultCode == RESULT_OK){

                String result=data.getStringExtra("result");
                // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                getLocation();
//                    Log.d(TAG, result);

            } if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
*/





        else if (requestCode == 3 && resultCode == getActivity().RESULT_OK)
        {
            //  new_pic_selected=true;
            /*
            try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                profile_view.setImageBitmap(thumbnail);
               // imageurl = getRealPathFromURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
/*
            Bundle extras = data.getExtras();

            // get bitmap
             Bitmap bitMap = (Bitmap) extras.get("data");
            profile_view.setImageBitmap(bitMap);

*/
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(), imageUri);
                //  Bitmap rotatedBitmap = rotateImage(thumbnail, -90);
                // profile_view.setImageBitmap(rotatedBitmap);
                // String imageurl = getRealPathFromURI(imageUri);
                // profile_view.setImageURI(imageurl);
                ExifInterface ei = new ExifInterface(getRealPathFromURI(imageUri));
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = thumbnail;
                }
                //  profile_view.setImageBitmap(rotatedBitmap);
                Uri myUri = Uri.fromFile(new File(getActivity().getCacheDir(),"cropped"));

                Crop.of(imageUri, myUri).asSquare().start(getActivity(),settings_fragment_view.this);




            } catch (Exception e) {
                e.printStackTrace();
            }





/*
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profile_view.setImageBitmap(photo);
                */



        }
        else {
            //   Toast.makeText(getApplicationContext(), "No data!", Toast.LENGTH_SHORT).show();

                /*
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
               profImage = (BitmapFactory.decodeFile(picturePath));
               profile_view.setImageBitmap(profImage);
               */
                /*
                Uri uri=data.getData();
                String[]projection ={MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null,null);
                cursor.moveToFirst();
                int columnIndex= cursor.getColumnIndex(projection[0]);
                String filepath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap sele_image = BitmapFactory.decodeFile(filepath);
                Drawable d=  new BitmapDrawable(sele_image);
                profile_view.setBackground(d);
                */


        }
    }

    private void handleCrop(int code, Intent result)
    {

        if (code==getActivity().RESULT_OK) {
            //  profile_view.setImageURI(Crop.getOutput(result));

            Uri selectedImage = Crop.getOutput(result);
            /*
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
*/
            new_pic_selected=true;

            try {

                /*
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), selectedImage);
                //  Bitmap rotatedBitmap = rotateImage(thumbnail, -90);
                // profile_view.setImageBitmap(rotatedBitmap);
                // String imageurl = getRealPathFromURI(imageUri);
                // profile_view.setImageURI(imageurl);
                ExifInterface ei = new ExifInterface(Crop.getOutput(result).getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = thumbnail;
                }
                */
                //
                //  profile_view.setImageBitmap(rotatedBitmap);



                Glide.with(this).load(selectedImage).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(prof_image_view);


                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }
                builder.setTitle(getResources().getString(R.string.confirm_string))
                        .setMessage(getResources().getString(R.string.profile_photo_alert_dialog))
                        .setPositiveButton(getResources().getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                dialog.dismiss();
                                if (get_activity_state() && is_connected()) {
                                    // continue with delete
                                    save_image_to_db();

                                }
                                else {
                                   if (getActivity()!=null && get_activity_state())
                                   {
                                       ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                                   }
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no_string), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                                new_pic_selected=false;
                                dialog.dismiss();
                            }
                        }).show();



            } catch (Exception e) {
                // profile_view.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                if (get_activity_state()) {
                    Toast.makeText(getActivity(), "Exception", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }



        }
        else {
            if (get_activity_state()) {
                Toast.makeText(getActivity(), "Error crop", Toast.LENGTH_SHORT).show();

                Log.d(TAG, "Error in Cropping");
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void save_image_to_db()
    {


        if (mAuth!=null && mAuth.getCurrentUser()!=null) {
                                    /*
                                    profile_view.setDrawingCacheEnabled(true);
                                    profile_view.buildDrawingCache();
                                    */


            if (mAuth.getCurrentUser().getPhotoUrl() == null || new_pic_selected == true) {

                try {
                    Bitmap bitmap = ((BitmapDrawable) prof_image_view.getDrawable()).getBitmap();
                    //  Bitmap bitmap = ((GlideBitmapDrawable) profile_view.getDrawable().getCurrent()).getBitmap();
                    // Bitmap bitmap = profile_view.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    StorageReference storageReference = firebaseStorage.getReference();
                    StorageReference prof_reference = storageReference.child(mAuth.getCurrentUser().getUid());
                    start_bar();
                    UploadTask uploadTask = prof_reference.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                           // Toast.makeText(getActivity(), "Error 1", Toast.LENGTH_SHORT).show();
                            error_bar_new();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            UserProfileChangeRequest myRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl).build();
                            mAuth.getCurrentUser().updateProfile(myRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        new_pic_selected = false;
                                        error_bar_new();



                                        Toast.makeText(getActivity(), getResources().getString(R.string.image_saved_string), Toast.LENGTH_SHORT).show();
                                        // Intent i = new Intent(Main3Activity.this,Main4Activity.class);
                                        // startActivity(i);



                                    } else {
                                        error_bar_new();
                                      //  Toast.makeText(getActivity(), "Error 2", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                            Log.d(TAG, downloadUrl.toString());
                        }
                    });

                }catch (Exception e)
                {
                   // Toast.makeText(getActivity(),"Error getting Bitmap",Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }



            }
            else{
                error_bar_new();
            }
        }
        else {
            error_bar_new();
        }




    }

    public void error_bar_new()
    {

        try {
            progressBar.setVisibility(View.INVISIBLE);
            select_image.setClickable(true);
            sele_camera.setClickable(true);
        }catch (Exception e)
        {
            e.printStackTrace();
        }



    }
    public void start_bar()
    {
        try {
            sele_camera.setClickable(false);
            select_image.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

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
            ((home_activity)getActivity()).set_bottom_settings();
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


    private Task<String> delete_user(String text) {
        // Create the arguments to the callable function, which is just one string
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);

        return mFunctions
                .getHttpsCallable("delete_user")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.

                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }


    private void set_name(String text)
    {

        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) name_view.getLayoutParams();
        layoutParams.height= LinearLayout.LayoutParams.WRAP_CONTENT;
        name_view.setLayoutParams(layoutParams);
        name_view.setText(text);
    }

    private void remove_name()
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) name_view.getLayoutParams();
        layoutParams.height= 0;
        name_view.setLayoutParams(layoutParams);
    }


    private void set_nick(String text)
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) nick_view.getLayoutParams();
        layoutParams.height= LinearLayout.LayoutParams.WRAP_CONTENT;
        nick_view.setLayoutParams(layoutParams);
        nick_view.setText("@ " + text);
    }

    private void remove_nick()
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) nick_view.getLayoutParams();
        layoutParams.height= 0;
        nick_view.setLayoutParams(layoutParams);
    }


    private void set_location(String text)
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) loc_view.getLayoutParams();
        layoutParams.height= LinearLayout.LayoutParams.WRAP_CONTENT;
        loc_view.setLayoutParams(layoutParams);
        loc_view.setText(" " + text);
    }

    private void remove_location()
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) loc_view.getLayoutParams();
        layoutParams.height= 0;
        loc_view.setLayoutParams(layoutParams);
    }


    private void set_phone_view(String ph)
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) phone_view.getLayoutParams();
        layoutParams.height= LinearLayout.LayoutParams.WRAP_CONTENT;
        phone_view.setLayoutParams(layoutParams);
        phone_view.setText(ph);
    }

    private void remove_phone_view()
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams)phone_view.getLayoutParams();
        layoutParams.height= 0;
        phone_view.setLayoutParams(layoutParams);
    }

    private void set_email_view(String em)
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) email_view.getLayoutParams();
        layoutParams.height= LinearLayout.LayoutParams.WRAP_CONTENT;
        email_view.setLayoutParams(layoutParams);
            email_view.setText(em);
    }

    private void remove_email_view()
    {
        LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) email_view.getLayoutParams();
        layoutParams.height= 0;
        email_view.setLayoutParams(layoutParams);
    }


    private void check_for_auth_type()
    {

        if (get_activity_state()&&is_connected()) {
            databaseReference.child("users_auth").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (get_activity_state()) {
                        if (dataSnapshot.hasChild("type")) {
                            if (dataSnapshot.child("type").getValue().toString().equals("email")) {
                                if (dataSnapshot.hasChild("email") && !dataSnapshot.child("email").getValue().toString().equals("")) {
                                    send_email_for_forget_password(dataSnapshot.child("email").getValue().toString());
                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_retrieving_email), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.password_cant_be_changed_for_accoutn_s_email), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_retrieving_email), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            if (getActivity()!=null)
            {
                ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
            }
        }


    }

    private void send_email_for_forget_password(String res_email)
    {



        if (get_activity_state() && is_connected()) {
            mAuth.sendPasswordResetEmail(res_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (get_activity_state()) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            change_password_button.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), getResources().getString(R.string.email_sent), Toast.LENGTH_SHORT).show();


                    /*

                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressBar.setVisibility(View.INVISIBLE);
                                change_password_button.setVisibility(View.VISIBLE);
                                sharedPreferences.edit().putString("token_set","false").apply();
                                mAuth.signOut();
                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                                change_password_button.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(),"Error, signing out",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    */


                        } else {
                            if (get_activity_state()) {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                change_password_button.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
        }
        else {
            if (getActivity()!=null)
            {
                ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
            }
        }






    }

    private void show_change_name_alert()
    {

        if (personal_data_snap!=null) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View mView = getActivity().getLayoutInflater().inflate(R.layout.update_email_alert_dialog_layout, null);
            final EditText updated_email = mView.findViewById(R.id.upated_email_home_top_view);
            TextView tit_view = mView.findViewById(R.id.top_title_emai_alert_text_view);

            tit_view.setText(getResources().getString(R.string.please_update_your_full_name));

            if (personal_data_snap.hasChild("full_name")) {
                current_name =personal_data_snap.child("full_name").getValue().toString();
                updated_email.setHint(current_name);
            }
            ConstraintLayout dialog_cancel_button = mView.findViewById(R.id.cancel_button_email_alert_dialog);
            ConstraintLayout dialog_update_button = mView.findViewById(R.id.confirm_button_email_alert_dialog);

            builder.setView(mView);
            final AlertDialog dialog = builder.create();


            dialog_update_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String up_name = updated_email.getText().toString().trim();

                    if (!(updated_email.getText().toString().trim().equals("")) && !up_name.equals(current_name) ) {


                        dialog.dismiss();
                        if (is_connected()) {



                            progressBar.setVisibility(View.VISIBLE);
                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("full_name").setValue(up_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (get_activity_state()) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.name_changed), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            get_profile_data();
                                        } else {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                        } else {
                            if (get_activity_state()) {
                                ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                            }
                        }

                    } else {
                        if (get_activity_state()) {
                            if (up_name.equals("")) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.name_cant_be_blank), Toast.LENGTH_SHORT).show();
                            } else if (up_name.equals(current_name)) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.same_name), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });




            dialog_cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
          //  builder.setTitle("Horrible Friends").setMessage("Please update your full name.");
          //  builder.setIcon(R.drawable.logo_icon_horrbile_very_small);


            /*
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    final String up_name = updated_email.getText().toString().trim();

                    if (!(updated_email.getText().toString().trim().equals("")) && !up_name.equals(current_name) ) {


                        if (is_connected()) {
                            dialog.dismiss();


                            progressBar.setVisibility(View.VISIBLE);
                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("full_name").setValue(up_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getActivity(), "Name changed!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        get_profile_data();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (up_name.equals("")) {
                            Toast.makeText(getActivity(), "Name can't be blank!", Toast.LENGTH_SHORT).show();
                        } else if (up_name.equals(current_name)) {
                            Toast.makeText(getActivity(), "Same Name!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            */




        }




    }

    private void show_change_nick_alert()
    {

        if (personal_data_snap!=null) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View mView = getActivity().getLayoutInflater().inflate(R.layout.update_email_alert_dialog_layout, null);
            final EditText updated_email = mView.findViewById(R.id.upated_email_home_top_view);
            TextView tit_view = mView.findViewById(R.id.top_title_emai_alert_text_view);

            tit_view.setText(getResources().getString(R.string.please_up_your_nick_name));

            if (personal_data_snap.hasChild("name")) {
                current_name =personal_data_snap.child("name").getValue().toString();
                updated_email.setHint(current_name);
            }

            ConstraintLayout dialog_cancel_button = mView.findViewById(R.id.cancel_button_email_alert_dialog);
            ConstraintLayout dialog_update_button = mView.findViewById(R.id.confirm_button_email_alert_dialog);

            builder.setView(mView);
            final AlertDialog dialog = builder.create();


            dialog_update_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String up_name = updated_email.getText().toString().trim();




                    if (!(updated_email.getText().toString().trim().equals("")) && !up_name.equals(current_name) ) {

                        dialog.dismiss();
                        if (is_connected()) {



                            progressBar.setVisibility(View.VISIBLE);



                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {



                                            boolean found =false;
                                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                                            {
                                                for (DataSnapshot dataSnapshot2:dataSnapshot1.getChildren())
                                                {
                                                    if (dataSnapshot2.getKey().equals("name"))
                                                    {
                                                        if (dataSnapshot2.getValue().equals(up_name))
                                                        {
                                                            found=true;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (found)
                                                {
                                                    break;
                                                }
                                            }

                                            if (!found)
                                            {
                                                if (get_activity_state())
                                                {


                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("name").setValue(up_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (get_activity_state()) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(getActivity(), getResources().getString(R.string.nick_changed), Toast.LENGTH_SHORT).show();
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            get_profile_data();
                                                                        } else {
                                                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });



                                                }


                                            }else {
                                                if (get_activity_state()) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.nick_already_exitsts), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }




                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            if (get_activity_state())
                                            {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getActivity(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                });

                                            }
                                        }
                                    });



                                }
                            }).start();




                        } else {
                            if (getActivity()!=null) {
                                ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                            }
                        }

                    } else {
                        if (get_activity_state()) {
                            if (up_name.equals("")) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.nick_cant_be_blank), Toast.LENGTH_SHORT).show();
                            } else if (up_name.equals(current_name)) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.same_nick), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                }
            });




            dialog_cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();











            /*



            builder.setTitle("Horrible Friends");

            builder.setIcon(R.drawable.logo_icon_horrbile_very_small);
            builder.setMessage("Please update your Nick name.");
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    final String up_name = updated_email.getText().toString().trim();

                    if (!(updated_email.getText().toString().trim().equals("")) && !up_name.equals(current_name) ) {


                        if (is_connected()) {
                            dialog.dismiss();


                            progressBar.setVisibility(View.VISIBLE);
                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("name").setValue(up_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getActivity(), "Nick changed!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        get_profile_data();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (up_name.equals("")) {
                            Toast.makeText(getActivity(), "Nick can't be blank!", Toast.LENGTH_SHORT).show();
                        } else if (up_name.equals(current_name)) {
                            Toast.makeText(getActivity(), "Same Nick!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setView(mView);
            final AlertDialog dialog = builder.create();
            dialog.show();
            */


        }




    }



    private void show_change_phone_alert() {


        if (get_activity_state() && is_connected())
        {

        databaseReference.child("users_auth").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (get_activity_state() && dataSnapshot.hasChild("type")) {
                    if (!dataSnapshot.child("type").getValue().toString().equals("phone_auth")) {

                        if (personal_data_snap != null) {


                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            View mView = getActivity().getLayoutInflater().inflate(R.layout.update_phone_alert_dialog_layout, null);
                            final EditText updated_email = mView.findViewById(R.id.phone_no_edit_text_update_alert);
                            final CountryCodePicker myCcp = mView.findViewById(R.id.ccp_update_alert);
                            updated_email.setFilters(new InputFilter[]{filter});
                            myCcp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                                @Override
                                public void onCountrySelected() {
                                    my_selected_city_code = myCcp.getSelectedCountryCode();
                                }
                            });
                            // updated_email.setInputType(InputType.TYPE_CLASS_NUMBER);


                            if (personal_data_snap.hasChild("phone") && !personal_data_snap.child("phone").getValue().toString().equals("")) {
                                current_phone = personal_data_snap.child("phone").getValue().toString();

                                StringBuilder my_str = new StringBuilder(current_phone);
                                StringBuilder new_str = new StringBuilder();
                                for (int i = 0; i < my_str.length(); i++) {
                                    if (my_str.charAt(0) == ' ') {

                                        my_str.deleteCharAt(0);
                                        break;
                                    } else {
                                        new_str.append(my_str.charAt(0));

                                        my_str.deleteCharAt(0);
                                    }
                                }
                                if (new_str != null && new_str.length() > 0) {
                                    myCcp.setCountryForPhoneCode(Integer.valueOf(new_str.toString()));
                                }
                                // myCcp.resetToDefaultCountry();
                                if (my_str != null && my_str.length() > 0) {
                                    current_phone = my_str.toString();
                                    updated_email.setHint(current_phone);
                                }
                            }


                            ConstraintLayout dialog_cancel_button = mView.findViewById(R.id.cancel_button_phone_alert_dialog);
                            ConstraintLayout dialog_update_button = mView.findViewById(R.id.confirm_button_phone_alert_dialog);
                            TextView tit_view = mView.findViewById(R.id.top_title_phone_alert_text_view);

                            tit_view.setText(getResources().getString(R.string.please_up_your_phone));

                            builder.setView(mView);
                            final AlertDialog dialog = builder.create();


                            dialog_update_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String up_name = updated_email.getText().toString().trim();

                                    if (!(updated_email.getText().toString().trim().equals("")) && !up_name.equals(current_phone)) {

                                        dialog.dismiss();
                                        if (is_connected()) {


                                            progressBar.setVisibility(View.VISIBLE);

                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("phone").setValue("+" + my_selected_city_code + " " + updated_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (get_activity_state()) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.phone_num_changed), Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            get_profile_data();
                                                        } else {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });

                                        } else {
                                            if (get_activity_state()) {
                                                ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                                            }
                                        }

                                    } else {
                                        if (get_activity_state()) {
                                            if (up_name.equals("")) {
                                                Toast.makeText(getActivity(), getResources().getString(R.string.phoen_no_cant_be_blank), Toast.LENGTH_SHORT).show();
                                            } else if (up_name.equals(current_phone)) {
                                                Toast.makeText(getActivity(), getResources().getString(R.string.same_phone_num), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }


                                }
                            });


                            dialog_cancel_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();

























                                /*
                                builder.setTitle("Horrible Friends");

                                builder.setIcon(R.drawable.logo_icon_horrbile_very_small);
                                builder.setMessage("Please update your Phone no.");
                                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        final String up_name = updated_email.getText().toString().trim();

                                        if (!(updated_email.getText().toString().trim().equals("")) && !up_name.equals(current_phone) ) {


                                            if (is_connected()) {
                                                dialog.dismiss();


                                                progressBar.setVisibility(View.VISIBLE);

                                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("phone").setValue( "+" + my_selected_city_code + " " + updated_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            Toast.makeText(getActivity(), "Phone no. changed!", Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            get_profile_data();
                                                        }
                                                        else {
                                                            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            if (up_name.equals("")) {
                                                Toast.makeText(getActivity(), "Phone no. can't be blank!", Toast.LENGTH_SHORT).show();
                                            } else if (up_name.equals(current_phone)) {
                                                Toast.makeText(getActivity(), "Same Phone no!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setView(mView);
                                final AlertDialog dialog = builder.create();
                                dialog.show();
*/

                        }

                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.phone_num_cant_change_for_phone_auth), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_retrieving_phone_num), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    else {
            if (get_activity_state())
            {
                ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
            }
        }





    }


    private void show_change_city_alert()
    {

        if (personal_data_snap!=null) {


            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View mView = getActivity().getLayoutInflater().inflate(R.layout.update_email_alert_dialog_layout, null);
            final EditText updated_email = mView.findViewById(R.id.upated_email_home_top_view);
            TextView tit_view = mView.findViewById(R.id.top_title_emai_alert_text_view);

            tit_view.setText(getResources().getString(R.string.please_up_your_city));

            if (personal_data_snap.hasChild("City")) {
                current_city =personal_data_snap.child("City").getValue().toString();
                updated_email.setHint(current_city);
            }

            ConstraintLayout dialog_cancel_button = mView.findViewById(R.id.cancel_button_email_alert_dialog);
            ConstraintLayout dialog_update_button = mView.findViewById(R.id.confirm_button_email_alert_dialog);

            builder.setView(mView);
            final AlertDialog dialog = builder.create();


            dialog_update_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String up_name = updated_email.getText().toString().trim();

                    if (!(updated_email.getText().toString().trim().equals("")) && !up_name.equals(current_city) ) {

                        dialog.dismiss();
                        if (is_connected()) {



                            progressBar.setVisibility(View.VISIBLE);
                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("City").setValue(up_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (get_activity_state()) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.city_changed), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            get_profile_data();
                                        } else {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                        } else {
                            if (get_activity_state()) {
                                ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                            }
                        }

                    } else {
                        if (get_activity_state()) {
                            if (up_name.equals("")) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.city_cant_blank), Toast.LENGTH_SHORT).show();
                            } else if (up_name.equals(current_phone)) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.same_city), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });




            dialog_cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

            /*

            builder.setTitle("Horrible Friends");

            builder.setIcon(R.drawable.logo_icon_horrbile_very_small);
            builder.setMessage("Please update your City");
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    final String up_name = updated_email.getText().toString().trim();

                    if (!(updated_email.getText().toString().trim().equals("")) && !up_name.equals(current_city) ) {


                        if (is_connected()) {
                            dialog.dismiss();


                            progressBar.setVisibility(View.VISIBLE);
                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("City").setValue(up_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getActivity(), "City changed!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        get_profile_data();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (up_name.equals("")) {
                            Toast.makeText(getActivity(), "City can't be blank!", Toast.LENGTH_SHORT).show();
                        } else if (up_name.equals(current_phone)) {
                            Toast.makeText(getActivity(), "Same City!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setView(mView);
            final AlertDialog dialog = builder.create();
            dialog.show();

            */


        }




    }


    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }

    };

    private void remove_token()
    {
        if (mAuth!=null && mAuth.getCurrentUser()!=null && is_connected())
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (get_activity_state()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            sharedPreferences.edit().putString("token_set", "false").apply();
                            mAuth.signOut();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        else{
            if (get_activity_state())
            {
                ((home_activity)getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection),true);
            }
        }

    }

    private void check_verification()
    {
        if (mAuth!=null && mAuth.getCurrentUser()!=null && !(mAuth.getCurrentUser().isEmailVerified())) {
            if (is_connected())
            {

                databaseReference.child("users_auth").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (get_activity_state())
                        {
                            if (dataSnapshot.hasChild("type")) {
                                if (dataSnapshot.child("type").getValue().toString().equals("email")) {
                                    final String current_email = dataSnapshot.child("email").getValue().toString();
                                    veri_em_text_view.setText(getResources().getString(R.string.please_verify_your_email)+" (" + dataSnapshot.child("email").getValue().toString() + "), "+ getResources().getString(R.string.with_the_link_set));
                                    resend_email.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mAuth != null && mAuth.getCurrentUser() != null) {
                                                mAuth.getCurrentUser().sendEmailVerification().addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // error_bar();

                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {


                                                            Toast.makeText(getActivity(), getResources().getString(R.string.email_sent_please_verify), Toast.LENGTH_SHORT).show();


                                                        } else {

                                                            // error_bar();
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_sending_email), Toast.LENGTH_SHORT).show();


                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    update_email.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            View mView = getActivity().getLayoutInflater().inflate(R.layout.update_email_alert_dialog_layout, null);
                                            final EditText updated_email = mView.findViewById(R.id.upated_email_home_top_view);
                                            ConstraintLayout dialog_cancel_button = mView.findViewById(R.id.cancel_button_email_alert_dialog);
                                            ConstraintLayout dialog_update_button = mView.findViewById(R.id.confirm_button_email_alert_dialog);
                                            TextView tit_view = mView.findViewById(R.id.top_title_emai_alert_text_view);
                                            updated_email.setHint(current_email);
                                            // builder.setTitle("Horrible Friends");

                                            tit_view.setText(getResources().getString(R.string.please_update_your_email_address));
                                            // builder.setIcon(R.drawable.logo_icon_horrbile_very_small);
                                            //  builder.setMessage("Please update your email address.");
                                            builder.setView(mView);
                                            final AlertDialog dialog = builder.create();

                                            dialog_update_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    final String up_email = updated_email.getText().toString().trim();

                                                    if (!(updated_email.getText().toString().trim().equals("")) && !up_email.equals(current_email) && isEmailValid(up_email)) {


                                                        if (is_connected()) {
                                                            dialog.dismiss();


                                                            progressBar.setVisibility(View.VISIBLE);

                                                            databaseReference.child("users_auth").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                                    if (get_activity_state()) {
                                                                        boolean dup_check = false;
                                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                                            if (dataSnapshot1.hasChild("email")) {
                                                                                if (dataSnapshot1.child("email").getValue().toString().equals(up_email)) {
                                                                                    dup_check = true;
                                                                                    break;
                                                                                }
                                                                            }
                                                                        }
                                                                        if (dup_check) {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            Toast.makeText(getActivity(), getResources().getString(R.string.email_already_exists), Toast.LENGTH_SHORT).show();
                                                                        } else {


                                                                            Map<String, Object> data = new HashMap<>();
                                                                            data.put("uid", current_uid);
                                                                            data.put("email", up_email);


                                                                            mFunctions.getHttpsCallable("update_email")
                                                                                    .call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        if (get_activity_state()) {
                                                                                            // Log.d(TAG, task.getResult().getData().toString());
                                                                                            progressBar.setVisibility(View.INVISIBLE);

                                                                                            // email updated




                                                                                            Toast.makeText(getActivity(), getResources().getString(R.string.email_up_successfully), Toast.LENGTH_SHORT).show();
                                                                                            send_password_reset_email();
                                                                                        }

                                                                                    } else {
                                                                                        if (get_activity_state()) {
                                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });









                                                                    /*

                                                                    mAuth.getCurrentUser().updateEmail(up_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                // email updated
                                                                                Toast.makeText(getActivity(), "Email updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                check_verification();
                                                                            } else {
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                // task failed
                                                                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });
                                                                    */


                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    if (get_activity_state()) {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        Toast.makeText(getActivity(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    // error
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                                        }

                                                    } else {
                                                        if (up_email.equals("")) {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.email_cant_be_empty), Toast.LENGTH_SHORT).show();
                                                        } else if (!isEmailValid(up_email)) {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_a_valid_email), Toast.LENGTH_SHORT).show();
                                                        } else if (up_email.equals(current_email)) {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.same_email_address), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });




                                            dialog_cancel_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            dialog.show();







                                        /*

                                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                                final String up_email = updated_email.getText().toString().trim();

                                                if (!(updated_email.getText().toString().trim().equals("")) && !up_email.equals(current_email) && isEmailValid(up_email)) {


                                                    if (is_connected()) {
                                                        dialog.dismiss();


                                                        progressBar.setVisibility(View.VISIBLE);

                                                        databaseReference.child("users_auth").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                if (get_activity_state()) {
                                                                    boolean dup_check = false;
                                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                                        if (dataSnapshot1.hasChild("email")) {
                                                                            if (dataSnapshot1.child("email").getValue().toString().equals(up_email)) {
                                                                                dup_check = true;
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (dup_check) {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        Toast.makeText(getActivity(), "Email already exists!", Toast.LENGTH_SHORT).show();
                                                                    } else {


                                                                        Map<String, Object> data = new HashMap<>();
                                                                        data.put("uid", current_uid);
                                                                        data.put("email", up_email);


                                                                        mFunctions.getHttpsCallable("update_email")
                                                                                .call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    if (get_activity_state()) {
                                                                                        // Log.d(TAG, task.getResult().getData().toString());
                                                                                        progressBar.setVisibility(View.INVISIBLE);

                                                                                        // email updated




                                                                                        Toast.makeText(getActivity(), "Email updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                        send_password_reset_email();
                                                                                    }

                                                                                } else {
                                                                                    if (get_activity_state()) {
                                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                                        Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }
                                                                        });












                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                if (get_activity_state()) {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                                                }
                                                                // error
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {
                                                    if (up_email.equals("")) {
                                                        Toast.makeText(getActivity(), "Email can't be empty!", Toast.LENGTH_SHORT).show();
                                                    } else if (!isEmailValid(up_email)) {
                                                        Toast.makeText(getActivity(), "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                                                    } else if (up_email.equals(current_email)) {
                                                        Toast.makeText(getActivity(), "Same Email Address!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        */





                                        }
                                    });

                                    LinearLayout.LayoutParams LayoutParams = (LinearLayout.LayoutParams) un_veri_layout.getLayoutParams();
                                    LayoutParams.height = LayoutParams.WRAP_CONTENT;
                                    un_veri_layout.setLayoutParams(LayoutParams);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//cancelled
                    }
                });
            }else {
                if (get_activity_state()) {
                    ((home_activity) getActivity()).show_snackbar(getResources().getString(R.string.no_internet_connection), true);
                }
            }
        }

        else {
            if (get_activity_state()) {
                LinearLayout.LayoutParams LayoutParams = (LinearLayout.LayoutParams) un_veri_layout.getLayoutParams();
                LayoutParams.height = 0;
                un_veri_layout.setLayoutParams(LayoutParams);
            }
        }

    }

    private void send_password_reset_email()
    {
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().sendEmailVerification().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    check_verification();
                    // error_bar();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    check_verification();

                    if (task.isSuccessful()) {


                        Toast.makeText(getActivity(), getResources().getString(R.string.email_sent_please_verify), Toast.LENGTH_SHORT).show();


                    } else {

                        // error_bar();
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_sending_email), Toast.LENGTH_SHORT).show();


                    }
                }
            });
        }
    }

    public static boolean isEmailValid(String email) {

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }


    public boolean check_storage_permission()
    {

        if (getActivity()!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                // permissionCheck +=this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

                // Toast.makeText(this,String.valueOf(permissionCheck),Toast.LENGTH_SHORT).show();
                // if(permissionCheck!=0){
                if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   return false;
                } else {
                  return true;
                }
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }

    }




}





