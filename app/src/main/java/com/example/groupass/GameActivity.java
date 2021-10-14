package com.example.groupass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import java.util.Random;

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
        public Ball ball = new Ball(0, 0, getColor(R.color.red), 50);
        public Target target = new Target(50, 50, getColor(R.color.gold), 50);
        public livesPowerUp extraLives = new livesPowerUp(0, 0, getColor(R.color.black), 50);
        public Random rnd = new Random();

        //creating lists
        public List<gameObject> objects = new ArrayList<gameObject>();
        public List<Integer> high_scores = new ArrayList<Integer>();

        //Screen width and height for calculations
        float screenWidth;
        float screenHeight;

        //Makes sure ball doesn't move unless a touch is initiated
        boolean start = false;
        boolean gameOver = false;
        boolean activePowerUp = false;

        String TAG = "TAG_GESTURE";
        int current_score, minHigh, size;


        //setting preferences
        SharedPreferences prefs = getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();




        //Constructor to initialise the view with the correct context and sets the brush color
        public GraphicsView(Context context)
        {
            super(context);
            gestureDetector = new GestureDetector(context, new BallGestureListener());
            //Adds the ball and target to the object list
            objects.add(ball);
            objects.add(target);
        }

        //Overrides the draw method to draw a circle on the canvas
        @Override
        public void onDraw(Canvas canvas) {
            if(gameOver == true){
                return;
            }
            super.onDraw(canvas);

            //if score is zero
            if(current_score==0){
                // set the prev score preference as 0
                editor.putInt("prevkey", 0);
                editor.commit();
            }

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
                //checks if they have run out of lives
                if(ball._lives==0){
                    gameOver = true;
                    gameOver();
                }
                //Moves objects and checks for wall collisions
                for(int i = 0; i < objects.size(); i++)
                {
                    objects.get(i).moveObject();
                    objects.get(i).checkWallCollision(screenWidth, screenHeight);
                }
                //Checks if the ball collides with the target
                if(ball.objectCollision(target)){
                    //If it does update score
                    updateScore();
                }
                //Checks extra life
                if(current_score != 0)
                {
                    checkExtraLive();
                }
            }
            //####################Drawing################

            //Draws objects
            for(int i = 0; i < objects.size(); i++)
            {
                objects.get(i).drawObject(canvas);
            }
            invalidate();
        }


        public void gameOver()
        {
            int size = prefs.getInt("Status_size", 0);

            for (int i = 0; i < size; i++) {
                high_scores.add(prefs.getInt("Status_" + i, 0));
            }
            Collections.sort(high_scores);
            
            //if scoreboard is full set lowest high score to 0 else find lowest value
            if(5>size){
                minHigh=0;
            }
            else{
                minHigh=findMin(high_scores);
            }
            if(current_score>minHigh){
                //removes lowest high score if high score page is already full
                if(5==size){
                    high_scores.remove(0);
                }
                high_scores.add(current_score);
                saveArray();
            }
            Intent game_over = new Intent(GameActivity.this, HighScores.class);
            startActivity(game_over);
        }

        public boolean saveArray()
        {

            size = high_scores.size();

            editor.putInt("Status_size", size);

            for(int i=0; i<size; i++)
            {
                editor.remove("Status_" + i);
                editor.putInt("Status_" + i, high_scores.get(i));
            }

            return editor.commit();
        }

        //find the lowest of the top 5 high scores
        public Integer findMin(List<Integer> list)
        {
            List<Integer> sortedHigh = new ArrayList<>(list);
            Collections.sort(sortedHigh);
            return sortedHigh.get(0);
        }

        //Checks if a extra life is required
        public void checkExtraLive()
        {
            //Checks if the ball collides with the powerup
            if(ball.objectCollision(extraLives))
            {
                ball.incrementLives(2);
                //If the ball collides with it reset the powerup and remove it from the drawing list
                extraLives.reset();
                objects.remove(extraLives);
                //Makes the powerup able to spawn again
                activePowerUp = false;
            }
        }

        //Updates the current score on the screen
        public void updateScore()
        {
            ball.resetBall(screenWidth, screenHeight);
            //Updates the score in the text view
            current_score++;
            String scr = String.valueOf(current_score);
            score.setText(scr);
            //stores current score as preference
            editor.putInt("prevkey", current_score);
            editor.commit();

            //If current score is a multiple of 5 it spawns an extralives powerup
            //Can only spawn when the updateScore is called therefore can't be duplicates
            if(current_score % 5 == 0 && activePowerUp == false)
            {
                extraLives.spawn(screenWidth, screenHeight, rnd);
                objects.add(extraLives);
                activePowerUp = true;
            }
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
        float _xVelocity;
        float _yVelocity;
        int _radius;

        //Constructor for initialising a gameObject
        public gameObject(float xPos, float yPos, int rColor, int radius)
        {
            _x = xPos;
            _y = yPos;
            brush.setColor(rColor);
            _radius = radius;
        }

        //Sets the X and Y value of the gameObject
        public void setXY(float newX, float newY)
        {
            _x = newX;
            _y = newY;
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

    }

    //Class for a ball game object
    public class Ball extends gameObject
    {
        public int _lives = 10;

        public Ball(float xPos, float yPos, int rColor, int radius)
        {
            //Calls the game object super class
            super(xPos, yPos, rColor, radius);
            //When the ball is created so are its lives
            String scrL = String.valueOf(_lives);
            curr_lives.setText(scrL);
        }

        //Resets the ball to the starting position
        public void resetBall(float width, float height){
            //Sets the x and y of the ball to current height and width of screen
            setXY((width) / 2, (height+20));
            //Calls super as no need to go through unnecessary processing
            super.changeDirection(0, 0);
        }

        @Override
        //Makes sure the ball only travels at a certain speed once flung
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

            //Calls the main method
            super.changeDirection(xDir, yDir);
        }

        @Override
        public void checkWallCollision(float x_col, float y_col)
        {
            //Extra processing for if ball has missed
            //If hits the top of the screen ball is reset
            if(_y < 0){
                //When the ball is reset the lives counter goes down
                _lives-=1;
                String scrL = String.valueOf(_lives);
                curr_lives.setText(scrL);
                resetBall(x_col, y_col);
            }
            super.checkWallCollision(x_col, y_col);
        }

        //Increments the players lives by a set amount
        public void incrementLives(int lives)
        {
            _lives += lives;
            String scrL = String.valueOf(_lives);
            curr_lives.setText(scrL);
        }
    }

    //Class for a target game object
    public class Target extends gameObject
    {
        public Target(float xPos, float yPos, int rColor, int radius) {
            //Calls the game object super class
            super(xPos, yPos, rColor, radius);
        }
    }

    //Class for a lives power up
    public class livesPowerUp extends gameObject
    {
        public livesPowerUp(float xPos, float yPos, int rColor, int radius)
        {
            //Calls the game object super class
            super(xPos, yPos, rColor, radius);
        }

        //Spawns the extra live powerup
        public void spawn(float width, float height, Random rnd)
        {
            int xLive = rnd.nextInt((int)width);
            int yLive = rnd.nextInt((int)(height-(height/5)));
            //Spawns a random extra lives powerup on the screen somewhere
            setXY(xLive, yLive);
        }

        //Resets the powerup
        public void reset()
        {
            setXY(-1, -1);
        }
    }
}