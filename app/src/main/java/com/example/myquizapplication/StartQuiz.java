package com.example.myquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class StartQuiz extends AppCompatActivity{

    private TextView timer, question;
    private RadioGroup options;
    private RadioButton option1, option2, option3, option4;
    private Button btnSubmit;
    private static final long COUNTDOWN = 20000;
    private CountDownTimer countDownTimer;
    private DatabaseReference mDatabase;
    private boolean answered;
    private String answer;

    private ColorStateList textColorRb;
    private ColorStateList textColorCd;
    private long cdLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);
        timer = findViewById(R.id.timer);

        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        question = findViewById(R.id.question);
        options = findViewById(R.id.options);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        btnSubmit = findViewById(R.id.btnSend);

        //Get the default colors;
        textColorCd = timer.getTextColors();
        textColorRb = option1.getTextColors();

        getData();


    }

    public void onSend(View view){
        //Checks if the user already picked an answer or not
        if(!answered){
            if(option1.isChecked() || option2.isChecked() || option3.isChecked() || option4.isChecked()){
                checkAnswer();
            }else{
                Toast.makeText(this, "Please select an answer!", Toast.LENGTH_LONG).show();
            }
        }else{
            checkAnswer();
            getData();
        }

    }

    private void getData(){
        //start the timer
        cdLeft = COUNTDOWN;
        startCountDown();

        //reset to default
        btnSubmit.setText("Submit");
        option1.setTextColor(textColorRb);
        option2.setTextColor(textColorRb);
        option3.setTextColor(textColorRb);
        option4.setTextColor(textColorRb);
        options.clearCheck();
        answered = false;

        //Get a random data in the database (firebase)
        mDatabase.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String key = ds.getKey();
//                    Log.d("Mia", key);
//                    Log.d("Mia", ds.toString());
//                    Log.d("Mia", Long.toString(ds.getChildrenCount()));
//                    long childrenCount = ds.getChildrenCount();
//                    Log.d("Mia", Long.toString(childrenCount));
//                    int count = (int) childrenCount;
//                    Log.d("Mia", Integer.toString(count));
                    int randomNumber = getRandomNumber();
//                    Log.d("Mia", Integer.toString(randomNumber));
                    int i = Integer.parseInt(key);
//                    Log.d("Mia", Integer.toString(i));
                    if( randomNumber == i){
                        String ask = ds.child("question").getValue().toString();
                        String correctAnswer = ds.child("answer").getValue().toString();
                        String optionOne = ds.child("option1").getValue().toString();
                        String optionTwo = ds.child("option2").getValue().toString();
                        String optionThree = ds.child("option3").getValue().toString();
                        String optionFour = ds.child("option4").getValue().toString();

                        answer = correctAnswer;
                        question.setText(ask);
                        option1.setText(optionOne);
                        option2.setText(optionTwo);
                        option3.setText(optionThree);
                        option4.setText(optionFour);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });
    }

    private int getRandomNumber(){
        return new Random().nextInt(100) + 1;
    }


    private void startCountDown(){
        countDownTimer = new CountDownTimer(cdLeft, 1000){
            @Override
            public void onTick(long l){
                cdLeft = l;
                updateCdText();
            }

            @Override
            public void onFinish(){
                cdLeft = 0;
                updateCdText();
                checkAnswer();
            }
        }.start();
    }

    private void checkAnswer(){
        answered = true;
        countDownTimer.cancel();
        String getAnswer = "";
        //Checks if there is a selected radio button and grabs the selected radio button in a group
        if(option1.isChecked() || option2.isChecked() || option3.isChecked() || option4.isChecked()){
            RadioButton selectedRb = findViewById(options.getCheckedRadioButtonId());
            getAnswer  = selectedRb.getText().toString();
        }
//        Log.d("Mia", getAnswer);
        if(getAnswer == answer){

        }

        showSolution();
    }

    private void showSolution(){
        option1.setTextColor(Color.RED);
        option2.setTextColor(Color.RED);
        option3.setTextColor(Color.RED);
        option4.setTextColor(Color.RED);

//        Log.d("Mia", option1.getText().toString());
//        Log.d("Mia", answer);
        if(answer.equals(option1.getText().toString())){
            option1.setTextColor(Color.GREEN);
        }else if(answer.equals(option2.getText().toString())){
            option2.setTextColor(Color.GREEN);
        }else if(answer.equals(option3.getText().toString())){
            option3.setTextColor(Color.GREEN);
        }else if(answer.equals(option4.getText().toString())){
            option4.setTextColor(Color.GREEN);
        }

        btnSubmit.setText("Next Question");
    }

    private void updateCdText(){
        int minutes = (int) (cdLeft / 1000) / 60;
        int seconds = (int) (cdLeft / 1000) % 60;

//        Log.d("Mia", Integer.toString(minutes));
//        Log.d("Mia", Integer.toString(seconds));
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timer.setText(timeFormatted);
//        Log.d("Mia", timer.getText().toString());

        if(cdLeft < 10000){
            timer.setTextColor(Color.RED);
        }else{
            timer.setTextColor(textColorCd);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}
