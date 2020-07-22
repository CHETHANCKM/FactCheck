package adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.text.util.Linkify;
import com.betalabs.factcheck.R;
import com.betalabs.factcheck.comments_activity;
import com.betalabs.factcheck.new_post;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class adapter_post extends RecyclerView.Adapter<adapter_post.Myholder>
{



    Context context;
    List<new_post> postList;
    String uid;




    private DatabaseReference voteplusRef; //pluscounts
    private DatabaseReference voteminusRef; //minuscounts
    private DatabaseReference postRef;

    boolean mProcessplus= false;
    boolean mProcessminus = false;
    int voted_total;



    public adapter_post(Context context, List<new_post> postList) {
        this.context = context;
        this.postList = postList;
        uid = FirebaseAuth.getInstance().getUid();

        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        voteplusRef = FirebaseDatabase.getInstance().getReference().child("voteplus");
        voteminusRef = FirebaseDatabase.getInstance().getReference().child("voteminus");

    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.new_post, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(final Myholder myholder, final int i) {

  final String timestamp= postList.get(i).getTime_Stamp();
        final String profilepic = postList.get(i).getProfile_Image();
        final String title = postList.get(i).getTitle();
        final String post_image = postList.get(i).getImage();
        final String description = postList.get(i).getDescription();
        String name = postList.get(i).getName();
        String verified = postList.get(i).getVerified();
        String role = postList.get(i).getRole();
        String plusvotes = postList.get(i).getVoteplus();
        String minusvotes = postList.get(i).getVoteminus();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String ptime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        myholder.username.setText(name);
        myholder.time.setText(ptime);
        myholder.title.setText(title);
        myholder.description.setText(description);
        Linkify.addLinks(myholder.description, Linkify.WEB_URLS);

        int plusvotes_integer = Integer.parseInt(plusvotes);
        int minusvotes_integer = Integer.parseInt(minusvotes);


        double doble_total = minusvotes_integer+plusvotes_integer;
        double temp = 100*(minusvotes_integer/doble_total);

        int data = (int)temp;
        voted_total = plusvotes_integer+minusvotes_integer;

        myholder.comment_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent (v.getContext(), comments_activity.class);
               i.putExtra("postid", timestamp);
               v.getContext().startActivity(i);

            }
        });




        if (doble_total == 0)
        {
            myholder.factness_percentage.setText("Be the first to responce");
            myholder.factness_percentage.setTextColor(context.getResources().getColor(R.color.dark_blue));
        }
        else
        {
            if ((data>=50))
            {
                myholder.factness_percentage.setText(""+data+"% Fake");
                myholder.factness_percentage.setTextColor(context.getResources().getColor(R.color.red));
            }
            else if (data<49)
            {
                myholder.factness_percentage.setText(""+data+"% Fake");
                myholder.factness_percentage.setTextColor(context.getResources().getColor(R.color.green));
            }
            else {
                myholder.factness_percentage.setVisibility(View.INVISIBLE);

            }

        }


       setvoteplus(myholder, timestamp);
       setminusvotes(myholder, timestamp);


       myholder.options.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               try {
                   PopupMenu popupMenu = new PopupMenu(context, myholder.options);
                   popupMenu.inflate(R.menu.options);
                   popupMenu.show();

                   popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                       @Override
                       public boolean onMenuItemClick(MenuItem item) {

                           switch (item.getItemId())
                           {
                               case R.id.options_report:
                                   report_post();
                                   break;
                               default: break;
                           }
                           return false;
                       }
                   });



               }
               catch (Exception e)
               {
                   Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
               }





           }

           private void report_post() {


               final String uemail = FirebaseAuth.getInstance().getCurrentUser().getUid();
               final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reported Posts").child(timestamp);

               final String options[] = {"Inappropriate content", "Hate Speech or symbols", "Violence or dangerous organisations", "Sale of illegal or regulated goods", "Bullying or Harrasment","Scam or fraud", "I just don't like it"};


               AlertDialog.Builder builder = new  AlertDialog.Builder(context);
               builder.setTitle("Why are you reporting this post?");
               builder.setItems(options, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which)
                   {
                       String reason;
                        if (which ==0)
                        {
                            reason = options[0];
                            HashMap<Object, String> reported = new HashMap<>();
                            reported.put("postid", timestamp);
                            reported.put("Reported by", uemail);
                            reported.put("Title", title);
                            reported.put("Description", description);
                            reported.put("Reason", reason);


                            ref.child(uemail).setValue(reported).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Post reported. Thanks for letting us know. ", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                       if (which ==1)
                       {
                            reason = options[1];
                           HashMap<Object, String> reported = new HashMap<>();
                           reported.put("postid", timestamp);
                           reported.put("Reported by", uemail);
                           reported.put("Title", title);
                           reported.put("Description", description);
                           reported.put("Reason", reason);


                           ref.child(uemail).setValue(reported).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(context, "Post reported \n Post reported. Thanks for letting us know. ", Toast.LENGTH_LONG).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                       if (which ==2)
                       {
                            reason = options[2];
                           HashMap<Object, String> reported = new HashMap<>();
                           reported.put("postid", timestamp);
                           reported.put("Reported by", uemail);
                           reported.put("Title", title);
                           reported.put("Description", description);
                           reported.put("Reason", reason);


                           ref.child(uemail).setValue(reported).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(context, "Post reported. Thanks for letting us know. ", Toast.LENGTH_LONG).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                       if (which ==3)
                       {
                            reason = options[3];
                           HashMap<Object, String> reported = new HashMap<>();
                           reported.put("postid", timestamp);
                           reported.put("Reported by", uemail);
                           reported.put("Title", title);
                           reported.put("Description", description);
                           reported.put("Reason", reason);


                           ref.child(uemail).setValue(reported).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(context, "Post reported. Thanks for letting us know. ", Toast.LENGTH_LONG).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                       if (which ==4)
                       {
                            reason = options[4];
                           HashMap<Object, String> reported = new HashMap<>();
                           reported.put("postid", timestamp);
                           reported.put("Reported by", uemail);
                           reported.put("Title", title);
                           reported.put("Description", description);
                           reported.put("Reason", reason);


                           ref.child(uemail).setValue(reported).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(context, "Post reported. Thanks for letting us know. ", Toast.LENGTH_LONG).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                       if (which ==5)
                       {
                            reason = options[5];
                           HashMap<Object, String> reported = new HashMap<>();
                           reported.put("postid", timestamp);
                           reported.put("Reported by", uemail);
                           reported.put("Title", title);
                           reported.put("Description", description);
                           reported.put("Reason", reason);


                           ref.child(uemail).setValue(reported).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(context, "Post reported. Thanks for letting us know. ", Toast.LENGTH_LONG).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                       if (which ==6)
                       {
                           reason = options[6];
                           HashMap<Object, String> reported = new HashMap<>();
                           reported.put("postid", timestamp);
                           reported.put("Reported by", uemail);
                           reported.put("Title", title);
                           reported.put("Description", description);
                           reported.put("Reason", reason);


                           ref.child(uemail).setValue(reported).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(context, "Post reported. Thanks for letting us know. ", Toast.LENGTH_LONG).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                   }
               });
               builder.create().show();


           }

       });



        myholder.notvoted_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int voteplus = Integer.parseInt(postList.get(i).getVoteplus());
                mProcessplus=true;

                // id of the post clicked
                final String postid= postList.get(i).getTime_Stamp();

                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessplus)
                        {
                            if (dataSnapshot.child(postid).hasChild(uid))
                            {
                                postRef.child(postid).child("voteplus").setValue(""+(voteplus-1));
                                voteplusRef.child(postid).child(uid).removeValue();
                                mProcessplus=false;
                            }
                            else
                            {
                                postRef.child(postid).child("voteplus").setValue(""+(voteplus+1));
                                voteplusRef.child(postid).child(uid).setValue("voted");
                                mProcessplus=false;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        myholder.notvoted_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int voteminus = Integer.parseInt(postList.get(i).getVoteminus());
                mProcessminus=true;

                // id of the post clicked
                final String postid= postList.get(i).getTime_Stamp();
                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessminus)
                        {
                            if (dataSnapshot.child(postid).hasChild(uid))
                            {
                                postRef.child(postid).child("voteminus").setValue(""+(voteminus-1));
                                voteminusRef.child(postid).child(uid).removeValue();
                                mProcessminus=false;
                            }
                            else
                            {
                                postRef.child(postid).child("voteminus").setValue(""+(voteminus+1));
                                voteminusRef.child(postid).child(uid).setValue("voted");
                                mProcessminus=false;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        try
        {

            Picasso
                    .get()
                    .load(profilepic)
                    .fetch(new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso
                                    .get()
                                    .load(profilepic)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(myholder.profile_image);

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profilepic).into(myholder.profile_image);

                        }
                    });
        }
        catch (Exception e)
        {
            Picasso.get().load(profilepic).into(myholder.profile_image);
        }


        if (post_image.equals("No Image"))
        {
            myholder.post_image.setVisibility(View.GONE);
        }
        else
        {
            try
            {

                Picasso
                        .get()
                        .load(post_image)
                        .fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                Picasso
                                        .get()
                                        .load(post_image)
                                        .into(myholder.post_image);

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });


            }
            catch (Exception e)
            {
                Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
            }
        }


        try
        {
            if (role.equals("Journalist") && (verified.equals("Yes")))
            {
               myholder.badge.setImageResource(R.drawable.blue_tick);
            }
            else if (role.equals("Journalist") && (verified.equals("No")))
            {
                myholder.badge.setImageResource(R.drawable.j_badge);
            }
            else
            {
                myholder.badge.setImageResource(R.drawable.empty);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }


    private void setminusvotes(final Myholder myholder, final String timestamp) {
        voteminusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.child(timestamp).hasChild(uid))
                {

                    myholder.vote_status.setText(" VOTED ");
                    myholder.vote_status.setTextSize(9);
                    myholder.notvoted_plus.setEnabled(false);
                    myholder.notvoted_minus.setEnabled(false);
                    myholder.choice_back.setImageResource(R.drawable.ic_choice_background_done);

                    myholder.notvoted_plus.setImageResource(R.drawable.neg_ic_plussel);
                    myholder.notvoted_minus.setImageResource(R.drawable.ic_minussel);
                    myholder.vote_status.setTextColor(context.getResources().getColor(R.color.white));


                }
                else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setvoteplus(final Myholder holder, final String timestamp) {
        voteplusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(timestamp).hasChild(uid))

                {
                    holder.vote_status.setText(" VOTED ");
                    holder.vote_status.setTextSize(9);
                    holder.notvoted_minus.setEnabled(false);
                    holder.notvoted_plus.setEnabled(false);
                    holder.choice_back.setImageResource(R.drawable.ic_choice_background_done);
                    holder.notvoted_plus.setImageResource(R.drawable.ic_plussel);
                    holder.notvoted_minus.setImageResource(R.drawable.neg_ic_minus);
                    holder.vote_status.setTextColor(context.getResources().getColor(R.color.white));

                }
                else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class Myholder extends RecyclerView.ViewHolder{


        CircleImageView profile_image;
        ImageView badge, post_image, notvoted_plus, notvoted_minus, choice_back,options;
        TextView title, description, username, time, comment_post, vote_status,factness_percentage;



        public Myholder(@NonNull View itemView)
        {
            super(itemView);
            options = itemView.findViewById(R.id.options);
            factness_percentage = itemView.findViewById(R.id.factness_percentage);
            choice_back = itemView.findViewById(R.id.choice_back);
            vote_status = itemView.findViewById(R.id.vote_status);
            comment_post=itemView.findViewById(R.id.comment_post);
            notvoted_minus=itemView.findViewById(R.id.notvoted_minus);
            notvoted_plus = itemView.findViewById(R.id.notvoted_plus);
            profile_image = itemView.findViewById(R.id.post_profile_pic);
            badge = itemView.findViewById(R.id.post_badge);
            title = itemView.findViewById(R.id.post_title);
            description = itemView.findViewById(R.id.post_description);
            time = itemView.findViewById(R.id.post_time);
            username = itemView.findViewById(R.id.post_username);
            post_image = itemView.findViewById(R.id.post_image);



        }
    }

}
