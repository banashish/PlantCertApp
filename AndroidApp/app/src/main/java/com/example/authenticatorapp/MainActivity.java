package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    TextView fullName,email,phone,verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button resendCode,nextPage,updatePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone=findViewById(R.id.profilePhone);
        fullName=findViewById(R.id.profileName);
        email=findViewById(R.id.profileEmail);
        nextPage=findViewById(R.id.nextPage);
        updatePage=findViewById(R.id.UpdateBtn);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        resendCode=findViewById(R.id.resendCode);
        verifyMsg=findViewById(R.id.verifyMsg);

        userId=fAuth.getCurrentUser().getUid();


        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyMsg.getVisibility()==View.VISIBLE)
                {
                    Toast.makeText(MainActivity.this, "Verify your e-mail", Toast.LENGTH_SHORT).show();
                }
                else {
                    //  Toast.makeText(MainActivity.this, "Hello ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplication(), UploadImage.class));
                }
            }
        });

        updatePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyMsg.getVisibility()==View.VISIBLE)
                {
                    Toast.makeText(MainActivity.this, "Verify your e-mail", Toast.LENGTH_SHORT).show();
                }
                else {
                    //  startActivity(new Intent(getApplication(),UpdatePage.class));
                    startActivity(new Intent(getApplication(),ListViewPage.class));
                }
            }
        });

        final FirebaseUser user=fAuth.getCurrentUser();

        if(!user.isEmailVerified())
        {
            resendCode.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has been Sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag","onFailure: Email not sent "+ e.getMessage());
                        }
                    });
                }
            });
        }

        DocumentReference documentReference=fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                phone.setText(documentSnapshot.getString("phone"));
                fullName.setText(documentSnapshot.getString("fName"));
                email.setText(documentSnapshot.getString("email"));
            }
        });
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}
