package de.rwth;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by MiniP on 3/25/2016.
 */
public class DobavljacSetova extends AsyncTask<String, String, String> {
    public final static String LOG_TAG = "DobavljacSetova";

    private final Activity _activity;
    private final Context _context;
    private final ProgressBar _progressBar;

    private ArrayList<Set> _lista = null;
    ArrayAdapter<View> _adapter;
    String[] _params;

    int n = 0;

    public DobavljacSetova(Activity activity, ArrayAdapter<View> adapter, ProgressBar prog){
        this._activity = activity;
        this._context = activity.getApplicationContext();
        this._adapter = adapter;
        this._lista =  new ArrayList<>();
        this._progressBar = prog;
    }

    @Override
    protected void onPreExecute() {
        _lista.clear();
        _adapter.clear();
        _progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        _params = strings;

        try {
            String json = Utility.GET(Spremnik.getInstance().getSetServiceAddress());
            Log.d(LOG_TAG, "json: " + json);
            JSONArray setovi = new JSONArray(json);
            for (n = 0; n < setovi.length(); n++) {
                JSONObject set = setovi.getJSONObject(n);
                Log.d(LOG_TAG, "set(" + n + "): " + set.toString());
                _lista.add(new Set(set.getInt("id"), set.getString("naziv")));
            }
        } catch (org.json.JSONException je) {
            Log.d(LOG_TAG, "Error parsing response to JSONArray.", je);
            _lista.add(new Set(-1, "Privremeni Set"));
            n++;
        } catch (Throwable ex) {
            Log.d(LOG_TAG, "error: " + ex.getMessage(), ex);
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG);
            _lista.add(new Set(-1, "Privremeni Set"));
            n++;
        }
        return _params[0];
    }

    @Override
    protected void onPostExecute(String result){
        for(int i = 0; i<n; i++) {
            final Set s = _lista.get(i);
            ListItemButton b = new ListItemButton(_context, s.getNaziv());
            b.setText(s.getNaziv());
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DobavljacPiktograma(_activity, _adapter, _progressBar).execute(_params[0], Integer.toString(s.getId()), "2");
                }
            });
            _adapter.add(b);
        }
        _progressBar.setVisibility(View.GONE);
        _adapter.notifyDataSetChanged();
        Log.d("DobavljacSetova", "Adapter obavjesten! n = " + n);
    }
}