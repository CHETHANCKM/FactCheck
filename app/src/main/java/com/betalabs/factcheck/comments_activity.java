package com.betalabs.factcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.util.Linkify;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import adapters.Adapter_comment;
import de.hdodenhof.circleimageview.CircleImageView;

public class comments_activity extends AppCompatActivity {

    private DatabaseReference mDatabase;


    FirebaseAuth firebaseAuth;
    StorageReference mStorageRef;
    FirebaseFirestore db;


    CircleImageView post_profile_pic, comment_pp;
    TextView post_username, factness_percentage,post_title, post_description,post_time, post_comment, vote_status, count;
    EditText comment_text;
    ImageView post_image, post_badge;
    int voted_total;


    ProgressDialog pd;

    RecyclerView recyclerView;

    String currentuseremail, myname, profileimage;


    List<comment> commentList;
    Adapter_comment adapter_comments;


    @Override
    public void onBackPressed(){
        super.onBackPressed();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = new Intent(comments_activity.this, j_home.class);
            startActivity(i);
        } else {
            Intent i = new Intent(comments_activity.this, j_home.class);
            startActivity(i);
        }


    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_activity);

        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();


        post_profile_pic = findViewById(R.id.post_profile_pic);
        post_username = findViewById(R.id.post_username);
        factness_percentage = findViewById(R.id.factness_percentage);
        post_title = findViewById(R.id.post_title);
        post_description = findViewById(R.id.post_description);
        post_time = findViewById(R.id.post_time);
        post_image = findViewById(R.id.post_image);
        post_badge = findViewById(R.id.post_badge);
        comment_text = findViewById(R.id.comment_text);
        comment_pp = findViewById(R.id.comment_pp);
        post_comment = findViewById(R.id.post_comment);
        vote_status=findViewById(R.id.vote_status);
        currentuseremail = currentUser.getEmail();
        count = findViewById(R.id.com_count);
        recyclerView = findViewById(R.id.comments_list);


        if (currentUser == null)
        {
            Intent login = new Intent(this, login.class);
            startActivity(login);
        }
        else
        {


            try {
                DocumentReference documentReference = db.collection("Journalist").document(currentuseremail);
                documentReference.addSnapshotListener(comments_activity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        profileimage = documentSnapshot.getString("Profile Uri");
                        myname = documentSnapshot.getString("Name");




                        try
                        {

                            Picasso
                                    .get()
                                    .load(profileimage)
                                    .fetch(new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Picasso
                                                    .get()
                                                    .load(profileimage)
                                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                                    .into(comment_pp);

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(profileimage).into(comment_pp);

                                        }
                                    });
                        }
                        catch (Exception e1)
                        {
                            Picasso.get().load(profileimage).into(comment_pp);
                        }







                    }
                });

            }
            catch (Exception e)
            {
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }

        }

        loadpostinfo();

        loadcomments();


        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postcomment();

                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });


    }

    private void loadcomments() {
        Intent i = getIntent();
        String retivedid = i.getStringExtra("postid");


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


        commentList = new ArrayList<>();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Posts").child(retivedid).child("Comments");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    comment comment = ds.getValue(com.betalabs.factcheck.comment.class);

                    commentList.add(comment);

                    adapter_comments = new Adapter_comment(getApplicationContext(), commentList);
                    recyclerView.setAdapter(adapter_comments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postcomment() {
        Intent i = getIntent();
        String retivedid = i.getStringExtra("postid");

        currentuseremail = firebaseAuth.getCurrentUser().getEmail();


        pd = new ProgressDialog(this);
        pd.setMessage("Adding comments");




        String comment = comment_text.getText().toString().trim();

        if (TextUtils.isEmpty(comment))
        {
            Toast.makeText(this, "Feild required", Toast.LENGTH_SHORT).show();
            return;
        }



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(retivedid).child("Comments");
        String timestamp = String.valueOf(System.currentTimeMillis());


        HashMap<String , Object> hashMap = new HashMap<>();

        hashMap.put("comment_id", timestamp);
        hashMap.put("comment", comment);
       hashMap.put("profile_image", profileimage);
       hashMap.put("Name", myname);


       reference.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               pd.dismiss();
               comment_text.setText("");
               Toast.makeText(comments_activity.this, "Commented", Toast.LENGTH_SHORT).show();
               updatecommnetcount();


           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               pd.dismiss();
               Toast.makeText(comments_activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

           }
       });




    }

    boolean mprocesscomment = false;
    private void updatecommnetcount() {
        Intent i = getIntent();
        String retivedid = i.getStringExtra("postid");
        mprocesscomment =true;
        final DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(retivedid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mprocesscomment)
                {
                    String comments_count = ""+dataSnapshot.child("comments_count").getValue();
                    int newcount = Integer.parseInt(comments_count) +1;
                    databaseReference.child("comments_count").setValue(""+newcount);
                    mprocesscomment=false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadpostinfo()
    {

        Intent i = getIntent();
        String retivedid = i.getStringExtra("postid");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");


        final ArrayList<String> data = new ArrayList<>();

        reference.child(retivedid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String profile_image = ""+dataSnapshot.child("Profile_Image").getValue();
                    String title = ""+dataSnapshot.child("Title").getValue();
                    String desc = ""+dataSnapshot.child("Description").getValue();
                    String name = ""+dataSnapshot.child("Name").getValue();
                    String verified = ""+dataSnapshot.child("Verified").getValue();
                    String image = ""+dataSnapshot.child("Image").getValue();
                    String timestamp = ""+dataSnapshot.child("Time_Stamp").getValue();
                    String role = ""+dataSnapshot.child("Role").getValue();
                    String voteminus = ""+dataSnapshot.child("voteminus").getValue();
                    String voteplus = ""+dataSnapshot.child("voteplus").getValue();
                    String comment_count = ""+dataSnapshot.child("comments_count").getValue();


                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(timestamp));
                    String ptime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();



                    int plusvotes_integer = Integer.parseInt(voteplus);
                    int minusvotes_integer = Integer.parseInt(voteminus);


                    double doble_total = minusvotes_integer+plusvotes_integer;
                    double temp = 100*(minusvotes_integer/doble_total);

                    int data = (int)temp;
                    voted_total = plusvotes_integer+minusvotes_integer;






                    vote_status.setText(""+voted_total+" has voted.");

                    if (doble_total == 0)
                    {
                        factness_percentage.setText("");
                        factness_percentage.setTextColor(getResources().getColor(R.color.white));
                    }
                    else
                    {
                        if ((data>=50))
                        {
                            factness_percentage.setText(""+data+"% Fake");
                           factness_percentage.setTextColor(getResources().getColor(R.color.red));
                        }
                        else if (data<49)
                        {
                            factness_percentage.setText(""+data+"% Fake");
                            factness_percentage.setTextColor(getResources().getColor(R.color.green));
                        }
                        else {
                           factness_percentage.setVisibility(View.INVISIBLE);

                        }

                    }


                    Picasso.get().load(profile_image).into(post_profile_pic);

                    post_title.setText(title);
                    post_description.setText(desc);
                    Linkify.addLinks(post_description, Linkify.WEB_URLS);
                    post_username.setText(name);
                    post_time.setText(ptime);


                    int comments = Integer.parseInt(comment_count);

                    if (comments==0)
                    {
                        count.setText("No comments");
                    }
                    else if (comments ==1)
                    {
                        count.setText("1 comment");
                    }
                    else
                    {
                        count.setText(""+comment_count+" comments");
                    }







                    Picasso.get().load(profile_image).into(post_profile_pic);


                    try
                    {
                        if (role.equals("Journalist") && (verified.equals("Yes")))
                        {
                            post_badge.setImageResource(R.drawable.blue_tick);
                        }
                        else if (role.equals("Journalist") && (verified.equals("No")))
                        {
                            post_badge.setImageResource(R.drawable.j_badge);
                        }
                        else
                        {
                            post_badge.setImageResource(R.drawable.empty);
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (image.equals("No Image"))
                    {
                        post_image.setVisibility(View.GONE);
                    }
                    else
                    {
                        try
                        {
                            Picasso.get().load(image).into(post_image);

                        }
                        catch (Exception e)
                        {

                        }
                    }
//


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }



}
