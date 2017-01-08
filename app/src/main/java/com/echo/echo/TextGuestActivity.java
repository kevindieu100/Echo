package com.echo.echo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;
import android.view.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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


public class TextGuestActivity extends AppCompatActivity {

    //Main Send Button
    private Button toSend;

    //Buttons that can be enabled/disabled for valid URLs
    private FloatingActionButton backButton;

    private Button snapButton;
    private Button linkedButton;
    private Button facebookButton;
    private Button instaButton;
    private Button twitterButton;
    private Button emailButton;
    private Button resumeButton;
    private Button phoneButton;

    private static EditText requestPhoneNumber;

    //Grab the current user's name
    private static String thisfullName = "";

    //The URLs of all Social Media Platforms
    private static String urlSnapchat = "";
    private static String urlFacebook = "";
    private static String urlInstagram = "";
    private static String urlLinkedIn = "";
    private static String urlTwitter = "";
    private static String urlEmail = "";
    private static String urlPhoneNumber = "";
    private static String urlDriveResume = "";

    private static boolean fbSelect = true;
    private static boolean snapSelect = true;
    private static boolean linkedSelect = true;
    private static boolean instaSelect = true;
    private static boolean twitterSelect = true;
    private static boolean phoneSelect = true;
    private static boolean emailSelect = true;
    private static boolean resumeSelect = true;

    /*
    private static boolean existsSnapchat = false;
    private static boolean existsFacebook = false;
    private static boolean existsInstagram = false;
    private static boolean existsLinkedIn = false;
    private static boolean existsTwitter = false;
    private static boolean existsEmail = false;
    private static boolean existsPhoneNumber = false;
    private static boolean existsDriveResume = false;
    */

    //Twilio Authentication Keys
    public static final String ACCOUNT_SID = "ACc3dd90bd8d17706a39ca85e0378f2733";
    public static final String AUTH_TOKEN = "5cb58342614022ab49a7ccf44fbb5b6f";

