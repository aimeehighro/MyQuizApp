package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity{
    private TextView signOut;
    private FirebaseAuth mAuth;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnStart = findViewById(R.id.btnStart);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        signOut = findViewById(R.id.signOut);

        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startQuiz();
            }
        });

    }

    public void signOut(View view){
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void startQuiz(){
        Intent intent = new Intent(this, StartQuiz.class);
        startActivity(intent);
    }
}
