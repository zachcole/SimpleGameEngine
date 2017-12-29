package com.zachcole.simplegameengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class SimpleGameEngineActivity extends AppCompatActivity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_game_engine);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize new GameView object
        gameView = new GameView(this);
        //Set new GameView object as the view
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameView.pause();
    }

    // GameView class

    class GameView extends SurfaceView implements Runnable {

        // Declarations
        Bitmap bitmapBuzz;
        Canvas canvas;
        long fps;
        boolean isMoving = false;
        SurfaceHolder ourHolder;
        Paint paint;
        volatile boolean playing;
        private long timeThisFrame;

        // Initializations
        float flySpeedPerSecond = 150;
        Thread gameThread = null;
        float buzzXPosition = 10;

        // Constructor
        public GameView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();

            bitmapBuzz = BitmapFactory.decodeResource(this.getResources(), R.drawable.buzz);
        }

        // run method aka infinite game loop (in this case)
        @Override
        public void run() {
            while (playing) {

                long startFrameTime = System.currentTimeMillis();

                update();
                draw();

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        // Game logic
        public void update() {
            if (isMoving) {
                buzzXPosition = buzzXPosition + (flySpeedPerSecond / fps);
            }
        }

        // UI logic
        public void draw() {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255,  249, 129, 0));
                paint.setTextSize(45);
                canvas.drawText("FPS:" + fps, 20, 40, paint);
                canvas.drawBitmap(bitmapBuzz, buzzXPosition, 200, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        // Custom logic for onPause
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        // Cusom logic for onResume
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // Handle touch input
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isMoving = true;
                    break;
                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    break;
            }

            return true;
        }
    }
}
