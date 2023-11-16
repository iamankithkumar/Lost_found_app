package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import kotlinx.coroutines.scheduling.Task;

public class changepasswordactivity extends AppCompatActivity {

    private FirebaseAuth authProile ;
    private EditText editTextoldpassword , editTextsetnewpassword, editTextconfirmnewpassword;
    private Button buttonauthenticate , buttonupdatepassword;
    private ProgressBar progressBar;
    private String userPwdCurr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepasswordactivity);

        editTextsetnewpassword = findViewById(R.id.newpass);
        editTextoldpassword = findViewById(R.id.currpass);
        editTextconfirmnewpassword = findViewById(R.id.newpassconf);

        progressBar = findViewById(R.id.progressbarforchangepassword);
        buttonauthenticate = findViewById(R.id.authenticate);
        buttonupdatepassword = findViewById(R.id.confchangepass);


        // Disabling user to edit any content of new password before authentication

        editTextsetnewpassword.setEnabled(false);
        editTextconfirmnewpassword.setEnabled(false);
        buttonupdatepassword.setEnabled(false);

        authProile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProile.getCurrentUser();


        if(firebaseUser.equals("")){
            Toast.makeText(changepasswordactivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

        }
        else{
            AuthenticateUser(firebaseUser);
        }



    }

    private void AuthenticateUser(FirebaseUser firebaseUser)
     {
        buttonauthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr = editTextoldpassword.getText().toString();

                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(changepasswordactivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    editTextoldpassword.setError("Please enter your password to verify that is you.");
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                // Disabling user to enter oldpassword
                                //Enabling user to set new password
                                editTextoldpassword.setEnabled(false);
                                editTextsetnewpassword.setEnabled(true);
                                editTextconfirmnewpassword.setEnabled(true);
                                buttonauthenticate.setEnabled(false);
                                buttonupdatepassword.setEnabled(true);

                                Toast.makeText(changepasswordactivity.this, "You can change the password now.", Toast.LENGTH_SHORT).show();

                                buttonupdatepassword.setBackgroundTintList(ContextCompat.getColorStateList(changepasswordactivity.this,R.color.black));
                                buttonupdatepassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changepwd(firebaseUser);

                                    }
                                });

                            }
                            else{
                                try{
                                    throw task.getException();
                                }catch(Exception e){
                                    Toast.makeText(changepasswordactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                }
            }
        });
    }

    private void changepwd(FirebaseUser firebaseUser) {
        String userPwdnew =editTextsetnewpassword.getText().toString();
        String userPwdconfirmnew = editTextconfirmnewpassword.getText().toString();

        if(TextUtils.isEmpty(userPwdnew)){
            Toast.makeText(changepasswordactivity.this, "New Password is required", Toast.LENGTH_SHORT).show();
            editTextsetnewpassword.setError("Please enter your new password");
            editTextsetnewpassword.requestFocus();
        }else if(TextUtils.isEmpty(userPwdconfirmnew)){
            Toast.makeText(changepasswordactivity.this, "Confirmation for new Password is required", Toast.LENGTH_SHORT).show();
            editTextconfirmnewpassword.setError("Please confirm your new password");
            editTextconfirmnewpassword.requestFocus();

        }else if(!userPwdnew.matches(userPwdconfirmnew)){
            Toast.makeText(changepasswordactivity.this, "Both passwords should match", Toast.LENGTH_SHORT).show();
            editTextconfirmnewpassword.setError("Password didn't match");
            editTextconfirmnewpassword.requestFocus();
        }
        else if(userPwdCurr.matches(userPwdconfirmnew)){
            Toast.makeText(changepasswordactivity.this, "You have entered a password that is used earlier", Toast.LENGTH_SHORT).show();
            editTextconfirmnewpassword.setError("Please enter a new password");
            editTextconfirmnewpassword.requestFocus();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userPwdnew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(changepasswordactivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(changepasswordactivity.this,LostFoundmainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(changepasswordactivity.this,LostFoundmainActivity.class);
                        try {
                            throw task.getException();
                        }catch(Exception e){
                            Toast.makeText(changepasswordactivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

}