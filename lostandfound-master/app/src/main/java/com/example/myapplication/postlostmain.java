package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher;

///**********************************


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

//import kotlinx.coroutines.scheduling.Task;

public class postlostmain extends AppCompatActivity {
    private ImageSwitcher imageIS;
    private Button addimagelost,submitlost,nextinlost,previousinlost;
    private EditText messagepostlost;
    private static final int  PICK_IMAGES_CODE = 0 ;
    int position =0 ;
    private ArrayList<Uri> imageUris;

    private String current_user_id;
    private String messagelost;
    private String saveCurrentDate,saveCurrentTime ,Postlostname;
    private StorageReference PostlostReference;
    private  DatabaseReference reference,postlostref;

    private FirebaseAuth authProfile;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlostmain);
        ImageButton buttonforhome = findViewById(R.id.imageButton3);
        buttonforhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(postlostmain.this,LostFoundmainActivity.class);
                startActivity(intent);
            }
        });

        PostlostReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("Registered Users");

        authProfile = FirebaseAuth.getInstance();
        current_user_id =  authProfile.getCurrentUser().getUid();




        messagepostlost = findViewById(R.id.desclost);

        progressBar = findViewById(R.id.progressbarforpostinglostitems);



        imageIS = findViewById(R.id.lostimgdisp);





        imageUris = new ArrayList<>();
        imageIS.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });




        submitlost = findViewById(R.id.lostimgupload);
        Button buttonaddimagelost = findViewById(R.id.lostimgadd);




        submitlost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidatePostLost();
                progressBar.setVisibility(View.VISIBLE);

            }
        });


        buttonaddimagelost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImagesintent();


            }
        });

    }

    private void ValidatePostLost() {


        messagelost =  messagepostlost.getText().toString();
        if(imageUris == null ){
            Toast.makeText(postlostmain.this, "Please select an image ", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(messagelost)){
            Toast.makeText(postlostmain.this, "Please add a message", Toast.LENGTH_SHORT).show();
        }


        else{
            StoringImagetoFirebaseStorage();
        }

    }

    private void StoringImagetoFirebaseStorage() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        Postlostname = current_user_id+  saveCurrentDate + saveCurrentTime   ;

        StorageReference ImagelostFolder = FirebaseStorage.getInstance().getReference().child("LostItems");

        for(int j =0 ; j < imageUris.size() ; j++){
            Uri IndividualImage = imageUris.get(j);
            StorageReference ImageName  = ImagelostFolder.child("Image" + IndividualImage.getLastPathSegment() + Postlostname + ".jpg " );
            ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url =  String.valueOf(uri);
                            StoreLink(url);

                        }
                    });
                }
            });
        }

    }



    private void StoreLink(String url) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("LostItems");
        reference.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String namelost = snapshot.child("FullName").getValue().toString();
                    String phonelost = snapshot.child("PhoneNumber").getValue().toString();
                    String emaillost = snapshot.child("Email").getValue().toString();


                    HashMap postlostMap = new HashMap();
                    postlostMap.put("date",saveCurrentDate);
                    postlostMap.put("emaillost",emaillost);
                    postlostMap.put("Imagelink",url);




                    postlostMap.put("time",saveCurrentTime);
                    postlostMap.put("uid",current_user_id);
                    postlostMap.put("FullName",namelost);

                    postlostMap.put("PhoneNumber",phonelost);
                    postlostMap.put("Message",messagelost);

                    databaseReference.child(Postlostname).updateChildren(postlostMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){

                                sendusertopostlostmainactivity();
                                progressBar.setVisibility(View.GONE);


                                Toast.makeText(postlostmain.this, "Post is updated succesfully ", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(postlostmain.this, "Error occured while updating your post", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendusertopostlostmainactivity() {

        Intent intent = new Intent(postlostmain.this,LostFoundmainActivity.class);
        startActivity(intent);

    }

    private void pickImagesintent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"),PICK_IMAGES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGES_CODE){
            if(data.getClipData()!=null){

                int count = data.getClipData().getItemCount();

                for(int i =0 ; i < count ; i++ ){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
                imageIS.setImageURI(imageUris.get(0));
                position = 0;
            }
            else{
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                imageIS.setImageURI(imageUris.get(0));
                position = 0;
            }
        }
    }
}