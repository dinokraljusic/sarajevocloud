package de.rwth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import system.ArActivity;
import util.Log;

/**
 * Created by dinok on 5/3/2016.
 */
public class Login extends Activity {
    /**
     * Called when the activity is first created.
     */
    //private String _url = "http://192.168.0.112:33";
    //private String _url = "http://192.168.1.6:33";
    //private String _url = "http://192.168.0.110";
    //private String _url = "http://192.168.137.14";
    private String _url = "http://192.168.1.5";
    public static String LOG_TAG = "Login";
    public Location l1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sp = getSharedPreferences("sc", 0);
        String u = sp.getString("userName", ""), i = sp.getString("userId", "");
        if(u!=null && !u.equals("") && !i.equals("")){
            Spremnik.getInstance().setUserId(i);
            Spremnik.getInstance().setUserName(u);
            //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
            startActivity(new Intent(this, Swipes.class));
            finish();
        }
        else {

            final  String userName = Spremnik.getInstance().getUserName();
            if(userName != null && !userName.equals("")) {
                String userId = "";
                try {
                    userId = new GetUserIdAsync().execute(userName).get();
                }
                catch (Throwable t){}
                if(userId == null || userId.equals("0")){
                    Toast.makeText(Login.this, "User not logged. Please register", Toast.LENGTH_LONG).show();
                    Log.i("LOGIN", "Korisnik nije logovan!");
                }else{
                    Spremnik.getInstance().setUserId(userId);
                    startActivity(new Intent(this, Swipes.class));
                    //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
                    finish();
                }
            }


            final EditText etIme = (EditText) findViewById(R.id.etIme);
            Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
           // Button btnFacebook = (Button) findViewById(R.id.btnFacebook);

            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/ACTOPOLIS.otf");

            etIme.setTypeface(type);
            btnSignIn.setTypeface(type);
    //        btnFacebook.setTypeface(type);

            l1=null;
            updateL1();

            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String userName = etIme.getText().toString();
                    // TODO: Check if username is available!
                    if (!userName.matches("")) {
                        String userId = null;
                        try {
                            userId = new RegisterUserIdAsync().execute(userName).get();
                        }catch (Throwable t) {

                        }
                        if (userId == null || userId.equals("") || userId.isEmpty()) {
                            Toast.makeText(Login.this, "Korisni?ko ime zauzeto! Molimo izaberite drugo."
                                    , Toast.LENGTH_LONG).show();
                            Log.i("LOGIN", "Korisni?ko ime zauzeto! Molimo izaberite drugo.");
                        } else {
                            Spremnik.getInstance().setUserId(userId);
                            Spremnik.getInstance().setUserName(userName);

                            SharedPreferences sp = getSharedPreferences("sc", 0);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("userId",userId);
                            editor.putString("userName",userName);
                            editor.commit();

                            Intent i = new Intent(Login.this, Swipes.class);
                            startActivity(i);

                            finish();
                            //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
                        }
                    }
                }
            });

        }
    }

    public void updateL1(){
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            l1 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (l1 == null) {
                l1 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            else if(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getAccuracy() < l1.getAccuracy())
                l1 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (l1 == null)
                l1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            else if(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getAccuracy() < l1.getAccuracy())
                l1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //Log.i("location l1:", Double.toString(l1.getLatitude()) + "; " + Double.toString(l1.getLongitude()));
    }


        class RegisterUserIdAsync extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> args = new ArrayList<>(1);
                args.add(new BasicNameValuePair("ime", params[0]));
                String userID = Utility.POST(Spremnik.getInstance().getUserServiceAddress(), args);
//Log.i("LOG_LOGIN", userID);
                String userId = Utility.GET(Spremnik.getInstance().getUserServiceAddress() + "?ime=" + params[0]);
                updateL1();
                return userId;
            }
        }
    class GetUserIdAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return Integer.toString(Utility.getUserId(params[0]));
        }
    }
}
