package com.example.groupass;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class GameActivity extends AppCompatActivity {

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

        //Gets hold of the constraint layout and creates a new graphics view instance
        GraphicsView graphics = new GraphicsView(this);
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.graphics_contraint);

        //Adds the graphic view to the constraint layout
        layout.addView(graphics);
    }

    //Creates the custom class GraphicsView which inherits from the view class to draw objects on the screen
    public class GraphicsView extends View {

        //Initialises a ball game object
        gameObject ball = new gameObject(0, 0, 50, getColor(R.color.design_default_color_primary));
        gameObject target = new gameObject(50, 50, 50, getColor(R.color.design_default_color_secondary));

        //Screen width and height for calculations
        float screenWidth;
        float screenHeight;

        //Makes sure ball doesn't move unless a touch is initiated
        boolean start = false;

        //Constructor to initialise the view with the correct context and sets the brush color
        public GraphicsView(Context context) {
            super(context);
        }

        //Overrides the draw method to draw a circle on the canvas
        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //If the initial Drawing
            if (start == false) {
                start = true;
                screenWidth = canvas.getWidth();
                screenHeight = canvas.getHeight();
                //Sets the initial x and y position of the ball
                ball.setXY((screenWidth) / 2, (screenHeight) / 2);
            }
            //Otherwise object is set in motion
            else {
                //Moves the object
                ball.moveObject();

                //If hits left or right of phone
                if (ball._x < 0 || ball._x > screenWidth) {
                    ball.changeDirection(ball._xVelocity * -1, ball._yVelocity);
                }
                //If its top or bottom of phone
                if (ball._y < 0 || ball._y > screenHeight) {
                    ball.changeDirection(ball._xVelocity, ball._yVelocity*-1);
                }
            }

            //Draws the ball
            ball.drawObject(canvas);
            //Draws a target
            target.drawObject(canvas);
            invalidate();
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
        int _xVelocity = 3;
        int _yVelocity = 3;

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

        //Draws the gameObject
        public void drawObject(Canvas canvas)
        {
            canvas.drawCircle(_x, _y, _radius, brush);
        }

        //Moves the game object in its current direction
        public void moveObject()
        {
            _x += _xVelocity;
            _y += _yVelocity;
        }

        //Change object direction
        public void changeDirection(int xDir, int yDir)
        {
            _xVelocity = xDir;
            _yVelocity = yDir;
        }
    }
}