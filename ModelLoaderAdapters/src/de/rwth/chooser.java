package de.rwth;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import system.ArActivity;

/**
 * Created by MiniP on 3/23/2016.
 */
public class chooser extends Activity {
    public static String LOG_TAG = "chooser";
    /**
     * Called when the activity is first created.
     */
    //private String _url = "http://192.168.0.112:33";
    //private String _url = "http://192.168.1.6:33";
    //private String _url = "http://192.168.0.110";
    //private String _url = "http://192.168.137.14";
    //private String _url = "http://192.168.0.105";
    private String _url = "http://192.168.1.3";
    private ProgressBar _progressBar;
    private TextView txtError;

    ArrayList<View> _listSetovi = new ArrayList<View>();
    ArrayAdapter<View> _adapterSetovi;
    private ListView _listViewSetovi;
    ListView listPiktogrami;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser);

        _progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.VISIBLE);

        txtError = (TextView) findViewById(R.id.txtError);
        txtError.setVisibility(View.GONE);

        _listViewSetovi = (ListView) findViewById(R.id.listSetovi);
        _adapterSetovi = new ArrayAdapter<View>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                _listSetovi);
        _listViewSetovi.setAdapter(_adapterSetovi);
        _listViewSetovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    ((Button)adapterView.getItemAtPosition(i)).performClick();
                }catch (Throwable t){
                    t.printStackTrace();
                }
            }
        });

        new DobavljacSetova(this, _adapterSetovi, _progressBar).execute("0", "1", "2");

        //_listViewSetovi.setVisibility(View.VISIBLE);
        //setContentView(_listViewSetovi);
        /*
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "starting");


            }
        });
        t.start();
        try {
            t.join();

            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
        }*/
    }
/*
    private View newPiktButton(JSONObject piktogram) throws JSONException {
        String objExt = "obj",
                tekExt = "png";
        String jsonVal = piktogram.getString("put_piktogram");
        Log.i(LOG_TAG, "put_piktogram: " + jsonVal);

        int extId = jsonVal.lastIndexOf('.') + 1;
        Log.i(LOG_TAG, "put_piktogram ext index: " + extId);
        if (extId > 0)
            objExt = jsonVal.substring(extId);

        jsonVal = piktogram.getString("put_tekstura");
        extId = jsonVal.lastIndexOf('.') + 1;
        Log.i(LOG_TAG, "put_tekstura: " + jsonVal);
        Log.i(LOG_TAG, "put_piktogram ext index: " + extId);
        if (extId > 0)
            tekExt = jsonVal.substring(extId);
        Log.i(LOG_TAG, "extenzije: " + objExt + ", " + tekExt);

        String fileName = piktogram.getString("naziv") + "." + objExt;
        String textureName = piktogram.getString("naziv") + "." + tekExt;
        int id = piktogram.getInt("id");

        return newButton(fileName, textureName, id);
    }

    private View newButton(String fileName, String textureName, int id) {
        Context context = (Context) this;

        final String finalFileName = Utility.downloadAndSaveFile(getApplicationContext(), _url + "/download.php", id, false, fileName, LOG_TAG);
        final String finalTextureName = Utility.downloadAndSaveFile(getApplicationContext(), _url + "/download.php", id, true, textureName, LOG_TAG);
        Log.i(LOG_TAG, "final names :" + finalFileName + ", " + finalTextureName);
        Button b = new Button(this);
        b.setText("Load " + fileName);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArActivity.startWithSetup(chooser.this, new ModelLoaderSetup(
                        finalFileName, finalTextureName));
            }
        });
        return b;
    }
    */

    private View newSetButton(JSONObject set) throws JSONException {

        String jsonVal = set.getString("naziv");
        Log.i(LOG_TAG, "naziv: " + jsonVal);
        Button b = new Button(this);
        b.setText(jsonVal);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        return b;
    }

}