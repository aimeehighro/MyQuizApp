package com.example.myquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity{
    private TextView signOut, highscore, hsLabel, user;
    private FirebaseAuth mAuth;
    private Button btnStart;
    private DatabaseReference mDatabase;
    private RequestQueue mReqQueue;
    private String tag = "QueueTag";
    private String url = "http://jservice.io/api/clues?category=1079";
    private ImageView globe;
    Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private Boolean displayHighScore;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnStart = findViewById(R.id.btnStart);
        globe = findViewById(R.id.globe);
        highscore = findViewById(R.id.highScoreValue);
        hsLabel = findViewById(R.id.highScoreLabel);
        user = findViewById(R.id.welcomeUser);



        // Initialize Firebase Auth and database
        mAuth = FirebaseAuth.getInstance();
        signOut = findViewById(R.id.signOut);
        //question table
        //mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        //user table
        mDatabase = FirebaseDatabase.getInstance().getReference("users");


        // Initializes new volley request queue
        //  mReqQueue = Volley.newRequestQueue(this);

        //make sure to use a theme with no action bar (set in your manifest)
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);


        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Retrieves json data from url
                // getJsonData();
                Animation zoomIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
                globe.startAnimation(zoomIn);
                zoomIn.setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationStart(Animation animation){

                    }

                    @Override
                    public void onAnimationEnd(Animation animation){
                        startQuiz();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation){

                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //    This function retrieves json data from url and store in on the firebase database
//    private void getJsonData(){
//
//        // JsonArrayRequest class instance to get json array
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        // Iterates for each value in response array and
//                        // sets to text view for display
//                        for (int i = 0; i < response.length(); i++){
//                            try {
//                                Log.d("Mia", Integer.toString(response.length()));
//                                // Gets jsonObject at index i
//                                JSONObject jsonObject = response.getJSONObject(i);
//
//                                int id = jsonObject.getInt("id");
//                                String question = jsonObject.getString("question");
//                                int points = jsonObject.getInt("value");
//                                String answer = jsonObject.getString("answer");
//                                String option1 = "", option2 = "", option3 = "", option4 = "";
//
//                                // gets individual values of data according to key names
////                            String name = jsonObject.getString("first") + " " + jsonObject.getString("last");
////                            String email = jsonObject.getString("email");
//
////                                Log.d("Mia", jsonObject.getString("question"));
////                                Question quiz = new Question(id,question,points,answer, option1,option2, option3, option4);
//                                mDatabase.child(Integer.toString(i)).child("id").setValue(id);
//                                mDatabase.child(Integer.toString(i)).child("question").setValue(question);
//                                mDatabase.child(Integer.toString(i)).child("points").setValue(points);
//                                mDatabase.child(Integer.toString(i)).child("answer").setValue(answer);
//                                mDatabase.child(Integer.toString(i)).child("option1").setValue(option1);
//                                mDatabase.child(Integer.toString(i)).child("option2").setValue(option2);
//                                mDatabase.child(Integer.toString(i)).child("option3").setValue(option3);
//                                mDatabase.child(Integer.toString(i)).child("option4").setValue(option4);
////                                mDatabase.setValue(quiz);
////                            tvData.append("Name: " + name + "\n");
////                            tvData.append("Email: " + email + "\n\n");
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // On error in parsing logs the error
//                Log.e("Volley", error.toString());
//            }
//        });
//
//        // Sets tag for the request
//        jsonArrayRequest.setTag(tag);
//        // Adds jsonArrayRequest to the request queue
//        mReqQueue.add(jsonArrayRequest);
//
//}

    public void signOut(View view){
        mAuth.signOut();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name","");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void startQuiz(){
        Intent intent = new Intent(this, StartQuiz.class);
        intent.putExtra("highscore", Integer.parseInt(highscore.getText().toString()));
        startActivity(intent);
    }

    @Override
    protected void onStart(){
        super.onStart();
        getUserData();
    }

    @Override
    protected void onResume(){
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        displayHighScore = sharedPreferences.getBoolean("display_high_score", true);
        name = sharedPreferences.getString("name","User");
        getUserData();

    }

    private void getUserData(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
//                String value = dataSnapshot.getValue().toString();
//                Log.d("Mia", value);
                    final String INSERT_NAME = name;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    highscore.setText(postSnapshot.child("highscore").getValue().toString());
                    if(displayHighScore){
                        highscore.setVisibility(View.VISIBLE);
                        hsLabel.setVisibility(View.VISIBLE);
                    }else{
                        highscore.setVisibility(View.INVISIBLE);
                        hsLabel.setVisibility(View.INVISIBLE);
                    }

                    user.setText("Hello " + name + ",");

                    postSnapshot.getRef().child("name").setValue(INSERT_NAME);
//                    Log.e("Mia", "======="+postSnapshot.child("highscore").getValue());
//                    Log.e("Mia", "======="+postSnapshot.child("name").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });
    }



    @Override
    protected void onStop(){
        super.onStop();

        // if queue is not empty
        // cancels all the requests with given tag
        if (mReqQueue != null){
            mReqQueue.cancelAll(tag);
        }
    }
}
