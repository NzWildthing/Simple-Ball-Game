package com.example.groupass;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HighScores extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_high_scores);

        //Hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Enabling immersive mode
        int uiOptions = View. SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View. SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        ;

        //Initialises views for scores
        TextView scoreP = (TextView) findViewById(R.id.score_prev);
        TextView scoreH = (TextView) findViewById(R.id.score_high);
        //getting preferences
        SharedPreferences prefs = getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int prev_score = prefs.getInt("prevkey", 0); //0 is the default value
        int high_score = prefs.getInt("highkey", 0); //0 is the default value
        String scrP = String.valueOf(prev_score);
        String scrH = String.valueOf(high_score);
        scoreP.setText(scrP);
        scoreH.setText(scrH);

    }

    //When the replay button is clicked the game activity is opened
    public void onClickReplay(View v){
        //Creates an explicit intent which opens up the GameActivity class
        Intent start = new Intent(this, GameActivity.class);
        startActivity(start);
    }

    //When the home button is clicked the home screen is opened
    public void onClickHome(View v){
        //Creates an explicit intent which opens up the HighScores class
        Intent open = new Intent(this, MainActivity.class);
        startActivity(open);
    }
}