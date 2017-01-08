package com.echo.echo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by Kevin on 11/13/2016.
 */

@IgnoreExtraProperties
public class User {
    public String userName = "";
    public String fullName = "";
    public String pw = "";
    public String phoneNumber = "";
    public String facebook = "";
    public String email = "";
    public String snapchat = "";
    public String linkedin = "";
    public String twitter = "";
    public String instagram = "";
    public String resume = "";
    public ArrayList<String> friendsList = new ArrayList<String>();
    public ArrayList<String> requestList = new ArrayList<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName, String fullName, String password, String phoneNumber) {
        this.userName = userName;
        this.fullName = fullName;
        this.pw = password;
        this.phoneNumber = phoneNumber;
    }

    public void addRequest(String username) {requestList.add(username); }

    public void addFriend(String username){
        friendsList.add(username);
    }

    //functions to call to see if a user has links saved already
    public boolean hasFacebook(){
        if(facebook.trim().equals("") || facebook == null)
            return false;
        return true;
    }
    public boolean hasTwitter(){
        if(twitter.trim().equals("") || twitter == null)
            return false;
        return true;
    }
    public boolean hasInstagram(){
        if(instagram.trim().equals("") || instagram == null)
            return false;
        return true;
    }
    public boolean hasSnapchat(){
        if(snapchat.trim().equals("") || snapchat == null)
            return false;
        return true;
    }
    public boolean hasLinkedin(){
        if(linkedin.trim().equals("") || linkedin == null)
            return false;
        return true;
    }
    public boolean hasEmail(){
        if(email.trim().equals("") || email == null){
            return false;
        }
        return true;
    }
    public boolean hasPhoneNumber(){
        if(phoneNumber.trim().equals("") || phoneNumber== null)
            return false;
        return true;
    }
    public boolean hasResume(){
        if(resume.trim().equals("") || phoneNumber ==null)
            return false;
        return true;
    }
}
