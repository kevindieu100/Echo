package com.echo.echo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Administrator on 11/16/2016.
 */

public class Guest_Login_Activity extends AppCompatActivity {
    private static EditText name_edittext;
    private static EditText phone_edittext;
    private static EditText email_edittext;
    private Button send_button;
    private FloatingActionButton back_button;
    private static EditText guest_phonenumber_edittext;
    private boolean ifSend;

    //Twilio Authentication
    public static final String ACCOUNT_SID = "ACc3dd90bd8d17706a39ca85e0378f2733";
    public static final String AUTH_TOKEN = "5cb58342614022ab49a7ccf44fbb5b6f";

    private static String preSplitBody;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guest_login);

        back_button = (FloatingActionButton)findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fromGuesttoLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(fromGuesttoLogin);
            }
        });

        initializeComponents();
        addEvent();
    }

    private void addEvent() {
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfAllEdittextFilled();
                preSplitBody = compileUrls();
                int index = 0;
                while (index < preSplitBody.length()) {
                    String sendSplit = "";

                    sendSplit = preSplitBody.substring(index, Math.min(index + 120, preSplitBody.length()));
                    index += 120;
                    sendMessage(sendSplit);
                }
            }
        });
    }

    private void checkIfAllEdittextFilled() {
        String warning = "Please enter";
        if (name_edittext.getText().toString().isEmpty()) {
            warning += " name";
        }
        if (phone_edittext.getText().toString().isEmpty()) {
            warning += " phone";
        }
        if (email_edittext.getText().toString().isEmpty()) {
            warning += " email";
        }
        if (guest_phonenumber_edittext.getText().toString().isEmpty()) {
            warning += " your phone number";
        }
        if (!warning.equals("Please enter")) {
            ifSend = true;
            Toast.makeText(this, warning, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeComponents() {
        ifSend = true;
        name_edittext = (EditText)findViewById(R.id.guest_name_edittext);
        phone_edittext = (EditText)findViewById(R.id.guest_phone_edittext);
        email_edittext = (EditText)findViewById(R.id.guest_email_edittext);
        send_button = (Button)findViewById(R.id.guest_send_button);
        guest_phonenumber_edittext = (EditText)findViewById(R.id.guest_enterPhoneNum_EditText);
    }

    private void sendMessage(String message) {
        EditText requestPhoneNumber = (EditText) findViewById(R.id.phone_number_editview);
        String body = message;
        String from = "+14088729220";
        String to = "+1" + guest_phonenumber_edittext.getText().toString();

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
        );

        Map<String, String> data = new HashMap<>();
        data.put("From", from);
        data.put("To", to);
        data.put("Body", body);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twilio.com/2010-04-01/")
                .build();
        Guest_Login_Activity.TwilioApi api_guest_login = retrofit.create(Guest_Login_Activity.TwilioApi.class);

        api_guest_login.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) Log.d("TAG", "onResponse->success");
                else Log.d("TAG", "onResponse->failure");
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TAG", "onFailure");
            }
        });

        //goes back to login after message is sent
        Intent IntentLogout = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(IntentLogout);
    }

    private interface TwilioApi {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/SMS/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );
    }

    private static String compileUrls(){
        String guestString = "";

        guestString += (name_edittext.getText().toString() + " sent you their info." + "\n");

        if (!phone_edittext.getText().toString().isEmpty()) {
            guestString += ("Phone Number: " + phone_edittext.getText().toString() + "\n");
        }
        if (!email_edittext.getText().toString().isEmpty()) {
            guestString += ("Email: " + email_edittext.getText().toString() + "\n");
        }

        return guestString;
    }

}
