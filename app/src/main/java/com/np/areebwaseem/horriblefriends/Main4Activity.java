package com.np.areebwaseem.horriblefriends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class Main4Activity extends AppCompatActivity {
    private static final String TAG = "Main4Activity";
    Button delete_profile;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    Button sign_out_button;
    TextView profile_text_view;
    Button edit_profile_button;
    String user_name, user_city,user_email,user_phone,user_full_name = "";
    ProgressBar myBar;
    LinearLayout myLinear;
    volatile boolean is_activity_running;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        is_activity_running=true;

        delete_profile = findViewById(R.id.button2_delete_profile);
        sign_out_button = findViewById(R.id.button3_sign_out_profile);
        profile_text_view = findViewById(R.id.textView7_n_prof_view_txt);
        edit_profile_button = findViewById(R.id.button2_edit_profile);
        myLinear = findViewById(R.id.activ_main_4_linear_layo);
        myBar= findViewById(R.id.progress_bar_main_4_act);
        myBar.setVisibility(View.INVISIBLE);

        myBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        sharedPreferences = this.getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);



        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();





         DatabaseReference myRef=  databaseReference.child("users");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
if (mAuth!=null&& mAuth.getCurrentUser()!=null && is_activity_running) {
    if (dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {

        for (DataSnapshot dsp : dataSnapshot.getChildren()) {

            if (dsp.getKey().equals("name")) {
                user_name = dsp.getValue().toString();
            }
            if (dsp.getKey().equals("City")) {
                user_city = dsp.getValue().toString();
            }
            if (dsp.getKey().equals("Email")) {
                user_email = dsp.getValue().toString();
            }
            if (dsp.getKey().equals("phone")) {
                user_phone = dsp.getValue().toString();
            }
            if (dsp.getKey().equals("full_name"))
            {
                user_full_name = dsp.getValue().toString();
            }
        }


        setProfileData(user_name, user_email, user_city, user_phone,user_full_name);



    }

}
            }




            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (mAuth!=null && mAuth.getCurrentUser()!=null && is_activity_running) {
                    if (dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                            if (dsp.getKey().equals("name")) {
                                user_name = dsp.getValue().toString();
                            }
                            if (dsp.getKey().equals("City")) {
                                user_city = dsp.getValue().toString();
                            }
                            if (dsp.getKey().equals("Email")) {
                                user_email = dsp.getValue().toString();
                            }
                            if (dsp.getKey().equals("phone")) {
                                user_phone = dsp.getValue().toString();
                            }
                            if (dsp.getKey().equals("full_name"))
                            {
                                user_full_name = dsp.getValue().toString();
                            }
                        }


                        setProfileData(user_name, user_email, user_city, user_phone, user_full_name);
                    }
                }
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























        delete_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                    if (mAuth!=null &&mAuth.getCurrentUser() != null) {


                        final String my_currrent_uid = mAuth.getCurrentUser().getUid();

                        start_bar();
                        DatabaseReference auth_ref = databaseReference.child("users_auth");

                        auth_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // if (dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid()))


                                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                    databaseReference.child("users_auth").child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                DatabaseReference user_ref = databaseReference.child("users");
                                                user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {

                                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    error_bar();

                                                                                    //       databaseReference.child("users_auth").child(my_currrent_uid).removeValue();
                                                                                    //      databaseReference.child("users").child(my_currrent_uid).removeValue();

                                                                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                                                    if (sharedPreferences.getString("type", "none").equals("email")) {


                                                                                        sharedPreferences.edit().putString("email", "none").apply();
                                                                                        sharedPreferences.edit().putString("password", "none").apply();
                                                                                        sharedPreferences.edit().putString("type", "none").apply();
                                                                                        mAuth.signOut();
                                                                                        //finish();
                                                                                    } else {
                                                                                        sharedPreferences.edit().putString("type", "none").apply();
                                                                                        mAuth.signOut();
                                                                                        // finish();
                                                                                    }


                                                                                    // finish();
                                                                                } else {
                                                                                    error_bar();
                                                                                    if (task.getException().getMessage() != null) {
                                                                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                                                    } else {
                                                                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        error_bar();
                                                                        Toast.makeText(getApplicationContext(), "Error try again!", Toast.LENGTH_SHORT).show();

                                                                    }

                                                                }
                                                            });


                                                        } else {
                                                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        error_bar();

                                                                        //       databaseReference.child("users_auth").child(my_currrent_uid).removeValue();
                                                                        //      databaseReference.child("users").child(my_currrent_uid).removeValue();

                                                                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                                        if (sharedPreferences.getString("type", "none").equals("email")) {

                                                                            sharedPreferences.edit().putString("email", "none").apply();
                                                                            sharedPreferences.edit().putString("password", "none").apply();
                                                                            sharedPreferences.edit().putString("type", "none").apply();
                                                                            mAuth.signOut();
                                                                            //finish();
                                                                        } else {
                                                                            sharedPreferences.edit().putString("type", "none").apply();
                                                                            mAuth.signOut();
                                                                            // finish();
                                                                        }


                                                                        // finish();
                                                                    } else {
                                                                        error_bar();
                                                                        if (task.getException().getMessage() != null) {
                                                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }
                                                            });


                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
