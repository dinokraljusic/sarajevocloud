package de.rwth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MiniP on 3/26/2016.
 */
public class Utility {
    private static String LOG_TAG = "Utility";

    public static List<Piktogram> GetNewPiktograms(Context context) {
        List<Piktogram> dodaniPiktogrami = new ArrayList<>();
        int lastId = Integer.parseInt(Spremnik.getInstance().getLastId());
        try {
            String json = Utility.GET(Spremnik.getInstance().getPiktogramLokacijaServiceAddress()
                    + "?lastId=" + Integer.toString(lastId));
            Log.d(LOG_TAG, "json: " + json);
            JSONArray piktogrami = new JSONArray(json);
            for (int n = 0; n < piktogrami.length(); n++) {

                JSONObject piktogram = piktogrami.getJSONObject(n);
                Log.d(LOG_TAG, "piktogram(" + n + "): " + piktogram.toString());
                Piktogram pikrogramObj = new Piktogram(
                        piktogram.getInt("id"),
                        piktogram.getString("naziv"),
                        piktogram.getString("put_piktogram"),
                        piktogram.getString("put_tekstura")
                );
                if (pikrogramObj.getId() > lastId)
                    lastId = pikrogramObj.getId();
                String tmp = piktogram.getString("naziv") + "." + Utility.getEkstension(piktogram.getString("put_piktogram"));
                pikrogramObj.setPutPiktogram(Utility.downloadAndSaveFile(context, piktogram.getInt("id"), false, tmp, LOG_TAG));

                tmp = piktogram.getString("naziv") + "." + Utility.getEkstension(piktogram.getString("put_tekstura"));
                pikrogramObj.setPutTekstura(Utility.downloadAndSaveFile(context, piktogram.getInt("id"), true, tmp, LOG_TAG));

                dodaniPiktogrami.add(pikrogramObj);
            }
        } catch (Throwable ex) {
            Log.d(LOG_TAG, ex.getMessage(), ex);
        }
        Spremnik.getInstance().setLastId(Integer.toString(lastId));
        return dodaniPiktogrami;
    }

    public static String downloadAndSaveFile(Context context, int id, boolean slika, String fileName, String LOG_TAG) {
        try {
            String PATH = null;
            FileOutputStream fos = null;
            String url_string = Spremnik.getInstance().getDownloadServiceAddress();

            File file = context.getDir(null, Context.MODE_WORLD_READABLE);
            //File file = context.getFilesDir();
            if (!file.mkdir()) {
                Log.d(LOG_TAG, "Could not create directories");
            }
            fileName = file.getAbsolutePath() + "/" + fileName;
            if (new File(fileName).exists()) {
                Log.i(LOG_TAG, "returning existing file");
                return fileName;
            }

            URL url = new URL(url_string + "?id=" + id + (slika ? "&slika" : ""));

            //region OLD CODE FOR DOWNLOAD - does not work in LG
			/*
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			fos = new FileOutputStream(fileName);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			int l1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1);
				l1+=len1;
				if(len1<1024)
					Log.i(LOG_TAG, "temp Length = " + len1);
			}
			Log.v(LOG_TAG, "streamLength = " + l1);
			fos.flush();
			fos.close();
			is.close();
			*/
            //endregion

            /*File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/");
            if (dir.exists() == false) {
                dir.mkdirs();
            }
*/
            long startTime = System.currentTimeMillis();
            Log.d(LOG_TAG, "download url:" + url);
            Log.d(LOG_TAG, "download file name:" + fileName);

            URLConnection uconn = url.openConnection();
            //uconn.setReadTimeout(TIMEOUT_CONNECTION);
            //uconn.setConnectTimeout(TIMEOUT_SOCKET);

            InputStream is = null;
            try {
                is = uconn.getInputStream();
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            BufferedInputStream bufferinstream = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bufferinstream.read()) != -1) {
                baf.append((byte) current);
            }

            fos = new FileOutputStream(fileName);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
            Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + "sec");

