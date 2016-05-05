package de.rwth;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by MiniP on 3/26/2016.
 */
public class Utility {
    private static String LOG_TAG = "Utility";

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

    public static String downloadAndSaveFile(Context context, int id, boolean slika, String fileName, String LOG_TAG) {
        try {
            String PATH = null;
            FileOutputStream fos = null;
            String url_string = Spremnik.getInstance().getDownloadServiceAddress();

            File file = context.getDir("", context.MODE_WORLD_READABLE);
            if (!file.mkdir()) {
                Log.d(LOG_TAG,"Could not create directories" );
            }
            fileName = file.getAbsolutePath() + "/" + fileName;
            if(new File(fileName).exists()){
            	Log.i(LOG_TAG, "returning existing file");
            	return  fileName;
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
            try{
                is = uconn.getInputStream();
            }
            catch(Exception exc){
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

    public static String getEkstension(String path){
        int extId = path.lastIndexOf('.') + 1;
        Log.i(LOG_TAG, "put_piktogram ext index: " + extId);
        if (extId > 0)
            return path.substring(extId);
        return  path;
    }
}
