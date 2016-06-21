package de.rwth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import system.ArActivity;

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
    public static final String CREDENTIALS = "credentials.sc";
    private String _url = "http://192.168.1.5";
    public static String LOG_TAG = "Login";
    public Location l1;
    Typeface type;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //SharedPreferences settings = getSharedPreferences(CREDENTIALS, 0);

        //final String userName = settings.getString("userName", "");
        final String userName="";
        if(!userName.isEmpty() && !userName.equals("")){
            Spremnik.getInstance().setUserName(userName);
            String userId = "";
            try {
                userId = new GetUserIdAsync().execute(userName).get();
            }catch (Exception ex) {
                showMessage(ex.getMessage());
            }
            if (userId == null || userId.isEmpty() || userId.equals("0")) {
                showMessage("KORISNIK NIJE LOGOVAN! MOLIMO REGISTRUJTE SE.");
            } else {
                Spremnik.getInstance().setUserId(userId);
                ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
            }
        }


        TextView tvUsloviKoristenjaLink = (TextView) findViewById(R.id.uslovi_koristenja_link);
        TextView tvUsloviKoristenja = (TextView) findViewById(R.id.uslovi_koristenja);
        //tvUsloviKoristenjaLink.setMovementMethod(LinkMovementMethod.getInstance());
        tvUsloviKoristenjaLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.goethe.de/ins/ba/bs/sar/ver.cfm?fuseaction=events.detail&event_id=20764379"));
                startActivity(new Intent(Login.this, UsloviKoristenja.class));
            }
        });
        final EditText etIme = (EditText) findViewById(R.id.etIme);
        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        //Button btnFacebook = (Button) findViewById(R.id.btnFacebook);

        type = Typeface.createFromAsset(getAssets(), "fonts/ACTOPOLIS.otf");

        etIme.setTypeface(type);
        btnSignIn.setTypeface(type);

        etIme.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            final String userName = etIme.getText().toString();
                            // TODO: Check if username is available!
                            if (!userName.matches("")) {
                                String userId = null;
                                try {
                                    userId = new RegisterUserIdAsync().execute(userName).get();
                                }catch (Throwable t) {

                                }
                                if (userId == null || userId.equals("") || userId.isEmpty()) {
                                    showMessage("Korisnicko ime zauzeto. Molimo izaberite drugo.");
                                } else {
                                    Spremnik.getInstance().setUserId(userId);
                                    Spremnik.getInstance().setUserName(userName);

                                    SharedPreferences settings = getSharedPreferences(CREDENTIALS, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("userName", userName);
                                    editor.commit();

                                    //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
                                    Intent i = new Intent(Login.this, Swipes.class);
                                    startActivity(i);
                                    //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
                                }
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        //btnFacebook.setTypeface(type);

      //  l1=null;
      //  updateL1();
        
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
                        showMessage("Korisnicko ime zauzeto. Molimo izaberite drugo.");
                    } else {
                        Spremnik.getInstance().setUserId(userId);
                        Spremnik.getInstance().setUserName(userName);

                        SharedPreferences settings = getSharedPreferences(CREDENTIALS, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("userName", userName);
                        editor.commit();

                        //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
                        Intent i = new Intent(Login.this, Swipes.class);
                        startActivity(i);
                        //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
                    }
                }
            }
        });
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

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showMessage(String text) {
        /*findViewById(R.id.messageBox).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.messageBox_text)).setText(text);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.messageBox).setVisibility(View.GONE);
                    }
                });
            }
        }, 3000);*/
    }

    class RegisterUserIdAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                List<NameValuePair> args = new ArrayList<>(1);
                args.add(new BasicNameValuePair("ime", params[0]));
                String userID = Utility.registerUser(Spremnik.getInstance().getUserServiceAddress(), args);
                return userID;
            } catch (java.io.IOException ioe) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMessage("PROBLEMI SA KONEKCIJOM!");
                    }
                });
            }
            return "";
        }
    }

    class GetUserIdAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return Integer.toString(Utility.getUserId(params[0]));
        }
    }
}
