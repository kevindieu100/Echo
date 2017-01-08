package com.echo.echo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Vector;

public class RequestListActivity extends AppCompatActivity {

    private ListView listView;
    ArrayAdapter<String> adapter;
    LocalUser localUser;
    Vector<String> requests = new Vector<>();
    Vector<ArrayList<String>> links = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        localUser = new LocalUser();
        localUser = LocalUser.getInstance();

        loadSuggestions();
    }

    private void loadSuggestions (){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(localUser.userName).child("requests");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        requests.add((String) data.getKey());
                        getLinks((String) data.getKey());
                    }
                    addLayout();
                }
                else {
                    error();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        });
    }

    public void getLinks(String user) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(localUser.userName).child("requests").child(user);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> temp = new ArrayList<String>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    temp.add((String)data.getValue());
                }
                links.add(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addLayout() {
        listView = (ListView) findViewById(R.id.ListView1);
        adapter = new ArrayAdapter<>(this, R.layout.fragment_request_list,R.id.t2, requests);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent fromRequestListToRequestProfileIntent = new Intent(getApplicationContext(), RequestProfileActivity.class);
                fromRequestListToRequestProfileIntent.putExtra("Username", requests.get(position) );
                fromRequestListToRequestProfileIntent.putExtra("Links", links.get(position) );
                startActivity(fromRequestListToRequestProfileIntent);
            }
        });
    }

    public void error() {
        Toast.makeText(this, "No new requests!", Toast.LENGTH_LONG).show();
    }
    public void success(String name) {
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
    }



}
