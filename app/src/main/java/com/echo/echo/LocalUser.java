package com.echo.echo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class LocalUser implements Serializable {
    static LocalUser localuser;
    public static String userName = "";
    public static String fullName = "";
    public static String pw = "";
    public static String phoneNumber = "";
    public static String facebook = "";
    public static String email = "";
    public static String snapchat = "";
    public static String linkedin = "";
    public static String twitter = "";
    public static String instagram = "";
    public static String resume = "";
    public static ArrayList<Connection> connections = new ArrayList<Connection>();
    public static String fileName = "localUser.data";
    public static String pathToImage = "";
    public static Bitmap profileImage = null;

    public LocalUser(){

    }

    public LocalUser(String userName, String fullName, String pw, String phoneNumber){
        this.userName = userName;
        this.fullName = fullName;
        this.pw = pw;
        this.phoneNumber = phoneNumber;
    }

    public static LocalUser getInstance(){
        if( localuser == null)
            localuser = new LocalUser();
        return localuser;
    }//end of getInstance

    //saves this user locally
    public static void saveUser(LocalUser instance){
        ObjectOutput out;
        try {
            File outFile = new File(Environment.getExternalStorageDirectory(), fileName);
            out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(instance);
            out.close();
        } catch (Exception e) {e.printStackTrace();}
    }//end of saveUser

    //returns the user
    public static LocalUser loadData(){
        ObjectInput in;
        LocalUser user =null;
        try {
            in = new ObjectInputStream(new FileInputStream(fileName));
            user =(LocalUser) in.readObject();
            in.close();
        } catch (Exception e) {e.printStackTrace();}
        return user;
    }//end of loadData

    //adds a new Connection
    public void addNewConnection(Connection newConnection){
        this.connections.add(newConnection);
    }

    //removes a connection
    public void removeConnectionByName(String friendName){
        for(Connection con : this.connections){
            if(con.username.equals(friendName))
                this.connections.remove(con); //removes that connection
        }
    }//end of removeConnectionByName

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    //functions to call to see if a user has links saved already
    public boolean hasFacebook(){
        if(facebook.isEmpty() || facebook == null)
            return false;
        return true;
    }
    public boolean hasTwitter(){
        if(twitter.isEmpty() || twitter == null)
            return false;
        return true;
    }
    public boolean hasInstagram(){
        if(instagram.isEmpty() || instagram == null)
            return false;
        return true;
    }
    public boolean hasSnapchat(){
        if(snapchat.isEmpty() || snapchat == null)
            return false;
        return true;
    }
    public boolean hasLinkedin(){
        if(linkedin.isEmpty() || linkedin == null)
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
    public boolean hasImage(){
//        if(pathToImage.trim().equals("") || pathToImage ==null)
//            return false;
//        return true;
        if(profileImage == null)
            return false;
        return true;
    }

    public void setConnections(ArrayList<Connection> newConnections) {
        this.connections = newConnections;
    }

    public Bitmap getProfileImage(){
        return profileImage;
    }

    //loads the profile image and returns as a bitmap
    //returns null if fails to load image
    public Bitmap loadProfileImage()
    {
        try {
            File f=new File(pathToImage, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Connection findConnectionByName(String nameToFind) {
        for(Connection c : connections) {
            if(c.username.equals(nameToFind)) {
                return c;
            }
        }
        return null;
    }
}
