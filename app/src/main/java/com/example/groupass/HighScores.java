package com.example.groupass;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        //Initialises views for scores
        TextView scoreP = findViewById(R.id.score_prev);

        //making list to hold scores
        List<Integer> high_scores = new ArrayList<Integer>();

        SharedPreferences prefs = getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int prev_score = prefs.getInt("prevkey", 0);
        String scrP = String.valueOf(prev_score);
        scoreP.setText(scrP);



        int size = prefs.getInt("Status_size", 0);

        for (int i = 0; i < size; i++) {
            high_scores.add(prefs.getInt("Status_" + i, 0));
        }
        Collections.sort(high_scores, Collections.reverseOrder());



        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item, high_scores);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
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