    //Create a Local User
    private static LocalUser localUser;
    private static String preSplitBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_guest);

        initializeComponents();
        disableButtons();
        addEvents();
    }

    private void initializeComponents() {

        //Grab Current User's Instance to later Access URL Data
        localUser = new LocalUser();
        localUser = localUser.getInstance();

        requestPhoneNumber = (EditText) findViewById(R.id.phone_number_editview1);
        toSend             = (Button) findViewById(R.id.send_button);
        backButton = (FloatingActionButton)findViewById(R.id.back_button);

        facebookButton = (Button)findViewById(R.id.facebook_linkbutton);
        twitterButton = (Button)findViewById(R.id.twitter_linkbutton);
        instaButton = (Button)findViewById(R.id.ins_linkbutton);
        snapButton = (Button)findViewById(R.id.snap_linkbutton);
        linkedButton = (Button)findViewById(R.id.linkin_linkbutton);
        resumeButton = (Button)findViewById(R.id.resume_linkbutton);
        phoneButton = (Button)findViewById(R.id.phone_linkbutton);
        emailButton = (Button)findViewById(R.id.gmail_linkbutton);

    }

    //Add OnClick Listeners
    private void addEvents() {
        toSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preSplitBody = compileUrls();
                int index = 0;
                while (index < preSplitBody.length()) {
                    String sendSplit = "";

                    sendSplit = preSplitBody.substring(index, Math.min(index + 120, preSplitBody.length()));
                    index += 120;
                    sendMessage(sendSplit);
                }
                //While character != null; iterate to 160; copy characters to new string; then send; restart
                //sendMessage(/*Split up String*/);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fromTexttoMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(fromTexttoMain);
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
    }
    private void sendMessage(String message) {
        String body = message;
        String from = "+14088729220";
        String to = "+1" + requestPhoneNumber.getText().toString();;

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
        TextGuestActivity.TwilioApi_TextGuestActivity api_text_guest = retrofit.create(TextGuestActivity.TwilioApi_TextGuestActivity.class);

        api_text_guest.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
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

        //goes back to main after a text is sent
        //goes back to login after message is sent
        Intent IntentLogout = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(IntentLogout);
    }
    /*
    private void sendMessage_2() {
        String body = compileUrls_2();
        String from = "+14088729220";
        String to = "+1" + requestPhoneNumber.getText().toString();;

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
        TextGuestActivity.TwilioApi_TextGuestActivity api_text_guest_2 = retrofit.create(TextGuestActivity.TwilioApi_TextGuestActivity.class);

        api_text_guest_2.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
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
    }*/

    private interface TwilioApi_TextGuestActivity {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/SMS/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );
    }

    private static String compileUrls() {
        String finalString = "";

        String thisFullName = "";

        thisFullName = localUser.fullName;
        finalString = (thisFullName + "'s contact information." + "\n");

        if (localUser.hasPhoneNumber() && phoneSelect == true) {
            urlPhoneNumber = localUser.phoneNumber;
            //existsPhoneNumber = true;
            finalString += ("Phone Number: "  + urlPhoneNumber + "\n");
        }

        if (localUser.hasFacebook() && fbSelect == true) {
            urlFacebook = localUser.facebook;
            //existsFacebook = true;
            finalString += ("Facebook: "  + urlFacebook + "\n");
        }

        if (localUser.hasInstagram() && instaSelect == true) {
            urlInstagram = localUser.instagram;
            //existsInstagram = true;
            finalString += ("Instagram: "  + urlInstagram + "\n");
        }

        if (localUser.hasSnapchat() & snapSelect == true) {
            urlSnapchat = localUser.snapchat;
            //existsSnapchat = true;
            finalString += ("Snapchat: "  + urlSnapchat + "\n");
        }

        if (localUser.hasTwitter() && twitterSelect == true) {
            urlTwitter = localUser.twitter;
            //existsTwitter = true;
            finalString += ("Twitter: "  + urlTwitter + "\n");
        }

        if (localUser.hasLinkedin() && linkedSelect == true) {
            urlLinkedIn = localUser.linkedin;
            //existsLinkedIn = true;
            finalString += ("LinkedIn: "  + urlLinkedIn + "\n");
        }

        if (localUser.hasEmail() && emailSelect == true) {
            urlEmail = localUser.email;
            //existsEmail = true;
            finalString += ("Email: "  + urlEmail + "\n");
        }

        if (localUser.hasResume() && resumeSelect == true) {
            urlDriveResume = localUser.resume;
            //existsDriveResume = true;
            finalString += ("Resume: "  + urlDriveResume + "\n");
        }

        /*
        if(existsPhoneNumber) {
            finalString += "Phone Number: " + urlPhoneNumber +"\n";
        }

        if(existsEmail) {
            finalString += "Email: "    + urlEmail    + "\n";
        }

        if (existsFacebook) {
            finalString += "Facebook: " + urlFacebook + "\n";
        }

        if (existsSnapchat) {
            finalString += "Snapchat: " + urlSnapchat + "\n";
        }

        if (existsInstagram) {
            finalString += "Instagram: "+ urlInstagram + "\n";
        }

        if (existsTwitter) {
            finalString += "Twitter: "  + urlTwitter  + "\n";
        }

        if (existsLinkedIn) {
            finalString += "LinkedIn: " + urlLinkedIn + "\n";
        }

        if (existsDriveResume) {
            finalString += "Resume: " + urlDriveResume +"\n";
        }
        */

        return finalString;

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

    private static String listToString(List<String> convertMe) {
        StringBuilder sb = new StringBuilder();
        for (String s : convertMe)
        {
            sb.append(s);
            sb.append("\t");
        }

        System.out.println(sb.toString());

        return sb.toString();
    }

    /*
    private static String compileUrls_2() {
        String finalString = "";

        String thisFullName = "";

        thisFullName = localUser.fullName;
        finalString = (thisFullName + "'s additional contact information." + "\n");

        if (localUser.hasTwitter()) {
            urlTwitter = localUser.twitter;
            //existsTwitter = true;
            finalString += ("Twitter: "  + urlTwitter + "\n");
        }

        if (localUser.hasLinkedin()) {
            urlLinkedIn = localUser.linkedin;
            //existsLinkedIn = true;
            finalString += ("LinkedIn: "  + urlLinkedIn + "\n");
        }

        if (localUser.hasEmail()) {
            urlEmail = localUser.email;
            //existsEmail = true;
            finalString += ("Email: "  + urlEmail + "\n");
        }

        if (localUser.hasResume()) {
            urlDriveResume = localUser.resume;
            //existsDriveResume = true;
            finalString += ("Resume: "  + urlDriveResume + "\n");
        }

        /*
        if(existsPhoneNumber) {
            finalString += "Phone Number: " + urlPhoneNumber +"\n";
        }

        if(existsEmail) {
            finalString += "Email: "    + urlEmail    + "\n";
        }

        if (existsFacebook) {
            finalString += "Facebook: " + urlFacebook + "\n";
        }

        if (existsSnapchat) {
            finalString += "Snapchat: " + urlSnapchat + "\n";
        }

        if (existsInstagram) {
            finalString += "Instagram: "+ urlInstagram + "\n";
        }

        if (existsTwitter) {
            finalString += "Twitter: "  + urlTwitter  + "\n";
        }

        if (existsLinkedIn) {
            finalString += "LinkedIn: " + urlLinkedIn + "\n";
        }

        if (existsDriveResume) {
            finalString += "Resume: " + urlDriveResume +"\n";
        }


        return finalString;

    }
    */


    /*
    private static void checkLocalURLs(){
        if (localUser.hasEmail()) {
            urlEmail = localUser.resume;
            existsEmail = true;
        }

        if (localUser.hasFacebook()) {
            urlFacebook = localUser.facebook;
            existsFacebook = true;
        }

        if (localUser.hasInstagram()) {
            urlInstagram = localUser.instagram;
            existsInstagram = true;
        }

        if (localUser.hasLinkedin()) {
            urlLinkedIn = localUser.linkedin;
            existsLinkedIn = true;
        }

        if (localUser.hasPhoneNumber()) {
            urlPhoneNumber = localUser.phoneNumber;
            existsPhoneNumber = true;
        }

        if (localUser.hasSnapchat()) {
            urlSnapchat = localUser.snapchat;
            existsSnapchat = true;
        }

        if (localUser.hasTwitter()) {
            urlTwitter = localUser.twitter;
            existsTwitter = true;
        }

        if (localUser.hasResume()) {
        if (localUser.hasResume()) {
            urlDriveResume = localUser.resume;
            existsDriveResume = true;
        }
    }*/

    private static String compileUrls_test() {
        String finalString;

        finalString =
            "Snapchat: " + urlSnapchat + "\n" +
            "Facebook: " + urlFacebook + "\n" +
            "LinkedIn: " + urlLinkedIn + "\n" +
            "Twitter: "  + urlTwitter  + "\n" +
            "Instagram: "+ urlInstagram+ "\n" +
            "Email: "    + urlEmail    + "\n" +
            "Phone Number: " + urlPhoneNumber + "\n" +
            "Resume: " + urlDriveResume     + "\n";
        return finalString;
    }


}