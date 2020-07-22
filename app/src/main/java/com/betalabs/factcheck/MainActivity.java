package com.betalabs.factcheck;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 800;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {


                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)


                    //if internet connection is avaliable
                    {
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        if (currentUser == null)
                        {

                            Intent login = new Intent(MainActivity.this, login.class);
                            startActivity(login);
                        }
                        else //internet connection is not avaliable
                        {
                            String user_email = currentUser.getEmail();
                            DocumentReference documentReference = db.collection("users").document(user_email);

                            documentReference.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                    String role = documentSnapshot.getString("Role");

                                    if (role.equals("Journalist"))
                                    {
                                        Intent journalist = new Intent(MainActivity.this, j_home.class);
                                        startActivity(journalist);
                                    }
                                    else if (role.equals("Readers"))
                                    {
                                        Intent readers = new Intent(MainActivity.this, r_home.class);
                                        startActivity(readers);
                                    }
                                    else if (role.equals(null))
                                    {
                                        Intent login = new Intent(MainActivity.this, login.class);
                                        startActivity(login);
                                    }
                                    else
                                        {
                                        Intent login = new Intent(MainActivity.this, login.class);
                                        startActivity(login);
                                    }

                                }
                            });




                        }
                    }
                    else
                    {

                        // set a alert dilog
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("No internet Connection.")
                                .setMessage("Please check internet connection")
                                .setIcon(R.mipmap.icon)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(i);
                                        overridePendingTransition(0, 0);
                                    }
                                })
                                .show();
                    }
                }
            },SPLASH_TIME_OUT);

    }



}
