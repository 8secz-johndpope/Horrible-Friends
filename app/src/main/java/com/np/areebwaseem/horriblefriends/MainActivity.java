package com.np.areebwaseem.horriblefriends;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.icu.text.UnicodeSetSpanner;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {



     TwitterLoginButton loginButton;
     CallbackManager mCallbackManager;
    // EditText email_text;
    // EditText passwordText;
     LinearLayout verify_phone_button;
     Button siginButton;
     boolean fb_button_once_clicked=false;
     Button login_button_main;

    String account_kit_phone_auth="";
    LinearLayout line_lay_4_join;
    private GoogleApiClient googleApiClient;
    public static int APP_REQUEST_CODE = 99;
    TwitterAuthClient twitterAuthClient;
    private CloudFunctions mCloudFunctions;
    boolean check_auth_type=false;
    Button signUpbutton;
    ImageButton twitter_login_button_custom;
    boolean twitter_mAuth_found_bool=false;
    String user_current_facebook_email="";
    int twitter_child_count=0;
    ImageButton facebook_login_custom;
    Button forgot_password_button;
    int facebook_child_count=0;
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
    LinearLayout bottom_linear_layout;
    boolean email_pressed_check = false;
    ProgressBar myBar;
   ImageButton reg_email;
   TextView terms_under_view;
   TextView privacy_under_view;
   Button confirm_terms_button;
   Button back_terms_button;
   String attempt_type;


    // Button signOutButton;
     int child_count=0;
     SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;
    String type_from_db="";
    View parentLayout;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    boolean is_activity_running;
    LinearLayout terms_and_privacy_layout;

    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);
        Twitter.initialize(this);
        databaseReference  = FirebaseDatabase.getInstance().getReference();

        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        is_activity_running=true;




       /////////////////////////// Socket.io ///////////////////////////////////////

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-firedemocomplete.cloudfunctions.net/")
                .build();
        mCloudFunctions = retrofit.create(CloudFunctions.class);


        /*

        try {
            mSocket= IO.socket(Constants.IP_LOCAL_HOST);
        } catch (URISyntaxException e) {
          Log.d(TAG, "Error with Socket init" + e.getMessage());

          e.printStackTrace();
        }
       mSocket.connect();

*/




        ///////////////////////////// Initialization //////////////////////////////////



     //  email_text = findViewById(R.id.enter_email_edit_text);
     //   passwordText= findViewById(R.id.enter_password_edit_text);
      //  siginButton = findViewById(R.id.sign_in_button);
      //  signUpbutton = findViewById(R.id.sign_up_button);


        twitter_login_button_custom= findViewById(R.id.twitter_login_button_custom);
        facebook_login_custom = findViewById(R.id.facebook_login_button_custom);
        forgot_password_button = findViewById(R.id.forgot_password_button);
        verify_phone_button = findViewById(R.id.verify_sms_button);
        //signOutButton = findViewById(R.id.sign_out_button);
        login_button_main= findViewById(R.id.login_button_activ_main);
        bottom_linear_layout = findViewById(R.id.already_have_an_account_linear_layout);
        myBar = findViewById(R.id.progress_bar_sign_up_main_activity);
        reg_email = findViewById(R.id.reg_email_button);
        myBar.setVisibility(View.INVISIBLE);
        terms_and_privacy_layout=findViewById(R.id.terms_and_privacy_join_layout);
        line_lay_4_join = findViewById(R.id.linearLayout4_join);
        terms_under_view= findViewById(R.id.terms_underline_text_view);
        privacy_under_view = findViewById(R.id.privacy_underline_text_view);
        confirm_terms_button = findViewById(R.id.confirm_terms_button_main);
        back_terms_button = findViewById(R.id.back_terms_button_main);
        attempt_type = "";

        myBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);


        sharedPreferences = this.getSharedPreferences("com.example.areebwaseem.horriblefriendsfinal", Context.MODE_PRIVATE);


        mAuth = FirebaseAuth.getInstance();






       if (sharedPreferences.getString("first_run","true").equals("true"))
       {




           if (mAuth!=null && mAuth.getCurrentUser()!=null)
           {
               mAuth.signOut();


           }

           sharedPreferences.edit().putString("first_run","false").apply();


       }

       if (mAuth!=null && mAuth.getCurrentUser()!=null && !sharedPreferences.getString("first_run","true").equals("true"))
       {
          // Toast.makeText(getApplicationContext(), mAuth.getCurrentUser().getDisplayName(),Toast.LENGTH_SHORT).show();
           /*
           if (mAuth.getCurrentUser().isEmailVerified()) {
               Intent i = new Intent(MainActivity.this, Main3Activity.class);
               startActivity(i);
           }
           else {
               if (!(sharedPreferences.getString("type","abc").equals("facebook"))&& ! sharedPreferences.getString("type","abc").equals("twitter") ){
                   Toast.makeText(getApplicationContext(), "Verify Email!", Toast.LENGTH_SHORT).show();
                   Intent i = new Intent(MainActivity.this, Main2Activity.class);
                   startActivity(i);
               }
               else {
                   Intent i = new Intent(MainActivity.this, Main3Activity.class);
                   startActivity(i);
               }
           }
           */




          // Intent i = new Intent(MainActivity.this, Main4Activity.class);
         // startActivity(i);

           Intent i = new Intent(MainActivity.this, home_activity.class);
             startActivity(i);
       }



       /*
        DatabaseReference my_new_Ref = databaseReference.child("users_auth");
        my_new_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data Changed!!!!!! outside");
                initial_child_count= Math.round(dataSnapshot.getChildrenCount());

                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                myFile.writeLine(currentDateTimeString + ": on Create initial child count changed");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

*/




     /*

        siginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (mAuth.getCurrentUser()!=null)
               {
                  Toast.makeText(getApplicationContext(), "Log Out First!", Toast.LENGTH_SHORT).show();

               }
               else {
                   final String email = email_text.getText().toString();
                   final String password = passwordText.getText().toString();
                   if (!email.equals("") && !password.equals(""))
                   {
                       mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                              if (task.isSuccessful())
                              {


                                  if (mAuth.getCurrentUser()!=null)
                                  {
                                      sharedPreferences.edit().putString("email", email).apply();
                                      sharedPreferences.edit().putString("password", password).apply();
                                      sharedPreferences.edit().putString("type", "email").apply();
                                      email_text.setText("");
                                      passwordText.setText("");

                                    //  if (mAuth.getCurrentUser().isEmailVerified())
                                    //  {
                                    //      Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                     //     startActivity(i);
                                     //     Toast.makeText(getApplicationContext(), "Verified",Toast.LENGTH_SHORT).show();
                                  //}
                                  //else {
                                   //       Intent i = new Intent(MainActivity.this, Main2Activity.class);
                                   //       startActivity(i);
                                   //     Toast.makeText(getApplicationContext(), "Please verify Email",Toast.LENGTH_SHORT).show();
                                   //   }


                                      Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                      startActivity(i);

                                  }
                              }
                              else {
                                  Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                              }
                           }
                       });
                   }
                   else {
                       Toast.makeText(getApplicationContext(),"Fields can't be empty",Toast.LENGTH_SHORT).show();
                   }



               }
            }
        });



*/














        //////////////////////////////////////////////// Facebook Auth ////////////////////////////////////////////////////////////
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                //Toast.makeText(getApplicationContext(), "Success with facebook",Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_with_facebook), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {

            Toast.makeText(getApplicationContext(),  getResources().getString(R.string.error_with_facebook), Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Error Was.."+  error.getMessage().toString());

            }

        });


        facebook_login_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attempt_type="facebook";
                sign_up_attempt();

            }
        });

        /*
        LoginButton loginButton = findViewById(R.id.login_button_facebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                // ...
            }
        });
        */



       AccessTokenTracker fbTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {

                if (accessToken2 == null && fb_button_once_clicked) {
                    /*
                    sharedPreferences.edit().putString("email", "none").apply();
                    sharedPreferences.edit().putString("password", "none").apply();
                    sharedPreferences.edit().putString("type","none").apply();
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();
                    Log.d("FB", "User Logged Out.");
                    */
                   // Toast.makeText(getApplicationContext(),"Please tap again!", Toast.LENGTH_SHORT).show();

                    fb_button_once_clicked=false;

                    isFacebook = true;
                    LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
                }
            }
        };


       /*

        com.facebook.accountkit.AccessTokenTracker fb_Tracker = new com.facebook.accountkit.AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(com.facebook.accountkit.AccessToken accessToken, com.facebook.accountkit.AccessToken accessToken1) {
                if (accessToken1==null) {
                  //  phoneLogin();
                   // Toast.makeText(getApplicationContext(), "1 was null", Toast.LENGTH_SHORT).show();
                }
                else if (accessToken==null)
                {
                   // Toast.makeText(getApplicationContext(), "0 was null", Toast.LENGTH_SHORT).show();
                }
                else {
               //     Toast.makeText(getApplicationContext(), "wasn't null", Toast.LENGTH_SHORT).show();
                }
            }
        };

*/


      ////////////////////////////////////////////// Twitter Auth ////////////////////////////////////////////////////////////

  twitter_login_button_custom.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {




          attempt_type="twitter";
          sign_up_attempt();


      }
  });



        /*
             Twitter.initialize(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("qSKmUl3YNmi7kE2POumHfon0c", "9fsPAnbJjfoB76irkC41Oz59ENE85tSVEwILSASi2kzCgCj21k"))
                .debug(true)
                .build();
        Twitter.initialize(config);
        */

      //  TwitterAuthConfig authConfig = new TwitterAuthConfig("qSKmUl3YNmi7kE2POumHfon0c", "9fsPAnbJjfoB76irkC41Oz59ENE85tSVEwILSASi2kzCgCj21k");
       // Fabric.with(this, new Twitter(authConfig));



