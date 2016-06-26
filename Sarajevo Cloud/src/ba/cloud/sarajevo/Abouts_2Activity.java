package ba.cloud.sarajevo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import commands.system.CommandDeviceVibrate;

/**
 * Created by dinok on 6/21/2016.
 */
public class Abouts_2Activity extends Activity {
    private static final long VIBRATION_DURATION_IN_MS = 20;

    private CommandDeviceVibrate _vibrateCommand;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abouts_2);
        _vibrateCommand = new CommandDeviceVibrate(getApplication(), VIBRATION_DURATION_IN_MS);

        ImageView imgview = (ImageView) findViewById(R.id.ivBack);
        //imgview.setScaleType(ImageView.ScaleType.FIT_XY);

        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _vibrateCommand.execute();
                finish();
                //getActivity().finish();
            }
        });

        TextView txt7 = (TextView)findViewById(R.id.textView7);
        txt7.setText(Html.fromHtml("SARAJEVO CLOUD JE DIO <a href='http://www.goethe.de/ins/ba/bs/sar/ver.cfm?fuseaction=events.detail&event_id=20764379'>ACTOPOLIS SARAJEVO LABARATORIJA</a>, U ORGANIZACIJI GOETHE INSTITUTA U BIH"));
        txt7.setMovementMethod(LinkMovementMethod.getInstance());
    }
}