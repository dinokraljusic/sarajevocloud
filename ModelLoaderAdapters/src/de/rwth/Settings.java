package de.rwth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by MiniP on 4/3/2016.
 */
public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        final EditText txtURL = (EditText)findViewById(R.id.txtURL);
        Button btnOK = (Button)findViewById(R.id.btnOK);

        txtURL.setText(Spremnik.getInstance().getUrl());
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtURL.getText().toString().trim().length() > 0)
                    Spremnik.getInstance().setURL(txtURL.getText().toString());
                finish();
            }
        });
    }
}
