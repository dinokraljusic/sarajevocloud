package ba.cloud.sarajevo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import commands.Command;
import commands.system.CommandDeviceVibrate;
import de.rwth.ModelLoaderSetup;
import de.rwth.Spremnik;
import system.ArActivity;

/**
 * Created by dinok on 6/24/2016.
 */
public class Abouts_3Activity extends Activity {

    private static final long VIBRATION_DURATION_IN_MS = 20;

    private CommandDeviceVibrate _vibrateCommand;
    ImageView iv;
    int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abouts_3);
        _vibrateCommand = new CommandDeviceVibrate(getApplication(), VIBRATION_DURATION_IN_MS);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        iv = (ImageView) findViewById(R.id.abouts_3_next);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _vibrateCommand.execute();
                iv.setImageResource(R.drawable.black_arrow_green);
                iv.invalidate();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv.setImageResource(R.drawable.black_arrow_yellow);
                            }
                        });
                    }
                }, 500);

                if (!Spremnik.getInstance().getPreviousActivity().toLowerCase().equals("modelloaderssetup"))
                {
                    ArActivity.startWithSetup(Abouts_3Activity.this, new ModelLoaderSetup(
                            new Command() {
                                @Override
                                public boolean execute() {
                                    Intent i = new Intent(Abouts_3Activity.this, Abouts_1Activity.class);
                                    startActivity(i);
                                    return false;
                                }
                            }
                    ));
                    finish();
                    return;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("closeSelf", true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}