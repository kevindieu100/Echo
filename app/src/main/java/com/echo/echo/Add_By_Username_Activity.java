package com.echo.echo;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class Add_By_Username_Activity extends AppCompatActivity {
    private LocalUser localUser;
    private Button sendButton;

    private Button snapButton;
    private Button linkedButton;
    private Button facebookButton;
    private Button instaButton;
    private Button twitterButton;
    private Button emailButton;
    private Button resumeButton;
    private Button phoneButton;

    private EditText userNameText;
    private TextView warningLabel;
    private String username;
    private FloatingActionButton back_button;
    private boolean userExist;

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
        setContentView(R.layout.activity_add__by__username_);

        initializeComponents();
        disableButtons();
        addListeners();
    }

    private void initializeComponents() {
        localUser = new LocalUser();
        localUser = LocalUser.getInstance();
        sendButton = (Button)findViewById(R.id.send_button);
        userNameText = (EditText) findViewById(R.id.addUserByNameTextField);
        warningLabel = (TextView) findViewById(R.id.addByUserNameWarningLabel);
        back_button = (FloatingActionButton)findViewById(R.id.floatingActionButton);

        facebookButton = (Button)findViewById(R.id.facebook_linkbutton);
        twitterButton = (Button)findViewById(R.id.twitter_linkbutton);
        instaButton = (Button)findViewById(R.id.ins_linkbutton);
        snapButton = (Button)findViewById(R.id.snap_linkbutton);
        linkedButton = (Button)findViewById(R.id.linkin_linkbutton);
        resumeButton = (Button)findViewById(R.id.resume_linkbutton);
        phoneButton = (Button)findViewById(R.id.phone_linkbutton);
        emailButton = (Button)findViewById(R.id.gmail_linkbutton);

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fromUsertoMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(fromUsertoMain);
            }
        });
    }

    //TODO: initialize select boolean values based on what the user already has

    private void addListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = userNameText.getText().toString();
                if(username.equals(localUser.userName)) {
                    updateLabel("You cannot add yourself!");
                    clearTextField();
                }
                else {
                    userExists(username);
                }

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
                    snapButton.setAlpha(0.5f);
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
                    instaButton.setAlpha(0.5f);
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

    private void checkIfAlreadyRequested(String user) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user).child("requests");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot data : dataSnapshot.getChildren() ){
                        if(data.getValue().equals(localUser.userName)) {
                            updateLabel("You have already requested to add this user!");
                            return;
                        }
                    }
                    addUserToRequests(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateLabel(String warning) {
        warningLabel.setText(warning);
        clearTextField();
    }

    private void userExists(final String user) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //Check if user already exists in this user's request list... If yes, give warning, else call addUserToRequest(user)
                    addUserToRequests(user);
                }
                else {
                    updateLabel("User does not exist!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addUserToRequests(String user) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user).child("requests");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.child(localUser.userName).setValue(getLinks());
                updateLabel("Request successfully sent!");
                clearTextField();
                resetButtons();
                disableButtons();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void clearTextField() {
        userNameText.setText("");
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
}