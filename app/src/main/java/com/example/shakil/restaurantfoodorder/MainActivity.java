package com.example.shakil.restaurantfoodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shakil.restaurantfoodorder.Common.Common;
import com.example.shakil.restaurantfoodorder.Model.User;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {

    //private static final int REQUEST_CODE = 99; /*7171*/
    /*Button btnContinue;*/
    Button btnSignIn, btnSignUp;
    TextView textSlogan;

    /*FirebaseDatabase database;
    DatabaseReference users;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //AccountKit.initialize(this);
        setContentView(R.layout.activity_main);

        printKeyHash();

        //Init Firebase
        /*database = FirebaseDatabase.getInstance();
        users = database.getReference("User");*/


        //btnContinue = findViewById(R.id.btn_continue);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        textSlogan = findViewById(R.id.textSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        textSlogan.setTypeface(face);

        //init paper
        Paper.init(this);

        /*btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginSystem();

            }
        });*/

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        //check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if (user != null && pwd != null){
            if (!user.isEmpty() && !pwd.isEmpty()){
                login(user, pwd);
            }
        }

        //check session facebook account kit
        /*if (AccountKit.getCurrentAccessToken() != null){
            //create dialog

            //Show dialog
            final AlertDialog waitingDialog = new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Please wait");
            waitingDialog.setCancelable(false);

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    //Login
                    users.child(account.getPhoneNumber().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User localUser = dataSnapshot.getValue(User.class);
                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = localUser;
                            startActivity(homeIntent);

                            //Dismiss dialog
                            waitingDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });

        }*/
    }

    /*private void startLoginSystem() {
        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new
                AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.CODE);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }*/

    private void printKeyHash() {
        try {

            PackageInfo info = getPackageManager().getPackageInfo("com.example.shakil.restaurantfoodorder", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void login(final String phone, final String pwd) {
        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please waiting....");
            mDialog.show();


            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Check if user not exist in database
                    if (dataSnapshot.child(phone).exists()) {

                        //Get User Information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone); //Set Phone
                        if (user.getPassword().equals(pwd)) {

                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();

                                Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password !!!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exist in Database", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Please check your internet connection !!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() != null){
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            else if (result.wasCancelled()){
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                if (result.getAccessToken() != null){
                    //Show dialog
                    final AlertDialog waitingDialog = new SpotsDialog(this);
                    waitingDialog.show();
                    waitingDialog.setMessage("Please wait");
                    waitingDialog.setCancelable(false);

                    //Get current phone
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            final String userPhone = account.getPhoneNumber().toString();

                            //check if exists on firebase users
                            users.orderByKey().equalTo(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //if not exists
                                    if (!dataSnapshot.child(userPhone).exists()){
                                        // we will create new user and login
                                        User newUser = new User();
                                        newUser.setPhone(userPhone);
                                        newUser.setName("");

                                        //add to firebase
                                        users.child(userPhone).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(MainActivity.this, "User registration successful.", Toast.LENGTH_SHORT).show();

                                                }

                                                //Login
                                                users.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        User localUser = dataSnapshot.getValue(User.class);
                                                        Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                        Common.currentUser = localUser;
                                                        startActivity(homeIntent);

                                                        //Dismiss dialog
                                                        waitingDialog.dismiss();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                    //if exists
                                    else {
                                        //Login
                                        users.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User localUser = dataSnapshot.getValue(User.class);
                                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                Common.currentUser = localUser;
                                                startActivity(homeIntent);

                                                //Dismiss dialog
                                                waitingDialog.dismiss();
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(MainActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }
    }*/

}
