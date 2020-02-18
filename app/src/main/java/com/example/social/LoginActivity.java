package com.example.social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt;
    TextView notHaveAccountTv, mRecoverPassTv;
    Button mLoginButton;

    private FirebaseAuth mAuth;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        notHaveAccountTv = findViewById(R.id.nothave_accountTv);
        mRecoverPassTv = findViewById(R.id.recoverPassTv);
        mLoginButton = findViewById(R.id.loginBtn);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEt.getText().toString();
                String passw = mPasswordEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                }
                else {
                    loginUser(email, passw);
                }
            }
        });

        notHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        mRecoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        pd = new ProgressDialog(this);


    }

    private void showRecoverPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);

        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String email) {

        pd.setMessage("Sending email...");
        pd.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loginUser(String email, String passw) {

        pd.setMessage("Logging In...");
        pd.show();
        mAuth.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();

        return super.onSupportNavigateUp();
    }
}
