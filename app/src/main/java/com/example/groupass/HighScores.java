package com.example.groupass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HighScores extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
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