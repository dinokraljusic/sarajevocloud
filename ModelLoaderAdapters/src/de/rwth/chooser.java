package de.rwth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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


    }

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