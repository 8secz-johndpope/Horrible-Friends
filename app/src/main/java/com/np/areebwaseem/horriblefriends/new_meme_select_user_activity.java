package com.np.areebwaseem.horriblefriends;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class new_meme_select_user_activity extends AppCompatActivity {

    private static final String TAG = "new_meme_select_user_ac";
    ListView users_view;
    ProgressBar myBar;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    volatile boolean isrunning;
    CustomAdapter customAdapter;
    ArrayList<String> all_friends_list;
    //ChildEventListener friends_child_event_listener_may;
    String current_uid="";
    DataSnapshot all_users_snapshot;
    ArrayList<String> all_users_name_list;
    EditText search_user_edit_text;
   boolean isSearching=false;
   ArrayList<String> final_uids;
    private Toolbar mTopToolbar;
    ImageButton search_friends_button;
    TextView select_friends_jibrish;
    int write_meme_request_code = 1001;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    View parentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meme_select_user_activity);


        users_view = findViewById(R.id.new_meme_select_user_list_view);
        myBar= findViewById(R.id.progressBar_new_meme_select_user);
        search_user_edit_text = findViewById(R.id.search_new_meme_user_edit_text);
        search_friends_button = findViewById(R.id.search_horrible_friends_image_button);
        select_friends_jibrish = findViewById(R.id.select_friend_view);

        parentLayout = findViewById(android.R.id.content);


        /*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(new_meme_select_user_activity.this, R.color.yellow_700));
        }
        */

        mAuth= FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        isrunning=true;
        all_friends_list= new ArrayList<>();
        all_users_name_list= new ArrayList<>();
        final_uids = new ArrayList<>();




        /*
        ActionBar actionBar = getSupportActionBar();



        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_bar_less_logo));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
*/


        mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
       // clear_cache();


        mTopToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        search_friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    search_friends_button.setVisibility(View.INVISIBLE);
                    select_friends_jibrish.setVisibility(View.INVISIBLE);
                    search_user_edit_text.setVisibility(View.VISIBLE);
                    search_user_edit_text.setFocusableInTouchMode(true);
                    search_user_edit_text.requestFocus();
                    final InputMethodManager inputMethodManager = (InputMethodManager) new_meme_select_user_activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(search_user_edit_text, InputMethodManager.SHOW_IMPLICIT);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
        });






        if (get_activity_state()) {


            if (is_connected()) {


                try {
                    current_uid = mAuth.getCurrentUser().getUid();

                    myBar.setVisibility(View.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DatabaseReference all_friends = databaseReference.child("users");
                                all_friends.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (get_activity_state()) {



                                            all_users_snapshot = dataSnapshot;

                                            if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild("friends")) {

                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("friends").getChildren()) {


                                                    if (all_users_snapshot.child(dataSnapshot1.getValue().toString()).hasChild("full_name")) {
                                                        all_friends_list.add(dataSnapshot1.getValue().toString());
                                                    }

                                                }

                                                boolean is_wrong = false;


                                                while (!is_wrong) {
                                                    is_wrong = true;
                                                    for (int i = 1; i < all_friends_list.size(); i++) {
                                                        if (all_users_snapshot.child(all_friends_list.get(i - 1)).child("full_name").getValue().toString().compareTo(all_users_snapshot.child(all_friends_list.get(i)).child("full_name").getValue().toString()) > 0) {
                                                            is_wrong = false;
                                                            String temp = all_friends_list.get(i);
                                                            all_friends_list.set(i, all_friends_list.get(i - 1));
                                                            all_friends_list.set(i - 1, temp);

                                                        }
                                                    }
                                                }

                                                for (int i = 0; i < all_friends_list.size(); i++) {
                                                    all_users_name_list.add(all_users_snapshot.child(all_friends_list.get(i)).child("full_name").getValue().toString());
                                                }

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            myBar.setVisibility(View.INVISIBLE);

                                                            customAdapter = new CustomAdapter(new_meme_select_user_activity.this, all_users_name_list, all_friends_list);


                                                            users_view.setAdapter(customAdapter);
                                                            customAdapter.notifyDataSetChanged();

                                                            users_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                    try {
                                                                        Intent intent = new Intent(new_meme_select_user_activity.this, write_meme_activity.class);
                                                                        TextView name_view = view.findViewById(R.id.custom_meme_new_user_list_item_name_text_name_view);
                                                                        TextView nick_view = view.findViewById(R.id.custom_meme_new_users_nick_view);
                                                                        intent.putExtra("selected_user", final_uids.get(position));
                                                                        intent.putExtra("selected_user_name", name_view.getText().toString());
                                                                        intent.putExtra("selected_user_nick", nick_view.getText().toString());
                                                                        startActivityForResult(intent, write_meme_request_code);
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }
                                                            });
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });


                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        search_user_edit_text.addTextChangedListener(new TextWatcher() {
                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                                if (s.toString().length() != 0) {
                                                                    isSearching = true;
                                                                } else {
                                                                    isSearching = false;
                                                                }
                                                            }

                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                                customAdapter.getFilter().filter(s);
                                                                if (s.toString().length() != 0) {
                                                                    isSearching = true;
                                                                } else {
                                                                    isSearching = false;
                                                                }
                                                            }

                                                            @Override
                                                            public void afterTextChanged(Editable s) {

                                                            }
                                                        });
                                                    }
                                                });



                       /*


                        friends_child_event_listener_may = databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                for (int i=0;i<all_friends_list.size();i++)
                                {
                                    if (all_friends_list.get(i).equals(dataSnapshot.getValue()))
                                    {
                                        all_friends_list.remove(all_friends_list.get(i));
                                        all_users_name_list.remove(all_users_name_list.get(i));
                                        customAdapter.notifyDataSetChanged();
                                    }
                                }

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
                                                if (get_activity_state()) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                myBar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_friends), Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });

                                                }
                                            }

                                    }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        if (get_activity_state()) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        myBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_string), Toast.LENGTH_SHORT).show();
                                                    }catch (Exception e)
                                                    {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                        }
                                    }
                                });
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                }catch (Exception e)
                {
                    e.printStackTrace();
                }





            }
            else {
                try {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    finish();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

        }






    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isrunning=false;
        /*
        if (friends_child_event_listener_may!=null)
        {
            try {
                databaseReference.child("users").child(current_uid).child("friends").removeEventListener(friends_child_event_listener_may);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==write_meme_request_code)
        {
            if (resultCode== RESULT_OK)
            {
                finish();
            }
        }
    }

    private boolean get_activity_state() {
        if ( mAuth != null && mAuth.getCurrentUser() != null && isrunning ) {
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
                mView.setBackgroundColor(ContextCompat.getColor(new_meme_select_user_activity.this, R.color.colorPrimaryDark));
                TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
                mTextView.setTextColor(ContextCompat.getColor(new_meme_select_user_activity.this, R.color.white));
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



    class CustomAdapter extends BaseAdapter implements Filterable {

        Context c;
        ArrayList<String> names_list;
        CustomAdapter.CustomFilter myFilter;
        ArrayList<String> filterList;
        ArrayList<String> uid_list;
        ArrayList<String> filter_uid_list;



        public CustomAdapter(Context ctx, ArrayList<String> names_list, ArrayList<String> uid_list)
        {
            this.c=ctx;
            this.names_list=names_list;
            this.filterList=names_list;
            this.uid_list= uid_list;
            this.filter_uid_list= uid_list;
            final_uids= uid_list;
        }

        @Override
        public int getCount() {
            return  uid_list.size();
        }

        @Override
        public Object getItem(int i) {

            //return null;
           return uid_list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return uid_list.indexOf(getItem(i));
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.custom_new_meme_user_list_item,null);
            TextView name_view = view.findViewById(R.id.custom_meme_new_user_list_item_name_text_name_view);
            TextView nick_view = view.findViewById(R.id.custom_meme_new_users_nick_view);
            final ImageView imageView = view.findViewById(R.id.profile_pic_custom_meme_new_user_list_item);

            final int req_ref=i;












            if (req_ref<uid_list.size() && get_activity_state()) {

                try {
                    if (all_users_snapshot.child(uid_list.get(req_ref)).hasChild("full_name")) {
                        name_view.setText(all_users_snapshot.child(uid_list.get(req_ref)).child("full_name").getValue().toString());
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }


                if (all_users_snapshot.child(uid_list.get(req_ref)).hasChild("name"))
                {
                    try {
                        if (!all_users_snapshot.child(uid_list.get(req_ref)).child("name").getValue().toString().equals("")) {
                            nick_view.setText("@ " + all_users_snapshot.child(uid_list.get(req_ref)).child("name").getValue().toString());
                            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) nick_view.getLayoutParams();
                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            nick_view.setLayoutParams(layoutParams);
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }

                if (isrunning && req_ref<uid_list.size()) {

                    final StorageReference storageReference_one = firebaseStorage.getReference();

                    try {
                        Glide.with(getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(storageReference_one.child(uid_list.get(req_ref))).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.asset_four).into(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                /*

                final StorageReference storageReference_one = firebaseStorage.getReference();
                StorageReference prof_reference_one = storageReference_one.child(uid_list.get(req_ref));
                prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        if (req_ref < uid_list.size()) {
                            if (isrunning) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {








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
                */






            }


            return view;
        }

        @Override
        public Filter getFilter() {
            if (myFilter==null)
            {
                myFilter = new CustomAdapter.CustomFilter();
            }
            return myFilter;
        }

        class CustomFilter extends Filter
        {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                if (charSequence!=null && charSequence.length()>0)
                {
                    charSequence = charSequence.toString().toUpperCase();
                    ArrayList<String> filters = new ArrayList<>();
                    for (int i=0;i<filterList.size();i++)
                    {
                        if (filterList.get(i).toUpperCase().contains(charSequence))
                        {
                            filters.add(filter_uid_list.get(i));
                        }
                    }
                    results.count=filters.size();
                    results.values=filters;
                }
                else {
                    results.count=filter_uid_list.size();
                    results.values=filter_uid_list;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                uid_list = (ArrayList<String>) filterResults.values;
                final_uids = uid_list;
                notifyDataSetChanged();

            }
        }
    }
    private void clear_cache()
    {
        if (isrunning)
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
                            Glide.get(getApplicationContext()).clearDiskCache();
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
