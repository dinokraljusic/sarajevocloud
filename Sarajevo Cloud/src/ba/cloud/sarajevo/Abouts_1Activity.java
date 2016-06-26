package ba.cloud.sarajevo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import commands.system.CommandDeviceVibrate;
import de.rwth.Spremnik;

/**
 * Created by dinok on 6/21/2016.
 */
public class Abouts_1Activity extends Activity {

    private static final long VIBRATION_DURATION_IN_MS = 20;

    private CommandDeviceVibrate _vibrateCommand;
    int _timesBackPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abouts_1);
        _vibrateCommand = new CommandDeviceVibrate(getApplication(), VIBRATION_DURATION_IN_MS);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ImageView iv_about_nextSlide = (ImageView) findViewById(R.id.iv_about_nextSlide);
        iv_about_nextSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _vibrateCommand.execute();
                Intent i = new Intent(Abouts_1Activity.this, Abouts_3Activity.class);
                startActivityForResult(i, 1);
            }
        });
        TextView tv = (TextView) findViewById(R.id.textView13);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _vibrateCommand.execute();
                startActivity( new Intent( Abouts_1Activity.this, Abouts_2Activity.class ) );
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(Spremnik.getInstance().getPreviousActivity().toLowerCase().equals("modelloaderssetup") || _timesBackPressed > 1){
                finish();// return super.onKeyDown(a, keyCode, event);
                return;
        }
        Toast.makeText(getApplicationContext(), "Press back once more to exit", Toast.LENGTH_SHORT).show();
        _timesBackPressed++;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Boolean closeSelf = data.getBooleanExtra("closeSelf", false);
                if(closeSelf)
                    finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

}
