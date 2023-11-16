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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

//import kotlinx.coroutines.scheduling.Task;

public class postforfound extends AppCompatActivity {
    private ImageSwitcher imageIS;
    private Button addimagefound,submitfound;
    private EditText messagepostfound;
    private static final int  PICK_IMAGES_CODE = 0 ;
    int position =0 ;
    private ArrayList<Uri> imageUris;

    private String current_user_id;
    private String messagefound;
    private String saveCurrentDate,saveCurrentTime,Postfoundname;
   private StorageReference PostfoundReference;
    private DatabaseReference reference,postfoundtref;

    private FirebaseAuth authProfile;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postforfound);

        ImageButton buttonforhome = findViewById(R.id.imageButton2);
        buttonforhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(postforfound.this,LostFoundmainActivity.class);
                startActivity(intent);
            }
        });


       PostfoundReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("Registered Users");

        authProfile = FirebaseAuth.getInstance();
        current_user_id =  authProfile.getCurrentUser().getUid();




        messagepostfound = findViewById(R.id.descfound);

        progressBar = findViewById(R.id.progressbarforpostingfounditems);



        imageIS = findViewById(R.id.foundimgdisp);



        imageUris = new ArrayList<>();
        imageIS.setFactory(() -> {
            ImageView imageView = new ImageView(getApplicationContext());
            return imageView;
        });

        submitfound = findViewById(R.id.foundimgupld);
        Button buttonaddimagefound = findViewById(R.id.foundimgadd);




        submitfound.setOnClickListener(v -> {

            ValidatePostFound();
            progressBar.setVisibility(View.VISIBLE);

        });


        buttonaddimagefound.setOnClickListener(v -> pickImagesintent());

    }
    private void ValidatePostFound() {


        messagefound =  messagepostfound.getText().toString();
        if(imageUris == null ){
            Toast.makeText(postforfound.this, "Please select an image ", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(messagefound)){
            Toast.makeText(postforfound.this, "Please add a message", Toast.LENGTH_SHORT).show();
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

        Postfoundname = current_user_id+  saveCurrentDate + saveCurrentTime   ;

        StorageReference ImagefoundFolder = FirebaseStorage.getInstance().getReference().child("FoundItems");

        for(int j =0 ; j < imageUris.size() ; j++){
            Uri IndividualImage = imageUris.get(j);
            StorageReference ImageName  = ImagefoundFolder.child("Image" + IndividualImage.getLastPathSegment() + Postfoundname + ".jpg " );
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FoundItems");
        reference.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String namefound = snapshot.child("FullName").getValue().toString();
                    String phonefound= snapshot.child("PhoneNumber").getValue().toString();
                    String emailfound = snapshot.child("Email").getValue().toString();


                    HashMap postfoundMap = new HashMap();
                    postfoundMap.put("date",saveCurrentDate);
                    postfoundMap.put("emailfound",emailfound);
                    postfoundMap.put("Imagelink",url);




                    postfoundMap.put("time",saveCurrentTime);
                    postfoundMap.put("uid",current_user_id);
                    postfoundMap.put("FullName",namefound);

                    postfoundMap.put("PhoneNumber",phonefound);
                    postfoundMap.put("Message",messagefound);

                    databaseReference.child(Postfoundname).updateChildren(postfoundMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){

                                sendusertopostmainactivity();
                                progressBar.setVisibility(View.GONE);


                                Toast.makeText(postforfound.this, "Post is updated succesfully ", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(postforfound.this, "Error occured while updating your post", Toast.LENGTH_SHORT).show();

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

    private void sendusertopostmainactivity() {
        Intent intent = new Intent(postforfound.this,LostFoundmainActivity.class);
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