package com.np.areebwaseem.horriblefriends;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.ExifInterface;
import android.media.MediaCas;
import android.media.MediaExtractor;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.internal.ImageDownloader;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Permission;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class more_info_activity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient googleApiClient;
    Button sign_out_button;
    FirebaseAuth mAuth;
    private Location mylocation;
    private static final String TAG = "Main3Activity";
    SharedPreferences sharedPreferences;
    ImageView profile_view;
    TextView profile_text_view;
   // Button select_image;
   // Button delete_profile;
    String mCurrentPhotoPath;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    Bitmap profImage;
    String user_full_name="";
    ContentValues values;
    private String pictureImagePath = "";
    //Button sele_camera;
    Uri imageUri;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    TextView prof_save_button;
    boolean my_loc_check=false;
    EditText nick_edit_text;
    EditText city_edit_text;
    LocationManager locationManager;
    private Uri mImageUri;
    String user_name="";
    String user_email="";
    String user_city="";
    String user_phone="";
    String my_selected_city_code="";
    EditText phone_number_edit_text;
    EditText full_name_edit_text;
    ProgressBar myBar;
    boolean new_pic_selected=false;
    CountryCodePicker myCcp;
    private int selected_image=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_activity);
        nick_edit_text = findViewById(R.id.nick_edit_text);
        city_edit_text = findViewById(R.id.city_edit_text);
        // delete_profile = findViewById(R.id.delete_profile_button);
        phone_number_edit_text = findViewById(R.id.phone_no_edit_text);
        phone_number_edit_text.setFilters(new InputFilter[] { filter });
        nick_edit_text.setFilters(new InputFilter[]{filter});
        prof_save_button = findViewById(R.id.prof_save_edit_text);
       // profile_view = findViewById(R.id.profile_pic_image_view);
        // profile_text_view = findViewById(R.id.profile_details_text_view);
    //    sele_camera = findViewById(R.id.select_camera);
        full_name_edit_text = findViewById(R.id.full_name_edit_text);
        myCcp = findViewById(R.id.ccp);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        sharedPreferences = this.getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null) {
            final StorageReference myImaref = firebaseStorage.getReference(mAuth.getCurrentUser().getUid());
        }
        //sign_out_button = findViewById(R.id.sign_out_button);
      //  select_image = findViewById(R.id.select_image_gallery_button);
        myBar =  findViewById(R.id.progress_bar_activ_more_info);
         myBar.setVisibility(View.INVISIBLE);


        my_selected_city_code= "+1";
        final StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        sharedPreferences.edit().putString("first_run", "false").apply();






        ////////////////////////////  Permission Check ///////////////////////////////////////////////



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);

            }
        }





        ////////////////////////////  Get Profile Data ///////////////////////////////////////////////
/*
    if (mAuth.getCurrentUser().getDisplayName()!=null) {
        profile_text_view.setText(mAuth.getCurrentUser().getDisplayName());
    }
    */

        ////////////////////////// Get Default Data from Auth providers //////////////////////////////

        if (mAuth.getCurrentUser()!=null)
        {
            if (mAuth.getCurrentUser().getDisplayName()!=null)
            {
                full_name_edit_text.setText(mAuth.getCurrentUser().getDisplayName());
            }
        }

/*


        DatabaseReference myRef=  databaseReference.child("users");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mAuth.getCurrentUser()!=null) {
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
                if (mAuth.getCurrentUser()!=null) {
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






*/







////////////////////////////////////// Change Listener /////////////////////////////////////////////








        if (mAuth.getCurrentUser().getPhotoUrl()==null)
        {

//            profile_view.setImageDrawable(getResources().getDrawable(R.drawable.logo_icon_one));
        }
        else {
            Log.d(TAG, mAuth.getCurrentUser().getPhotoUrl().toString());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    setuplocationclient();
                }
                else {
                    getLocation();
                }

            }

        }
        else {

            LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                setuplocationclient();
            }
            else {
                getLocation();
            }
        }




