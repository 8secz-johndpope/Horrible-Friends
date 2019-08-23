package com.np.areebwaseem.horriblefriends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

public class Main2Activity extends AppCompatActivity {
     Button verify_email;
     Button send_code_again;
     SharedPreferences sharedPreferences;
     Button maybe_later_button;
     FirebaseAuth myAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sharedPreferences = this.getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);

        maybe_later_button = findViewById(R.id.maybe_later_button);


        myAuth = FirebaseAuth.getInstance();
        verify_email = findViewById(R.id.verify_email_button);
        send_code_again = findViewById(R.id.send_verification_code_button);


         verify_email.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (myAuth.getCurrentUser()!=null)
                 {
                     myAuth.signOut();
                     myAuth.signInWithEmailAndPassword(sharedPreferences.getString("email","abc"),sharedPreferences.getString("password","abc")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()) {
                                 if (myAuth.getCurrentUser().isEmailVerified()) {
                                     Intent i = new Intent(Main2Activity.this, Main3Activity.class);
                                     startActivity(i);
                                 }
                                 else {
                                     Toast.makeText(getApplicationContext(), "Please Verify First!", Toast.LENGTH_SHORT).show();
                                 }
                             }
                             else {
                                 Toast.makeText(getApplicationContext(), "Error, Try verifying again!", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });

                 }
                 else {

                     myAuth.signInWithEmailAndPassword(sharedPreferences.getString("email","abc"),sharedPreferences.getString("password","abc")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()) {
                                 if (myAuth.getCurrentUser().isEmailVerified()) {
                                     Intent i = new Intent(Main2Activity.this, Main3Activity.class);
                                     startActivity(i);
                                 }
                                 else {
                                     Toast.makeText(getApplicationContext(), "Please Verify First!", Toast.LENGTH_SHORT).show();
                                 }
                             }
                             else {
                                 Toast.makeText(getApplicationContext(), "Error, Try verifying again!", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                 }
             }
         });

         send_code_again.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (myAuth.getCurrentUser()!=null)
                 {
                     myAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful())
                             {
                                 Toast.makeText(getApplicationContext(), "Email Sent!", Toast.LENGTH_SHORT).show();
                             }
                             else {
                                 Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                 }
             }
         });

         maybe_later_button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent(Main2Activity.this, Main3Activity.class);
                 startActivity(i);
             }
         });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myAuth.getCurrentUser()==null)
        {
            finish();
        }

    }
}

