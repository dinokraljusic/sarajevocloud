package ba.cloud.sarajevo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dinok on 6/21/2016.
 */
public class FragmentAbouts2 extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*TextView tvPrvi = (TextView) getActivity().findViewById(R.id.tvPrvi);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");

        if(tvPrvi != null) tvPrvi.setTypeface(type);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(ba.cloud.sarajevo.R.layout.fragment_about2, container, false);

        /*TextView tvPrvi = (TextView) rootView.findViewById(R.id.tvPrvi);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");
        if(tvPrvi != null) tvPrvi.setTypeface(type);*/
        TextView tv4 = (TextView) rootView.findViewById(R.id.tv4_fragment_about1);
        tv4.setText(Html.fromHtml("SARAJEVO CLOUD JE DIO <font color=\"blue\">  ACTOPOLIS SARAJEVO LABORATORIJA </font>, U ORGANIZACIJI GOETHE INSTITUTA U BIH") );
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.goethe.de/ins/ba/bs/sar/ver.cfm?fuseaction=events.detail&event_id=20764379") ));
            }
        });

        return rootView;
    }
}