package com.np.areebwaseem.horriblefriends;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceIDSer";
    private String token_broadcast = "fcmtokenbroadcast";
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
       /// String refreshedToken = FirebaseInstanceId.getInstance().getToken();
       // Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
       //sendRegistrationToServer(refreshedToken);
       // getApplicationContext().sendBroadcast(new Intent(token_broadcast));
      //  storeToken(refreshedToken);
        sharedPreferences = this.getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);

        mAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences.edit().putString("token_set", "false").apply();
        store_token();
        Log.d(TAG,"Token Refreshed");

    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    private void storeToken(String token)
    {
        SharedprefManager.getInstance(getApplicationContext()).storeToken(token);
    }

    private void store_token()
    {
        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            /*
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful())
                    {

                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue(task.getResult().getToken()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sharedPreferences.edit().putString("token_set","true").apply();
                            }
                        });
                    }else {

                    }
                }

            });
            */
            String curr_token = FirebaseInstanceId.getInstance().getToken();
            if (curr_token!=null) {
                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue(curr_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences.edit().putString("token_set", "true").apply();
                        }
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(),"Null token",Toast.LENGTH_SHORT).show();
            }
        }


    }




}
