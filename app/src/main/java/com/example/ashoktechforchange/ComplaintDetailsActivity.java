package com.example.ashoktechforchange;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ashoktechforchange.Models.Complaint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ComplaintDetailsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private StorageReference videoRef;

    Button register;
    EditText description, location;

    String userName = "NA";
    String address;
    String uid;
    String compID;
    Uri videoUri;
    String videoUrl;
    String date;
    String currentTime;
    ProgressDialog progressDialog;
    private static int VIDEO_REQUEST = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_details);

        initFirebase();
        initDatabase();
        initStorage();
        initViews();
        getUserInfo();
        openRecorder();
        setDate();
        setTime();
    }

    private void initFirebase(){
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
    }

    private void initDatabase(){
        mDatabase = FirebaseDatabase.getInstance().getReference("complaints");
    }

    private void initStorage(){
        videoRef = FirebaseStorage.getInstance().getReference().child("videos");
    }

    private void getUserInfo(){
        final DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users");

        userDatabase.child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               userName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userName = "ERROR";
            }
        });
    }

    private void initViews(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading video...");
        progressDialog.setCanceledOnTouchOutside(false);
        register = findViewById(R.id.registercomplaint);
        location = findViewById(R.id.et_place);
        description = findViewById(R.id.et_description);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInfo()) return;
                progressDialog.show();
                uploadVideo();
                Toast.makeText(ComplaintDetailsActivity.this,"Register Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadVideo(){
        if(videoUri != null){
            final StorageReference storageReference = videoRef.child(uid + videoUri.getLastPathSegment());
            UploadTask  uploadTask =  storageReference.putFile(videoUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ComplaintDetailsActivity.this,"Failure", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ComplaintDetailsActivity.this, e.toString(),Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    progressDialog.setTitle("This might take a while...");
                    progressDialog.show();
                    Toast.makeText(ComplaintDetailsActivity.this,"Upload finished", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            videoUrl = uri.toString();
                            registerComplaint();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    updateProgress(taskSnapshot);
                }
            });
        }
        else {
            Toast.makeText(this,"Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }
    private void registerComplaint(){
        //add child("uid")
        DatabaseReference ref = mDatabase.push();
        compID = ref.getKey();
        Complaint complaint = new Complaint("NA","No comments",userName,uid,"UNASSIGNED",description.getText().toString().trim(),
                location.getText().toString().trim(), 1,date,videoUrl,"latlong","unassigned",0,compID,currentTime,"tag","yes");

        ref.setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {
                    Toast.makeText(ComplaintDetailsActivity.this, task.getException().toString(),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ComplaintDetailsActivity.this, "Your complaint has been registered Successfully",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void updateProgress(UploadTask.TaskSnapshot taskSnapshot){
        long fileSize = taskSnapshot.getTotalByteCount();

        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress  = (100 * uploadBytes) / fileSize;

        progressDialog.setProgress((int)progress);
    }

    private void setDate(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        date = df.format(c);
    }

    private void setTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
// you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

        currentTime = date.format(currentLocalTime);
    }
    private void openRecorder(){
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(videoIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(videoIntent, VIDEO_REQUEST);
        }
    }

    private boolean validateInfo(){
        if (videoUri == null){
            Toast.makeText(this,"Please go back and make video again",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(location.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Address",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(description.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Complaint Description",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == VIDEO_REQUEST && resultCode == RESULT_OK){
            videoUri = data.getData();
        }
    }

}
