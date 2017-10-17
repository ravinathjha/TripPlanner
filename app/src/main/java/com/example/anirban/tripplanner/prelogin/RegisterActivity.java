package com.example.anirban.tripplanner.prelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anirban.tripplanner.R;
import com.example.anirban.tripplanner.home.HomeMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextView mLinkToLogIn;
    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mSignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress = new ProgressDialog(this);

        mName = (EditText) findViewById(R.id.signUpName);
        mEmail = (EditText) findViewById(R.id.signUpEmail);
        mPassword = (EditText) findViewById(R.id.signUpPassword);
        mSignUp = (Button) findViewById(R.id.signUpButton);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });


        mLinkToLogIn = (TextView) findViewById(R.id.linklogIn);
        mLinkToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent linkToLogInIntent = new Intent(RegisterActivity.this, LogInActivity.class);
                startActivity(linkToLogInIntent);
            }
        });

    }

    private void startRegister() {
        final String name = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Enter the name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Enter the email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Enter the password", Toast.LENGTH_SHORT).show();
        }
        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Enter minimum 6 letters password", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Enter the name, email and password", Toast.LENGTH_SHORT).show();

        }
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) && password.length() >= 6) {
            mProgress.setMessage("Signing up....");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentDatabase = mDatabase.child(userId);
                        currentDatabase.child("name").setValue(name);

                        mProgress.dismiss();

                        Intent mainIntent = new Intent(RegisterActivity.this, HomeMapActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "Please enter the Name, Email and Password", Toast.LENGTH_SHORT).show();
        }

    }

    protected void onResume() {
        super.onResume();
    }
}
