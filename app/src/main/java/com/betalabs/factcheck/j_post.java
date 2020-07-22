package com.betalabs.factcheck;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class j_post extends Fragment {

    FirebaseAuth firebaseAuth;
    StorageReference mStorageRef;
    FirebaseFirestore db;
    CardView imagecard;

    EditText ptitle, pdesc;
    TextView pdone;
    ImageView p_image;
    ImageButton pupload;
    Uri image_uri = null;
    String profileimage;
    String verified, name;
    String role;
    TextView imagepercentage;
    ProgressBar imageprogress;


    private  static final int CAMERA_REQUEST_CODE= 100;
    private  static final int STORAGE_REQUEST_CODE= 200;

    private static  final  int IMAGE_PICK_CAMERA_CODE= 300;
    private static  final  int IMAGE_PICK_GALLARY_CODE= 400;

    String [] campermission;
    String [] storagepermission;


    public j_post() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_j_post, container, false);

        firebaseAuth  = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        imagecard = v.findViewById(R.id.imagecard);
        ptitle= v.findViewById(R.id.ptitle);
        pdesc = v.findViewById(R.id.pdesc);
        p_image= v.findViewById(R.id.p_image);
        pupload = v.findViewById(R.id.pupload);
        pdone = v.findViewById(R.id.pdone);

        imagepercentage = v.findViewById(R.id.imagepercentage);
        imageprogress = v.findViewById(R.id.imageprogress);

        imagepercentage.setVisibility(View.GONE);
        imageprogress.setVisibility(View.GONE);

        campermission = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};


        String user_email = firebaseAuth.getCurrentUser().getEmail();

        DocumentReference documentReference = db.collection("Journalist").document(user_email);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                profileimage = documentSnapshot.getString("Profile Uri");
                role = documentSnapshot.getString("Role");
                verified = documentSnapshot.getString("Verified");
                name = documentSnapshot.getString("Name");
            }
        });



        pdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String title = ptitle.getText().toString().trim();
                final String description = pdesc.getText().toString().trim();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);




                if (TextUtils.isEmpty(title) ||  TextUtils.isEmpty(description)) {

                    Snackbar.make(getView(),"All fields are required" , Snackbar.LENGTH_LONG).show();
                }
                else if (title.length()>50)
                {
                    Snackbar.make(getView(),"Title too long!" , Snackbar.LENGTH_LONG).show();
                }
                else if(image_uri==null)
                {


                    upload_data(title, description, "No Image");
                    ptitle.setEnabled(false);
                    pdesc.setEnabled(false);
                    pupload.setEnabled(false);
                    pdone.setText("Publishing...");



                }
                else
                {
                    upload_data(title, description, String.valueOf(image_uri));
                    ptitle.setEnabled(false);
                    pdesc.setEnabled(false);
                    pupload.setEnabled(false);
                    pdone.setText("Publishing...");
                }


            }
        });


        pupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showimagepicdilog();

            }
        });



        return  v;
    }

    private void upload_data(final String title, final String description, String no_image) {

        final String timestamp = String.valueOf(System.currentTimeMillis());
        String  filepathandname = "Post/"+"post_"+timestamp;


        if (image_uri!=null)
        {


            //post with image

            imagepercentage.setVisibility(View.VISIBLE);
            imageprogress.setVisibility(View.VISIBLE);




            mStorageRef = FirebaseStorage.getInstance().getReference().child(filepathandname);
            mStorageRef.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());

                    String downloadurl = uriTask.getResult().toString();

                    if (uriTask.isSuccessful())
                    {
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("Name", name);
                        hashMap.put("Title",title );
                        hashMap.put("Description",description );
                        hashMap.put("Time_Stamp",timestamp );
                        hashMap.put("Image", downloadurl);
                        hashMap.put("Profile_Image", profileimage);
                        hashMap.put("Role", role );
                        hashMap.put("Verified", verified);
                        hashMap.put("voteminus" ,"0");
                        hashMap.put("voteplus", "0");
                        hashMap.put("comments_count", "0");


                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        reference.child(timestamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pdone.setText("Posted");
                                        Snackbar.make(getView(),"Post Published" , Snackbar.LENGTH_LONG).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });



                        HashMap<Object, String> noti = new HashMap<>();
                        noti.put("Name", name);
                        noti.put("Title",title );
                        noti.put("Time_Stamp",timestamp );
                        noti.put("Image", downloadurl);
                        noti.put("Profile_Image", profileimage);


                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications");
                        ref.child(timestamp).setValue(noti)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        pdone.setText("Publish");
                                        ptitle.getText().clear();
                                        pdesc.getText().clear();
                                        ptitle.setEnabled(true);
                                        pdesc.setEnabled(true);
                                        pupload.setEnabled(true);
                                        imagecard.setVisibility(View.GONE);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(getView(),""+e.getMessage() , Snackbar.LENGTH_LONG).show();






                            }
                        });
                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        imageprogress.setProgress((int) progress);
                        imagepercentage.setText(""+(int)progress+"% done.");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(getView(),""+e.getMessage() , Snackbar.LENGTH_LONG).show();
                }
            });



        }
        else
        {
            //post without image
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("Name", name);
            hashMap.put("Title",title );
            hashMap.put("Description",description );
            hashMap.put("Time_Stamp",timestamp );
            hashMap.put("Image", "No Image");
            hashMap.put("Profile_Image", profileimage);
            hashMap.put("Role", role);
            hashMap.put("Verified", verified);
            hashMap.put("voteminus" ,"0");
            hashMap.put("voteplus", "0");
            hashMap.put("comments_count", "0");


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
            reference.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pdone.setText("Posted");
                            Snackbar.make(getView(),"Post Published" , Snackbar.LENGTH_LONG).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(getView(),""+e.getMessage() , Snackbar.LENGTH_LONG).show();
                }
            });


            HashMap<Object, String> noti = new HashMap<>();
            noti.put("Name", name);
            noti.put("Title",title );
            noti.put("Time_Stamp",timestamp );
            noti.put("Image", "No Image");
            noti.put("Profile_Image", profileimage);


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications");
            ref.child(timestamp).setValue(noti)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            pdone.setText("Publish");
                            ptitle.getText().clear();
                            pdesc.getText().clear();
                            ptitle.setEnabled(true);
                            pdesc.setEnabled(true);
                            pupload.setEnabled(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(getView(),""+e.getMessage() , Snackbar.LENGTH_LONG).show();
                }
            });

        }


    }

    private void showimagepicdilog() {
        String options[] = {"Camera", "Gallary"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image from");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which==0)
                {

                    if (!checkcampermission())
                    {
                        requestcameraepermission();
                    }
                    else
                    {
                        pickfromcamera();
                    }

                }
                if (which==1){

                    if (!checkcampermission())
                    {
                        requestcameraepermission();
                    }
                    else
                    {
                        picfromgallary();
                    }

                }
            }
        });
        builder.create().show();
    }

    public void picfromgallary()
    {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
       startActivityForResult(intent, IMAGE_PICK_GALLARY_CODE);

    }

    public void pickfromcamera()
    {

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "desc");
        image_uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(i, IMAGE_PICK_CAMERA_CODE);
    }


    private boolean checkcampermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestcameraepermission() {
        ActivityCompat.requestPermissions(getActivity(), campermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:
            {
                if (grantResults.length>0)
                {
                    boolean cameraaccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean staorageaccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;

                    if (cameraaccepted && staorageaccepted)
                    {
                            pickfromcamera();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Camera adnd storge permisson requiried", Toast.LENGTH_SHORT).show();
                    }

                }

                else

                    {

                }
            }
            break;
            case STORAGE_REQUEST_CODE:
            {
                if (grantResults.length>0)
                {
                    boolean staorageaccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;

                    if (staorageaccepted)
                    {
                        picfromgallary();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "storage permisson requiried", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {

                }
            }
            break;

        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK)
        {
            if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                Picasso.get().load(image_uri).into(p_image);
            }
            else if (requestCode == IMAGE_PICK_GALLARY_CODE)
            {
                image_uri = data.getData();
                Picasso.get().load(image_uri).into(p_image);

            }
        }

    }
}
