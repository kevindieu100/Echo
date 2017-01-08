package com.echo.echo;

/**
 * Created by Administrator on 11/19/2016.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class mainFragment extends Fragment {

    private static final int request = 2;
    private static final int enablebt = 3;

    private Button SendButton;
    private Button EchoButton;
    private Button fb_button;
    private Button ins_button;
    private Button email_button;
    private Button linkin_button;
    private Button snap_button;
    private Button twi_button;
    private Button phone_button;
    private Button resume_button;

    private String deviceName = null;
    private StringBuffer stringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private bluetooth b = null;
    //private booleans for the linkButtons
    private boolean fbSelect = false;
    private boolean snapSelect = false;
    private boolean linkedSelect = false;
    private boolean instaSelect = false;
    private boolean twitterSelect = false;
    private boolean phoneSelect = false;
    private boolean emailSelect = false;
    private boolean resumeSelect = false;

    private PulsatorLayout pulse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, enablebt);

        } else if (b == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (b != null) {
            b.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (b != null) {
            if (b.getState() == bluetooth.no) {
                b.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SendButton = (Button) view.findViewById(R.id.send_button);
        EchoButton = (Button) view.findViewById(R.id.main_button);

        fb_button = (Button)view.findViewById(R.id.facebook_linkbutton);
        fb_button.setVisibility(View.INVISIBLE);
        ins_button = (Button)view.findViewById(R.id.ins_linkbutton);
        ins_button.setVisibility(View.INVISIBLE);
        email_button = (Button)view.findViewById(R.id.gmail_linkbutton);
        email_button.setVisibility(View.INVISIBLE);
        linkin_button = (Button)view.findViewById(R.id.linkin_linkbutton);
        linkin_button.setVisibility(View.INVISIBLE);
        snap_button = (Button)view.findViewById(R.id.snap_linkbutton);
        snap_button.setVisibility(View.INVISIBLE);
        twi_button = (Button)view.findViewById(R.id.twitter_linkbutton);
        twi_button.setVisibility(View.INVISIBLE);
        phone_button = (Button)view.findViewById(R.id.phone_linkbutton);
        phone_button.setVisibility(View.INVISIBLE);
        resume_button = (Button)view.findViewById(R.id.resume_linkbutton);
        resume_button.setVisibility(View.INVISIBLE);

        pulse = (PulsatorLayout) view.findViewById(R.id.pulsator);
        pulse.start();
        determineInitialLinkButtons();
        addLinkButtonListeners();
    }

    private void setupChat() {
        SendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EchoButton.setVisibility(View.VISIBLE);
                SendButton.setVisibility(View.INVISIBLE);
                animateButton(fb_button, false);
                animateButton(ins_button, false);
                animateButton(email_button, false);
                animateButton(linkin_button, false);
                animateButton(snap_button, false);
                animateButton(twi_button, false);
                animateButton(phone_button, false);
                animateButton(resume_button, false);
                String messageToSend = determineLinksToSend();
                sendMessage(messageToSend);

                pulse.start();
            }
        });




        EchoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);
                }
                else{
                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, request);

                    resetButtons();
                    disableButtons();
                    animateButton(fb_button, true);
                    animateButton(ins_button, true);
                    animateButton(email_button, true);
                    animateButton(linkin_button, true);
                    animateButton(snap_button, true);
                    animateButton(twi_button, true);
                    animateButton(phone_button, true);
                    animateButton(resume_button, true);

                    EchoButton.setVisibility(View.INVISIBLE);
                    SendButton.setVisibility(View.VISIBLE);
                    pulse.stop();
                }
            }
        });


        b = new bluetooth(getActivity(), mHandler);

        stringBuffer = new StringBuffer("");
    }

    private void animateButton(Button input, boolean doesUnveil) {
        if (doesUnveil) {
            input.setVisibility(View.VISIBLE);
        } else {
            input.setVisibility(View.INVISIBLE);
        }
    }



    private void sendMessage(String message) {
        if (b.getState() != bluetooth.connected) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            Toast.makeText(getActivity(),"sending message",Toast.LENGTH_SHORT).show();
            byte[] send = message.getBytes();
            b.write(send);

            stringBuffer.setLength(0);
        }
    }

    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };


    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case bluetooth.connected:

                            setStatus("connected to");
                            break;
                        case bluetooth.connecting:
                            setStatus("connecting");
                            break;
                        case bluetooth.listen:
                        case bluetooth.no:
                            EchoButton.setVisibility(View.VISIBLE);
                            SendButton.setVisibility(View.INVISIBLE);
                            animateButton(fb_button, false);
                            animateButton(ins_button, false);
                            animateButton(email_button, false);
                            animateButton(linkin_button, false);
                            animateButton(snap_button, false);
                            animateButton(twi_button, false);
                            animateButton(phone_button, false);
                            animateButton(resume_button, false);
                            setStatus("not connected");
                            pulse.start();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    createConnection(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        pulse.stop();
                        resetButtons();
                        disableButtons();
                        animateButton(fb_button, true);
                        animateButton(ins_button, true);
                        animateButton(email_button, true);
                        animateButton(linkin_button, true);
                        animateButton(snap_button, true);
                        animateButton(twi_button, true);
                        animateButton(phone_button, true);
                        animateButton(resume_button, true);

                        EchoButton.setVisibility(View.INVISIBLE);
                        SendButton.setVisibility(View.VISIBLE);
                        Toast.makeText(activity, "Connected to "+ deviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case request:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case enablebt:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Toast.makeText(getActivity(),"Bluetooth is not enabled",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }


    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        b.connect(device, secure);
    }

    private void determineInitialLinkButtons(){
        LocalUser localUser = new LocalUser();
        localUser = localUser.getInstance();

        if(!localUser.hasResume()){
            resume_button.setClickable(false);
            resume_button.setEnabled(false);
            resume_button.setAlpha(0.2f);
            resumeSelect = true;
        }
        if(!localUser.hasPhoneNumber()){
            phone_button.setClickable(false);
            phone_button.setEnabled(false);
            phone_button.setAlpha(0.2f);
            phoneSelect = true;
        }
        if(!localUser.hasTwitter()){
            twi_button.setClickable(false);
            twi_button.setEnabled(false);
            twi_button.setAlpha(0.2f);
            twitterSelect = true;
        }
        if(!localUser.hasLinkedin()){
            linkin_button.setClickable(false);
            linkin_button.setEnabled(false);
            linkin_button.setAlpha(0.2f);
            linkedSelect = true;
        }
        if(!localUser.hasSnapchat()){
            snap_button.setClickable(false);
            snap_button.setEnabled(false);
            snap_button.setAlpha(0.2f);
            snapSelect = true;
        }
        if(!localUser.hasFacebook()){
            fb_button.setClickable(false);
            fb_button.setEnabled(false);
            fb_button.setAlpha(0.2f);
            fbSelect = true;
        }
        if(!localUser.hasEmail()){
            email_button.setClickable(false);
            email_button.setEnabled(false);
            email_button.setAlpha(0.2f);
            emailSelect = true;
        }
        if(!localUser.hasInstagram()){
            ins_button.setClickable(false);
            ins_button.setEnabled(false);
            ins_button.setAlpha(0.2f);
            instaSelect = true;
        }
    }

    private void addLinkButtonListeners(){
        fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fbSelect == true) {
                    fbSelect = false;
                    fb_button.setAlpha(0.2f);
                } else {
                    fbSelect = true;
                    fb_button.setAlpha(1.0f);
                }
            }
        });

        twi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (twitterSelect == true) {
                    twitterSelect = false;
                    twi_button.setAlpha(0.2f);
                } else {
                    twitterSelect = true;
                    twi_button.setAlpha(1.0f);
                }
            }
        });

        snap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (snapSelect == true) {
                    snapSelect = false;
                    snap_button.setAlpha(0.2f);
                } else {
                    snapSelect = true;
                    snap_button.setAlpha(1.0f);
                }
            }
        });

        linkin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linkedSelect == true) {
                    linkedSelect = false;
                    linkin_button.setAlpha(0.2f);
                } else {
                    linkedSelect = true;
                    linkin_button.setAlpha(1.0f);
                }
            }
        });

        ins_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instaSelect == true) {
                    instaSelect = false;
                    ins_button.setAlpha(0.2f);
                } else {
                    instaSelect = true;
                    ins_button.setAlpha(1.0f);
                }
            }
        });

        resume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resumeSelect == true) {
                    resumeSelect = false;
                    resume_button.setAlpha(0.5f);
                } else {
                    resumeSelect = true;
                    resume_button.setAlpha(1.0f);
                }
            }
        });

        email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailSelect == true) {
                    emailSelect = false;
                    email_button.setAlpha(0.2f);
                } else {
                    emailSelect = true;
                    email_button.setAlpha(1.0f);
                }
            }
        });

        phone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneSelect == true) {
                    phoneSelect = false;
                    phone_button.setAlpha(0.2f);
                } else {
                    phoneSelect = true;
                    phone_button.setAlpha(1.0f);
                }
            }
        });
    }


    public String determineLinksToSend(){
        LocalUser user = new LocalUser();
        user = user.getInstance();
        String returnString = user.userName + "::";
        if(fbSelect)
            returnString+="facebook::";
        if(snapSelect)
            returnString+="snapchat::";
        if(linkedSelect)
            returnString +="linkedin::";
        if(instaSelect)
            returnString += "instagram::";
        if(twitterSelect)
            returnString += "twitter::";
        if(phoneSelect)
            returnString += "phone::";
        if(emailSelect)
            returnString += "email::";
        if(resumeSelect)
            returnString += "resume::";

        return returnString;
    }

    private void createConnection(String message){
        if(message.trim().isEmpty()){
            Toast.makeText(getActivity(),"Nothing sent from connection!", Toast.LENGTH_SHORT).show();
        }
        else{
            String[] parsedMessage = message.split("::");

            checkIfAlreadyFriends(parsedMessage[0], parsedMessage);
        }
    }

    private void checkIfAlreadyFriends(String friendName, String[] parsedMessage) {
        LocalUser user = new LocalUser();
        user = user.getInstance();
        final String friend = friendName;
        final String[] message = parsedMessage;
        boolean samePerson = false;
        if(user.userName.equals(friendName))
            samePerson = true;

        if(samePerson){
            createToast("Can not make connection with yourself!");
        }
        else {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.userName).child("friends").child(friendName);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        createToast("Connection already exists!");
                    } else {
                        addConnection(friend, message);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void addConnection(String friendName, String[] parsedMessage){
        LocalUser user = new LocalUser();
        user = user.getInstance();
        Connection newFriend = new Connection();
        ArrayList<String> linkKeys = new ArrayList<String>();
        for(int i = 1; i < parsedMessage.length; i++ ){
            if(!parsedMessage[i].trim().isEmpty())
                linkKeys.add(parsedMessage[i]);
                newFriend.addLinkKey(parsedMessage[i]);
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.userName).child("friends").child(friendName);
        mDatabase.setValue(linkKeys);


        user.addNewConnection(newFriend);
        user.saveUser(user);

        createToast("Connection made with " + friendName);
    }

    private void createToast(String toastMessage){
        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
    }


    private void resetButtons() {
        fb_button.setEnabled(true);
        fbSelect = true;
        fb_button.setAlpha(1.0f);

        ins_button.setEnabled(true);
        instaSelect = true;
        ins_button.setAlpha(1.0f);

        snap_button.setEnabled(true);
        snapSelect = true;
        snap_button.setAlpha(1.0f);

        email_button.setEnabled(true);
        emailSelect = true;
        email_button.setAlpha(1.0f);

        resume_button.setEnabled(true);
        resumeSelect = true;
        resume_button.setAlpha(1.0f);

        phone_button.setEnabled(true);
        phoneSelect = true;
        phone_button.setAlpha(1.0f);

        twi_button.setEnabled(true);
        twitterSelect = true;
        twi_button.setAlpha(1.0f);

        linkin_button.setEnabled(true);
        linkedSelect = true;
        linkin_button.setAlpha(1.0f);
    }//end of resetButtons

    private void disableButtons() {
        LocalUser localUser = new LocalUser();
        localUser = localUser.getInstance();

        if(!localUser.hasEmail()) {
            email_button.setEnabled(false);
            emailSelect = false;
            email_button.setAlpha(0.5f);
        }
        if(!localUser.hasFacebook()) {
            fb_button.setEnabled(false);
            fbSelect = false;
            fb_button.setAlpha(0.5f);
        }
        if(!localUser.hasInstagram()) {
            ins_button.setEnabled(false);
            instaSelect = false;
            ins_button.setAlpha(0.5f);
        }
        if(!localUser.hasLinkedin()) {
            linkin_button.setEnabled(false);
            linkedSelect = false;
            linkin_button.setAlpha(0.5f);
        }
        if(!localUser.hasTwitter()) {
            twi_button.setEnabled(false);
            twitterSelect = false;
            twi_button.setAlpha(0.5f);
        }
        if(!localUser.hasPhoneNumber()) {
            phone_button.setEnabled(false);
            phoneSelect = false;
            phone_button.setAlpha(0.5f);
        }
        if(!localUser.hasResume()) {
            phone_button.setEnabled(false);
            resumeSelect = false;
            phone_button.setAlpha(0.5f);
        }
        if(!localUser.hasSnapchat()) {
            snap_button.setEnabled(false);
            snapSelect = false;
            snap_button.setAlpha(0.5f);
        }
    }

}
