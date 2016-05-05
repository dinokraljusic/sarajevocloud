package de.rwth;

import system.ArActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DemoMain extends Activity {
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
        setContentView(R.layout.main);

		//ArActivity.startWithSetup(DemoMain.this, new ModelLoaderSetup("STOLIC.obj", "STOLIC.jpeg"));

        Button btnStart = (Button)findViewById(R.id.btnStart);
        Button btnSettings = (Button)findViewById(R.id.btnSettings);
        Button btnNew = (Button)findViewById(R.id.btnNew);
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        Button btnFace = (Button) findViewById(R.id.btnFace);
        Button btnSwipes = (Button) findViewById(R.id.btnSwipes);

        btnStart.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArActivity.startWithSetup(DemoMain.this, new ModelLoaderSetup());
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
        Log.i("location", Double.toString(l1.getLatitude()));
	}
}