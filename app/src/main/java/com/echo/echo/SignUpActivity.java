package com.echo.echo;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class SignUpActivity extends AppCompatActivity {
    private String username;
    private String password;
    private String reenter_password;
    private String Name;
    private String phoneNum;

    private FloatingActionButton backButton;
    private Button registerButton;
    private EditText username_editview;
    private EditText Name_editview;
    private EditText password_editview;
    private EditText phone_editView;
    private EditText reenterpassword_editView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        backButton = (FloatingActionButton)findViewById(R.id.back_button);
        registerButton = (Button)findViewById(R.id.sign_up_button);
        username_editview = (EditText)findViewById(R.id.username_TextView);
        Name_editview = (EditText)findViewById(R.id.name_TextView);
        password_editview = (EditText)findViewById(R.id.password_TextView);
        phone_editView = (EditText)findViewById(R.id.phone_number_editview);
        reenterpassword_editView = (EditText)findViewById(R.id.VerifyPassword_TextView);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fromSigntoLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(fromSigntoLogin);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                username = username_editview.getText().toString();
                password = password_editview.getText().toString();
                reenter_password = reenterpassword_editView.getText().toString();
                Name = Name_editview.getText().toString();
                phoneNum = phone_editView.getText().toString();

                //does simple checks if fields are empty or if passwords match
                if (checkAbleToReg()){
                    //checks if user exists, if exists displays error, if not creates user
                    userExists();
                }
            }
        });

        phone_editView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return false;
                if(keyCode == KeyEvent.KEYCODE_ENTER ){
                    username = username_editview.getText().toString();
                    password = password_editview.getText().toString();
                    reenter_password = reenterpassword_editView.getText().toString();
                    Name = Name_editview.getText().toString();
                    phoneNum = phone_editView.getText().toString();

                    //does simple checks if fields are empty or if passwords match
                    if (checkAbleToReg()){
                        //checks if user exists, if exists displays error, if not creates user
                        userExists();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    //calls functions to see if user can be created
    private boolean checkAbleToReg(){
        //check if fields are empty
        if(checkIfStringsEmpty()){
            return false;
        }
        else if(!checkIfSamePassword()){
            return false;
        }
        else if(!checkIfHasSpecialCharacters()){
            return false;
        }
        return true;
    }

    //checks if has special characters
    private boolean checkIfHasSpecialCharacters(){
        String badSet = ".$[]#/";
        for(int i = 0; i < username.length(); ++i) {
            for(int j = 0; j < badSet.length(); ++j) {
                if(username.charAt(i) == badSet.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }//checkIfHasSpecialCharacters

    //checks if both both passwords match
    private boolean checkIfSamePassword(){
        boolean samePassword = false;
        if(password.equals(reenter_password))
            samePassword = true;

        //passwords don't match, display toast
        if(!samePassword){
            Toast.makeText(this, "Error: Password fields do not match.", Toast.LENGTH_LONG).show();
        }

        return samePassword;
    }//end of checkIfSamePassword()

    //checks if any of the text fields are empty
    private boolean checkIfStringsEmpty(){
        boolean hasEmpty = false;
        String errorMessage = "Error: Please enter ";
        if(TextUtils.isEmpty(password) || password.length() < 8){
            errorMessage += "password, ";
            hasEmpty = true;
        }
        if(TextUtils.isEmpty(username)){
            errorMessage += "username, ";
            hasEmpty = true;
        }
        if(TextUtils.isEmpty(reenter_password) || reenter_password.length() < 8){
            errorMessage += "re-enter password, ";
            hasEmpty = true;
        }
        if(TextUtils.isEmpty(Name)){
            errorMessage += "name, ";
            hasEmpty = true;
        }
        if(TextUtils.isEmpty(phoneNum)){
            errorMessage += "phone number ";
            hasEmpty = true;
        }

        //displays the toast if any of the edit texts are empty
        if(hasEmpty){
            //displays toast
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
        return hasEmpty;
    }//end of checkIfStringIsEmpty()

    //checks if a user already exists with that username
    private void userExists(){
        //queries the firebase database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(username);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // TODO: handle the case where the data already exists
                    displayUserExists();

                }
                else {
                    registerUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }//end of userExists

    private void displayUserExists(){
        Toast.makeText(this, "Error: Username already exists!", Toast.LENGTH_LONG).show();
    }//end of displayUserExists

    private void registerUser (){
        Toast.makeText(this, "Registering user!", Toast.LENGTH_LONG).show();
        //creates a new localUser instance
        LocalUser localUser = new LocalUser(username, Name, password, phoneNum);
        localUser.saveUser(localUser);

        //queries the firebase database
        User u = new User(username, Name, password, phoneNum);

        //saves the user to firebase database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(username);
        mDatabase.setValue(u);

        //after creating user, moves on to MyProfile activity
        Intent fromSignUpToMyProfileIntent = new Intent(getApplicationContext(), MyProfileActivity.class);
        startActivity(fromSignUpToMyProfileIntent);
    }//end of registerUser()
}
