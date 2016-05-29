package de.rwth;

import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import system.ArActivity;

/**
 * Created by dinok on 5/3/2016.
 */
public class Splash extends Activity {
    /** Called when the activity is first created. */
    //private String _url = "http://192.168.0.112:33";
    //private String _url = "http://192.168.1.6:33";
    //private String _url = "http://192.168.0.110";
    //private String _url = "http://192.168.137.14";
    private String _url = "http://192.168.1.5";
    public static String LOG_TAG = "ModelLoader";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        TextView tvSarajevo = (TextView)findViewById(R.id.tvSarajevo);
        TextView tvCloud = (TextView)findViewById(R.id.tvCloud);
        TextView tvWelcome = (TextView)findViewById(R.id.tvWelcome);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/ACTOPOLIS.otf");
        tvSarajevo.setTypeface(type);
        tvCloud.setTypeface(type);
        tvWelcome.setTypeface(type);



        tvWelcome.setText("DOBRODOSLI");

        /**
         * ovdje provjera je li logovan - if logged tvWelcom.setText(tvWelcome.getText()+"\n"+Ime)
         */
        //ArActivity.startWithSetup(DemoMain.this, new ModelLoaderSetup("STOLIC.obj", "STOLIC.jpeg"));

        final Handler mHandler = new Handler();
        final Runnable wait3sec = new Runnable() {
            public void run() {
                ArActivity.startWithSetup(Splash.this, new ModelLoaderSetup());
            }
        };

        //if not logged in:
        /*
        final Runnable wait3secSignup = new Runnable() {
            public void run() {
                Intent i = new Intent(Splash.this, Login.class);
                startActivity(i);
            }
        };*/


        //if(loggedin)
        mHandler.postDelayed(wait3sec, 3000);
        //else mHandler.postDelayed(waait3secSignup, )




    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(0);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }
}
