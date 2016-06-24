package ba.cloud.sarajevo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import commands.Command;
import de.rwth.ModelLoaderSetup;
import system.ArActivity;

/**
 * Created by dinok on 6/24/2016.
 */
public class Activity9_12 extends Activity {
    ImageView iv;
    int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_9_12);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        iv = (ImageView) findViewById(R.id.activity_9_12_iv);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);

        final GestureDetector gesture = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i("SWIPE", "onFling has been called!");
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_MAX_OFF_PATH = 250;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && current >= 0) {
                        //Log.i("SWIPE", "Right to Left");

                        current++;
                        iv.setImageResource(R.drawable.ss12);
                        if(current > 1) {
                            ArActivity.startWithSetup(Activity9_12.this, new ModelLoaderSetup(
                                    new Command() {
                                        @Override
                                        public boolean execute() {
                                            startActivity(new Intent(Activity9_12.this, AboutActivity.class));
                                            return true;
                                        }
                                    },
                                    new Command() {
                                        @Override
                                        public boolean execute() {
                                            Intent i = new Intent(Activity9_12.this, Activity9_12.class);
                                            startActivity(i);
                                            return true;
                                        }
                                    }));
                            finish();
                        }
                    }
                    else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && current < 2) {
                        current = 0;
                        iv.setImageResource(R.drawable.ss9);
                    }
                } catch (Exception e) {
                    // nothing
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        findViewById(R.id.activity_9_12_iv).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
    }
}
