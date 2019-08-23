package com.np.areebwaseem.horriblefriends;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.security.spec.ECField;

public class terms_of_use extends AppCompatActivity {
    private static final String TAG = "terms_of_use";
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    TextView terms_of_use_view;

TextView feedback_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);
        terms_of_use_view = findViewById(R.id.terms_of_use_text_view);
        feedback_view = findViewById(R.id.terms_feedback_text_view);

     //   getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.status_bar_less_logo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.terms_string));

        feedback_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                    startActivity(new Intent(Intent.ACTION_VIEW,marketUri));
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
        /*


*/
        /*

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(terms_of_use.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(terms_of_use.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        displayWelcomeMessage();
                    }
                });
                */
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    private void displayWelcomeMessage() {
        String welcomeMessage = mFirebaseRemoteConfig.getString("privacy_policy");
        Log.d(TAG,welcomeMessage);
        terms_of_use_view.setText(welcomeMessage);
        String my_String = "asdfds";

    }
}