error_bar();

                                                    }
                                                });

                                            } else {
                                                error_bar();
                                                Toast.makeText(getApplicationContext(), "Error try again!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {

                                    DatabaseReference user_ref = databaseReference.child("users");
                                    user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {

                                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        error_bar();

                                                                        //       databaseReference.child("users_auth").child(my_currrent_uid).removeValue();
                                                                        //      databaseReference.child("users").child(my_currrent_uid).removeValue();

                                                                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                                        if (sharedPreferences.getString("type", "none").equals("email")) {

                                                                            sharedPreferences.edit().putString("email", "none").apply();
                                                                            sharedPreferences.edit().putString("password", "none").apply();
                                                                            sharedPreferences.edit().putString("type", "none").apply();
                                                                            mAuth.signOut();
                                                                            //finish();
                                                                        } else {
                                                                            sharedPreferences.edit().putString("type", "none").apply();
                                                                            mAuth.signOut();
                                                                            // finish();
                                                                        }


                                                                        // finish();
                                                                    } else {
                                                                        error_bar();
                                                                        if (task.getException().getMessage() != null) {
                                                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            error_bar();
                                                            Toast.makeText(getApplicationContext(), "Error try again!", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });


                                            } else {
                                                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            error_bar();

                                                            //       databaseReference.child("users_auth").child(my_currrent_uid).removeValue();
                                                            //      databaseReference.child("users").child(my_currrent_uid).removeValue();

                                                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                            if (sharedPreferences.getString("type", "none").equals("email")) {

                                                                sharedPreferences.edit().putString("email", "none").apply();
                                                                sharedPreferences.edit().putString("password", "none").apply();
                                                                sharedPreferences.edit().putString("type", "none").apply();
                                                                mAuth.signOut();
                                                                //finish();
                                                            } else {
                                                                sharedPreferences.edit().putString("type", "none").apply();
                                                                mAuth.signOut();
                                                                // finish();
                                                            }


                                                            // finish();
                                                        } else {
                                                            error_bar();
                                                            if (task.getException().getMessage() != null) {
                                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
   error_bar();
                                        }
                                    });


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
error_bar();
                            }
                        });


                    }
                    else {
                        finish();
                    }



            }
        });

        edit_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main4Activity.this, edit_profile_later_activity.class);
                startActivity(i);
            }
        });







        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    if (mAuth!=null && mAuth.getCurrentUser() != null) {

                        if (sharedPreferences.getString("type", "none").equals("email")) {

                            sharedPreferences.edit().putString("email", "none").apply();
                            sharedPreferences.edit().putString("password", "none").apply();
                            sharedPreferences.edit().putString("type", "none").apply();
                            mAuth.signOut();
                            //finish();
                        } else {
                            sharedPreferences.edit().putString("type", "none").apply();
                            mAuth.signOut();
                            // finish();
                        }
                    }
                    else {
                        finish();
                    }
            }
        });


        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null)
                {
                    finish();
                }
            }
        });






    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_activity_running=false;
    }

    private void setProfileData(String na, String em, String ci, String ph, String ful)
    {
        if (mAuth!=null && mAuth.getCurrentUser()!=null) {
            String emaili = mAuth.getCurrentUser().getEmail();
            profile_text_view.setText("Name: " + ful + "\n" + "Nick: " + na + "\n" + "Email: " + emaili + "\n" + "City: " + ci + "\n" + "Phone: " + ph);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            /*
            if (adapter!=null)
            {
                if (adapter.mFragmentTitleList.size()>=1) {
                    if (current_frag_index>0) {
                        setViewPager(adapter.mFragmentTitleList.get(current_frag_index - 1));
                    }

                }


            }
            */
            moveTaskToBack(true);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void start_bar()
    {
        myBar.setVisibility(View.VISIBLE);
        myLinear.setVisibility(View.INVISIBLE);
    }
    public void error_bar()
    {
        myBar.setVisibility(View.INVISIBLE);
        myLinear.setVisibility(View.VISIBLE);
    }










}
