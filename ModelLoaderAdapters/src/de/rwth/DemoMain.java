package de.rwth;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import system.ArActivity;

/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;*/

public class DemoMain extends Activity // implements
        /*GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,*/
//        LocationListener
{
    /** Called when the activity is first created. */
    //private String _url = "http://192.168.0.112:33";
    //private String _url = "http://192.168.1.6:33";
    //private String _url = "http://192.168.0.110";
    //private String _url = "http://192.168.137.14";
    private String _url = "http://192.168.0.100";
    public static String LOG_TAG = "ModelLoader";


    private TextView tvFusedLocation;
    //private GoogleApiClient mGoogleApiClient;
    //private LocationRequest mLocationRequest;/*  */

    private Location l1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //ArActivity.startWithSetup(DemoMain.this, new ModelLoaderSetup("STOLIC.obj", "STOLIC.jpeg"));

        Button btnStart = (Button)findViewById(R.id.btnStart);
        Button btnSettings = (Button)findViewById(R.id.btnSettings);
        Button btnNew = (Button)findViewById(R.id.btnNew);
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        Button btnFace = (Button) findViewById(R.id.btnFace);
        Button btnSwipes = (Button) findViewById(R.id.btnSwipes);
        tvFusedLocation = (TextView) findViewById(R.id.fused_location);

        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ArActivity.startWithSetup(DemoMain.this, new ModelLoaderSetup(null));
            }
        });
        btnSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                //startActivity(getActivity().getApplicationContext(), intent);
                startActivityForResult(intent, 0);
            }
        });

        btnNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Splash.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

        btnFace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DemoMain.this, Face.class);
                startActivity(i);
            }
        });

        btnSwipes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DemoMain.this, Swipes.class);
                startActivity(i);
            }
        });


        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/


        l1=null;
        updateL1();
        //Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
        //onLocationChanged(loc);
        //Log.i("location", Double.toString(l1.getLatitude()));
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
    protected void onStart() {
        super.onStart();
        // Connect the client.
        //mGoogleApiClient.connect();
    }
/*
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(400);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //LocationServices.FusedLocationApi.re

    }
    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {
        updateL1();
        tvFusedLocation.setText("loc: " + location.getLatitude() + ", " + location.getLongitude() + "; "+ location.getAccuracy() + " " + location.getProvider());
        String added = "\nl1: " + l1.getLatitude() + ", " + l1.getLongitude() + "; "+ l1.getAccuracy() + " " + l1.getProvider() +
                "\nloc alt: " + location.getAltitude() + " l1 alt: " + l1.getAltitude() + "\nloc speed: " + location.getSpeed() + " l1 speed: " + l1.getSpeed();
        tvFusedLocation.setText(tvFusedLocation.getText() + added);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    */
}