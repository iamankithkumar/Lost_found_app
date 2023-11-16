package com.example.myapplication;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//import kotlinx.coroutines.scheduling.Task;

public class register extends AppCompatActivity {
    private EditText editTextRegisterFullName,editTextRegisterEmail,editTextRegisterRollNo,
            editTextRegisterPassword,editTextRegisterConfirmPassword,
            editTextRegisterPhoneNumber;

    private static final String TAG="register";




   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        editTextRegisterFullName = findViewById(R.id.namereg);
        editTextRegisterRollNo = findViewById(R.id.rollnoreg);

        editTextRegisterEmail =findViewById(R.id.emailreg);
        editTextRegisterPassword = findViewById(R.id.passreg);
        editTextRegisterConfirmPassword = findViewById(R.id.confpassreg);
        editTextRegisterPhoneNumber = findViewById(R.id.phnoreg);

        Button buttonSignUp = findViewById(R.id.SignUp);

        buttonSignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textFullName = editTextRegisterFullName.getText().toString();
                String textRollNo = editTextRegisterRollNo.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textPassword = editTextRegisterPassword.getText().toString();
                String textConfirmPassword = editTextRegisterConfirmPassword.getText().toString();

                String textPhoneNumber= editTextRegisterPhoneNumber.getText().toString();
                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(register.this, "Please Enter Your Full Name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.setError("Full Name is Required");
                    editTextRegisterFullName.requestFocus();
                }
                else if(TextUtils.isEmpty(textRollNo)){
                    Toast.makeText(register.this, "Please enter Roll No", Toast.LENGTH_SHORT).show();
                    editTextRegisterRollNo.setError("Roll No is required");

                }
                else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(register.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Email is Required");
                    editTextRegisterEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(register.this, "Please re-enter Email address", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(register.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Password is Required");
                    editTextRegisterEmail.requestFocus();
                }
                else if (textPassword.length()<6) {
                    Toast.makeText(register.this, "Password should be 6 digits", Toast.LENGTH_SHORT).show();
                    editTextRegisterPassword.setError("Password is too weak");
                    editTextRegisterPassword.requestFocus();

                }
                else if(TextUtils.isEmpty(textConfirmPassword)){
                    Toast.makeText(register.this, "Please confirm password", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Confirm the password");
                    editTextRegisterEmail.requestFocus();
                }
                else if(!textPassword.equals(textConfirmPassword)){
                    Toast.makeText(register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPassword.requestFocus();

                    editTextRegisterPassword.clearComposingText();
                    editTextRegisterConfirmPassword.clearComposingText();
                }
                else if(TextUtils.isEmpty(textPhoneNumber)){
                    Toast.makeText(register.this, "Please enter the phone number", Toast.LENGTH_SHORT).show();
                    editTextRegisterPhoneNumber.setError("Phone number is required");
                    editTextRegisterPhoneNumber.requestFocus();

                }
                else if (textPhoneNumber.length() != 10) {
                    Toast.makeText(register.this, "Phone re enter your phone number", Toast.LENGTH_LONG).show();
                    editTextRegisterPhoneNumber.setError("Phone number should be 10 digits");
                    editTextRegisterPhoneNumber.requestFocus();

                }
                else{
                    registerUser(textFullName,textRollNo,textEmail,textPassword,textConfirmPassword,textPhoneNumber);

                }
            }
        });
    }
    // Register user using the given credits
    private void registerUser(String textFullName, String textRollNo, String textEmail, String textPassword, String textConfirmPassword, String textPhoneNumber) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    firebaseUser.sendEmailVerification();

                    Intent intent = new Intent(register.this,MainActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);




                    ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails(textFullName,textRollNo,textPhoneNumber,textEmail);


                    DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Send verification Email
                            if (task.isSuccessful()) {

                                //to close register activity
                            }
                            else{
                                Toast.makeText(register.this, "Registration Failed.Please Verify.", Toast.LENGTH_LONG).show();


                            }
                        }
                    });



                }
                else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        editTextRegisterPassword.setError("Weak Password");
                        editTextRegisterPassword.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e ){
                        editTextRegisterEmail.setError("Invalid or already in use");
                        editTextRegisterEmail.requestFocus();
                    }
                    catch(FirebaseAuthUserCollisionException e){
                        editTextRegisterEmail.setError("User is already registered");
                        editTextRegisterEmail.requestFocus();
                    }
                    catch( Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(register.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }
}