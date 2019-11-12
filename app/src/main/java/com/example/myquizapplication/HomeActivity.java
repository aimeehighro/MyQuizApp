package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity{
    private TextView signOut;
    private FirebaseAuth mAuth;
    private Button btnStart;
    private DatabaseReference mDatabase;
    private RequestQueue mReqQueue;
    private String tag = "QueueTag";
    private String url = "http://jservice.io/api/clues?category=1079";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnStart = findViewById(R.id.btnStart);

        // Initialize Firebase Auth and database
        mAuth = FirebaseAuth.getInstance();
        signOut = findViewById(R.id.signOut);
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");

        // Initializes new volley request queue
        mReqQueue = Volley.newRequestQueue(this);

        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Retrieves json data from url
                // getJsonData();
                startQuiz();

            }
        });

    }

//    This function retrieves json data from url and store in on the firebase database
    private void getJsonData(){

        // JsonArrayRequest class instance to get json array
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Iterates for each value in response array and
                        // sets to text view for display
                        for (int i = 0; i < response.length(); i++){
                            try {
                                Log.d("Mia", Integer.toString(response.length()));
                                // Gets jsonObject at index i
                                JSONObject jsonObject = response.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String question = jsonObject.getString("question");
                                int points = jsonObject.getInt("value");
                                String answer = jsonObject.getString("answer");
                                String option1 = "", option2 = "", option3 = "", option4 = "";

                                // gets individual values of data according to key names
//                            String name = jsonObject.getString("first") + " " + jsonObject.getString("last");
//                            String email = jsonObject.getString("email");

//                                Log.d("Mia", jsonObject.getString("question"));
//                                Question quiz = new Question(id,question,points,answer, option1,option2, option3, option4);
                                mDatabase.child(Integer.toString(i)).child("id").setValue(id);
                                mDatabase.child(Integer.toString(i)).child("question").setValue(question);
                                mDatabase.child(Integer.toString(i)).child("points").setValue(points);
                                mDatabase.child(Integer.toString(i)).child("answer").setValue(answer);
                                mDatabase.child(Integer.toString(i)).child("option1").setValue(option1);
                                mDatabase.child(Integer.toString(i)).child("option2").setValue(option2);
                                mDatabase.child(Integer.toString(i)).child("option3").setValue(option3);
                                mDatabase.child(Integer.toString(i)).child("option4").setValue(option4);
//                                mDatabase.setValue(quiz);
//                            tvData.append("Name: " + name + "\n");
//                            tvData.append("Email: " + email + "\n\n");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // On error in parsing logs the error
                Log.e("Volley", error.toString());
            }
        });

        // Sets tag for the request
        jsonArrayRequest.setTag(tag);
        // Adds jsonArrayRequest to the request queue
        mReqQueue.add(jsonArrayRequest);

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
