package com.example.ashoktechforchange;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ashoktechforchange.Holder.ViewHolder;
import com.example.ashoktechforchange.Models.Complaint;
import com.example.ashoktechforchange.Models.ComplaintNew;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyComplaintAcitvity extends AppCompatActivity {

    Toolbar toolbar;

    String uid;
    FirebaseAuth auth;
    DatabaseReference compDatabase;
    FirebaseAuth.AuthStateListener authListener;

    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);

        initFirebase();
        initViews();
        initRecyclerView();

    }


    private void initViews(){

        toolbar = findViewById(R.id.toolbar_my_activity);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.rv_my_activity);

        compDatabase = FirebaseDatabase.getInstance().getReference().child("complaints");

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
    }

    private void initFirebase(){
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MyComplaintAcitvity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("complaints")
                .orderByChild("uid").equalTo(uid);

        FirebaseRecyclerOptions<ComplaintNew> options =
                new FirebaseRecyclerOptions.Builder<ComplaintNew>()
                        .setQuery(query, new SnapshotParser<ComplaintNew>() {
                            @NonNull
                            @Override
                            public ComplaintNew parseSnapshot(@NonNull DataSnapshot snapshot) {

                                String isLiked ;
                                if (snapshot.child("Like").hasChild(uid)){
                                    isLiked = "yes";
                                }
                                else {
                                    isLiked = "no";
                                }
                                return new ComplaintNew(String.valueOf(snapshot.child("officerName").getValue()),
                                        String.valueOf(snapshot.child("officerComment").getValue()),
                                        String.valueOf(snapshot.child("name").getValue()),uid,
                                        String.valueOf(snapshot.child("department").getValue()),
                                        String.valueOf(snapshot.child("desc").getValue()),
                                        String.valueOf(snapshot.child("location").getValue()),
                                        Integer.parseInt(String.valueOf(snapshot.child("status").getValue())),
                                        String.valueOf(snapshot.child("date").getValue()),
                                        String.valueOf(snapshot.child("videoUrl").getValue()),
                                        String.valueOf(snapshot.child("latLong").getValue()),
                                        String.valueOf(snapshot.child("officer").getValue()),
                                        Integer.parseInt( String.valueOf(snapshot.child("Like").getChildrenCount())),
                                        String.valueOf(snapshot.child("compID").getValue()),
                                        String.valueOf(snapshot.child("time").getValue()),
                                        String.valueOf(snapshot.child("tag").getValue()),
                                        String.valueOf(snapshot.child("isFirstComplaint").getValue()),
                                        isLiked);
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<ComplaintNew, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_feeds, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, final ComplaintNew complaint) {
                holder.setName(complaint.getName());
                holder.setAddress(complaint.getLocation());
                holder.setDate(complaint.getDate() + " | " + complaint.getTime());
                holder.setLike(complaint.getLikes());
                holder.setDepartment(complaint.getDepartment());
                holder.setStatus(complaint.getStatus(), getApplicationContext());
                holder.setDuration(getDuration(complaint.getDate()));
                holder.setLikeButton(complaint.getLiked(),MyComplaintAcitvity.this);
//                holder.setVideoView(complaint.getVideoUrl());
                //holder.videoView.requestFocus();

                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String compID = complaint.getCompID();
                        if(complaint.getLiked().equals("yes")){
                            unLikePost(compID);
                        }
                        else {
                            likePost(compID);
                        }
                    }
                });


                holder.readMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //holder.videoView.start();
                        Intent i = new Intent(MyComplaintAcitvity.this, DescriptionActivity.class);
                        i.putExtra("compID", complaint.getCompID());
                        startActivity(i);
                    }
                });
            }

        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private String getDuration(String date){
        Date postDate = stringToDate(date, "dd MMM yyyy");
        Date currentDate = stringToDate(getCurrentDate(), "dd MMM yyyy");
        int duration = calculateDays(postDate, currentDate);
        String time ;
        if(duration == 0){
            time = "Today";
        }
        else if (duration == 1){
            time = "Yesterday";
        }
        else {
            time = String.valueOf(duration) + " days ago";
        }
        return time;
    }

    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }
    private String getCurrentDate(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        String currentDate = df.format(c);
        return currentDate;
    }

    private int calculateDays(Date startDateValue, Date endDateValue){
        long diff = endDateValue.getTime() - startDateValue.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = (hours / 24) + 1;

        return (int)days - 1;
    }

    private void likePost(String compID){
        compDatabase.child(compID).child("Like").child(uid).setValue("true");

    }

    private void unLikePost(String compID){
        compDatabase.child(compID).child("Like").child(uid).removeValue();
    }
}