package com.example.authenticatorapp;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    String currentPhotoPath;
    StorageReference storageReference;
    int PERMISSION_ID = 44;
    ImageView selectedImage;
    Button cameraBtn, galleryBtn,updateBtn;
    TextView latTextView, lonTextView,nameText,issuedTo,city;
    String image_link,sel_id;
    String result="";

    FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);


        city=findViewById(R.id.city);
        selectedImage = findViewById(R.id.displayImageView);
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        nameText = findViewById(R.id.nameText);
        updateBtn=findViewById(R.id.updateBtn);
        issuedTo=findViewById(R.id.issuedTo);



        storageReference = FirebaseStorage.getInstance().getReference();

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        fstore = FirebaseFirestore.getInstance();


        latTextView = findViewById(R.id.lonText);
        lonTextView = findViewById(R.id.latText);
        Intent intent = getIntent();
        result = intent.getStringExtra("item");
        fillDetails(result);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //             USE OF SPINNER
//        CollectionReference subjectsRef = fstore.collection("issuedPlants");
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//        spinner.setOnItemSelectedListener(this);
//        final List<String> subjects = new ArrayList<>();
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, subjects);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        subjectsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        String subject = document.getString("id");
//                        subjects.add(subject);
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
            //                     END OF SPINNER
/////////////////////////////////////////////////////////////////////////////////////////////////

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image_link==null) {
                    Toast.makeText(UpdateInfo.this, "Select Image First", Toast.LENGTH_SHORT).show();
                    return;
                }
                DocumentReference documentReference=fstore.collection("issuedPlants").document(result);
                documentReference.update("Image",image_link);
                Toast.makeText(UpdateInfo.this, "Record Inserted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplication(), ListViewPage.class));

            }
        });

    }



    private void askCameraPermission() {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
            } else {
                // openCamera();
                dispatchTakePictureIntent();
            }
        }

//   private void openCamera() {
//        Intent camera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//       startActivityForResult(camera, CAMERA_REQUEST_CODE);
//
//    }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if(requestCode==CAMERA_PERM_CODE){
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //openCamera();
                    dispatchTakePictureIntent();
                }
                else{
                    Toast.makeText(this, "Camera Permission is required to Use Camera", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if(requestCode==CAMERA_REQUEST_CODE){
                if(resultCode== Activity.RESULT_OK){
                    File f=new File((currentPhotoPath));
                    //   selectedImage.setImageURI(Uri.fromFile(f));
                    Log.d("tag","Absolute Uri of Image is "+ Uri.fromFile(f));

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);

                    uploadImageToFirebase(f.getName(),contentUri);
                }
            }

            if(requestCode==GALLERY_REQUEST_CODE){
                if(resultCode== Activity.RESULT_OK){
                    Uri contentUri=data.getData();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName= "JPEG_" + timeStamp + "_"+getFileExt(contentUri);
                    Log.d("tag","OnActivityResult: Gallery Image Uri: "+ imageFileName);
                    //  selectedImage.setImageURI(contentUri);

                    uploadImageToFirebase(imageFileName,contentUri);
                }
            }
        }

        private void  uploadImageToFirebase(String name, Uri contentUri) {
            final StorageReference image=storageReference.child("pictures/" + name);
            image_link=name;
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(selectedImage);
                            //Log.d("tag","OnSuccess Upload Image uri is: "+uri.toString() );
                        }
                    });
                    Toast.makeText(UpdateInfo.this, "Image is Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateInfo.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }

        // images/image.jpg
        private String getFileExt(Uri contentUri) {
            ContentResolver c=getContentResolver();
            MimeTypeMap mime=MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(c.getType(contentUri));
        }


        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            //  File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File storageDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();
            return image;
        }

        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
            }



        }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sel_id=parent.getItemAtPosition(position).toString();

        Toast.makeText(this, " "+sel_id, Toast.LENGTH_SHORT).show();

        DocumentReference documentReference=fstore.collection("issuedPlants").document(sel_id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                nameText.setText(documentSnapshot.getString("Name"));
                lonTextView.setText(documentSnapshot.getString("Longitude"));
                latTextView.setText(documentSnapshot.getString("Latitude"));
                issuedTo.setText(documentSnapshot.getString("issuedTo"));
            }
        });
    }

    private void fillDetails(String res) {
        DocumentReference documentReference=fstore.collection("issuedPlants").document(res);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                nameText.setText(documentSnapshot.getString("Name"));
                lonTextView.setText(documentSnapshot.getString("Longitude"));
                latTextView.setText(documentSnapshot.getString("Latitude"));
                issuedTo.setText(documentSnapshot.getString("issuedTo"));
                city.setText(documentSnapshot.getString("City"));

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}