////////////////////////////////////// Country Spinner Country Selected /////////////////////////////////////////////



        myCcp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                my_selected_city_code = myCcp.getSelectedCountryCode();
            }
        });





        ///////////////////////////////////////// Auth State Change Listener ////////////////////////////////////////////////




        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null)
                {
                    finish();
                }
            }
        });

        // setUpGClient();

        //getMyLocation();

        //////////////////////////// Facebook Account kit Check ////////////////////////////////////////////////////

        if (sharedPreferences.getString("type","abc").equals("phone_auth"))
        {

            if (!sharedPreferences.getString("account_kit_phone_no", "abc").equals("abc"))
            {
                phone_number_edit_text.setText(sharedPreferences.getString("account_kit_phone_no","abc"));
            }
            if (!sharedPreferences.getString("phone_country_code","abc").equals("abc"))
            {

                Log.d(TAG, "Country COde is: " +"+" + sharedPreferences.getString("phone_country_code","abc") );
                myCcp.setCountryForPhoneCode(Integer.valueOf( sharedPreferences.getString("phone_country_code","abc")));
                //myCcp.resetToDefaultCountry();
            }
        }




        //////////////////////////////// Database provider Listener ////////////////////////////////////////////










        ///////////////////////////////////////// Database change Listener ////////////////////////////////////////////////


        DatabaseReference myRef=  databaseReference.child("users");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mAuth.getCurrentUser()!=null) {
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


                       // setProfileData(user_name, user_email, user_city, user_phone,user_full_name);
                    }
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (mAuth.getCurrentUser()!=null) {
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


                       // setProfileData(user_name, user_email, user_city, user_phone, user_full_name);
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






        ////////////////////////////////////////////// Get Current Photo ////////////////////////////////////////////////

        StorageReference storageReference_one = firebaseStorage.getReference();
        StorageReference prof_reference_one = storageReference_one.child(mAuth.getCurrentUser().getUid());


        prof_reference_one.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //getCurrentPhoto(true);
                // Got the download URL for 'users/me/profile.png'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
              //  getCurrentPhoto(false);
                // File not found
            }
        });








        if (databaseReference.child("users").child(mAuth.getCurrentUser().getUid())==null)
        {
            profile_text_view.setText(mAuth.getCurrentUser().getDisplayName());
        }







        //////////////////////////////////// Profile Add Button /////////////////////////////////////////////////





        prof_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (nick_edit_text.getText().toString().equals("") && city_edit_text.getText().toString().equals("") && phone_number_edit_text.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Fields cant be empty!", Toast.LENGTH_SHORT).show();
                }

                else {
                */
/*
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nick_edit_text.getText().toString()).build();
                    mAuth.getCurrentUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {

                                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();

                            }
                            else {

                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    */

                if (mAuth.getCurrentUser()!=null) {

                    myBar.setVisibility(View.VISIBLE);

                    Map<String, Object> taskMap = new HashMap<>();
                    if (!nick_edit_text.getText().toString().equals("")) {
                        taskMap.put("name", nick_edit_text.getText().toString());
                    }
                    else {
                        taskMap.put("name",user_name);
                    }

                    if (!full_name_edit_text.getText().toString().equals(""))
                    {
                        taskMap.put("full_name", full_name_edit_text.getText().toString());

                    }
                    else {
                        taskMap.put("full_name",user_full_name);
                    }

                    if (mAuth.getCurrentUser().getEmail() != null) {
                        taskMap.put("Email", mAuth.getCurrentUser().getEmail());
                    }
                    else {
                        taskMap.put("Email",user_email);
                    }

                    if (!city_edit_text.getText().toString().equals("")) {
                        taskMap.put("City", city_edit_text.getText().toString());
                    }
                    else {
                        taskMap.put("City", user_city);
                    }


                    if (!phone_number_edit_text.getText().toString().equals("")) {
                        //taskMap.put("phone", my_selected_city_code + phone_number_edit_text.getText().toString());
                        taskMap.put("phone", "+" + my_selected_city_code + " " + phone_number_edit_text.getText().toString());
                    }
                    else {
                        taskMap.put("phone",user_phone);
                    }

                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).setValue(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                myBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(more_info_activity.this, Main4Activity.class);
                                startActivity(i);

                            } else {
                                myBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                                    /*
                                    profile_view.setDrawingCacheEnabled(true);
                                    profile_view.buildDrawingCache();
                                    */




                }


            }
            //  }
        });

























        ///////////////////////////////////  Sign out /////////////////////////////////////////////////////////

