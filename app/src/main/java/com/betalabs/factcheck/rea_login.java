package com.betalabs.factcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class rea_login extends AppCompatActivity {
    private static final int RC_SIGN_IN =1 ;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Button read_google_signin;
    Button signout;
    FirebaseFirestore db;
    ProgressBar progressBar;

    FirebaseDatabase database;
    String name;
    String email;
    String checkemail;



    @Override
    public void onBackPressed() {
        Intent login = new Intent(this, login.class);
        startActivity(login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rea_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();


        read_google_signin = findViewById(R.id.rea_google_signin);
        progressBar= findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        read_google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                FirebaseGoogleAuth (account);
                progressBar.setVisibility(View.VISIBLE);
                read_google_signin.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
            }
            catch (ApiException e)
            {
                Toast.makeText(this, "Sign in Failed: ("+e+")", Toast.LENGTH_SHORT).show();
                FirebaseGoogleAuth (null);
            }
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    FirebaseUser user = mAuth.getCurrentUser();
                      updateUI(user);
                }
                else
                {
                 updateUI(null);
                }
            }
        });
    }


    private void updateUI(FirebaseUser fuser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (account!=null)
            name = account.getDisplayName();
            email= account.getEmail();

        Uri uri = account.getPhotoUrl();
        final String profile_string = uri.toString();




        Map<String, String> usermap = new HashMap<>();

        usermap.put("Name", name);
        usermap.put("Email", email);



        try
        {
            db.collection("Journalist").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists())
                    {
                        new AlertDialog.Builder(rea_login.this)
                                .setTitle("Error !")
                                .setMessage("You are already signed in as journalist.")
                                .setCancelable(true)
                                .show();
                        read_google_signin.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else {

                        Map<String, String> usermap = new HashMap<>();

                        usermap.put("Name", name);
                        usermap.put("Email", email);
                        usermap.put("Profile Uri", profile_string);


                        db.collection("Readers").document(email).set(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent rhome = new Intent(rea_login.this, r_home.class);
                                addusers();
                                startActivity(rhome);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(rea_login.this, "Failed: "+e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
        catch (Exception e)
        {
            Toast.makeText(rea_login.this, "Failed: "+e, Toast.LENGTH_SHORT).show();
        }


    }

    private void addusers() {


        Map<String, String> users = new HashMap<>();

        users.put("Name", name);
        users.put("Email", email);
        users.put("Role", "Readers");


        db.collection("users").document(email).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(rea_login.this, "Failed: "+e, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
