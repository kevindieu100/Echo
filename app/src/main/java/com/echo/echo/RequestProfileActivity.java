package com.echo.echo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RequestProfileActivity extends AppCompatActivity {
    private Button acceptButton;
    private Button ignoreButton;
    private LocalUser localUser;
    private String requestsUsername;
    private ArrayList<String> linksReceived;

    private Button snapButton;
    private Button linkedButton;
    private Button facebookButton;
    private Button instaButton;
    private Button twitterButton;
    private Button emailButton;
    private Button resumeButton;
    private Button phoneButton;
    private TextView usernameLabel;

    private boolean fbSelect = true;
    private boolean snapSelect = true;
    private boolean linkedSelect = true;
    private boolean instaSelect = true;
    private boolean twitterSelect = true;
    private boolean phoneSelect = true;
    private boolean emailSelect = true;
    private boolean resumeSelect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestsUsername = (String)getIntent().getSerializableExtra("Username");

        linksReceived = (ArrayList<String>)getIntent().getSerializableExtra("Links");

        initializeVariables();
        disableButtons();
        addListeners();
    }

    private void initializeVariables() {
        localUser = new LocalUser();
        localUser = LocalUser.getInstance();

        acceptButton = (Button) findViewById(R.id.confirmButton);
        ignoreButton = (Button) findViewById(R.id.rejectButton);
        usernameLabel = (TextView) findViewById(R.id.requestName);
        usernameLabel.setText(requestsUsername);

        facebookButton = (Button)findViewById(R.id.facebook_linkbutton);
        twitterButton = (Button)findViewById(R.id.twitter_linkbutton);
        instaButton = (Button)findViewById(R.id.ins_linkbutton);
        snapButton = (Button)findViewById(R.id.snap_linkbutton);
        linkedButton = (Button)findViewById(R.id.linkin_linkbutton);
        resumeButton = (Button)findViewById(R.id.resume_linkbutton);
        phoneButton = (Button)findViewById(R.id.phone_linkbutton);
        emailButton = (Button)findViewById(R.id.gmail_linkbutton);
    }
    private void addListeners() {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection temp = new Connection(requestsUsername);
                temp.linkKeys = linksReceived;
                localUser.addNewConnection(temp);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(localUser.userName).child("friends").child(requestsUsername);
                mDatabase.setValue(linksReceived);
                DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("users").child(requestsUsername).child("friends").child(localUser.userName);
                mDatabase2.setValue(getLinks());
                //Help save this change pl0x :(
                //LocalUser.saveUser(localUser);
                removeRequestFromDatabase();
                resetButtons();
                disableButtons();
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fbSelect == true) {
                    fbSelect = false;
                    facebookButton.setAlpha(0.2f);
                } else {
                    fbSelect = true;
                    facebookButton.setAlpha(1.0f);
                }
            }
        });

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (twitterSelect == true) {
                    twitterSelect = false;
                    twitterButton.setAlpha(0.2f);
                } else {
                    twitterSelect = true;
                    twitterButton.setAlpha(1.0f);
                }
            }
        });

        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (snapSelect == true) {
                    snapSelect = false;
                    snapButton.setAlpha(0.2f);
                } else {
                    snapSelect = true;
                    snapButton.setAlpha(1.0f);
                }
            }
        });

        linkedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linkedSelect == true) {
                    linkedSelect = false;
                    linkedButton.setAlpha(0.2f);
                } else {
                    linkedSelect = true;
                    linkedButton.setAlpha(1.0f);
                }
            }
        });

        instaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instaSelect == true) {
                    instaSelect = false;
                    instaButton.setAlpha(0.2f);
                } else {
                    instaSelect = true;
                    instaButton.setAlpha(1.0f);
                }
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resumeSelect == true) {
                    resumeSelect = false;
                    resumeButton.setAlpha(0.2f);
                } else {
                    resumeSelect = true;
                    resumeButton.setAlpha(1.0f);
                }
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailSelect == true) {
                    emailSelect = false;
                    emailButton.setAlpha(0.2f);
                } else {
                    emailSelect = true;
                    emailButton.setAlpha(1.0f);
                }
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneSelect == true) {
                    phoneSelect = false;
                    phoneButton.setAlpha(0.2f);
                } else {
                    phoneSelect = true;
                    phoneButton.setAlpha(1.0f);
                }
            }
        });

        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRequestFromDatabase();
            }
        });
    }
    public ArrayList<String> getLinks() {
        ArrayList<String> temp = new ArrayList<>();
        if(emailSelect) {
            temp.add("email");
        }
        if(fbSelect) {
            temp.add("facebook");
        }
        if(instaSelect) {
            temp.add("instagram");
        }
        if(linkedSelect) {
            temp.add("linkedin");
        }
        if(twitterSelect) {
            temp.add("twitter");
        }
        if(phoneSelect) {
            temp.add("phoneNumber");
        }
        if(resumeSelect) {
            temp.add("resume");
        }
        if(snapSelect) {
            temp.add("snapchat");
        }
        return temp;
    }

    private void disableButtons() {
        if(!localUser.hasEmail()) {
            emailButton.setEnabled(false);
            emailSelect = false;
            emailButton.setAlpha(0.5f);
        }
        if(!localUser.hasFacebook()) {
            facebookButton.setEnabled(false);
            fbSelect = false;
            facebookButton.setAlpha(0.5f);
        }
        if(!localUser.hasInstagram()) {
            instaButton.setEnabled(false);
            instaSelect = false;
            instaButton.setAlpha(0.5f);
        }
        if(!localUser.hasLinkedin()) {
            linkedButton.setEnabled(false);
            linkedSelect = false;
            linkedButton.setAlpha(0.5f);
        }
        if(!localUser.hasTwitter()) {
            twitterButton.setEnabled(false);
            twitterSelect = false;
            twitterButton.setAlpha(0.5f);
        }
        if(!localUser.hasPhoneNumber()) {
            phoneButton.setEnabled(false);
            phoneSelect = false;
            phoneButton.setAlpha(0.5f);
        }
        if(!localUser.hasResume()) {
            resumeButton.setEnabled(false);
            resumeSelect = false;
            resumeButton.setAlpha(0.5f);
        }
        if(!localUser.hasSnapchat()) {
            snapButton.setEnabled(false);
            snapSelect = false;
            snapButton.setAlpha(0.5f);
        }
    }

    private void resetButtons() {
        facebookButton.setEnabled(true);
        fbSelect = true;
        facebookButton.setAlpha(1.0f);

        instaButton.setEnabled(true);
        instaSelect = true;
        instaButton.setAlpha(1.0f);

        snapButton.setEnabled(true);
        snapSelect = true;
        snapButton.setAlpha(1.0f);

        emailButton.setEnabled(true);
        emailSelect = true;
        emailButton.setAlpha(1.0f);

        resumeButton.setEnabled(true);
        resumeSelect = true;
        resumeButton.setAlpha(1.0f);

        phoneButton.setEnabled(true);
        phoneSelect = true;
        phoneButton.setAlpha(1.0f);

        twitterButton.setEnabled(true);
        twitterSelect = true;
        twitterButton.setAlpha(1.0f);

        linkedButton.setEnabled(true);
        linkedSelect = true;
        linkedButton.setAlpha(1.0f);
    }

    private void removeRequestFromDatabase() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(localUser.userName).child("requests");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        if(data.getKey().equals(requestsUsername)) {
                            mDatabase.child(data.getKey()).removeValue();
                        }
                    }
                }
                Intent fromRequestToMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(fromRequestToMain);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
