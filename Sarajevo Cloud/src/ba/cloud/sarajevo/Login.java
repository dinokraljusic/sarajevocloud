package ba.cloud.sarajevo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import de.rwth.Spremnik;
import de.rwth.Utility;
import util.Log;

/**
 * Created by dinok on 5/3/2016.
 */
public class Login extends Activity {
    /**
     * Called when the activity is first created.
     */
    public static final String CREDENTIALS = "credentials.sc";
    public static String LOG_TAG = "Login";
    public Location l1;
    private LinearLayout _llCredentials, _llWait;
    private int _timesBackPressed = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences settings = getSharedPreferences(CREDENTIALS, 0);

        _llCredentials = (LinearLayout) findViewById(R.id.login_credentials);
        _llWait = (LinearLayout) findViewById(R.id.login_wait);

        findViewById(R.id.messageBox).setVisibility(View.INVISIBLE);

        final String userName = settings.getString("userName", "");

        Bundle b = getIntent().getExtras();
        int loginPokusaj = 0;
        if(b != null)
            loginPokusaj = b.getInt("loginPokusaj");
        else
            b = new Bundle();
        if(loginPokusaj > 5) {
            showMessage("TRENUTNO NIJE MOGUCE LOGOVATI SE. POKUSAJTE KASNIJE");
        }else if(!userName.isEmpty() && !userName.equals("")){
            Intent i = new Intent(Login.this, PostLoginActivity.class);
            b.clear();
            b.putInt("loginPokusaj", loginPokusaj + 1);
            i.putExtras(b);
            Log.i(LOG_TAG, "loginPokusaj: " + loginPokusaj);
            startActivity(i);
            finish();
            return;
        }


        final EditText etIme = (EditText) findViewById(R.id.etIme);
        final Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        //Button btnFacebook = (Button) findViewById(R.id.btnFacebook);
        final TextView txtMessageBox = (TextView)findViewById(R.id.messageBox_text);

        TextView tvUsloviKoristenjaLink = (TextView) findViewById(R.id.uslovi_koristenja_link);
        TextView tvUsloviKoristenja = (TextView) findViewById(R.id.uslovi_koristenja);
        //tvUsloviKoristenjaLink.setMovementMethod(LinkMovementMethod.getInstance());
        tvUsloviKoristenjaLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.goethe.de/ins/ba/bs/sar/ver.cfm?fuseaction=events.detail&event_id=20764379"));
                startActivity(new Intent(Login.this, Uslovi.class));
            }
        });

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/ACTOPOLIS.otf");

        etIme.setTypeface(type);
        btnSignIn.setTypeface(type);
        //btnFacebook.setTypeface(type);
        txtMessageBox.setTypeface(type);

        etIme.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            final String userName = etIme.getText().toString().toUpperCase();
                            if(userName.length()>0){
                                _llCredentials.setVisibility(View.GONE);
                                _llWait.setVisibility(View.VISIBLE);
                                RegisterUser(userName);
                            }
                            else showMessage("Korisnicko ime mora sadržavati bar jedan znak. Molimo izaberite drugo.");
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        l1=null;
        updateL1();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = etIme.getText().toString().toUpperCase();
                if(userName.length()>0){
                    _llCredentials.setVisibility(View.GONE);
                    _llWait.setVisibility(View.VISIBLE);
                    RegisterUser(userName);
                }
                else showMessage("Korisnicko ime mora sadržavati bar jedan znak. Molimo izaberite drugo.");
            }
        });
        Spremnik.getInstance().setPreviousActivity(getLocalClassName());
    }

    public void updateL1(){
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(lm == null) return;

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
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        //Log.i("location l1:", Double.toString(l1.getLatitude()) + "; " + Double.toString(l1.getLongitude()));
    }

    @Override
    public void onBackPressed() {
        if (_timesBackPressed > 1){
            finish();// return super.onKeyDown(a, keyCode, event);
            return;
        }
        Toast.makeText(getApplicationContext(), "Press back once more to exit", Toast.LENGTH_SHORT).show();
        _timesBackPressed++;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!Spremnik.getInstance().getPreviousActivity().equals(getLocalClassName()))
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Spremnik.getInstance().getPreviousActivity().equals(getLocalClassName()))
            finish();
    }

    private void RegisterUser(final String userName){
        // TODO: Check if username is available!
        if (!userName.matches("")) {
            String userId = null;
            try {
                userId = new RegisterUserIdAsync().execute(userName).get();
            } catch (Throwable t) {
                showMessage(t.getMessage());
            }
            if (userId == null || userId.equals("") || userId.isEmpty()) {
                showMessage("Korisnicko ime zauzeto. Molimo izaberite drugo.");
                _llWait.setVisibility(View.GONE);
                _llCredentials.setVisibility(View.VISIBLE);
            } else {
                Spremnik.getInstance().setUserId(userId);
                Spremnik.getInstance().setUserName(userName);

                SharedPreferences settings = getSharedPreferences(CREDENTIALS, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("userName", userName);
                editor.commit();

                //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
                Intent i = new Intent(Login.this, Abouts_1Activity.class);
                startActivity(i);
                //ArActivity.startWithSetup(Login.this, new ModelLoaderSetup());
            }
        }
    }

    private void showMessage(String text) {
        findViewById(R.id.messageBox).setVisibility(View.VISIBLE);
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
        }, 4000);
    }

    class RegisterUserIdAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                List<NameValuePair> args = new ArrayList<>(1);
                args.add(new BasicNameValuePair("ime", params[0]));
                String userID = Utility.registerUser(Spremnik.getInstance().getUserServiceAddress(), args);
                return userID;
            } catch (final Exception ioe) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ioe.getClass().toString().toUpperCase().contains("NULLPOINTER"))
                            showMessage("Internet konekcija nije dostupna.");
                        else
                            showMessage(ioe.getMessage());
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

