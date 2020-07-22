package com.betalabs.factcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class invite extends AppCompatActivity {
    EditText invitecode;
    FirebaseFirestore db;
    ProgressBar invite_prog;


    @Override
    public void onBackPressed(){
        Intent login = new Intent(this, login.class);
        startActivity(login);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        getSupportActionBar().hide();

        invitecode = findViewById(R.id.invitecode);
        invite_prog = findViewById(R.id.invite_prog);
        invite_prog.setVisibility(View.INVISIBLE);


        invitecode.getText().toString();

        invitecode.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        db = FirebaseFirestore.getInstance();


        invitecode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {


                if ((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER))
                {
                    invite_prog.setVisibility(View.VISIBLE);
                    try
                    {
                        CollectionReference yourCollRef = db.collection("Journalist");
                        Query query = yourCollRef.whereEqualTo("Invite Code", invitecode.getText().toString());
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    for (DocumentSnapshot document : task.getResult())
                                    {
                                        Intent login = new Intent(invite.this, jou_login.class);
                                        String invited_by = document.getId();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("invited_by", invited_by);
                                        login.putExtras(bundle);
                                        startActivity(login);
                                        invite_prog.setVisibility(View.INVISIBLE);

                                    }
                                }
                                else
                                    {
                                        Toast.makeText(invite.this, "Not Present", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    catch (Exception e)
                    {
                        Toast.makeText(invite.this, "Error:" +e, Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            }
        });






    }
}
