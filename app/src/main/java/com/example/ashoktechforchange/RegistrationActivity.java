package com.example.ashoktechforchange;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ashoktechforchange.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    EditText et_email, et_password,et_confirm_password, et_name, et_mobile_number, et_addr, et_pin;
    Button register;
    private ProgressDialog progressDialog;
    String email, pass;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        initFirebase();
        initDatabase();
    }

    private void initViews() {
        et_email = findViewById(R.id.user_email);
        et_password = findViewById(R.id.user_pass);
        et_confirm_password = findViewById(R.id.user_pass_confirm);
        et_name = findViewById(R.id.user_name);
        et_mobile_number = findViewById(R.id.user_mobile);
        et_addr = findViewById(R.id.user_address);
        et_pin = findViewById(R.id.user_pincode);
        register = findViewById(R.id.button_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUserDetails()){
                    return;
                }
                progressDialog.show();
                email = et_email.getText().toString();
                pass = et_password.getText().toString();
                registerUser(email, pass);
            }
        });

    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
    }
    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }


    private void registerUser(String email, String Pass){
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if (!task.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.setTitle("This might take a while...");
                    progressDialog.show();
                    initFirebase();
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();
                    uploadUserInfo(uid);
                }
            }
        });
    }

    private void uploadUserInfo(String uid){
        String name = et_name.getText().toString().trim();
        String mobile = et_mobile_number.getText().toString().trim();
        String addr = et_addr.getText().toString().trim();
        String pin = et_pin.getText().toString().trim();
        User user = new User(name, mobile,pin,addr,"no");

        mDatabase.child(uid).setValue(user).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, task.getException().toString(),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(RegistrationActivity.this, "Registered Successfully",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    private boolean validateUserDetails(){

        if(et_email.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter email",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (et_name.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Name",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (et_mobile_number.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Mobile No.",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (et_password.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (et_confirm_password.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Password Confirmation",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (et_addr.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Address",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (et_pin.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Pincode",Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (!et_confirm_password.getText().toString().trim().equals(et_password.getText().toString().trim())){
            Toast.makeText(this,"Password does not match",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
