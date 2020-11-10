package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText nFullname,nEmail,nPassword,nPhone;
    Button nRegisterBtn;
    TextView nLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nFullname=findViewById(R.id.fullName);
        nEmail=findViewById(R.id.email);
        nPassword=findViewById(R.id.pass);
        nPhone=findViewById(R.id.phone);
        nRegisterBtn=findViewById(R.id.registerBtn);
        nLoginBtn=findViewById(R.id.createText);

        fAuth= FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() !=null)
        {
            startActivity(new Intent(getApplication(),MainActivity.class));
            finish();
        }

        nRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String email=nEmail.getText().toString().trim();
                String password=nPassword.getText().toString().trim();
                final String fullName=nFullname.getText().toString();
                final String phone=nPhone.getText().toString();

                if(TextUtils.isEmpty(fullName)){
                    nFullname.setError("Name is required");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    nEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    nPassword.setError("Password is required");
                    return;
                }

                if(password.length()<6){
                    nPassword.setError("Password Must be >= 6 Character");
                    return;
                }

                if(TextUtils.isEmpty(phone)){
                    nPhone.setError("Mobile No. is required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                // register user into the firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // send verification link

                            FirebaseUser fuser=fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email Has been Sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: Email not sent "+ e.getMessage());
                                }
                            });




                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fstore.collection("users").document(userID);
                            Map<String,Object> user=new HashMap<>();
                            user.put("fName",fullName);
                            user.put("email",email);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"OnSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure"+ e.toString());
                                }
                            });
                            startActivity(new Intent(getApplication(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(Register.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        nLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(),Login.class));
            }
        });
    }
}
