package de.rwth;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by dinok on 5/4/2016.
 */
public class Face extends Activity {
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
        setContentView(R.layout.face);

        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        Button btnFacebook = (Button) findViewById(R.id.btnFacebook);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/ACTOPOLIS.otf");
        etEmail.setTypeface(type);
        etPassword.setTypeface(type);
        btnFacebook.setTypeface(type);



        //ArActivity.startWithSetup(DemoMain.this, new ModelLoaderSetup("STOLIC.obj", "STOLIC.jpeg"));


    }
}