/*

        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPreferences.getString("type","none").equals("email")) {

                    sharedPreferences.edit().putString("email", "none").apply();
                    sharedPreferences.edit().putString("password", "none").apply();
                    sharedPreferences.edit().putString("type","none").apply();
                    mAuth.signOut();
                    //finish();
                }
                else {
                    sharedPreferences.edit().putString("type","none").apply();
                    mAuth.signOut();
                    // finish();
                }
            }
        });
        */

        ////////////////////////// Select Camera ///////////////////////////////////////////////////////


        /*

        sele_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {







                builder.detectFileUriExposure();



                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 3);






            }
        });
*/

        ////////////////////////// Image Select Gallery ////////////////////////////////////////////////////////

/*
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);


            }
        });

*/





    }



    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }




    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            // Toast.makeText(getApplicationContext(), "Got Location",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "latitude: " + String.valueOf(latitude));
            Log.d(TAG, "longitude: " + String.valueOf(longitude));
            Geocoder gcd = new Geocoder(more_info_activity.this, Locale.ENGLISH);
            //if (my_loc_check==false) {
            try {

                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    my_loc_check=true;
                    // System.out.println(addresses.get(0).getLocality());
                    user_city = addresses.get(0).getLocality();
                    city_edit_text.setText(user_city);
                    //   Toast.makeText(getApplicationContext(), "City: " + user_city,Toast.LENGTH_SHORT).show();

                    String user_country= addresses.get(0).getCountryName();
                    if(user_country!=null) {
                        country_codes myCodes = new country_codes();
                        if (myCcp!=null) {
                            if (!sharedPreferences.getString("type","abc").equals("phone_auth")) {
                                myCcp.setDefaultCountryUsingNameCode(myCodes.getCode(user_country));
                                myCcp.resetToDefaultCountry();
                            }
                                /*
                                myCcp.setDefaultCountryUsingNameCode(myCodes.getCode(user_country));
                                myCcp.resetToDefaultCountry();
                                */
                            // Toast.makeText(getApplicationContext(), "Country: " + user_country,Toast.LENGTH_SHORT).show();
                            my_selected_city_code= myCcp.getSelectedCountryCode();
                        }
                    }
                    //  Toast.makeText(getApplicationContext(), addresses.get(0).getLocality(), Toast.LENGTH_SHORT).show();
                } else {
                    // do your stuff
                    // Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    protected void onResume() {
        //   myCcp.setDefaultCountryUsingNameCode("United States");
        //  myCcp.resetToDefaultCountry();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //   new_pic_selected=true;

            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), selectedImage);
                //  Bitmap rotatedBitmap = rotateImage(thumbnail, -90);
                // profile_view.setImageBitmap(rotatedBitmap);
                // String imageurl = getRealPathFromURI(imageUri);
                // profile_view.setImageURI(imageurl);
                ExifInterface ei = new ExifInterface(getRealPathFromURI(selectedImage));
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = thumbnail;
                }
                //
                // profile_view.setImageBitmap(rotatedBitmap);
                Uri myUri = Uri.fromFile(new File(getCacheDir(),"cropped"));

                Crop.of(selectedImage, myUri).asSquare().start(this);
                //  profile_view.setImageURI(Crop.getOutput(data));


            } catch (Exception e) {
                // profile_view.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                e.printStackTrace();
            }













        }

        else if (requestCode==Crop.REQUEST_CROP)
        {
            handleCrop(resultCode,data);
        }

        else   if (requestCode == 1000) {
            if(resultCode == RESULT_OK){

                String result=data.getStringExtra("result");
                // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                getLocation();
//                    Log.d(TAG, result);

            } if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }






        else if (requestCode == 3 && resultCode == RESULT_OK)
        {
            //  new_pic_selected=true;
            /*
            try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                profile_view.setImageBitmap(thumbnail);
               // imageurl = getRealPathFromURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
/*
            Bundle extras = data.getExtras();

            // get bitmap
             Bitmap bitMap = (Bitmap) extras.get("data");
            profile_view.setImageBitmap(bitMap);

*/
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                //  Bitmap rotatedBitmap = rotateImage(thumbnail, -90);
                // profile_view.setImageBitmap(rotatedBitmap);
                // String imageurl = getRealPathFromURI(imageUri);
                // profile_view.setImageURI(imageurl);
                ExifInterface ei = new ExifInterface(getRealPathFromURI(imageUri));
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = thumbnail;
                }
                //  profile_view.setImageBitmap(rotatedBitmap);
                Uri myUri = Uri.fromFile(new File(getCacheDir(),"cropped"));

                Crop.of(imageUri, myUri).asSquare().start(this);


            } catch (Exception e) {
                e.printStackTrace();
            }





