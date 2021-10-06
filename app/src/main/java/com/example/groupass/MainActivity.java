package com.example.groupass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //When the start game button is clicked the game activity is opened
    public void onClickStart(View v){
        //Creates an explicit intent which opens up the GameActivity class
        Intent start = new Intent(this, GameActivity.class);
        startActivity(start);
    }

    //When the high scores button is clicked the high score list is opened
    public void onClickScore(View v){
        //Creates an explicit intent which opens up the HighScores class
        Intent open = new Intent(this, HighScores.class);
        startActivity(open);
    }
}