package com.echo.echo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.support.design.widget.FloatingActionButton;

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
import java.util.HashMap;
import java.util.Vector;

public class ManageConnectionsActivity extends AppCompatActivity {

    private ListView listView;
    ArrayAdapter<String> adapter;
    EditText searchBar;
    FloatingActionButton backButton;
    ArrayList<HashMap<String, String>> connectionList;
    LocalUser localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_connections);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backButton = (FloatingActionButton)findViewById(R.id.back_button);

        localUser = new LocalUser();
        localUser = localUser.getInstance();
        updateLocalDatabase();

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fromSigntoLogin = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(fromSigntoLogin);
            }
        });

    }

    private void updateLocalDatabase() {
        final ArrayList<Connection> updatedConnections = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(localUser.userName).child("friends");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        Connection c = new Connection(data.getKey());
                        for(DataSnapshot d : data.getChildren()) {
                            c.addLinkKey((String)d.getValue());
                        }
                        updatedConnections.add(c);
                    }
                    localUser.setConnections(updatedConnections);
                }
                implementSearchBar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void implementSearchBar (){
        final Vector<String> connections = new Vector<String>();
        for(Connection c : localUser.getConnections()) {
            connections.add(c.username);
        }
        listView = (ListView) findViewById(R.id.ListView);
        searchBar = (EditText) findViewById(R.id.search_bar);

        adapter = new ArrayAdapter<String>(this, R.layout.fragment_manage_connections,R.id.t1, connections);
        listView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ManageConnectionsActivity.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ManageConnectionsActivity.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent fromManageConToFriProfileIntent = new Intent(getApplicationContext(), FriendProfileActivity.class);
                fromManageConToFriProfileIntent.putExtra("Username", connections.get(position) );
                startActivity(fromManageConToFriProfileIntent);
            }
        });

    }




}
