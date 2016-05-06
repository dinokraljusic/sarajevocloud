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
import java.util.Map;

/**
 * Created by MiniP on 3/27/2016.
 */
public class DobavljacPiktograma extends AsyncTask<String, String, String>{
    public static String LOG_TAG = "DobavljacPiktograma";

    private final Activity _activity;
    private final Context _context;
    private final ProgressBar _progressBar;

    private ArrayList<Piktogram> _lista;
    ArrayAdapter<View> _adapter;
    int n = 0;
    String[] _parametri;

    public DobavljacPiktograma(Activity activity, ArrayAdapter<View> adapter, ProgressBar prog){
        this._activity = activity;
        this._context = activity.getApplicationContext();
        this._adapter = adapter;
        this._lista = new ArrayList<>();
        this._progressBar = prog;
    }

    @Override
    protected void onPreExecute(){
        _lista.clear();
        _adapter.clear();
        _adapter.notifyDataSetChanged();
        _progressBar.setVisibility(View.VISIBLE);
        mapaPiktogrami = new ArrayList<>();
    }

    @Override
    protected String doInBackground(String... strings) {
        _parametri = strings;


        try {
            String json = Utility.GET(Spremnik.getInstance().getPiktogramServiceAddress() + "?setid=" + strings[1]);
            Log.d(LOG_TAG, "json: " + json);
            JSONArray setovi = new JSONArray(json);
            for (n = 0; n < setovi.length(); n++) {

                JSONObject set = setovi.getJSONObject(n);
                Log.d(LOG_TAG, "set(" + n + "): " + set.toString());
                _lista.add(new Piktogram(set.getInt("id"),
                                         set.getString("naziv"),
                                         set.getString("put_piktogram"),
                                         set.getString("put_tekstura"))
                          );
                String tmp = set.getString("naziv") + "." + Utility.getEkstension(set.getString("put_piktogram"));
                final String finalFileName = Utility.downloadAndSaveFile(_context, set.getInt("id"), false, tmp, LOG_TAG);


                tmp = set.getString("naziv") + "."+Utility.getEkstension(set.getString("put_tekstura"));
                final String finalTextureName = Utility.downloadAndSaveFile(_context, set.getInt("id"), true, tmp, LOG_TAG);


                Par par = new Par(set.getString("naziv"),  finalFileName, finalTextureName);
                mapaPiktogrami.add(par);

            }
        } catch (org.json.JSONException je) {
            Log.d(LOG_TAG, "Error parsing response to JSONArray.", je);
            _lista.add(new Piktogram(-1,
                    "kutija",
                    "put_piktogram.obj",
                    "put_tekstura.jpg"));
            Par par = new Par("kutija",  "put_piktogram.obj", "put_tekstura.jpg");
            mapaPiktogrami.add(par);
            n++;
        } catch (Throwable ex) {
            Log.d(LOG_TAG, ex.getMessage(), ex);
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG);
            _lista.add(new Piktogram(-1,
                            "kutija",
                            "put_piktogram.obj",
                            "put_tekstura.jpg")
            );
            Par par = new Par("kutija",  "put_piktogram.obj", "put_tekstura.jpg");
            mapaPiktogrami.add(par);
            n++;
        }
        return null;
    }

    ArrayList<Par> mapaPiktogrami;

    private class Par{
        public String Key;
        public String Value;
        public String Tekstura;

        Par(String key, String value, String tekstura){
            Key= key;
            Value=value;
            Tekstura = tekstura;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        for( int i = 0; i<n; i++) {
            final int j =i;
            final Piktogram p = _lista.get(i);
            ListItemButton b = new ListItemButton(_context, p.getNaziv());
            b.setText(p.getNaziv());
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {


                        Spremnik.getInstance().setObjekatPut(mapaPiktogrami.get(j).Value);
                        Spremnik.getInstance().setTeksturaPut(mapaPiktogrami.get(j).Tekstura);
                        //Log.i(LOG_TAG, "final names :" + finalFileName + ", " + finalTextureName);

                        _progressBar.setVisibility(View.VISIBLE);
                        _adapter.clear();
                        _adapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    _activity.finish();
                }
            });
            _adapter.add(b);
        }
        _progressBar.setVisibility(View.GONE);
        _adapter.notifyDataSetChanged();
    }
}
