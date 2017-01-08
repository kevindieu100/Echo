package com.echo.echo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendProfileActivity extends AppCompatActivity {

    private ListView listView;
    ArrayAdapter<String> adapter;
    private TextView username;
    private TextView twitter_edit;
    private TextView ins_edit;
    private TextView snap_edit;
    private TextView linkin_edit;
    private TextView facebook_edit;
    private TextView email_edit;
    private TextView phone_edit;
    private TextView resume_edit;
    private CircleImageView profile_image;
    private FloatingActionButton back_button;
    ArrayList<String> savedLinks;
    Connection currFriend;
    LocalUser localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*ImageView im = (ImageView)findViewById(R.id.imageView);
        im.setFocusable(true);*/

        final String usersName = (String)getIntent().getSerializableExtra("Username");
        localUser = new LocalUser();
        localUser = localUser.getInstance();
        currFriend = null;

        profile_image = (CircleImageView)findViewById(R.id.profile_image);
        username = (TextView)findViewById(R.id.Username);
        username.setText(usersName);

        twitter_edit = (TextView)findViewById(R.id.Twitter);
        ins_edit = (TextView)findViewById(R.id.Instagram);
        snap_edit = (TextView)findViewById(R.id.Snapchat);
        linkin_edit = (TextView)findViewById(R.id.Linkedin);
        facebook_edit = (TextView)findViewById(R.id.Facebook);
        email_edit = (TextView)findViewById(R.id.Email);
        phone_edit = (TextView)findViewById(R.id.phoneNumber);
        resume_edit = (TextView)findViewById(R.id.Resume);

        back_button = (FloatingActionButton)findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fromFriendtoManage = new Intent(getApplicationContext(), ManageConnectionsActivity.class);
                startActivity(fromFriendtoManage);
            }
        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(usersName);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    for(String s : localUser.findConnectionByName(usersName).linkKeys) {
                        if (data.getKey().equals(s)) {
                            if(s.equals("facebook")) {
                                facebook_edit.setText((String)data.getValue());
                            }
                            else if(s.equals("twitter")) {
                                twitter_edit.setText((String)data.getValue());
                            }
                            else if(s.equals("instagram")) {
                                ins_edit.setText((String)data.getValue());
                            }
                            else if(s.equals("snapchat")) {
                                snap_edit.setText((String)data.getValue());
                            }
                            else if(s.equals("email")) {
                                email_edit.setText((String)data.getValue());
                            }
                            else if(s.equals("resume")) {
                                resume_edit.setText((String)data.getValue());
                            }
                            else if(s.equals("phoneNumber")) {
                                phone_edit.setText((String)data.getValue());
                            }
                            else if(s.equals("linkedin")) {
                                linkin_edit.setText((String)data.getValue());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        for(int i = 0; i < testLinks.size(); ++i) {
//            final int index = i;
//            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("kdieu").child(testLinks.get(index));
//            mDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
//                @Override
//                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        // TODO: handle the case where the data already exists
//                        Object o = snapshot.getValue();
//                        if(testLinks.get(index).equals("facebook")) {
//                            facebook_edit.setText((String)o);
//                            displayError(facebook_edit.getText().toString());
//                        }
//                        else if(testLinks.get(index).equals("twitter")) {
//                            twitter_edit.setText((String)o);
//                        }
//                        else if(testLinks.get(index).equals("instagram")) {
//                            ins_edit.setText((String)o);
//                        }
//                        else if(testLinks.get(index).equals("snapchat")) {
//                            snap_edit.setText((String)o);
//                        }
//                        else if(testLinks.get(index).equals("email")) {
//                            email_edit.setText((String)o);
//                        }
//                        else if(testLinks.get(index).equals("resume")) {
//                            resume_edit.setText((String)o);
//                        }
//                        else if(testLinks.get(index).equals("phoneNumber")) {
//                            phone_edit.setText((String)o);
//                        }
//                        else if(testLinks.get(index).equals("linkedin")) {
//                            linkin_edit.setText((String)o);
//                        }
//
//                    } else {
//                        //Do not change text if link does not exist
//                        displayError(savedLinks.get(index));
//                    }
//                }
//
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }

        //REMOVED THIS CODE BLOCK TO JUST SHOW BLANK HINT STRINGS WHEN INFO IS EMPTY
        /*if(!email_edit.getText().toString().isEmpty()) {
            //System.out.println
            email_edit.setVisibility(View.GONE);
        }
        if(!facebook_edit.getText().toString().isEmpty()) {
            facebook_edit.setVisibility(View.GONE);
        }
        if(!ins_edit.getText().toString().isEmpty()) {
            ins_edit.setVisibility(View.GONE);
        }
        if(!linkin_edit.getText().toString().isEmpty()) {
            linkin_edit.setVisibility(View.GONE);
        }
        if(!phone_edit.getText().toString().isEmpty()) {
            phone_edit.setVisibility(View.GONE);
        }
        if(!resume_edit.getText().toString().isEmpty()) {
            resume_edit.setVisibility(View.GONE);
        }
        if(!snap_edit.getText().toString().isEmpty()) {
            snap_edit.setVisibility(View.GONE);
        }
        if(!twitter_edit.getText().toString().isEmpty()) {
            twitter_edit.setVisibility(View.GONE);
        }*/

    }
    public void displayError(String link) {
        Toast.makeText(this, link + " does not exist on the database", Toast.LENGTH_LONG).show();
    }
}
