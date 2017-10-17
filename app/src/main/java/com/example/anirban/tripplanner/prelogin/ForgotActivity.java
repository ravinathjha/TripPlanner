package com.example.anirban.tripplanner.prelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anirban.tripplanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    private EditText mEmail;
    private Button mForgetButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mEmail = (EditText) findViewById(R.id.forgetEmail);
        mForgetButton = (Button) findViewById(R.id.forgetButton);
        mForgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotActivity.this, "Please enter the email id", Toast.LENGTH_SHORT).show();
                }
                mProgress.setMessage("Reset link sending to your mail");
                mProgress.show();
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotActivity.this, "We have send the reset link to your email id"
                                            , Toast.LENGTH_SHORT).show();
                                    Intent forgetPasswordIntent = new Intent(ForgotActivity.this, LogInActivity.class);
                                    startActivity(forgetPasswordIntent);
                                } else {
                                    Toast.makeText(ForgotActivity.this, "Failed to send. Please re-enter",
                                            Toast.LENGTH_SHORT).show();
                                }
                                mProgress.dismiss();
                            }
                        });
            }
        });
    }

    protected void onResume() {
        super.onResume();
    }
}
