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

import es.dmoral.toasty.Toasty;

public class StartQuiz extends AppCompatActivity{

    private TextView timer, question, points, score, numberQuestion;
    private RadioGroup options;
    private RadioButton option1, option2, option3, option4;
    private Button btnSubmit, btnNext;
    private static final long COUNTDOWN = 20000;
    private CountDownTimer countDownTimer;
    private DatabaseReference mDatabase, mDatabase1;
    private boolean answered;
    private String answer;
    private final int MAX_QUESTION = 10;
    //counter to check the number of questions
    private int countQuestion = 0;
    private int getPoints = 0;
    private int highscore = 0;


    private ColorStateList textColorRb;
    private ColorStateList textColorCd;
    private long cdLeft;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);
        timer = findViewById(R.id.timer);

        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabase1 = FirebaseDatabase.getInstance().getReference("users");
        question = findViewById(R.id.question);
        options = findViewById(R.id.options);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        btnSubmit = findViewById(R.id.btnSend);
        points = findViewById(R.id.pointsValue);
        score = findViewById(R.id.scoreValue);
        numberQuestion = findViewById(R.id.questionsValue);
        btnNext = findViewById(R.id.btnNext);

        //Get the default colors;
        textColorCd = timer.getTextColors();
        textColorRb = option1.getTextColors();

        try{
            int number = getRandomNumber();
            getData(number);
        }catch(Exception e){
            Toasty.error(this, e.getMessage(), Toasty.LENGTH_SHORT).show();
//                int getNumber = getRandomNumber();
//                getData(getNumber);
        }


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        highscore = intent.getIntExtra("highscore", 0);


    }

    public void onSend(View view){
        //Checks if the user already picked an answer or not
        if(!answered){
            if(option1.isChecked() || option2.isChecked() || option3.isChecked() || option4.isChecked()){
                checkAnswer();
            }else{
                Toasty.error(this, "Please select an answer!", Toast.LENGTH_SHORT).show();
            }
        }else{
            checkAnswer();
        }

    }

    public void onNext(View view){
        int number = getRandomNumber();
        getData(number);
    }

    private void getData(final int number){
        //start the timer
        cdLeft = COUNTDOWN;
        startCountDown();



        //reset to default
//        btnSubmit.setText("Submit");
        btnNext.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        option1.setTextColor(textColorRb);
        option2.setTextColor(textColorRb);
        option3.setTextColor(textColorRb);
        option4.setTextColor(textColorRb);

        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);
        answered = false;

        if(countQuestion != MAX_QUESTION){
            try{
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
                            int randomNumber = number;
//                    Log.d("Mia", Integer.toString(randomNumber));
                            int i = Integer.parseInt(key);
//                    Log.d("Mia", Integer.toString(i));
                            if( randomNumber == i){
                                countQuestion++;
                                Log.d("Mia",Integer.toString(countQuestion));
                                numberQuestion.setText(Integer.toString(countQuestion));
                                String ask = ds.child("question").getValue().toString();
                                String correctAnswer = ds.child("answer").getValue().toString();
                                String optionOne = ds.child("option1").getValue().toString();
                                String optionTwo = ds.child("option2").getValue().toString();
                                String optionThree = ds.child("option3").getValue().toString();
                                String optionFour = ds.child("option4").getValue().toString();
                                String value = ds.child("points").getValue().toString();

                                answer = correctAnswer;
                                question.setText(ask);
                                option1.setText(optionOne);
                                option2.setText(optionTwo);
                                option3.setText(optionThree);
                                option4.setText(optionFour);
                                points.setText(value);
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){

                    }
                });
            }catch(Exception e){
                Toasty.error(this, e.getMessage(), Toasty.LENGTH_SHORT).show();
//                int getNumber = getRandomNumber();
//                getData(getNumber);
            }

        }else{
            finishQuiz();
        }

    }

    private void finishQuiz(){
        Toasty.info(this, "You finish the Quiz. Thank you for playing!", Toast.LENGTH_LONG).show();
        if( Integer.parseInt(score.getText().toString()) > highscore){
            mDatabase1.addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                    final int NEW_HIGH_SCORE = Integer.parseInt(score.getText().toString());
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().child("highscore").setValue(NEW_HIGH_SCORE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError){

                }
            });

        }
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
        //Checks if there is a selected radio button and grabs the selected radio button in a group
        if(option1.isChecked() || option2.isChecked() || option3.isChecked() || option4.isChecked()){
            RadioButton selectedRb = findViewById(options.getCheckedRadioButtonId());
            getAnswer  = selectedRb.getText().toString();
        }

//        Log.d("Mia", getAnswer);
//        Log.d("Mia", answer);
        if(getAnswer.equals(answer)){
            getPoints += Integer.parseInt(points.getText().toString());
            Toasty.success(this, "You got it right", Toast.LENGTH_SHORT).show();
//            getPoints = +Integer.parseInt(points.getText().toString());
//            Log.d("Mia", Integer.toString(getPoints));
            score.setText(Integer.toString(getPoints));
        }else{
            Toasty.error(this, "Better luck next time!", Toast.LENGTH_SHORT).show();
        }

        showAnswer();
    }

    private void showAnswer(){
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

        if(countQuestion == MAX_QUESTION){
            btnNext.setText("Exit Game");
            finishQuiz();
        }else{
            options.clearCheck();
//            btnSubmit.setText("Next Question");
            btnSubmit.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        }
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