/*

        loginButton=  findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(getApplicationContext(), "Success in Loging in", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
        */


////////////////////////////////////////// Sign Out /////////////////////////////////////////////////
        Button mySignout = findViewById(R.id.logout_main);
        mySignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth!=null && mAuth.getCurrentUser()!=null)
                {
                    mAuth.signOut();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Already Signed out", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser()!=null)
                {
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(), "Signed Out!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Already Signed Out!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */

        ////////////////////////////////////// Forgot Password ////////////////////////////////////////////


        /*
        forgot_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (!email_text.getText().toString().equals(""))
               {
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
        /*
                   check_for_auth_type(email_text.getText().toString());
                   check_auth_type=false;
               }

               else {
                   Toast.makeText(getApplicationContext(), "Email Field cant be empty!", Toast.LENGTH_LONG).show();
               }
            }
        });
        */

        ////////////////////////////////// Verify Phone Button ///////////////////////////////////////

        verify_phone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                attempt_type = "phone_auth";
                sign_up_attempt();


            }
        });

        /////////////////////////////// Login Button Main ////////////////////////////////////////

        login_button_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, login_email_activ.class);
                startActivity(i);
            }
        });

        reg_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempt_type="email";
                sign_up_attempt();

            }
        });

        terms_under_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,terms_of_use.class);
                startActivity(i);
            }
        });

        privacy_under_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,privacy_policy.class);
                startActivity(i);
            }
        });

        back_terms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line_lay_4_join.setVisibility(View.VISIBLE);
                verify_phone_button.setVisibility(View.VISIBLE);
                terms_and_privacy_layout.setVisibility(View.INVISIBLE);
            }
        });

        confirm_terms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line_lay_4_join.setVisibility(View.VISIBLE);
                verify_phone_button.setVisibility(View.VISIBLE);
                terms_and_privacy_layout.setVisibility(View.INVISIBLE);
                if (attempt_type.equals("email")) {

                Intent i = new Intent(MainActivity.this, sign_in_email_activity.class);
                startActivity(i);

                }
                else if (attempt_type.equals("phone_auth"))
                {

                connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    com.facebook.accountkit.AccessToken accessToken = AccountKit.getCurrentAccessToken();
                    phoneLogin();
                }
                else {
                    show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                }

                }
                else if (attempt_type.equals("twitter"))
                {
                    connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    networkInfo = connMgr.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        isFacebook = false;
                        twitterAuthClient = new TwitterAuthClient();
                        twitterAuthClient.authorize(MainActivity.this, new Callback<TwitterSession>() {
                            @Override
                            public void success(final Result<TwitterSession> result) {
                                final TwitterSession sessionData = result.data;
                                // Do something with the returned TwitterSession (contains the user token and secret)
                                handleTwitterSession(sessionData);
                            }

                            @Override
                            public void failure(final TwitterException e) {
                                // Do something on fail
                            }
                        });
                    }
                    else {
                        show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                    }
                }
                else if (attempt_type.equals("facebook"))
                {
                    connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    networkInfo = connMgr.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {




                        if (AccessToken.getCurrentAccessToken() != null) {
                            fb_button_once_clicked = true;
                            LoginManager.getInstance().logOut();

                        } else {
                            isFacebook = true;
                            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email", "user_friends"));

                        }

                    }
                    else {
                        show_snackbar(getResources().getString(R.string.no_internet_connection),true);
                    }
                }


            }
        });



    }




    @Override
    protected void onResume() {
        super.onResume();






        if (mAuth!=null && mAuth.getCurrentUser()!=null) {

            if (mAuth.getCurrentUser().getDisplayName()!=null) {
                Log.d(TAG, mAuth.getCurrentUser().getDisplayName());

            }
        }

        else {
          //  Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
        }

        /*
        DatabaseReference my_new_Ref = databaseReference.child("users_auth");
        my_new_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "Data Changed!!!!!! outside");
                initial_child_count= Math.round(dataSnapshot.getChildrenCount());
                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                myFile.writeLine(currentDateTimeString + ": on Resume initial child count value added");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {









            }
        });
        */
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


///////////////////////////////////// Facebook ///////////////////////////////////////////////////////
      if (isFacebook) {
          mCallbackManager.onActivityResult(requestCode, resultCode, data);
      }
      else if (requestCode!=APP_REQUEST_CODE)  {
          twitterAuthClient.onActivityResult(requestCode, resultCode, data);
      }




        //////////////////////////// Twitter //////////////////////////////////////////
        /// //        loginButton.onActivityResult(requestCode, resultCode, data);
        //////////////// Facebook Account Kit ///////////////////////////






        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request



          final AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //showErrorAct(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {

               start_bar();


                if (loginResult.getAccessToken() != null) {

                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            // Get Account Kit ID
                            String accountKitId = account.getId();

                            // Get phone number
                            PhoneNumber phoneNumber = account.getPhoneNumber();
                            if (phoneNumber != null) {
                                //Toast.makeText(getApplicationContext(), phoneNumber.getCountryCode(),Toast.LENGTH_SHORT).show();
                                sharedPreferences.edit().putString("phone_country_code", phoneNumber.getCountryCode()).apply();
                                String fn = phoneNumber.getRawPhoneNumber();
                                String cd= phoneNumber.getCountryCode();

                                StringBuilder myPhFinal = new StringBuilder(fn);
                                for (int i=0;i<cd.length();i++)
                                {

                                   myPhFinal.deleteCharAt(0);

                                }

                                String phoneNumberString = myPhFinal.toString();

                                Log.d(TAG, "Country code is: " + phoneNumber.getCountryCode());
                                Log.d(TAG, "Phone number is: " + phoneNumber.getRawPhoneNumber());
                                Log.d(TAG, "String is: " + myPhFinal.toString());

                              //  String phoneNumberString = phoneNumber.getRawPhoneNumber().replace(phoneNumber.getCountryCode(),"");
                               account_kit_phone_auth=phoneNumberString;
                                Log.d(TAG, loginResult.getAccessToken().getAccountId());
                                Log.d(TAG, "Acceesssssss" + loginResult.getAccessToken().getToken());

                                getCustomToken(loginResult.getAccessToken());

                                Log.d(TAG, "Success with Access token");


                            }
                            else {
                               error_bar();

                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.couldnt_get_number), Toast.LENGTH_SHORT).show();

                            }

                            // Get email
                            String email = account.getEmail();
                        }

                        @Override
                        public void onError(final AccountKitError error) {
                            // Handle Error
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_with_phone_auth),Toast.LENGTH_SHORT).show();
                            error_bar();
                        }
                    });









                 
                } else {
                   error_bar();
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                  //  handleFacebookAccessToken(lo);
                    Log.d(TAG, "Success with Authorization Code");

                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
              //  goToMyLoggedInActivity();
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }




    private void handleTwitterSession(final TwitterSession session) {





      //  if (initial_child_count!=0) {





            Log.d(TAG, "handleTwitterSession:" + session);
            start_bar();

            TwitterAuthClient authClient = new TwitterAuthClient();
            authClient.requestEmail(session, new Callback<String>() {
                @Override
                public void success(final Result<String> result) {
                    // Do something with the result, which provides the email address
                    Log.d(TAG, "Email............" + result.data);


                    final DatabaseReference myRef = databaseReference.child("users_auth");
                    twitter_mAuth_found_bool = false;
                   twitter_pressed_check=false;
                    twitter_child_count = 0;

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren())
                            {

                                if (twitter_pressed_check==false)
                                {
                                    boolean twitter_child_check = false;
                                    String curr_email = "";
                                    twitter_child_count = twitter_child_count + 1;
                                    Log.d(TAG, "Children Count is......." + String.valueOf(dataSnapshot.getChildrenCount()));
                                    for (DataSnapshot dsp : snapshot.getChildren()) {

                                        if (dsp.getKey().equals("email")) {
                                            curr_email = dsp.getValue().toString();
                                            Log.d(TAG, curr_email);
                                            if (curr_email.equals(result.data)) {
                                                Log.d(TAG, "Email matched!");
                                                twitter_mAuth_found_bool = true;
                                                twitter_child_check = true;
                                            } else {
                                                Log.d(TAG, "Mismatched");
                                                twitter_child_check = false;
                                            }
                                        }
                                        else if (dsp.getKey().equals("phone"))
                                        {
                                            twitter_child_check=false;
                                        }

                                        else if (dsp.getKey().equals("type")) {
                                            if (twitter_child_check) {
                                                if (!dsp.getValue().equals("twitter")) {
                                                    error_bar();
                                                     show_snackbar(getResources().getString(R.string.cant_sign_up_your_account_is_signed_up_with) + dsp.getValue(),true);
                                                    //Toast.makeText(getApplicationContext(), "Can't SignUp, your account is signed up with: " + dsp.getValue(), Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    AuthCredential credential = TwitterAuthProvider.getCredential(
                                                            session.getAuthToken().token,
                                                            session.getAuthToken().secret);
/*
        mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        */


                                                    mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if (task.isSuccessful()) {
                                                                        twitter_pressed_check=true;
                                                                        // Sign in success, update UI with the signed-in user's information
                                                                        sharedPreferences.edit().putString("type", "twitter").apply();
                            /*
                            Intent i = new Intent(MainActivity.this, Main3Activity.class);
                            startActivity(i);
                            */
                                                                        if (mAuth!=null && mAuth.getCurrentUser() != null) {
                                                                            if (mAuth.getCurrentUser().getEmail() != null) {
                                    /*
                                    Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                    startActivity(i);
                                    */
                                                                                db_account_type(mAuth.getCurrentUser().getEmail(), "twitter");
                                                                            }
                                                                            else {
                                                                                error_bar();
                                                                            }
                                                                            // Sign in success, update UI with the signed-in user's information

                                                                        }
                                                                        else {
                                                                            error_bar();
                                                                        }
                                                                        Log.d(TAG, "signInWithCredential:success");


                                                                    } else {
                                                                        // If sign in fails, display a message to the user.
                                                                        error_bar();
                                                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                                        Toast.makeText(MainActivity.this, getResources().getString(R.string.auth_failed),
                                                                                Toast.LENGTH_SHORT).show();

                                                                    }

                                                                    // ...
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    }


                                    if (twitter_child_count == dataSnapshot.getChildrenCount()) {
                                        twitter_pressed_check=true;
                                        Log.d(TAG, "Child count greater than number of children!");
                                        if (!twitter_mAuth_found_bool) {
                                            AuthCredential credential = TwitterAuthProvider.getCredential(
                                                    session.getAuthToken().token,
                                                    session.getAuthToken().secret);
/*
        mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        */


                                            mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // Sign in success, update UI with the signed-in user's information
                                                                sharedPreferences.edit().putString("type", "twitter").apply();
                            /*
                            Intent i = new Intent(MainActivity.this, Main3Activity.class);
                            startActivity(i);
                            */
                                                                if (mAuth!=null && mAuth.getCurrentUser() != null) {
                                                                    if (mAuth.getCurrentUser().getEmail() != null) {
                                    /*
                                    Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                    startActivity(i);
                                    */
                                                                        db_account_type(mAuth.getCurrentUser().getEmail(), "twitter");
                                                                    }
                                                                    else {
                                                                        error_bar();
                                                                    }
                                                                    // Sign in success, update UI with the signed-in user's information

                                                                }
                                                                else {
                                                                    error_bar();
                                                                }
                                                                Log.d(TAG, "signInWithCredential:success");


                                                            } else {
                                                                error_bar();
                                                                // If sign in fails, display a message to the user.
                                                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                                Toast.makeText(MainActivity.this, getResources().getString(R.string.auth_failed),
                                                                        Toast.LENGTH_SHORT).show();

                                                            }

                                                            // ...
                                                        }
                                                    });

                                        }
                                    }
                                    Log.d(TAG, "Twitter Child Count value: " + String.valueOf(twitter_child_count));
                                }





                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                           //Toast.makeText(getApplicationContext(), "Error retrieving data from Data Base!",Toast.LENGTH_SHORT).show();
                            error_bar();

                        }
                    });














