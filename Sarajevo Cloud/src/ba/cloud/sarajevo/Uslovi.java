package ba.cloud.sarajevo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

/**
 * Created by dinok on 6/21/2016.
 */
public class Uslovi extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uslovi);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Login.class ));
        finish();
    }
}
