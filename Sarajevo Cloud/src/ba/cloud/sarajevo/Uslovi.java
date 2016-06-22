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

        /*ImageView ivUsloviKoristenjaBack = (ImageView) findViewById(R.id.uslovi_koristenja_back);
        ivUsloviKoristenjaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UsloviKoristenja.this,Login.class));
            }
        });*/
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Login.class ));
        finish();
    }
}
