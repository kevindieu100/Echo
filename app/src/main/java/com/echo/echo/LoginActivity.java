package com.echo.echo;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import org.w3c.dom.Text;

import java.lang.Character;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {
    protected DatabaseReference dataBase;
    protected DatabaseReference validation;
    protected String user;
    protected String pass;
    protected DatabaseReference key;
    protected boolean b;
    protected TextInputLayout username;
    protected TextInputLayout password;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);
        dataBase = FirebaseDatabase.getInstance().getReference().child("users");
        Button loginButton = (Button)findViewById(R.id.email_sign_in_button);
        username = (TextInputLayout)findViewById(R.id.emailText);
        password = (TextInputLayout)findViewById(R.id.passwordText);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithCredentials(username.getEditText().getText().toString(), password.getEditText().getText().toString());
            }
        });

        EditText loginKeyboard = (EditText)findViewById(R.id.password);

        loginKeyboard.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return false;
                if(keyCode == KeyEvent.KEYCODE_ENTER ){
                    loginWithCredentials(username.getEditText().getText().toString(), password.getEditText().getText().toString());
                    return true;
                }
                return false;
            }
        });

        Button signupButton = (Button)findViewById(R.id.email_sign_up_button);

        signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //clears the local user
                LocalUser u = new LocalUser();
                u.saveUser(null);
                Intent fromLoginToSignupIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(fromLoginToSignupIntent);
            }
        });

        Button GuestSignIn_Button = (Button)findViewById(R.id.sign_in_asGuest_Button);

        GuestSignIn_Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fromLoginToGuestLogin = new Intent(getApplicationContext(), Guest_Login_Activity.class);
                startActivity(fromLoginToGuestLogin);
            }
        });
    }

    public void loginWithCredentials(String username, String password) {
        if(!isValid(username)) {
            Toast.makeText(this, "Please enter a username!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!(username != null && username.trim().length() > 0)) {
            Toast.makeText(this, "Please enter a username!", Toast.LENGTH_LONG).show();
        } else if (!(password != null)) {
            Toast.makeText(this, "Please enter a password!", Toast.LENGTH_LONG).show();
        } else {
            pass = password;
            user = username;
            userExists(user);
        }
    }

    private void userExists(String userName){
        //queries the firebase database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userName);
        mDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // TODO: handle the case where the data already exists
                    passMatches(pass);
                }
                else {
                    userError();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }//end of userExists

    private void passMatches(final String mpassword){
        //queries the firebase database
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user).child("pw");
        mDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // TODO: handle the case where the data already exists
                    //login
                    Object o = snapshot.getValue();
                    if(o.equals(mpassword)) {
                        updateLocalUser();
                        Intent fromLoginToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(fromLoginToMainIntent);
                    }
                    else {
                        passwordError();
                    }
                }
                else {
                    passwordError();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }//end of userExists

    public void userError() {
        Toast.makeText(this, "Invalid username!", Toast.LENGTH_LONG).show();
    }
    public void passwordError() {
        Toast.makeText(this, "Invalid password!", Toast.LENGTH_LONG).show();
    }
    public boolean isValid(String username) {
        String badSet = ".$[]#/";
        for(int i = 0; i < username.length(); ++i) {
            for(int j = 0; j < badSet.length(); ++j) {
                if(username.charAt(i) == badSet.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    //called on login to create the new local user
    private void updateLocalUser(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user);
        mDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    LocalUser localUser = new LocalUser();
                    localUser.saveUser(null);
                    localUser = localUser.getInstance();
                    for(com.google.firebase.database.DataSnapshot data : snapshot.getChildren()){
                        if(String.valueOf(data.getKey()).equals("email")){
                            localUser.email = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("facebook")){
                            localUser.facebook = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("instagram")){
                            localUser.instagram = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("linkedin")){
                            localUser.linkedin = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("phoneNumber")){
                            localUser.phoneNumber = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("resume")){
                            localUser.resume = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("snapchat")){
                            localUser.snapchat = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("twitter")){
                            localUser.twitter = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("fullName")){
                            localUser.fullName = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("pathToImage")){
                            localUser.pathToImage = data.getValue().toString();
                        }
                        else if(String.valueOf(data.getKey()).equals("friends")){
                            ArrayList<Connection> connectionList = new ArrayList<Connection>();
                            for(com.google.firebase.database.DataSnapshot d : data.getChildren()){
                                Connection c = new Connection();
                                c.username = d.getKey();
                                for(com.google.firebase.database.DataSnapshot links : d.getChildren()){
                                    c.addLinkKey((String)links.getValue());
                                }
                                connectionList.add(c);
                            }
                            localUser.setConnections(connectionList);
                        }
                    }//end of for loop

                    localUser.userName = user;
                    localUser.saveUser(localUser); //saves the local user
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //makes it so that users cant go back if theyre on login screen (this is for logout)
        moveTaskToBack(true);
    }
}