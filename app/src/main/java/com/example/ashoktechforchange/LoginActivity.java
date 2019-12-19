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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView signup;
    EditText et_email, et_pass;
    Button login;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFirebase();
        checkSession();
        initViews();
    }

    private void initViews(){

        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        login = findViewById(R.id.button_login);
        signup = findViewById(R.id.tv_signup);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging In...");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(signupIntent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateUserDetails()){
                    return;
                }
                progressDialog.show();
                email = et_email.getText().toString().trim();
                pass = et_pass.getText().toString().trim();
                loginUser(email, pass);
            }
        });
    }

    private void initFirebase(){
        auth = FirebaseAuth.getInstance();
    }

    private void checkSession(){
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void loginUser(String email, String pass){
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {
                    // there was an error
                    Toast.makeText(LoginActivity.this, "Error logging in", Toast.LENGTH_LONG).show();

                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
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
        else if (et_pass.getText().toString().trim().equals("")){
            Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
