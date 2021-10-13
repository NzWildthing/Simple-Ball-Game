package com.example.groupass;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    public class GraphicsView extends View {

        //Declaring variables
        Paint paint = new Paint();
        float x, y;
        int radius = 50;
        float xVelo=20;
        float yVelo=20;

        List<Integer> givenList = Arrays.asList(1, 2);
        Random rand = new Random();
        int randomNum = givenList.get(rand.nextInt(givenList.size()));


        public GraphicsView (Context context) {
            super(context);
            paint.setColor(getColor(R.color.crimson));

        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //draws ball in defined position
            canvas.drawCircle(x + getWidth() / 2, y + getHeight() / 2, radius, paint);
            //uses the string value to determine direction the ball should go and then alters x or y position
            if (randomNum == 1) {
                x -= xVelo;
                y -= yVelo;
            }
            if (randomNum == 2) {

                x += xVelo;
                y += yVelo;
            }
            //inverts the velocity if the ball reaches the edge
            if (x + radius >= getWidth() / 2 || x - radius <= getWidth() / -2) {
                xVelo = -xVelo;
            }
            if (y + radius >= getHeight() / 2 || y - radius <= getHeight() / -2) {
                yVelo = -yVelo;
            }
            //keeps redrawing the ball
            invalidate();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GraphicsView graphicsView = new GraphicsView(this);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.background_graphics);
        constraintLayout.addView(graphicsView);

        //Hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Enabling immersive mode
        int uiOptions = View. SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View. SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        ;

    }

    //When the start game button is clicked the game activity is opened
    public void onClickStart(View v){
        //Creates an explicit intent which opens up the GameActivity class
        Intent start = new Intent(this, GameActivity.class);
        startActivity(start);
    }


}