package com.example.socialapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password, name;
    private Button mlogin;
    private TextView newdnewaccount, recoverpass;
    FirebaseUser currentUser;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // initialising the layout items
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        newdnewaccount = findViewById(R.id.needs_new_account);
        recoverpass = findViewById(R.id.forgetp);
        mAuth = FirebaseAuth.getInstance();
        mlogin = findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        // checking if user is null or not
        if (mAuth != null){
            currentUser = mAuth.getCurrentUser();
        }

        mlogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String emaill = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                // if format of email doesn't match return null
                if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()){
                    email.setError("Invalid Email");
                    email.setFocusable(true);
                } else {
                    loginUser(emaill, pass);
                }
            }
        });

        // if new account then move to Registration Activity
        newdnewaccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

    }
}
