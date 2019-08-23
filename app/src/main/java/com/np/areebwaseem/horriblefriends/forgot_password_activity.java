package com.np.areebwaseem.horriblefriends;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class forgot_password_activity extends AppCompatActivity {

    private static final String TAG = "forgot_password_activit";
    TextView back_text;
    Button forgot_password_button;
   EditText email_text;
    int child_count=0;
    DatabaseReference databaseReference;
    ProgressBar myBar;
    String type_from_db="";
    boolean check_auth_type=false;
  //  int initial_child_count=0;
    boolean mAuth_found_bool=false;

    private FirebaseAuth mAuth;
    View parentLayout;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_activity);
        parentLayout = findViewById(android.R.id.content);
        back_text = findViewById(R.id.back_forgot_pass_text);
        forgot_password_button = findViewById(R.id.forgot_password_active_forg_button);
        email_text = findViewById(R.id.forgot_email_edit_text);
        myBar = findViewById(R.id.progress_bar_forgot_password_active);
        myBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        databaseReference  = FirebaseDatabase.getInstance().getReference();
        back_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        myBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        forgot_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    if (!email_text.getText().toString().equals("")) {
                        myBar.setVisibility(View.VISIBLE);
                        forgot_password_button.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "buttom clicked");
                   /*
                   mAuth.sendPasswordResetEmail(email_text.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful())
                           {
                               Toast.makeText(getApplicationContext(), "Email Sent!", Toast.LENGTH_SHORT).show();
                           }
                           else {
                               Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                   */
                        check_for_auth_type(email_text.getText().toString());
                        check_auth_type = false;
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_field_cant_be_empty), Toast.LENGTH_LONG).show();
                    }
                }else {
                    show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                }
            }
        });



    }

    private void check_for_auth_type(final String user_email_to_check){




     //   if (initial_child_count!=0) {
            final DatabaseReference myRef = databaseReference.child("users_auth");
            mAuth_found_bool = false;

            child_count = 0;


            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren())
                    {

                        if (check_auth_type==false) {
                            boolean child_check = false;
                            String curr_email = "";
                            child_count = child_count + 1;
                            for (DataSnapshot dsp : snapshot.getChildren()) {

                                if (dsp.getKey().equals("email")) {
                                    curr_email = dsp.getValue().toString();
                                    Log.d(TAG, curr_email);
                                    if (curr_email.equals(user_email_to_check)) {
                                        Log.d(TAG, "Email matched!");
                                        mAuth_found_bool = true;
                                        child_check = true;
                                    } else {
                                        Log.d(TAG, "Mismatched");
                                        child_check = false;
                                    }
                                } else if (dsp.getKey().equals("type")) {
                                    if (child_check) {
                                        if (dsp.getValue().toString().equals("email")) {
                                            send_email_for_forget_password(curr_email);
                                        } else {
                                            show_snackbar(getResources().getString(R.string.cant_send_emai_your_account) +" " + dsp.getValue(),true);
                                           // Toast.makeText(getApplicationContext(), "Can't send Email, your account is signed up with: " + dsp.getValue(), Toast.LENGTH_SHORT).show();
                                            myBar.setVisibility(View.INVISIBLE);
                                            forgot_password_button.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }


                            if (child_count == dataSnapshot.getChildrenCount()) {
                                check_auth_type=true;
                                Log.d(TAG, "Child count greater than number of children!");
                                if (!mAuth_found_bool) {
                                    send_email_for_forget_password(user_email_to_check);
                                }
                            }

                        }











                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




















            /*

            myRef.addChildEventListener(new ChildEventListener() {
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


       // }
        /*
        else {
            myBar.setVisibility(View.INVISIBLE);

            forgot_password_button.setVisibility(View.VISIBLE);
        }
        */



    }

    private void send_email_for_forget_password(String res_email)
    {


        mAuth.sendPasswordResetEmail(res_email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                    myBar.setVisibility(View.INVISIBLE);
                    forgot_password_button.setVisibility(View.VISIBLE);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    myBar.setVisibility(View.INVISIBLE);
                    forgot_password_button.setVisibility(View.VISIBLE);
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
            mView.setBackgroundColor(ContextCompat.getColor(forgot_password_activity.this, R.color.colorPrimaryDark));
            TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
            mTextView.setTextColor(ContextCompat.getColor(forgot_password_activity.this, R.color.white));
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
