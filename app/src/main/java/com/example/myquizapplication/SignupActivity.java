package com.example.myquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class SignupActivity extends AppCompatActivity{
    private EditText email, password;
    private TextView login;
    private Button signUp;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);

        signUp.setOnClickListener(new View.OnClickListener(){
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
                
                registerUser(email_value, password_value);
            }
        });
    }

    private void registerUser(String email_value,String password_value){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email_value, password_value)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Mia", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //create new instance of intent and create flags that clear task and create new task so that the logged in user cannot go back to the login/register page
                            startActivity(new Intent(SignupActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Mia", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(SignupActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    public void loginPage(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
