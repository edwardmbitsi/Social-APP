package com.example.socialapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfilePage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String storagepath = "Users_Profile_Cover_image/";
    String uid;
    ImageView set;
    TextView profilepic, editname, editpassword;
    ProgressDialog pd;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST =400;
    String cameraPermission[];
    String storagePermission[];
    Uri imageuri;
    String profileOrCoverPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);

        profilepic = findViewById(R.id.profilepic);
        editname = findViewById(R.id.editname);
        set = findViewById(R.id.setting_profile_image);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        editpassword = findViewById(R.id.changepassword);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = firebaseDatabase.getReference("Users");
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String IMAGE = "" + dataSnapshot1.child("image").getValue();

                    try{
                        Glide.with(EditProfilePage.this).load(image).into(set);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });

        editpassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pd.setMessage("Changing Password");
                showPasswordChangeDailog();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String image = "" + dataSnapshot1.child("image").getValue();

                    try {
                        Glide.with(EditProfilePage.this).load(image).into(set);
                    } catch (Exception e) {
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        editpassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pd.setMessage("Changing Password");
                showPasswordChangeDailog();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String image = "" + dataSnapshot1.child("image").getValue();
                    try{
                        Glide.with(EditProfilePage.this).load(image).into(set);
                    } catch (Exception e) {
                }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        editpassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pd.setMessage("Changing Password");
                showPasswordChangeDailog();
            }
        });
    }

    // checking storage permission ,if given then we can add something in our storage
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // requesting for storage permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // checking camera permission ,if given then we can click image using our camera
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // requesting for camera permission if not given
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // We will show an alert box where we will write our old and new password
    private void showPasswordChangeDailog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_password, null);
        final EditText oldpass = view.findViewById(R.id.oldpasslog);
        final EditText newpass = view.findViewById(R.id.newpasslog);
        Button editpass = view.findViewById(R.id.updatepass);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        editpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldp = oldpass.getText().toString().trim();
                String newp = newpass.getText().toString().trim();
                if (TextUtils.isEmpty(oldp)) {
                    Toast.makeText(EditProfilePage.this, "Current Password can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(newp)) {
                    Toast.makeText(EditProfilePage.this, "New Password can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                dialog.dismiss();
                updatePassword(oldp, newp);
            }
        });
}


    // Now we will check that if old password was authenticated
    // correctly then we will update the new password
    private void updatePassword(String oldp, final String newp) {
        pd.show();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldp);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(newp)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                     pd.dismiss();
                                     Toast.makeText(EditProfilePage.this, "Changed Password", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(EditProfilePage.this, "Failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(EditProfilePage.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                });
                }
                //Updating name
                private void showNamephoneupdate(final String key) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Update" + key);

                // creating a layout to write the new name
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(10, 10, 10, 10);
                final EditText editText = new EditText(this);
                editText.setHint("Enter" + key);
                layout.addView(editText);
                builder.setView(layout);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        final String value = editText.getText().toString().trim();
                        if (!TextUtils.isEmpty(value)) {
                            pd.show();

                            // Here we are updating the new name
                            HashMap<String, Object> result = new HashMap<>();
                            result.put(key, value);
                            databaseReference.child(firebaseUser.getUid().updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();

                                    // after updated we will show updated
                                    Toast.makeText(EditProfilePage.this, "updated", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(EditProfilePage.this, "Unable to update", Toast.LENGTH_LONG).show();
                                }
                            });
                            if (key.equals("name")) {
                                final DatabaseReference databaser = FirebaseDatabase.getInstance().getReference("Posts");
                                Query query = databaser.orderByChild("uid").equalTo(uid);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            String child = databaser.getKey();
                                            dataSnapshot1.getRef().child("uname").setValue(value);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(EditProfilePage.this, "Unable to update", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

             builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                 @Override
                 public void onClick(DialogInterface dialog, int which){
                     pd.dismiss();
                        }
                    });
             builder.create().show();

  // Here we are showing image pic dialog where we will select
  // and image either from camera or gallery
  private void showImagePicDialog(){
      String options[] = {"Camera", "Gallery"};
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Pick Image From");
      builder.setItems(options, new DialogInterface.OnClickListener(){
      @Override
      public void onClick(DialogInterface dialog, int which) {
          // if access is not given then we will request for permission
          if (which == 0){
              if (!checkCameraPermission()) {
                  requestCameraPermission();
              } else {
                  pickFromCamera();
              }
              } else if (which == 1) {
          if (!checkStoragePermission()) {
              requestStoragePermission();
          } else {
              pickFromGallery();
          }
      }
      }
  });
      builder.create().show();
  }

  @Override
  public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
      switch (requestCode) {
          case CAMERA_REQUEST: {
              if (grantResults.length > 0) {
                  boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                  boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                  if (writeStorageaccepted) {
                      pickFromGallery();
                  } else {
                      Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                  }
              }
          }
          break;
      }
  }

  // Here we will click a photo and then go to staractivityforresult for updating data
  private void pickFromCamera(){
      ContentValues contentValues = new ContentValues();
      contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
      contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
      imageuri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
      Intent camerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
      startActivityForResult(camerIntent, IMAGE_PICKCAMERA_REQUEST);
  }

  // We will select an image from gallery