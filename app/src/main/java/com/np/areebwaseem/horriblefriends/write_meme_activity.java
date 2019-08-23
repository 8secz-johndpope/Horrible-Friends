package com.np.areebwaseem.horriblefriends;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class write_meme_activity extends AppCompatActivity {
    private static final String TAG = "write_meme_activity";



    String send_to_uid;
    FirebaseAuth mAuth;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;
     TextView disp_meme_text_view;
     EditText write_meme_edit_text;
    ImageButton send_meme_button;
    View parentLayout;
   volatile boolean is_running;
   CircleImageView profile_view;
   TextView name_view_text_view;
   TextView nick_text_view;
   String to_name;
   String to_nick;
    private Toolbar mTopToolbar;
    TextView char_left_view;
    ProgressBar mProgressbar;
    volatile boolean is_activity_running;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_meme_activity);
        parentLayout = findViewById(android.R.id.content);

        disp_meme_text_view = findViewById(R.id.write_meme_text_view_message_list_item);
      write_meme_edit_text = findViewById(R.id.write_meme_edit_text);
   send_meme_button = findViewById(R.id.send_meme_button);
   profile_view= findViewById(R.id.profile_pic_to_user_write_meme_activity);
   name_view_text_view = findViewById(R.id.write_meme_to_name_text_view);
   nick_text_view= findViewById(R.id.write_meme_to_nick_text_view);
   char_left_view = findViewById(R.id.char_left_count);
   mProgressbar = findViewById(R.id.progressBar_write_meme);

   is_activity_running=true;



        mTopToolbar = findViewById(R.id.write_meme_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTopToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

   /*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(write_meme_activity.this, R.color.yellow_700));
        }
        */


        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        is_running=true;



        if (savedInstanceState==null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras ==null)
            {
                send_to_uid= null;
                to_name=null;
                to_nick=null;
            }
            else {
                send_to_uid = extras.getString("selected_user");
                to_name = extras.getString("selected_user_name");
                to_nick = extras.getString("selected_user_nick");
            }
        }
        else {
            send_to_uid = (String) savedInstanceState.getSerializable("selected_user");
           to_name = (String) savedInstanceState.getSerializable("selected_user_name");
            to_nick = (String) savedInstanceState.getSerializable("selected_user_nick");
        }




        write_meme_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    disp_meme_text_view.setText(write_meme_edit_text.getText().toString());
                    char_left_view.setText(s.length() + "/140");

            /*
            if (s.length()>=140)
            {
                Toast.makeText(getApplicationContext(),"Maximum limit reached",Toast.LENGTH_LONG).show();
            }
            */
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send_meme_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth!=null && mAuth.getCurrentUser()!=null && is_running && send_to_uid!=null )
                {


                    if (!disp_meme_text_view.getText().toString().trim().equals("")) {

                        if (is_connected()) {
                            try {
                                send_meme_button.setClickable(false);
                                mProgressbar.setVisibility(View.VISIBLE);
                                DatabaseReference find_user = databaseReference.child("users");
                                find_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (get_activity_state())
                                        {

                                            try {
                                                if (dataSnapshot.hasChild(send_to_uid)) {
                                                  if ( dataSnapshot.child(send_to_uid).hasChild("full_name")) {
                                                      write_message(send_to_uid, dataSnapshot.child(send_to_uid).child("full_name").getValue().toString());
                                                  }else {
                                                      write_message(send_to_uid,"");
                                                  }
                                                } else {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                    mProgressbar.setVisibility(View.INVISIBLE);
                                                    setResult(Activity.RESULT_CANCELED);
                                                    finish();
                                                    // user not found
                                                }
                                            }catch (Exception e)
                                            {
                                                send_meme_button.setClickable(true);
                                                mProgressbar.setVisibility(View.INVISIBLE);
                                                e.printStackTrace();
                                            }

                                    }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        if (get_activity_state()) {
                                            try {
                                                mProgressbar.setVisibility(View.INVISIBLE);
                                                send_meme_button.setClickable(true);
                                            }catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }

                                        }

                                    }
                                });
                            }catch (Exception e)
                            {
                                send_meme_button.setClickable(true);
                                mProgressbar.setVisibility(View.INVISIBLE);
                                e.printStackTrace();
                            }

                        }
                        else {
                            show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                        }
                    }
                    else {
                       show_snackbar(getResources().getString(R.string.message_cant_be_empty), false);
                    }
                }
                else {
                    if (get_activity_state())
                    {
                        try {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        try {
            if (to_name!=null) {
                name_view_text_view.setText(to_name);
            }
            if (to_nick!=null)
            {
                nick_text_view.setText(to_nick);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }






        final StorageReference storageReference_one = firebaseStorage.getReference();


        if (mAuth != null && mAuth.getCurrentUser() != null && send_to_uid!=null) {

            final String curr_uid = send_to_uid;

            StorageReference prof_reference_one = storageReference_one.child(curr_uid);

            try {
                Glide.with(getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(storageReference_one.child(curr_uid)).error(R.drawable.asset_four).diskCacheStrategy(DiskCacheStrategy.NONE).into(profile_view);
            }catch (Exception e)
            {
                e.printStackTrace();
            }






            /*
            prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(getApplicationContext())
                                                .using(new FirebaseImageLoader())
                                                .load(storageReference_one.child(curr_uid)).into(profile_view);
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });


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
        else {
            try {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_activity_running=false;
        is_running=false;
    }

    public void write_message(String uid, String name)
    {
        try {
            if (get_activity_state()) {
                if (is_connected()) {
                    Map<String, Object> taskMap = new HashMap<>();
                    String selected_user_uid = uid;
                    taskMap.put("to", selected_user_uid);
                    taskMap.put("from", mAuth.getCurrentUser().getUid());
                    taskMap.put("to_name", name);
                    taskMap.put("message", disp_meme_text_view.getText().toString());
                    taskMap.put("timestamp", ServerValue.TIMESTAMP);
                    DatabaseReference new_message_reference = databaseReference.child("messages");
                    new_message_reference.push().setValue(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            try {
                            if (get_activity_state()) {
                                if (task.isSuccessful()) {
                                    mProgressbar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sent_string), Toast.LENGTH_SHORT).show();
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                    // close with positive result
                                } else {
                                    send_meme_button.setClickable(true);
                                    mProgressbar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_LONG).show();
                                    // un successful message
                                }
                            }
                        }catch (Exception e)
                            {
                                e.printStackTrace();
                            }


                        }
                    });
                }
                else {
                    mProgressbar.setVisibility(View.INVISIBLE);
                    send_meme_button.setClickable(true);
                show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                }
            }
        }catch (Exception e)
        {
            send_meme_button.setClickable(true);
            mProgressbar.setVisibility(View.INVISIBLE);
            e.printStackTrace();
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
                mView.setBackgroundColor(ContextCompat.getColor(write_meme_activity.this, R.color.colorPrimaryDark));
                TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
                mTextView.setTextColor(ContextCompat.getColor(write_meme_activity.this, R.color.white));
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

    private boolean get_activity_state() {

        if (mAuth != null && mAuth.getCurrentUser() != null && is_activity_running ) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
