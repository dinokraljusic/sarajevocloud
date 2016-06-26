package ba.cloud.sarajevo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dinok on 6/25/2016.
 */
public class ViseOProjektuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abouts_2);

        TextView tv4 = (TextView) findViewById(R.id.tv4_vise_o);
        tv4.setText(Html.fromHtml("SARAJEVO CLOUD JE DIO <font color=\"blue\">  ACTOPOLIS SARAJEVO LABORATORIJA </font>, U ORGANIZACIJI GOETHE INSTITUTA U BIH") );
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.goethe.de/ins/ba/bs/sar/ver.cfm?fuseaction=events.detail&event_id=20764379") ));
            }
        });

        ImageView ivok = (ImageView) findViewById(R.id.ivRight);
        ivok.setRotation(180.0f);
        ivok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
