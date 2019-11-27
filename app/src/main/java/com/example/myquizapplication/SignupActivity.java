package com.example.myquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity{
    private EditText email, password;
    private TextView login;
    private Button signUp;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

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
                            mDatabase.child(mAuth.getCurrentUser().getUid()).child("name").setValue("");
                            mDatabase.child(mAuth.getCurrentUser().getUid()).child("highscore").setValue(0);
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast toast = Toast.makeText(SignupActivity.this, "✓ - Sign in Success", Toast.LENGTH_LONG);
                            View view = toast.getView();

                            //To change the Background of Toast
                            view.setBackgroundColor(Color.GREEN);
                            TextView text = (TextView) view.findViewById(android.R.id.message);

                            //Shadow of the Of the Text Color
//                            text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
                            text.setTextColor(Color.WHITE);
                            toast.show();
                            //create new instance of intent and create flags that clear task and create new task so that the logged in user cannot go back to the login/register page
                            startActivity(new Intent(SignupActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Mia", "createUserWithEmail:failure", task.getException());
                            Toast toast = Toast.makeText(SignupActivity.this, "X - Authentication failed!", Toast.LENGTH_LONG);
                            View view = toast.getView();

                            //To change the Background of Toast
                            view.setBackgroundColor(Color.RED);
                            TextView text = (TextView) view.findViewById(android.R.id.message);

                            //Shadow of the Of the Text Color
//                            text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
                            text.setTextColor(Color.WHITE);
                            toast.show();
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

    public void loginPage(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