/*
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profile_view.setImageBitmap(photo);
                */



        }
        else {
            //   Toast.makeText(getApplicationContext(), "No data!", Toast.LENGTH_SHORT).show();

                /*
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
               profImage = (BitmapFactory.decodeFile(picturePath));
               profile_view.setImageBitmap(profImage);
               */
                /*
                Uri uri=data.getData();
                String[]projection ={MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null,null);
                cursor.moveToFirst();
                int columnIndex= cursor.getColumnIndex(projection[0]);
                String filepath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap sele_image = BitmapFactory.decodeFile(filepath);
                Drawable d=  new BitmapDrawable(sele_image);
                profile_view.setBackground(d);
                */


        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    ///////////////////////// Image Downloader /////////////////////////////////////////////////////////////

    public class ImageDownloader extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            }catch (MalformedURLException e)
            {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setProfileData(String na, String em, String ci, String ph, String ful)
    {

        String emaili= mAuth.getCurrentUser().getEmail();
      //  profile_text_view.setText("Name: " + ful + "\n" + "Nick: "  + na + "\n" + "Email: " + emaili+ "\n" + "City: " + ci  + "\n" + "Phone: "+ ph);
        if (!ful.equals("")) {
            full_name_edit_text.setText(ful);
        }
        if (!ci.equals("")) {
            city_edit_text.setText(ci);
        }
        if (!na.equals("")) {
            nick_edit_text.setText(na);
        }
        Toast.makeText(getApplicationContext(), ph, Toast.LENGTH_SHORT).show();
        if (!ph.equals("")) {
            phone_number_edit_text.setText(ph);
        }

    }
    private void getLocation()
    {




        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
                //  Toast.makeText(getApplicationContext(), "Location not granted",Toast.LENGTH_SHORT).show();

            }
            else {
                // Toast.makeText(getApplicationContext(), "Location granted",Toast.LENGTH_SHORT).show();
                Location location  = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    // Toast.makeText(getApplicationContext(), "Got Last Location",Toast.LENGTH_SHORT).show();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Geocoder gcd = new Geocoder(this, Locale.ENGLISH);
                    try {

                        List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                        if (addresses.size() > 0) {
                            // System.out.println(addresses.get(0).getLocality());
                            user_city = addresses.get(0).getLocality();
                            //   Toast.makeText(getApplicationContext(), "City: " + user_city,Toast.LENGTH_SHORT).show();
                            String user_country= addresses.get(0).getCountryName();
                            if(user_country!=null) {
                                country_codes myCodes = new country_codes();
                                if (!sharedPreferences.getString("type","abc").equals("phone_auth")) {
                                    myCcp.setDefaultCountryUsingNameCode(myCodes.getCode(user_country));
                                    myCcp.resetToDefaultCountry();
                                }
                                // Toast.makeText(getApplicationContext(), "Country: " + user_country,Toast.LENGTH_SHORT).show();
                                my_selected_city_code= myCcp.getSelectedCountryCode();

                            }
                            if (user_city!=null) {
                                city_edit_text.setText(user_city);
                            }
                            //  Toast.makeText(getApplicationContext(), addresses.get(0).getLocality(), Toast.LENGTH_SHORT).show();
                        } else {
                            // do your stuff
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Log.d(TAG, "No location!");


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                                    0,mLocationListener );
                            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mLocationListener);
                            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                        }
                    }
                    else {
                        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                                0,mLocationListener );
                        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                    }

                }









            }
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




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1001: {
                if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    //       getMyLocation();




                    LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        setuplocationclient();
                    }
                    else {
                        getLocation();
                    }


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    public void grabImage(ImageView imageView)
    {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void getCurrentPhoto(final boolean check)
    {


        if (mAuth.getCurrentUser()!=null) {
            if (mAuth.getCurrentUser().getPhotoUrl() != null) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        final ImageDownloader downloader = new ImageDownloader();

                        try {


                            if (sharedPreferences.getString("type", "abc").equals("twitter")) {
                                if (!check) {
                                    profImage = downloader.execute(mAuth.getCurrentUser().getPhotoUrl().toString().replace("_normal", "")).get();
                                } else {
                                    //   Log.d(TAG, myString_one);
                                    profImage = downloader.execute(mAuth.getCurrentUser().getPhotoUrl().toString()).get();
                                }
                            } else if (sharedPreferences.getString("type", "abc").equals("facebook")) {

                                if (!check) {
                                    String myString = "https://graph.facebook.com/" + sharedPreferences.getString("fb_id", "abc") + "/picture?type=large&redirect=true&width=600&height=600";
                                    profImage = downloader.execute(myString).get();
                                } else {
                                    // Toast.makeText(getApplicationContext(), myString_one, Toast.LENGTH_SHORT).show();
                                    //   Log.d(TAG,"aaaaaaaaaaaaaaaaaaaaa" + myString_one);
                                    profImage = downloader.execute(mAuth.getCurrentUser().getPhotoUrl().toString()).get();
                                }

                            } else {
                                profImage = downloader.execute(mAuth.getCurrentUser().getPhotoUrl().toString()).get();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                              //      profile_view.setImageBitmap(profImage);
                                }
                            });


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }


                };
                new Thread(runnable).start();
            }
        }

    }













    /*

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(Main3Activity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =                     LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(Main3Activity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(Main3Activity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }
    */

    /*
    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(Main3Activity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =                     LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                  // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest ,mLocationListener);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(Main3Activity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(Main3Activity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }
*/





    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(more_info_activity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            //getMyLocation();
        }

    }

    private void setuplocationclient()
    {


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(more_info_activity.this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder_one = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder_one.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder_one.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(more_info_activity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }




    }


    private void handleCrop(int code, Intent result)
    {
        if (code==RESULT_OK) {
            //  profile_view.setImageURI(Crop.getOutput(result));

            Uri selectedImage = Crop.getOutput(result);
            /*
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
*/
            new_pic_selected=true;

            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), selectedImage);
                //  Bitmap rotatedBitmap = rotateImage(thumbnail, -90);
                // profile_view.setImageBitmap(rotatedBitmap);
                // String imageurl = getRealPathFromURI(imageUri);
                // profile_view.setImageURI(imageurl);
                ExifInterface ei = new ExifInterface(Crop.getOutput(result).getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = thumbnail;
                }
                //
                profile_view.setImageBitmap(rotatedBitmap);


                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(more_info_activity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(more_info_activity.this);
                }
                builder.setTitle("Confirm")
                        .setMessage("Are you sure you want to make this your profile picture?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                save_image_to_db();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                                new_pic_selected=false;
                                dialog.dismiss();
                            }
                        }).show();



            } catch (Exception e) {
                // profile_view.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.d(TAG, "Exception: " + e.getMessage());
            }



        }
        else {
            Log.d(TAG, "Error in Cropping");
        }
    }

    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }

    };

    private void save_image_to_db()
    {


        if (mAuth.getCurrentUser()!=null) {
                                    /*
                                    profile_view.setDrawingCacheEnabled(true);
                                    profile_view.buildDrawingCache();
                                    */


            if (mAuth.getCurrentUser().getPhotoUrl() == null || new_pic_selected == true) {
                Bitmap bitmap = ((BitmapDrawable) profile_view.getDrawable()).getBitmap();
                // Bitmap bitmap = profile_view.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                StorageReference storageReference = firebaseStorage.getReference();
                StorageReference prof_reference = storageReference.child(mAuth.getCurrentUser().getUid());
              //  myBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = prof_reference.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), "Error 1", Toast.LENGTH_SHORT).show();
                      //  myBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        UserProfileChangeRequest myRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl).build();
                        mAuth.getCurrentUser().updateProfile(myRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    new_pic_selected = false;
                               //     myBar.setVisibility(View.INVISIBLE);


                                    Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();


                                } else {
                                //    myBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Error 2", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                        Log.d(TAG, downloadUrl.toString());
                    }
                });
            }
        }




    }








}
