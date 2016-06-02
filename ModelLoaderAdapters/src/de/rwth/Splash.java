package de.rwth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by dinok on 5/3/2016.
 */
public class Splash extends Activity {
    public static final String CREDENTIALS = "credentials.sc";
    /** Called when the activity is first created. */
    //private String _url = "http://192.168.0.112:33";
    //private String _url = "http://192.168.1.6:33";
    //private String _url = "http://192.168.0.110";
    //private String _url = "http://192.168.137.14";
    //private String _url = "http://192.168.1.5";
    public static String LOG_TAG = "ModelLoader";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView tvSarajevo = (TextView)findViewById(R.id.tvSarajevo);
        TextView tvCloud = (TextView)findViewById(R.id.tvCloud);
        TextView tvWelcome = (TextView)findViewById(R.id.tvWelcome);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/ACTOPOLIS.otf");
        tvSarajevo.setTypeface(type);
        tvCloud.setTypeface(type);
        tvWelcome.setTypeface(type);

        SharedPreferences settings = getSharedPreferences(CREDENTIALS, 0);
        final String userName = settings.getString("userName", "");
        if(userName != null && !userName.isEmpty() && !userName.equals("")){
            Spremnik.getInstance().setUserName(userName);
            tvWelcome.setText("DOBRODOSLI\nWELCOME\n" + userName);
        }else{
            tvWelcome.setText("DOBRODOSLI\nWELCOME");
        }

        final Handler mHandler = new Handler();
        final Runnable wait3secSignup = new Runnable() {
            public void run() {
                Intent i = new Intent(Splash.this, Login.class);
                startActivity(i);
                finish();
            }
        };

        mHandler.postDelayed(wait3secSignup,2500 );

        Location l1=null;
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            l1 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (l1 == null) {
                l1 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (l1 == null)
                l1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(l1!=null)
            Log.i("location", Double.toString(l1.getLatitude()));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(0);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }
}
