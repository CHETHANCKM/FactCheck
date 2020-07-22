package com.betalabs.factcheck;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.adapter_noti;


/**
 * A simple {@link Fragment} subclass.
 */
public class noti extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;

    List<new_notification> new_notificationList;
    adapter_noti adapter_noti;
    SwipeRefreshLayout refresh_swipe;


    public noti() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_noti, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = v.findViewById(R.id.notiview);
        refresh_swipe = v.findViewById(R.id.refresh_swipe);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        loadnotifictaion();



        refresh_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadnotifictaion();
                    }
                },1100);

            }
        });

        return v;


    }

    private void loadnotifictaion() {

        new_notificationList = new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Notifications");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new_notificationList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    new_notification new_notification = ds.getValue(new_notification.class);

                    new_notificationList.add(new_notification);


                    adapter_noti = new adapter_noti(getActivity(), new_notificationList);
                    recyclerView.setAdapter(adapter_noti);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Notifications").keepSynced(true);
        refresh_swipe.setRefreshing(false);
    }
    }

