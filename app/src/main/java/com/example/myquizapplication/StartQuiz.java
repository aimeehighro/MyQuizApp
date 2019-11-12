package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class StartQuiz extends AppCompatActivity{

    private TextView timer;
    private static final long COUNTDOWN = 20000;
    private CountDownTimer countDownTimer;

    private ColorStateList textColorCd;

    private long cdLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);
        timer = findViewById(R.id.timer);
        textColorCd = timer.getTextColors();

        cdLeft = COUNTDOWN;
        startCountDown();
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
            }
        }.start();
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
