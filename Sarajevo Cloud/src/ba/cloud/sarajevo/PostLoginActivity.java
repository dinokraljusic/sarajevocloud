package ba.cloud.sarajevo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import commands.Command;
import de.rwth.ModelLoaderSetup;
import de.rwth.Spremnik;
import de.rwth.Utility;
import system.ArActivity;

/**
 * Created by dinok on 6/21/2016.
 */
public class PostLoginActivity extends Activity {

    public static final String CREDENTIALS = "credentials.sc";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences settings = getSharedPreferences(CREDENTIALS, 0);
        Bundle b = getIntent().getExtras();
        int loginPokusaj = 0;
        if(b != null)
            loginPokusaj = b.getInt("loginPokusaj");

        final String userName = settings.getString("userName", "");
        if (!userName.isEmpty() && !userName.equals("")) {
            Spremnik.getInstance().setUserName(userName);
            String userId = "";
            try {
                userId = new GetUserIdAsync().execute(userName).get();
            } catch (Exception ex) {
                Intent i = new Intent(this, Login.class);
                b.clear();
                b.putInt("loginPokusaj", loginPokusaj + 1);
                i.putExtras(b);
                startActivity(i);
                finish();
            }
            if (userId == null || userId.isEmpty() || userId.equals("0")) {
                Intent i = new Intent(this, Login.class);
                b.clear();
                b.putInt("loginPokusaj", loginPokusaj+1);
                i.putExtras(b);
                startActivity(i);
                finish();
            } else {
                Spremnik.getInstance().setUserId(userId);
                ArActivity.startWithSetup(this, new ModelLoaderSetup(
                        new Command() {
                            @Override
                            public boolean execute() {
                                startActivity(new Intent(PostLoginActivity.this, Abouts_1Activity.class));
                                return true;
                            }
                        })
                );
                //finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    class GetUserIdAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return Integer.toString(Utility.getUserId(params[0]));
        }
    }
}