            Log.v(LOG_TAG, "stream closed");
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error: " + e);
            //Toast.makeText((Context) this, e.toString(), Toast.LENGTH_LONG)
            //        .show();
            String[] lista = fileName.split("/");
            if (lista != null)
                return "data/" + lista[lista.length - 1];
        }
        return fileName.substring(0);
    }

    public static String getEkstension(String path) {
        int extId = path.lastIndexOf('.') + 1;
        Log.i(LOG_TAG, "put_piktogram ext index: " + extId);
        if (extId > 0)
            return path.substring(extId);
        return path;
    }

    public static void SaveLog(String url, String value) {
        // Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("val", value));
        POST(url, nameValuePairs);
    }

    public static String GET(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Accept", "application/json");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                return json;
            } else {
                Log.e(LOG_TAG, "Failed to download file. StatusCode: " + statusCode);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String POST(String strUrl, List<NameValuePair> arguments) {
        String TAG = "POST_UTIL";
        HttpClient httpclient = new DefaultHttpClient();
        try {
            URL url = new URL(strUrl);
            String param = "";
            for (NameValuePair nvp : arguments) {
                param += nvp.getName() + "=" + nvp.getValue();
            }
            Log.d(TAG, "param:" + param);

            // Open a connection using HttpURLConnection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setReadTimeout(7000);
            con.setConnectTimeout(7000);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("POST");
            con.setFixedLengthStreamingMode(param.getBytes().length);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Connection", "close");

            // Send
            PrintWriter out = new PrintWriter(con.getOutputStream());
            out.print(param);
            out.close();

            con.connect();

            BufferedReader in = null;
            if (con.getResponseCode() != 200) {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                Log.d(TAG, "!=200: " + in);
            } else {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                Log.d(TAG, "POST request send successful: " + in);
                String resp = "", line = in.readLine();
                while (line != null)
                    resp += (line = in.readLine());
                Log.d(LOG_TAG, resp);
                return resp;
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception");
            e.printStackTrace();
            return null;
        }

        return "";
    }

    public static boolean SaveThisPiktogram(Location location) {
        try {
            String url = Spremnik.getInstance().getPiktogramLokacijaServiceAddress();
            List<NameValuePair> nvps = new ArrayList<>(4);
            nvps.add(new KeyValuePair("piktogramId", Spremnik.getInstance().getCurrId()));
            nvps.add(new KeyValuePair("userId", Spremnik.getInstance().getCurrId()));
            nvps.add(new KeyValuePair("long", Double.toString(location.getLongitude())));
            nvps.add(new KeyValuePair("lat", Double.toString(location.getLatitude())));

            return !POST(url, nvps).isEmpty();
        } catch (Throwable t) {
            Log.d("ModelLoader-postingPi", t.toString());
        }
        return false;
    }

    public static void uploadScreenshoot(final ModelLoaderSetup caller, String sourceFileUri, final Location location) {
        new AsyncTask<String, Void, String>(){
            InputStream inputStream;

            @Override
            protected String doInBackground(String... params) {
                File image = new File(params[0]);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
                byte[] byte_arr = stream.toByteArray();
                String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("image", image_str));
                nameValuePairs.add(new BasicNameValuePair("imageName", image.getName()));
                nameValuePairs.add(new BasicNameValuePair("userId", Spremnik.getInstance().getCurrId()));
                nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(location.getLatitude())));
                nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(location.getLongitude())));

                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost(Spremnik.getInstance().getUploadPictureServiceAddress());
                            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                            HttpResponse response = httpclient.execute(httppost);
                            final String the_string_response = convertResponseToString(response);
                            caller.getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    caller.showMessage("FOTOGRAFIJA POHRANjENA");
                                }
                            });

                        } catch (final Exception e) {
                            caller.getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    caller.showMessage("GRESKA PRI POSTAVLjANJU FOTOGRAFIJE");
                                }
                            });
                            System.out.println("Error in http connection " + e.toString());
                        }
                    }
                });
                t.start();
                return "";
            }

            public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException {

                String res = "";
                StringBuffer buffer = new StringBuffer();
                inputStream = response.getEntity().getContent();
                final int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..

                if (contentLength < 0){
                }
                else{
                    byte[] data = new byte[512];
                    int len = 0;
                    try
                    {
                        while (-1 != (len = inputStream.read(data)) )
                        {
                            buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        inputStream.close(); // closing the stream…..
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    res = buffer.toString();     // converting stringbuffer to string…..

                    final String finalRes = res;
                    //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
                }
                return res;
            }
        }.execute(sourceFileUri);
    }

    public static int getUserId(String userName) {
        try {
            JSONObject jsonObj = new JSONObject(GET(Spremnik.getInstance().getUserServiceAddress() + "?ime=" + userName));
            return jsonObj.getInt("id");
        }catch (JSONException jEx){
            jEx.printStackTrace();
        }
        return  0;
    }
}

class KeyValuePair implements NameValuePair{

    String _name,
        _value;

    public KeyValuePair(String name, String value) {
        _name = name;
        _value = value;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getValue() {
        return _value;
    }
}