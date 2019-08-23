package com.np.areebwaseem.horriblefriends;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.SignInButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

/**
 * Created by areebwaseem on 10/28/17.
 */

public class main_fragment_view extends Fragment{
    private static final String TAG = "main_fragment_view";

    ListView myView;
    String first_type_to_get_raw="0";
    String second_type_to_get_raw="1";

    SharedPreferences mySharedPreferences;
    ProgressBar circular_progress;
    String access_token="";
    String customer_id;
    Context context;
    Button add_button;
    EditText cust_name_add;
    EditText cust_phone_add;
    EditText cust_address_add;

    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    Button invite_button;
    int request_invite=1002;
    FloatingActionButton add_friend_fab;
    volatile boolean is_activity_running;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment_layout,container,false);

        context=this.getActivity();

        invite_button = view.findViewById(R.id.invite_friends_button);
        add_button = view.findViewById(R.id.add_friend_button);
        add_friend_fab = view.findViewById(R.id.fab);


        add_friend_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           Intent i = new Intent(getActivity(), add_friend_activity.class);
           if (getActivity()!=null) {
               getActivity().startActivity(i);
           }
            }
        });


        invite_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new AppInviteInvitation.IntentBuilder("Invite Friends")
                        .setMessage(getResources().getString(R.string.hey_i_am_on_horrible_check_it))
                        .setDeepLink(Uri.parse("www.google.com"))
                        .setCallToActionText("Invitation")
                        .build();
                startActivityForResult(intent,request_invite );
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                ContentResolver contentResolver = getActivity().getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,projection,null,null,ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
                if (cursor!=null)
                {
                    List<String> contacts= new ArrayList<String>();
                    while (cursor.moveToNext())
                    {
                        contacts.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
                    }
                    for (int i=0;i<contacts.size();i++)
                    {
                        Log.d(TAG, contacts.get(i));
                    }
                }
                */

                ContentResolver cr = getActivity().getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                if ((cur != null ? cur.getCount() : 0) > 0) {
                    while (cur != null && cur.moveToNext()) {
                        String id = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));

                        if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);

                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Log.i(TAG, "Name: " + name);
                                Log.i(TAG, "Phone Number: " + phoneNo);


                            }
                            pCur.close();
                        }
                    }
                }
                if(cur!=null){
                    cur.close();
                }
            }
        });






        return view;
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
        if (requestCode== request_invite)
        {
            if (resultCode == Activity.RESULT_OK)
            {
              //  String [] ids = AppInviteInvitation.getInvitationIds(requestCode,data);

            }
            else {
                Toast.makeText(getActivity(), getResources().getString(R.string.couldnt_send_invites),Toast.LENGTH_SHORT).show();
            }

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

        }
    }






}