/*

                //    myRef.addChildEventListener(new ChildEventListener() {
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


                }

                @Override
                public void failure(TwitterException exception) {
                    error_bar();
                    // Do something on failure
                }
            });
     //   }

    }

    private void handleFacebookAccountKitAccessToken(com.facebook.accountkit.AccessToken token)
    {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                // Get Account Kit ID
                String accountKitId = account.getId();

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                if (phoneNumber != null) {
                    String phoneNumberString = phoneNumber.toString();
                    Toast.makeText(getApplicationContext(), phoneNumberString,Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Phone",Toast.LENGTH_SHORT).show();
                }

                // Get email
                String email = account.getEmail();
                if (email!=null)
                {

                    Toast.makeText(getApplicationContext(), "User's email is: " + email,Toast.LENGTH_SHORT).show();


                }
                else {
                   // Toast.makeText(getApplicationContext(), "No Email",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(final AccountKitError error) {
                // Handle Error
            }
        });





        com.facebook.accountkit.AccessToken accessToken = AccountKit.getCurrentAccessToken();

        if (accessToken != null) {
            //Handle Returning User
            /*

            mAuth.signInWithCustomToken(token.toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(getApplicationContext(), "Sign Up successful", Toast.LENGTH_SHORT).show();
                }
            });
            */




        }














    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);


        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            mAuth.signOut();
            Toast.makeText(getApplicationContext(), "Now tap again!",Toast.LENGTH_SHORT).show();

        }


        else {



           // Toast.makeText(getApplicationContext(), "yeahhh one", Toast.LENGTH_SHORT).show();
            final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            user_current_facebook_email = "";

/*
            GraphRequestAsyncTask request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                    /*


                }
            });


*/


        //  show_snackbar("Please Wait!",true);
            

            GraphRequest request = GraphRequest.newMeRequest(
                    token,
                    new GraphRequest.GraphJSONObjectCallback() {





                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {


                           start_bar();

                           // Toast.makeText(getApplicationContext(), "yeahhh", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, object.optString("email"));
                            Log.d(TAG, object.optString("name"));
                            Log.d(TAG, object.optString("id"));
                            Log.d(TAG, "Email" + object.optString("email"));
                            Log.d(TAG, "Yeaahhhhhhhh" + object.toString());
                            user_current_facebook_email = object.optString("email");
                            sharedPreferences.edit().putString("fb_id", object.optString("id")).apply();


                            facebook_pressed_check = false;


                            final DatabaseReference myRef = databaseReference.child("users_auth");
                            facebook_mAuth_found_bool = false;

                            facebook_child_count = 0;



                          //  if (user_current_facebook_email.contains(".com")) {


                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot:dataSnapshot.getChildren())
                                    {

                                        if (facebook_pressed_check == false ) {
                                            boolean facebook_child_check = false;
                                            String curr_email = "";
                                            facebook_child_count = facebook_child_count + 1;
                                            Log.d(TAG, "Children Count is......." + String.valueOf(dataSnapshot.getChildrenCount()));
                                            for (DataSnapshot dsp : snapshot.getChildren()) {

                                                if (dsp.getKey().equals("email")) {
                                                    curr_email = dsp.getValue().toString();
                                                    Log.d(TAG, curr_email);
                                                    if (curr_email.equals(user_current_facebook_email)) {
                                                        Log.d(TAG, "Email matched!");
                                                        facebook_mAuth_found_bool = true;
                                                        facebook_child_check = true;
                                                    } else {
                                                        Log.d(TAG, "Mismatched");
                                                        facebook_child_check = false;
                                                    }
                                                }

                                                else if (dsp.getKey().equals("phone"))
                                                {
                                                    facebook_child_check=false;
                                                }



                                                else if (dsp.getKey().equals("type")) {
                                                    if (facebook_child_check) {
                                                        if (!dsp.getValue().equals("facebook")) {
                                                           error_bar();

                                                                      show_snackbar(getResources().getString(R.string.cant_sign_up_your_account_is_signed_up_with) + dsp.getValue(),true);
                                                            //Toast.makeText(getApplicationContext(), "Can't SignUp, your account is signed up with: " + dsp.getValue(), Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //  if (user_current_facebook_email.contains(".com"))

                                                            // {



                                                            if (mAuth!=null && mAuth.getCurrentUser() == null)
                                                            {

                                                                mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    facebook_pressed_check = true;
                                                                                    if ( mAuth.getCurrentUser().getPhotoUrl() != null) {
                                                                                        Log.d(TAG, mAuth.getCurrentUser().getPhotoUrl().toString());


                                                                                    } else {
                                                                                        //Toast.makeText(getApplicationContext(), "No Photo", Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                    /*
                                                                                    email_text.setText("");
                                                                                    passwordText.setText("");
                                                                                    */
                                                                                    sharedPreferences.edit().putString("type", "facebook").apply();
                                                                        /*
                                                                        if (mAuth.getCurrentUser()!=null) {
                                                                            if (mAuth.getCurrentUser().getEmail()!=null) {

                                   // Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                   // startActivity(i);


                                                                                db_account_type(mAuth.getCurrentUser().getEmail(),"facebook");
                                                                            }
                                                                            // Sign in success, update UI with the signed-in user's information
                                                                            Log.d(TAG, "signInWithCredential:success");

                                                                        }
*/        ///////////////////////////////////////////////////////////////// new logic ///////////////////////////////////////////////////////////////////////


                                                                                    if (mAuth!=null && mAuth.getCurrentUser() != null) {
                                                                                        //  if (mAuth.getCurrentUser().getEmail() != null && user_current_facebook_email.contains(".com")) {
                                    /*
                                    Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                    startActivity(i);
                                    */

                                                                                        db_account_type(mAuth.getCurrentUser().getEmail(), "facebook");
                                                                                        // } else {
                                                                                        // Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                                                                        // startActivity(i);
                                                                                        // }

                                                                                    }
                                                                                    else {
                                                                                        error_bar();
                                                                                    }

                                                                                    if (mAuth!=null && mAuth.getCurrentUser().isEmailVerified()) {
                               /*
                              Intent i = new Intent(MainActivity.this, Main3Activity.class);
                              startActivity(i);
                              */

                                                                                    } else {
                               /*
                               mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()) {
                                           Intent i = new Intent(MainActivity.this, Main2Activity.class);
                                           startActivity(i);
                                           Toast.makeText(getApplicationContext(), "Please verify Email", Toast.LENGTH_SHORT).show();
                                           Log.d(TAG, "Not Verified");
                                       }
                                       else {
                                           Toast.makeText(getApplicationContext(), "Error sending Email!", Toast.LENGTH_SHORT).show();

                                       }
                                   }
                               });
                               */


                                                                                    }


                                                                                } else {
                                                                                  error_bar();
                                                                                    // If sign in fails, display a message to the user.
                                                                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.auth_failed),
                                                                                            Toast.LENGTH_SHORT).show();

                                                                                }

                                                                                // ...
                                                                            }
                                                                        });
                                                            }
                                                            else {
                                                                error_bar();
                                                            }


                                                            //  }
                                                                /*
                                                                else {
                                                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                                                myFile.writeLine(currentDateTimeString + ": handle Facebook access token Graph API request completed login facebook sign up not available");
                                                                Toast.makeText(getApplicationContext(), "Facebook Sign up is not available, please choose another method!", Toast.LENGTH_SHORT).show();
                                                                */

                                                        }

                                                    }
                                                }
                                            }

                                            if (facebook_child_count ==dataSnapshot.getChildrenCount()) {

                                                facebook_pressed_check = true;

                                                Log.d(TAG, "Child count greater than number of children!");
                                                if (!facebook_mAuth_found_bool) {

                                                    //  Toast.makeText(getApplicationContext(), String.valueOf(dataSnapshot.getChildrenCount()),Toast.LENGTH_SHORT).show();

                                                    //   if (user_current_facebook_email.contains(".com"))

                                                    //         {




                                                    if (mAuth!=null && mAuth.getCurrentUser()==null) {


                                                        mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                                        if (task.isSuccessful()) {

                                                                            if (mAuth.getCurrentUser().getPhotoUrl() != null) {
                                                                                Log.d(TAG, mAuth.getCurrentUser().getPhotoUrl().toString());

                                                                            } else {
                                                                                //Toast.makeText(getApplicationContext(), "No Photo", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            //    email_text.setText("");
                                                                            //    passwordText.setText("");
                                                                            sharedPreferences.edit().putString("type", "facebook").apply();
                                                                            if (mAuth.getCurrentUser() != null) {
                                                                                //   if (mAuth.getCurrentUser().getEmail() != null && user_current_facebook_email.contains(".com")) {
                                    /*
                                    Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                    startActivity(i);currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                                                                            myFile.writeLine(currentDateTimeString + ": handle Facebook access token Graph API request completed login success db_account_called");
                                    */

                                                                                db_account_type(mAuth.getCurrentUser().getEmail(), "facebook");
                                                                                //  }

                                                                                //else {

                                                                                //    Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                                                                //    startActivity(i);

                                                                                // }
                                                                                // Sign in success, update UI with the signed-in user's information
                                                                                Log.d(TAG, "signInWithCredential:success");

                                                                            }
                                                                            else {
                                                                                error_bar();
                                                                            }

                                                                            if (mAuth!=null && mAuth.getCurrentUser().isEmailVerified()) {
                               /*
                              Intent i = new Intent(MainActivity.this, Main3Activity.class);
                              startActivity(i);
                              */

                                                                            } else {

                               /*
                               mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()) {
                                           Intent i = new Intent(MainActivity.this, Main2Activity.class);
                                           startActivity(i);
                                           Toast.makeText(getApplicationContext(), "Please verify Email", Toast.LENGTH_SHORT).show();
                                           Log.d(TAG, "Not Verified");
                                       }
                                       else {
                                           Toast.makeText(getApplicationContext(), "Error sending Email!", Toast.LENGTH_SHORT).show();

                                       }
                                   }
                               });
                               */


                                                                            }


                                                                        } else {
                                                                            error_bar();
                                                                            // If sign in fails, display a message to the user.
                                                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                                                    Toast.LENGTH_SHORT).show();

                                                                        }

                                                                        // ...
                                                                    }
                                                                });
                                                    }
                                                    else {
                                                        error_bar();
                                                    }




                                                    //  }

                                                    /*
                                                    else {
                                                        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                                        myFile.writeLine(currentDateTimeString + ": handle Facebook access token Graph API request completed login facebook sign up not available");

                                                        Toast.makeText(getApplicationContext(), "Facebook Sign up is not available, please choose another method!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    */

                                                }

                                                // Log.d(TAG, "Twitter Child Count value: " + String.valueOf(twitter_child_count));
                                            }


                                        }
                                        else {
                                            error_bar();
                                        }









                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                 error_bar();
                               // Toast.makeText(getApplicationContext(), "Error retreiving data from database!",Toast.LENGTH_SHORT).show();
                                }
                            });
















                            /*

                                myRef.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                        }
                                   // }


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

                          //  }
                                /*
                            else {
                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                myFile.writeLine(currentDateTimeString + ": handle Facebook access token Graph API request completed login facebook sign up not available");
                                Toast.makeText(getApplicationContext(), "Facebook Sign up is not available, please choose another method!", Toast.LENGTH_SHORT).show();

                            }
                            */
                        }
                        

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,email");
            request.setParameters(parameters);
            request.executeAsync();


        }

    }

    private void sendEmail()
    {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"areeb_waseem@yahoo.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }






    }


    private void db_phone_auth_account(final String phone_no)
    {

        if (mAuth!=null && mAuth.getCurrentUser()!=null) {


            final DatabaseReference auth_ref= databaseReference.child("users");
            auth_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                    {
                        error_bar();
                       // store_token();
                        Intent i = new Intent(MainActivity.this, home_activity.class);
                        startActivity(i);

                    }
                    else {


                        databaseReference.child("users_auth").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (is_activity_running)
                                {
                                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()) && dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild("is_suspended")) {
                                        databaseReference.child("users_auth").child((mAuth.getCurrentUser().getUid())).child("is_suspended").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (is_activity_running) {
                                                    if (task.isSuccessful()) {
                                                        error_bar();
                                                        Intent i = new Intent(MainActivity.this, home_activity.class);
                                                        startActivity(i);
                                                    } else {
                                                        error_bar();
                                                    }
                                                }
                                            }
                                        });
                                    } else {

                                        Map<String, Object> taskMap = new HashMap<>();
                                        taskMap.put("phone", phone_no);
                                        taskMap.put("type", "phone_auth");
                                        databaseReference.child("users_auth").child(mAuth.getCurrentUser().getUid()).setValue(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    error_bar();
                                                    type_set = true;
                                                    sharedPreferences.edit().putString("type", "phone_auth").apply();
                                                    sharedPreferences.edit().putString("account_kit_phone_no", phone_no).apply();
                                                    // store_token();
                                                    Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                                    startActivity(i);
                                                } else {
                                                    error_bar();
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                    type_set = false;
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                if (is_activity_running) {
                                    error_bar();
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
        else {
            error_bar();
        }

    }

    private void db_account_type(final String email_to_db, final String type_to_db)
    {

        /*
        if (!nick_edit_text.getText().toString().equals("")) {
            taskMap.put("name", nick_edit_text.getText().toString());
        }
*/

        if (mAuth!=null&&mAuth.getCurrentUser()!=null) {

            final DatabaseReference auth_ref= databaseReference.child("users");
            auth_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                    {
                        error_bar();
                       // store_token();
                        Intent i = new Intent(MainActivity.this, home_activity.class);
                        startActivity(i);

                    }

                    else {



                        databaseReference.child("users_auth").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (is_activity_running)
                                {
                                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()) && dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild("is_suspended")) {
                                        databaseReference.child("users_auth").child((mAuth.getCurrentUser().getUid())).child("is_suspended").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (is_activity_running) {
                                                    if (task.isSuccessful()) {
                                                        error_bar();
                                                        Intent i = new Intent(MainActivity.this, home_activity.class);
                                                        startActivity(i);
                                                    } else {
                                                        error_bar();
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        Map<String, Object> taskMap = new HashMap<>();
                                        taskMap.put("email", email_to_db);
                                        taskMap.put("type", type_to_db);

                                        databaseReference.child("users_auth").child(mAuth.getCurrentUser().getUid()).setValue(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (is_activity_running) {
                                                    if (task.isSuccessful()) {
                                                        error_bar();
                                                        type_set = true;

                                                        if (type_to_db.equals("email")) {

                                                            Intent i = new Intent(MainActivity.this, Main2Activity.class);
                                                            startActivity(i);
                                                        } else {
                                                            //  store_token();
                                                            Intent i = new Intent(MainActivity.this, Main3Activity.class);
                                                            startActivity(i);
                                                        }
                                                    } else {
                                                        error_bar();
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                                        type_set = false;
                                                    }
                                                }
                                            }
                                        });
                                    }
                            }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                if (is_activity_running) {
                                    error_bar();
                                }
                            }
                        });



                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (is_activity_running) {
                        error_bar();
                    }

                }
            });






        }
        else {
            error_bar();
        }




    }

    private void check_for_auth_type(final String user_email_to_check){




       // if (initial_child_count!=0) {
            final DatabaseReference myRef = databaseReference.child("users_auth");
            mAuth_found_bool = false;

            child_count = 0;





            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren())
                    {

                        if (check_auth_type==false) {
                            boolean child_check = false;
                            String curr_email = "";
                            child_count = child_count + 1;
                            for (DataSnapshot dsp  : snapshot.getChildren()) {

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

                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.cant_send_emai_your_account)+ dsp.getValue(), Toast.LENGTH_SHORT).show();
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

      //  }



    }

    private void send_email_for_forget_password(String res_email)
    {


        mAuth.sendPasswordResetEmail(res_email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    public void phoneLogin() {

        final Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);

    }

    @Override
    protected void onDestroy() {
      //  mSocket.disconnect();
        super.onDestroy();
        is_activity_running=false;
    }

    private void getCustomToken(final com.facebook.accountkit.AccessToken accessToken) {

        Log.d(TAG, "Getting custom token for Account Kit access token: " + accessToken.getToken());
        mCloudFunctions.getCustomToken(accessToken.getToken()).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        final String customToken = response.body().string();
                        Log.d(TAG, "Custom token: " + customToken);
                        if (customToken!=null) {
                            signInWithCustomToken(customToken);
                           // Toast.makeText(getApplicationContext(), "Yeah got custom access token", Toast.LENGTH_SHORT ).show();
                        }else {
                           error_bar();
                            Toast.makeText(getApplicationContext(), "null custom token", Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                       error_bar();
                        Log.e(TAG, response.errorBody().string());
                        Toast.makeText(getApplicationContext(), "Custom token response error!", Toast.LENGTH_SHORT).show();

                    }
                } catch (IOException e) {
                   error_bar();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable e) {
                error_bar();
                Log.e(TAG, "Request getCustomToken failed", e);
                Toast.makeText(getApplicationContext(), "Custom Token Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithCustomToken(String customToken) {
        mAuth.signInWithCustomToken(customToken).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "getCustomToken:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful())
                        {
                      db_phone_auth_account(account_kit_phone_auth);
                        }
                        else if (!task.isSuccessful()) {
                           error_bar();
                            Log.w(TAG, "getCustomToken", task.getException());
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.auth_failed),
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    public void error_bar()
    {
        myBar.setVisibility(View.INVISIBLE);
        bottom_linear_layout.setVisibility(View.VISIBLE);
        facebook_login_custom.setClickable(true);
        twitter_login_button_custom.setClickable(true);
        verify_phone_button.setClickable(true);
        reg_email.setClickable(true);

    }
    public void start_bar()
    {
        bottom_linear_layout.setVisibility(View.INVISIBLE);
        myBar.setVisibility(View.VISIBLE);
        facebook_login_custom.setClickable(false);
        twitter_login_button_custom.setClickable(false);
        verify_phone_button.setClickable(false);
        reg_email.setClickable(false);

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
            mView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
            TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
            mTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
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

    /*

    private void store_token()
    {
        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful())
                    {

                     databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue(task.getResult().getToken()).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {

                         }
                     });
                    }else {

                    }
                }
            });
        }

    }

*/

    private void sign_up_attempt()
    {
        if (mAuth!=null && mAuth.getCurrentUser()!=null)
        {
            mAuth.signOut();
            Toast.makeText(getApplicationContext(), "Now tap again!",Toast.LENGTH_SHORT).show();

        }
        else {
            line_lay_4_join.setVisibility(View.INVISIBLE);
            verify_phone_button.setVisibility(View.INVISIBLE);
            terms_and_privacy_layout.setVisibility(View.VISIBLE);
        }

    }







}
