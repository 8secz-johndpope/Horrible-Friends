package com.np.areebwaseem.horriblefriends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class sign_in_email_activity extends AppCompatActivity {
    private static final String TAG = "sign_in_email_activity";
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    ProgressBar myBar;
    Button signUpbutton;
    boolean facebook_mAuth_found_bool=false;
    boolean isFacebook=false;
    boolean type_set=false;
    int email_child_count=0;
   // int initial_child_count=0;
    boolean email_mAuth_found_bool=false;
    boolean email_child_check =false;
    boolean mAuth_found_bool=false;
    boolean twitter_pressed_check =false;
    boolean facebook_pressed_check = false;
    boolean email_pressed_check = false;
    EditText email_text;
    EditText passwordText;
    TextView bac_txt;
    ImageView my_back_im;
    View parentLayout;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_email_activity);
        parentLayout = findViewById(android.R.id.content);

        myBar= findViewById(R.id.progress_bar_signup_email);
        email_text = findViewById(R.id.login_email_edit_text_act);
        passwordText= findViewById(R.id.login_password_edit_text_act);
        bac_txt = findViewById(R.id.back_login_active_sin_up);
        my_back_im = findViewById(R.id.imageView7_back_login_email_activ);



        signUpbutton = findViewById(R.id.login_button_active_act);

        sharedPreferences = this.getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        myBar.setVisibility(View.INVISIBLE);

        myBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);








        databaseReference  = FirebaseDatabase.getInstance().getReference();




        bac_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });






        signUpbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connMgr.getActiveNetworkInfo();


                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (networkInfo != null && networkInfo.isConnected()) {
                    start_bar();

                    //   if (initial_child_count!=0) {
                    final String email = email_text.getText().toString().trim();
                    final String password = passwordText.getText().toString().trim();
                    Log.d(TAG, "Outside!");
                    if (mAuth != null && mAuth.getCurrentUser() == null) {


                        if (!email.equals("") && !password.equals("")) {
                            Log.d(TAG, "Inside!");
                            final DatabaseReference myRef = databaseReference.child("users_auth");
                            email_mAuth_found_bool = false;
                            email_pressed_check = false;
                            email_child_count = 0;


                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                                        if (email_pressed_check == false) {
                                            {

                                                boolean child_check = false;
                                                String curr_email = "";
                                                email_child_count = email_child_count + 1;
                                                for (DataSnapshot dsp : snapshot.getChildren()) {


                                                    if (dsp.getKey().equals("email")) {
                                                        curr_email = dsp.getValue().toString();
                                                        Log.d(TAG, curr_email);
                                                        if (curr_email.equals(email)) {
                                                            Log.d(TAG, "Email matched!");
                                                            email_mAuth_found_bool = true;
                                                            email_child_check = true;
                                                        } else {
                                                            Log.d(TAG, "Mismatched");
                                                            email_child_check = false;
                                                        }
                                                    } else if (dsp.getKey().equals("phone")) {
                                                        email_child_check = false;
                                                    } else if (dsp.getKey().equals("type")) {
                                                        if (email_child_check) {
                                                            if (dsp.getValue().toString().equals("email")) {
                                                                //send_email_for_forget_password(curr_email);
                                                                error_bar();
                                                                show_snackbar(getResources().getString(R.string.you_are_already_signedup_sign_in), true);
                                                                // Toast.makeText(getApplicationContext(), "You are already signed up, Please sign in!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                error_bar();
                                                                show_snackbar(getResources().getString(R.string.cant_sign_up_your_account_is_signed_up_with) + dsp.getValue(), true);
                                                                // Toast.makeText(getApplicationContext(), "Can't Sign Up, your account is signed up with: " + dsp.getValue(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                }


                                                if (email_child_count == dataSnapshot.getChildrenCount()) {
                                                    Log.d(TAG, "Child count greater than number of children!");
                                                    if (!email_mAuth_found_bool) {


                                                        mAuth.createUserWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                //   Toast.makeText(getApplicationContext(), "Failure",Toast.LENGTH_SHORT).show();
                                                                error_bar();
                                                            }
                                                        }).addOnCompleteListener(sign_in_email_activity.this, new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    email_pressed_check = true;
                                                                    Log.d(TAG, "Successfully signed up");
                                                                    if (mAuth != null && mAuth.getCurrentUser() != null) {

                                                                        mAuth.getCurrentUser().sendEmailVerification().addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                error_bar();
                                                                            }
                                                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {


                                                                                    email_text.setText("");
                                                                                    passwordText.setText("");
                                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_sent_please_verify), Toast.LENGTH_SHORT).show();
                                                    /*
                                                    Intent i = new Intent(MainActivity.this, Main2Activity.class);
                                                    startActivity(i);
                                                    */

                                                                                    db_account_type(email, "email");


                                                                                    sharedPreferences.edit().putString("email", email).apply();
                                                                                    sharedPreferences.edit().putString("password", password).apply();
                                                                                    sharedPreferences.edit().putString("type", "email").apply();
                                                                                } else {

                                                                                    error_bar();
                                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_sending_email), Toast.LENGTH_SHORT).show();


                                                                                }
                                                                            }
                                                                        });


                                                                    } else {
                                                                        error_bar();
                                                                    }


                                                                } else {
                                                                    error_bar();
                                                                    Log.d(TAG, "Sign up failed:");

                                                                    if (task.getException() != null) {

                                                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                                    }

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }


                                        } else {
                                            error_bar();
                                        }


                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    error_bar();

                                }
                            });















