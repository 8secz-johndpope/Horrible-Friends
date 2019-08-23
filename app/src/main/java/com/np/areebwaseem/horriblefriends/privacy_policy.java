package com.np.areebwaseem.horriblefriends;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class privacy_policy extends AppCompatActivity {

    TextView feedback_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.status_bar_less_logo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.privacy_string));
        feedback_view = findViewById(R.id.privacy_feedback_text_view);

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
}
