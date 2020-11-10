package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UploadImage extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    public static final int STORAGE_CODE = 110;
    ImageView selectedImage;
    Button cameraBtn, galleryBtn, locBtn,nxtBtn;
    String currentPhotoPath;
    StorageReference storageReference;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView,City;
    EditText nameText,cityText;
    FirebaseFirestore fstore;
    String image_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        //  requestPermission();
        selectedImage = findViewById(R.id.displayImageView);
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        nxtBtn=findViewById(R.id.nxtBtn);
        City=findViewById(R.id.City);
        nameText=findViewById(R.id.nameText);
        //idText=findViewById(R.id.idText);

        locBtn = findViewById(R.id.locBtn);
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

        fstore=FirebaseFirestore.getInstance();

        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });

        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=nameText.getText().toString().trim();
           //     String id=idText.getText().toString().trim();
                final String longitude=lonTextView.getText().toString();
                final String latitude=latTextView.getText().toString();
                final String city=City.getText().toString();
                if(TextUtils.isEmpty(name)){
                    nameText.setError("Plant Name is required");
                    return;
                }
//                if(TextUtils.isEmpty(id)){
//                    idText.setError("Plant ID is required");
//                    return;
//                }

                if(TextUtils.isEmpty(city))
                {
                    lonTextView.setError("Location is required");
                    latTextView.setError("Location is required");
                    Toast.makeText(UploadImage.this, "Click Get Location First", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(image_link==null)
                {
                    Toast.makeText(UploadImage.this, "Select Image First", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(UploadImage.this, "Record Inserted", Toast.LENGTH_SHORT).show();
                DocumentReference documentReference=fstore.collection("plants").document();
                Map<String,Object> plant=new HashMap<>();
                plant.put("Name",name);
              //  plant.put("id",id);
                plant.put("Latitude",latitude);
                plant.put("Longitude",longitude);
                plant.put("Image", image_link);
                plant.put("City",city);

                GeoPoint gp=new GeoPoint(Double.parseDouble(latitude),Double.parseDouble(longitude));
                plant.put("Location",gp);


                documentReference.set(plant).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("tag","OnSuccess: Record Inserted");
                        startActivity(new Intent(getApplication(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag","onFailure"+ e.toString());
                    }
                });

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");
                                    Geocoder gcd = new Geocoder(UploadImage.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (addresses.size() > 0) {
                                        City.setText(addresses.get(0).getLocality());
                                        System.out.println(addresses.get(0).getLocality());
                                    }

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    private void askCameraPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
        }
        else
        {
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

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==CAMERA_REQUEST_CODE){
           if(resultCode== Activity.RESULT_OK){
               File f=new File((currentPhotoPath));
            //   selectedImage.setImageURI(Uri.fromFile(f));
               Log.d("tag","Absolute Uri of Image is "+Uri.fromFile(f));

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

    private void uploadImageToFirebase(String name, Uri contentUri) {
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
                Toast.makeText(UploadImage.this, "Image is Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadImage.this, "Upload Failed", Toast.LENGTH_SHORT).show();
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
        File storageDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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

}
