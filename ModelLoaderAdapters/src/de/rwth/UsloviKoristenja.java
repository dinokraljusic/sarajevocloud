package de.rwth;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by dinok on 6/21/2016.
 */
public class UsloviKoristenja extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uslovi_koristenja);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*ImageView ivUsloviKoristenjaBack = (ImageView) findViewById(R.id.uslovi_koristenja_back);
        ivUsloviKoristenjaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UsloviKoristenja.this,Login.class));
            }
        });*/
    }
}