/*

                    //        myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    Log.d(TAG, "child changed");
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

                            */


                        } else {
                            error_bar();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fields_cant_be_empty), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        error_bar();
                    }
                /*
                else {
                    Toast.makeText(getApplicationContext(), "Sign out first", Toast.LENGTH_SHORT).show();
                }
                */
                    //  }
                }
                else {
                    show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();



        /*
        DatabaseReference my_new_Ref = databaseReference.child("users_auth");
        my_new_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data Changed!!!!!! outside");
                initial_child_count= Math.round(dataSnapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
    }


    private void db_account_type(String email_to_db, final String type_to_db)
    {
        Map<String, Object> taskMap = new HashMap<>();
        /*
        if (!nick_edit_text.getText().toString().equals("")) {
            taskMap.put("name", nick_edit_text.getText().toString());
        }
*/
        error_bar();

        if (mAuth!=null &&mAuth.getCurrentUser()!=null) {

            taskMap.put("email", email_to_db);
            taskMap.put("type", type_to_db);
            databaseReference.child("users_auth").child(mAuth.getCurrentUser().getUid()).setValue(taskMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    error_bar();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        type_set = true;

                        if (type_to_db.equals("email")) {

                            Intent i = new Intent(sign_in_email_activity.this, Main3Activity.class);
                            startActivity(i);
                        }
                        else {
                           // Intent i = new Intent(MainActivity.this, Main3Activity.class);
                           // startActivity(i);
                        }
                    } else {
                       // Toast.makeText(getApplicationContext(), "Error setting Type tp db", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        type_set = false;
                    }
                }
            });
        }






    }


    public void start_bar()
    {
        myBar.setVisibility(View.VISIBLE);
        signUpbutton.setClickable(false);
        bac_txt.setVisibility(View.INVISIBLE);
        my_back_im.setVisibility(View.INVISIBLE);

    }

    public void error_bar()
    {
        myBar.setVisibility(View.INVISIBLE);
        signUpbutton.setClickable(true);
        bac_txt.setVisibility(View.VISIBLE);
        my_back_im.setVisibility(View.VISIBLE);

    }
    public void show_snackbar(String txt, Boolean length)
    {
        if (parentLayout!=null) {
            Snackbar mSnackbar;
            if (length) {
                mSnackbar  = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_LONG);
            }
            else {
                mSnackbar = Snackbar.make(parentLayout, txt, Snackbar.LENGTH_SHORT);
            }

            View mView = mSnackbar.getView();
            mView.setBackgroundColor(ContextCompat.getColor(sign_in_email_activity.this, R.color.colorPrimaryDark));
            TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
            mTextView.setTextColor(ContextCompat.getColor(sign_in_email_activity.this, R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            else {
                mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            mSnackbar.show();

            // Snackbar.make(parentLayout, "Please wait!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }


}
