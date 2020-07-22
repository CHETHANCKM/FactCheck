package com.betalabs.factcheck;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class j_profile extends Fragment {

    TextView j_username;
    ImageView j_profilepic;
    TextView email_feild;
    TextView code;
    CardView logout, j_about, j_pp, j_tc, j_ref;
    ImageView badge;


    private FirebaseAuth mAuth;
    FirebaseFirestore db;


    public j_profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_j_profile, container, false);

        j_profilepic = v.findViewById(R.id.j_profilepic);
        j_username = v.findViewById(R.id.j_username);

        email_feild = v.findViewById(R.id.email_feild);
        logout= v.findViewById(R.id.logout);
        j_pp = v.findViewById(R.id.j_pp);
        j_tc = v.findViewById(R.id.j_tc);
        j_ref = v.findViewById(R.id.j_ref);
        j_about = v.findViewById(R.id.j_about);
        badge = v.findViewById(R.id.badge);
        mAuth = FirebaseAuth.getInstance();
        code = v.findViewById(R.id.code);
        code.setText("CODEX");
        db = FirebaseFirestore.getInstance();
        mAuth.getCurrentUser();

        String user_email = mAuth.getCurrentUser().getEmail();

        email_feild.setText(""+user_email);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
        {
            Intent login = new Intent(getContext(), login.class);
            startActivity(login);
        }





        DocumentReference documentReference = db.collection("Journalist").document(user_email);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                j_username.setText(documentSnapshot.getString("Name"));
                code.setText(documentSnapshot.getString("Invite Code"));

                final String pic = documentSnapshot.getString("Profile Uri");

                try
                {

                    Picasso
                            .get()
                            .load(pic)
                            .fetch(new Callback() {
                                @Override
                                public void onSuccess() {
                                    Picasso
                                            .get()
                                            .load(pic)
//                                            .networkPolicy(NetworkPolicy.OFFLINE)
                                            .into(j_profilepic);

                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });

                }
                catch (Exception e1)
                {

                    Uri uri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                    j_profilepic.setImageURI(uri);
                }

                String role = documentSnapshot.getString("Role");
                String verified = documentSnapshot.getString("Verified");


                Intent intent = new Intent (getContext(), comments_activity.class);
                intent.putExtra("profileimage", pic);

                 try
                 {
                     if (role.equals("Journalist"))
                     {
                         if (verified.equals("Yes"))
                         {
                             badge.setImageResource(R.drawable.blue_tick);
                         }
                         else
                         {
                             badge.setImageResource(R.drawable.j_badge);
                         }
                     }
                     else

                     {
                         badge.setImageResource(R.drawable.empty);
                     }
                 }
                 catch (Exception e5)
                 {
                     Toast.makeText(getActivity(), "Error loading. Restart your Application.", Toast.LENGTH_SHORT).show();
                 }

            }
        });


        j_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(getActivity(), about.class);
                startActivity(act);
            }
        });

        j_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(getActivity(), pp.class);
                startActivity(act);
            }
        });

        j_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(getActivity(), tc.class);
                startActivity(act);
            }
        });

        j_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Fact Check");
                    String shareMessage= "\nHey, Let us stop sharing fake news across social media.\nCheck whether the news you share is a fact or a myth ";
                    shareMessage = shareMessage + "https://example.com/" +"\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Refer to your friend"));
                } catch (Exception e)
                {
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Log out")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try
                                {
                                    mAuth.signOut();
                                    Intent signout = new Intent(getActivity(), MainActivity.class);
                                    startActivity(signout);
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getActivity(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        return v;
    }

}
