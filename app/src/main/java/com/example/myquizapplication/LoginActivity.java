package com.example.myquizapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity{
    private EditText email, password;
    private TextView signup;
    private Button login;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        login = findViewById(R.id.btnLogin);
        signup = findViewById(R.id.signUp);
        progressBar = findViewById(R.id.progressBar);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String email_value = email.getText().toString().trim();
                String password_value = password.getText().toString().trim();

                //Checks if email is empty and/or it is really an email address
                if(email_value.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email_value).matches()){
                    email.setError("Valid Email is required");
                    email.requestFocus();
                }

                if(password_value.isEmpty() || password_value.length() < 6){
                    password.setError("It should be minimum of 6 characters");
                    password.requestFocus();
                }

                loginUser(email_value, password_value);

            }
        });

    }

    private void loginUser(String email_value,String password_value){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email_value, password_value)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Mia", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Mia", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    public void signUp(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
