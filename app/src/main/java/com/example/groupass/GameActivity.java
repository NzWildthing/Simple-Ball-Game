package com.example.groupass;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private TextView score, curr_lives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Enabling immersive mode
        int uiOptions = View. SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View. SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        ;


        //Initialises views at the start so can be set in the graphics view onDraw method
        score = (TextView)findViewById(R.id.score_text);
        curr_lives = (TextView) findViewById(R.id.lives_text);
        //Gets hold of the constraint layout and creates a new graphics view instance
        GraphicsView graphics = new GraphicsView(this);
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.graphics_contraint);

        //Adds the graphic view to the constraint layout
        layout.addView(graphics);
    }

    //Creates the custom class GraphicsView which inherits from the view class to draw objects on the screen
    public class GraphicsView extends View {

        private GestureDetector gestureDetector;

        //Initialises a ball game object
        gameObject ball = new gameObject(0, 0, 50, getColor(R.color.crimson));
        gameObject target = new gameObject(50, 50, 50, getColor(R.color.gold));

        //Screen width and height for calculations
        float screenWidth;
        float screenHeight;

        //Makes sure ball doesn't move unless a touch is initiated
        boolean start = false;

        String TAG = "TAG_GESTURE";

        int current_score;
        int lives = 3;


        //setting preferences
        SharedPreferences prefs = getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //getting current high score
        int high_score = prefs.getInt("highkey", 0);

        //Constructor to initialise the view with the correct context and sets the brush color
        public GraphicsView(Context context)
        {
            super(context);
            gestureDetector = new GestureDetector(context, new BallGestureListener());
        }

        //Overrides the draw method to draw a circle on the canvas
        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //If the initial Drawing
            if (start == false)
            {
                start = true;
                //Sets the initial x and y position of the ball
                ball.resetBall(screenWidth, screenHeight);
                //Sets the targets motion speeds
                target.changeDirection(150,0);
            }
            //Otherwise object is set in motion
            else {

                //Moves the object
                ball.moveObject();
                //Checks for a wall collision
                ball.checkWallCollision(screenWidth, screenHeight);
                //Moves the target
                target.moveObject();
                //Checks for a wall collision
                target.checkWallCollision(screenWidth, screenHeight);
                //Checks if the ball collides with the target
                if(ball.objectCollision(target)){
                    Log.d(TAG, "collision");
                    ball.resetBall(screenWidth, screenHeight);
                    //Updates the score in the text view
                    current_score++;
                    String scr = String.valueOf(current_score);
                    score.setText(scr);
                    //stores current score as preference
                    editor.putInt("prevkey", current_score);
                    editor.commit();
                    if(current_score>high_score){
                        editor.putInt("highkey", current_score);
                        editor.commit();
                    }
                }

                //checks if they have missed
                if( ball.checkMiss() && !ball.objectCollision(target)){
                    lives-=1;
                    String scrL = String.valueOf(lives);
                    curr_lives.setText(scrL);
                }

                //checks if they have run out of lives
                if(lives==0){
                    Intent game_over = new Intent(this.getContext(), HighScores.class);
                    startActivity(game_over);
                }

            }

            //Draws the ball
            ball.drawObject(canvas);
            //Draws a target
            target.drawObject(canvas);
            invalidate();
        }

        //Gets called whenever the screen size is changed so that the canvas always has the correct size
        //Is called upon initial creation of the graphics view as well
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            screenWidth = w;
            screenHeight = h;
        }

        //Overrides the on touch so that the gesture detector knows that it is the motion event
        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            if (gestureDetector.onTouchEvent(event))
            {
                return true;
            }
            return super.onTouchEvent(event);
        }

        //A gesture listener for the ball
        public class BallGestureListener extends GestureDetector.SimpleOnGestureListener
        {

            //Overrides a onDown gesture and a onFling gesture
            @Override
            public boolean onDown(MotionEvent e)
            {
                //Ball is only throwable from bottom 1/5 of screen
                if(e.getY() > (screenHeight/5*4))
                {
                    //When finger is pressed down the ball should stop moving and go to the position of the finger
                    ball.setXY(e.getX(), e.getY());
                    ball.changeDirection(0,0);
                }
                //Log.d(TAG, "onDown");
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
            {
                double total_velocity = Math.sqrt((velocityX*velocityX)+(velocityY*velocityY));
                //Ball is only throwable from bottom 1/5 of screen
                if(e1.getY() > (screenHeight/5*4))
                {
                    //Ball will only be fling if enough power is put into the fling
                    if(total_velocity >5000 || total_velocity <-5000)
                    {
                        //When the ball is flung it should be sent in the direction it was flung
                        ball.changeDirection(velocityX, velocityY);
                    }
                }
                Log.d(TAG, String.valueOf(velocityY));
                return false;
            }
        }
    }



    //Class to create a gameObject
    public class gameObject
    {
        //Game object variables
        public float _x;
        public float _y;
        Paint brush = new Paint();
        public int _radius;
        float _xVelocity;
        float _yVelocity;

        //Constructor to be used if no initial position given
        public gameObject() { }

        //Constructor for initialising a gameObject
        public gameObject(float xPos, float yPos, int radius, int rColor)
        {
            _x = xPos;
            _y = yPos;
            _radius = radius;
            brush.setColor(rColor);
        }

        //Sets the X and Y value of the gameObject
        public void setXY(float newX, float newY)
        {
            _x = newX;
            _y = newY;
        }

        //checks if ball hits the top of the phone
        public boolean checkMiss() {
            if (_y < 0) {
                return true;
            }
            else{
                return false;
            }

        }


        //Draws the gameObject
        public void drawObject(Canvas canvas)
        {
            canvas.drawCircle(_x, _y, _radius, brush);
        }

        //Moves the game object in its current direction
        public void moveObject()
        {
            _x += _xVelocity/100;
            _y += _yVelocity/100;
        }

        //Change object direction
        public void changeDirection(float xDir, float yDir)
        {
            //Makes sure the ball always travels at the same speed so will take down if given a speed too fast
            if(xDir > 5000){
                xDir = 5000;
            }
            else if(xDir < -5000){
                xDir = - 5000;
            }
            if(yDir > 5000){
                yDir = 5000;
            }
            else if(yDir < -5000){
                yDir = -5000;
            }
            _xVelocity = xDir;
            _yVelocity = yDir;
        }

        //Checks for a collision with any of the walls
        public void checkWallCollision(float x_col, float y_col)
        {
            //If hits left or right of phone
            if (_x < 0 || _x > x_col) {
                changeDirection(_xVelocity * -1, _yVelocity);
            }
            //If its top or bottom of phone
            if (_y > y_col) {
                changeDirection(_xVelocity, _yVelocity*-1);
            }
            //If hits the top of the screen ball is reset
            if(_y < 0){
                resetBall(x_col, y_col);
            }
        }

        //Checks if there is a collision between two objects
        public boolean objectCollision(gameObject object){
            double xDistance = _x - object._x;
            double yDistance = _y - object._y;

            //Calculates the distance between the two circles radius and there current x and y to see if collided
            double sumOfRadii = _radius + object._radius;
            double distanceSquared = xDistance * xDistance + yDistance * yDistance;

            //If the two circles touch each other
            boolean isOverlapping = distanceSquared  <= sumOfRadii * sumOfRadii;
            return isOverlapping;
        }

        //Need to put in inherited ball class
        public void resetBall(float width, float height){
            setXY((width) / 2, (height+20));
            changeDirection(0, 0);
        }
    }
}