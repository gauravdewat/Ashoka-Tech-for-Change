package com.example.ashoktechforchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.VideoView;

import com.example.ashoktechforchange.Models.Complaint;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DescriptionActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView name;
    TextView status;
    TextView department;
    TextView date;
    TextView address;
    TextView like;
    VideoView videoView;
    TextView officeName;
    TextView desc;
    TextView duration;
    TextView officerComment;
    TextView playVideo;

    ImageView iv_like;
    ImageView iv_comment;
    ImageView iv_share;

    String videoURL;

    Boolean isLiked = false;

    String compID;
    String uID;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    DatabaseReference compDatabase, userDatabase;

    Complaint complaintData;

    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        getCompID();
        initFirebase();
        initViews();
        getCompInfo();
    }

    private void getCompID(){
        Intent intent = getIntent();

        if (intent!=null && intent.getData()!=null
                && (intent.getData().getScheme().equals("http"))){
            Log.d("********************","deep link opened");
            Uri data = intent.getData();
            List<String> pathSegments = data.getPathSegments();
            if(pathSegments.size()>0)
                compID=pathSegments.get(1); // This will give you prefix as path
            Log.d("********************","compID = " + compID);
        }
        else {
            compID = intent.getStringExtra("compID");
        }

    }

    private void initFirebase(){
        auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(DescriptionActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void initViews(){

        toolbar = findViewById(R.id.toolbar_da);
        duration = findViewById(R.id.da_duration);
        playVideo = findViewById(R.id.showVideo);
        name = findViewById(R.id.da_name);
        status = findViewById(R.id.da_status);
        department = findViewById(R.id.da_dept);
        date = findViewById(R.id.da_date);
        address = findViewById(R.id.da_addr);
        like = findViewById(R.id.da_like_count);
        desc = findViewById(R.id.da_desc);
        officeName = findViewById(R.id.da_officer);
        iv_like = findViewById(R.id.da_iv_like);
        iv_share = findViewById(R.id.share_da);
        officerComment = findViewById(R.id.da_officer_comment);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DescriptionActivity.this, VideoPlayerActivity.class);
                i.putExtra("videoURL", videoURL);
                startActivity(i);
                //videoView.start();
            }
        });

        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLiked){
                    unLikePost();
                    isLiked = false;
                    iv_like.setImageDrawable(ContextCompat.getDrawable(DescriptionActivity.this,R.drawable.ic_upvote_grey_24dp));
                }
                else {
                    likePost();
                    isLiked = true;
                    iv_like.setImageDrawable(ContextCompat.getDrawable(DescriptionActivity.this,R.drawable.ic_upvote_green_24dp));
                }
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareComp();
            }
        });

    }

    private void setData(){
        duration.setText(getDuration(complaintData.getDate()));
        name.setText(complaintData.getName());
        setStatus(complaintData.getStatus(),this);
        department.setText(complaintData.getDepartment());
        date.setText(complaintData.getDate() + "|" + complaintData.getTime());
        address.setText(complaintData.getLocation());
        like.setText(String.valueOf(complaintData.getLikes()));
        officeName.setText(complaintData.getOfficerName());
        desc.setText(complaintData.getDescription());
        officerComment.setText(complaintData.getOfficerComment());
        videoURL = complaintData.getVideoUrl();
        //setVideo();

        if (isLiked){
            iv_like.setImageDrawable(ContextCompat.getDrawable(DescriptionActivity.this,R.drawable.ic_thumb_up_green_24dp));
        }
        else {
            iv_like.setImageDrawable(ContextCompat.getDrawable(DescriptionActivity.this,R.drawable.ic_thumb_up_grey_24dp));
        }


    }


    private void getCompInfo(){
        compDatabase = FirebaseDatabase.getInstance().getReference().child("complaints");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("user");

        compDatabase.child(compID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 complaintData = new Complaint(String.valueOf(snapshot.child("officerName").getValue()),
                         String.valueOf(snapshot.child("officerComment").getValue()),
                         String.valueOf(snapshot.child("name").getValue()),uID,
                         String.valueOf(snapshot.child("department").getValue()),
                         String.valueOf(snapshot.child("description").getValue()),
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
                         String.valueOf(snapshot.child("isFirstComplaint").getValue()));
                 if (snapshot.child("Like").hasChild(uID)){
                     isLiked = true;
                 }
                 else {
                     isLiked = false;
                 }
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setStatus(int statusNo, Context context) {
        String taskStatus = "NA";
        int background = R.drawable.background_status_1 ;
        int color = Color.parseColor("#E60000");
        switch (statusNo){
            case 1 : taskStatus = "PENDING";
                background = R.drawable.background_status_1;
                color = Color.parseColor("#E60000");
                break;
            case 2 : taskStatus = "IN PROCESS";
                background = R.drawable.background_status_2;
                color = Color.parseColor("#FF7800");
                break;
            case 3 : taskStatus = "RESOLVED";
                background = R.drawable.background_status_3;
                color = Color.parseColor("#4CBB17");
                break;

            default : taskStatus = "NA"; break;
        }
        status.setText(taskStatus);
        status.setBackground(ContextCompat.getDrawable(context, background));
        status.setTextColor(color);
    }

    public void setVideo(String videoUrl){
        Uri uri = Uri.parse(videoUrl);
        videoView.setVideoURI(uri);
        videoView.requestFocus();

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

        private void likePost(){
            compDatabase.child(compID).child("Like").child(uID).setValue("true");

        }

        private void unLikePost(){
            compDatabase.child(compID).child("Like").child(uID).removeValue();
        }

    private void shareComp() {
        //Hey ! Your friend is facing a civic issue in his locality. He has registered a complaint regarding the same. Click here .... to view the complaint.
        String referralMessage = "Hey ! Your friend is facing a civic issue in his locality. He has registered a complaint regarding the same. Click here ";
        String link = "http://www.sahooliyat.com/share/"+compID;
        String inviteMessage = referralMessage + " " + link+ " to view the complaint and see updates.";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, inviteMessage);
        Intent receiver = new Intent(android.content.Intent.ACTION_SEND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivity(Intent.createChooser(shareIntent, "SHARE with:",pendingIntent.getIntentSender()));
        }else{
            startActivity(Intent.createChooser(shareIntent, "SHARE with:"));

        }
    }

}
