package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

        private EditText editTextLoginEmail, editTextLoginPassword;

        private ProgressBar progressBar;
        private FirebaseAuth authProfile;
        private static final String TAG = "Login Activity";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);


            editTextLoginEmail = findViewById(R.id.loginemail);
            editTextLoginPassword = findViewById(R.id.loginpass);
            progressBar = findViewById(R.id.progressbarinloginactivity);


           authProfile = FirebaseAuth.getInstance();

            Button buttonForgotPassword = findViewById(R.id.forgotpass);
            buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LoginActivity.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ForgotpasswordActivity.class));
                }
            });
            Button buttonLogin = findViewById(R.id.loginbtn);
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textEmail = editTextLoginEmail.getText().toString();
                    String textPassword = editTextLoginPassword.getText().toString();
                    if (TextUtils.isEmpty(textEmail)) {
                        Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        editTextLoginEmail.setError("Email is Required");
                        editTextLoginEmail.requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                        Toast.makeText(LoginActivity.this, "Please re - enter your email ", Toast.LENGTH_SHORT).show();
                        editTextLoginEmail.setError("Valid Email is Required");
                        editTextLoginEmail.requestFocus();

                    } else if (TextUtils.isEmpty(textPassword)) {
                        Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                        editTextLoginPassword.setError("Password is required");
                        editTextLoginPassword.requestFocus();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        loginuser(textEmail, textPassword);

                    }
                }
            });


        }

       private void loginuser(String username, String password) {
            authProfile.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {


                        FirebaseUser firebaseUser = authProfile.getCurrentUser();
                        if (firebaseUser.isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "You are Logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, LostFoundmainActivity.class);
                            startActivity(intent);


                        } else {
                            firebaseUser.sendEmailVerification();
                            authProfile.signOut();
                            showAlertDialogue();
                        }
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editTextLoginEmail.setError("Please verify the email address");
                            editTextLoginEmail.requestFocus();
                            editTextLoginPassword.setError("Please enter the correct password");
                            editTextLoginPassword.requestFocus();


                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    progressBar.setVisibility(View.GONE);
                }

            });
        }

        private void showAlertDialogue() {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Email-verification Pending");
            builder.setMessage("Please verify your email now.You cannot login without email verification.");


            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


    }
