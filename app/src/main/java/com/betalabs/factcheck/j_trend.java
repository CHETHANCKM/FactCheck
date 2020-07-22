package com.betalabs.factcheck;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.TextUtils;


import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.adapter_post;


/**
 * A simple {@link Fragment} subclass.
 */
public class j_trend extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<new_post> postList;
    adapter_post adapter_post;
    SwipeRefreshLayout refresh_swipe;
    SearchView search_view;

    FirebaseDatabase firebaseDatabase;

    public j_trend() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_j_trend, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = v.findViewById(R.id.recycleview);
        refresh_swipe = v.findViewById(R.id.refresh_swipe);
//        search_view = v.findViewById(R.id.search_view);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.stopScroll();



        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        postList = new ArrayList<>();
        loadpost();


        refresh_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myUpdateOperation();
                    }
                },1100);

            }
        });

        return  v;
    }


    private void enablePersistence()
    {

        try {
            firebaseDatabase.getInstance().setPersistenceEnabled(true);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
             reference.keepSynced(true);
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
        }


    }

    private void myUpdateOperation() {
        refresh_swipe.setRefreshing(false);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {

                    new_post new_post = ds.getValue(com.betalabs.factcheck.new_post.class);
                    postList.add(new_post);
                    adapter_post = new adapter_post(getActivity(), postList);
                    recyclerView.setAdapter(adapter_post);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        reference.child("Posts").keepSynced(false);
    }


    private void loadpost()
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    new_post new_post = ds.getValue(com.betalabs.factcheck.new_post.class);
                    postList.add(new_post);
                    adapter_post = new adapter_post(getActivity(), postList);
                    recyclerView.setAdapter(adapter_post);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
       reference.child("Posts").keepSynced(true);

    }



}
