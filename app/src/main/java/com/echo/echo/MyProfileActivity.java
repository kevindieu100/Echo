package com.echo.echo;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MyProfileActivity extends AppCompatActivity {
    private LocalUser localUser;
    private TextView username_text;
    private EditText twitter_edit;
    private EditText ins_edit;
    private EditText snap_edit;
    private EditText linkin_edit;
    private EditText facebook_edit;
    private EditText email_edit;
    private EditText phone_edit;
    private EditText resume_edit;
    private ImageView profile_image;
    private static int RESULT_LOAD_IMAGE = 1;
    private String globalPicturePath = "";

    private boolean emailEditted = false;
    private boolean facebookEditted = false;
    private boolean instagramEditted = false;
    private boolean linkedInEditted = false;
    private boolean phoneNumberEditted = false;
    private boolean resumeEditted = false;
    private boolean snapchatEditted = false;
    private boolean twitterEditted = false;
    private boolean imageEditted = false;

    private boolean editState = false;

    private Button edit_button;
    private Button save_button;
    private FloatingActionButton back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Firebase.setAndroidContext(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //gets the instance of the logged in user
        localUser = localUser.getInstance();

        initializeEditText();
        addTextChangeListeners();

        save_button = (Button)findViewById(R.id.save_button);
        save_button.setVisibility(View.INVISIBLE);

        edit_button = (Button)findViewById(R.id.edit_button);

        addOnlickListener();

        back_button = (FloatingActionButton)findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fromProftoEcho = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(fromProftoEcho);
            }
        });
    }

    private void addOnlickListener() {
        edit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                save_button.setVisibility(View.VISIBLE);
                enableAllEditText ();
                edit_button.setVisibility(View.INVISIBLE);
                editState = true;
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_button.setVisibility(View.INVISIBLE);
                edit_button.setVisibility(View.VISIBLE);
                editState = false;
                //send newly edited user profile to firebase
                updateNewUserProfile();
                disableAllEditText();
            }
        });
     }

    private void updateNewUserProfile(){
        //send newly edited user profile to firebase and update locally
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(localUser.userName);
        if(facebookEditted){
            mDatabase.child("facebook").setValue(facebook_edit.getText().toString());
            localUser.facebook = facebook_edit.getText().toString();
        }
        if(emailEditted) {
            mDatabase.child("email").setValue(email_edit.getText().toString());
            localUser.email = email_edit.getText().toString();
        }
        if(instagramEditted) {
            mDatabase.child("instagram").setValue(ins_edit.getText().toString());
            localUser.instagram = ins_edit.getText().toString();
        }
        if(linkedInEditted) {
            mDatabase.child("linkedin").setValue(linkin_edit.getText().toString());
            localUser.linkedin = linkin_edit.getText().toString();
        }
        if(phoneNumberEditted) {
            mDatabase.child("phoneNumber").setValue(phone_edit.getText().toString());
            localUser.phoneNumber = phone_edit.getText().toString();
        }
        if(resumeEditted) {
            mDatabase.child("resume").setValue(resume_edit.getText().toString());
            localUser.resume = resume_edit.getText().toString();
        }
        if(snapchatEditted){
            mDatabase.child("snapchat").setValue(snap_edit.getText().toString());
            localUser.snapchat = snap_edit.getText().toString();
        }
        if(twitterEditted){
            mDatabase.child("twitter").setValue(twitter_edit.getText().toString());
            localUser.twitter = twitter_edit.getText().toString();
        }
        if(imageEditted){
            mDatabase.child("pathToImage").setValue(globalPicturePath);
            localUser.pathToImage = globalPicturePath;
            Bitmap bitmap = ((BitmapDrawable)profile_image.getDrawable()).getBitmap();
            localUser.profileImage = bitmap;
            String url = "gs://echo-1f6b3.appspot.com";
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            // Get the data from an ImageView as bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
//                    displayToast("Error uploading file to Firebase!!!!!!!!!!!");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    displayToast("Successfully uploaded to Firebase!!!!!!!!!!");
                }
            });

        }


        //saves the updated User
        localUser.saveUser(localUser);

        //moves onto main activity
        Intent fromLoginToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(fromLoginToMainIntent);
    }

    private void disableEditText (EditText et){
        et.setFocusable(false);
    }

    private void enableEditText (EditText et){
        et.setFocusableInTouchMode(true);
    }

    private void disableAllEditText (){
        disableEditText(twitter_edit);
        disableEditText(ins_edit);
        disableEditText(snap_edit);
        disableEditText(linkin_edit);
        disableEditText(facebook_edit);
        disableEditText(email_edit);
        disableEditText(phone_edit);
        disableEditText(resume_edit);
        profile_image.setEnabled(false);
        profile_image.setClickable(false);
//        if(!localUser.hasFacebook())
//            facebook_edit.setVisibility(View.GONE);
//        if(!localUser.hasEmail())
//            email_edit.setVisibility(View.GONE);
//        if(!localUser.hasInstagram())
//            ins_edit.setVisibility(View.GONE);
//        if(!localUser.hasLinkedin())
//            linkin_edit.setVisibility(View.GONE);
//        if(!localUser.hasTwitter())
//            twitter_edit.setVisibility(View.GONE);
//        if(!localUser.hasPhoneNumber())
//            phone_edit.setVisibility(View.GONE);
//        if(!localUser.hasResume())
//            resume_edit.setVisibility(View.GONE);
//        if(!localUser.hasSnapchat())
//            snap_edit.setVisibility(View.GONE);
        twitterEditted = false;
        instagramEditted = false;
        snapchatEditted = false;
        linkedInEditted = false;
        facebookEditted = false;
        emailEditted = false;
        phoneNumberEditted = false;
        resumeEditted = false;
        determineEditTextValue();
    }

    private void enableAllEditText (){
        enableEditText(twitter_edit);
        enableEditText(ins_edit);
        enableEditText(snap_edit);
        enableEditText(linkin_edit);
        enableEditText(facebook_edit);
        enableEditText(email_edit);
        enableEditText(phone_edit);
        enableEditText(resume_edit);
        profile_image.setEnabled(true);
        profile_image.setClickable(true);
//        facebook_edit.setVisibility(View.VISIBLE);
//        email_edit.setVisibility(View.VISIBLE);
//        ins_edit.setVisibility(View.VISIBLE);
//        linkin_edit.setVisibility(View.VISIBLE);
//        phone_edit.setVisibility(View.VISIBLE);
//        resume_edit.setVisibility(View.VISIBLE);
//        snap_edit.setVisibility(View.VISIBLE);
//        twitter_edit.setVisibility(View.VISIBLE);
        determineEditTextValue();
    }

    private void initializeEditText(){
        twitter_edit = (EditText)findViewById(R.id.twitter_edittext);

        ins_edit = (EditText)findViewById(R.id.ins_edittext);

        snap_edit = (EditText)findViewById(R.id.snap_edittext);

        linkin_edit = (EditText)findViewById(R.id.linkin_edittext);

        facebook_edit = (EditText)findViewById(R.id.facebook_edittext);

        email_edit = (EditText)findViewById(R.id.email_edittext);

        phone_edit = (EditText)findViewById(R.id.phone_edittext);

        resume_edit = (EditText)findViewById(R.id.resume_edittext);

        username_text = (TextView)findViewById(R.id.username_TextView);
        username_text.setText(localUser.userName);

        profile_image = (ImageView)findViewById(R.id.profile_image);

        disableAllEditText ();
    }

    //method called to look through local user and instantiate any edit texts variables
    private void determineEditTextValue(){
        if(localUser.hasFacebook())
            facebook_edit.setText(localUser.facebook);
        if(localUser.hasEmail())
            email_edit.setText(localUser.email);
        if(localUser.hasInstagram())
            ins_edit.setText(localUser.instagram);
        if(localUser.hasLinkedin())
            linkin_edit.setText(localUser.linkedin);
        if(localUser.hasTwitter())
            twitter_edit.setText(localUser.twitter);
        if(localUser.hasPhoneNumber())
            phone_edit.setText(localUser.phoneNumber);
        if(localUser.hasResume())
            resume_edit.setText(localUser.resume);
        if(localUser.hasSnapchat())
            snap_edit.setText(localUser.snapchat);
        if(localUser.hasImage()) {
//            if(localUser.loadProfileImage() == null)
//                Toast.makeText(this, "Error loading profile image!", Toast.LENGTH_LONG).show();
//            else
              profile_image.setImageBitmap(localUser.getProfileImage());
        }

    }

    //adds the changelistener to listen for when edit text has changed
    private void addTextChangeListeners(){
        facebook_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(editState)
                    facebookEditted = true;
            }
        });

        email_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(editState)
                    emailEditted = true;
            }
        });

        ins_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(editState)
                    instagramEditted = true;
            }
        });

        linkin_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(editState)
                    linkedInEditted = true;
            }
        });

        phone_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(editState)
                    phoneNumberEditted = true;
            }
        });

        resume_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(editState)
                    resumeEditted = true;
            }
        });

        snap_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(editState)
                    snapchatEditted = true;
            }
        });

        twitter_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(editState)
                    twitterEditted = true;
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateImageSelection();
                int RESULT_LOAD_IMAGE = 1;
                requestPermission();
            }
        });
    }//end of addTextChangeListeners

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }//end of saveToInternalStorage

    private void initiateImageSelection(){
        Toast.makeText(this, "Initiate image selection", Toast.LENGTH_LONG).show();
    }

    private void errorRetrievingFromGallery(){
        Toast.makeText(this, "Error retrieving from gallery", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            globalPicturePath = picturePath;
            imageEditted = true;
            profile_image.setImageBitmap(getScaledBitmap(picturePath, 800, 800));
        }else{
            errorRetrievingFromGallery();
        }
    }

    //requests permission to read from gallery
    private void requestPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionCheck);
        } else {
            //gets called if has permission
            initiateGallery();
        }
    }//end requestPermission

    //opens up intent for gallery
    private void initiateGallery(){
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }//end of initiateGallery


    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private void displayToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }//end of displayToast

}
