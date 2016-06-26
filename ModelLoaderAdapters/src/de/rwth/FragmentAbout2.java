package de.rwth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import commands.Command;
import system.ArActivity;

/**
 * Created by dinok on 6/21/2016.
 */
public class FragmentAbout2 extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*TextView tvPrvi = (TextView) getActivity().findViewById(R.id.tvPrvi);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");

        if(tvPrvi != null) tvPrvi.setTypeface(type);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_about2_2, container, false);

        /*TextView tvPrvi = (TextView) rootView.findViewById(R.id.tvPrvi);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");
        if(tvPrvi != null) tvPrvi.setTypeface(type);*/
        /*TextView tv4 = (TextView) rootView.findViewById(R.id.tv4_fragment_about1);
        tv4.setText(Html.fromHtml("SARAJEVO CLOUD JE DIO <font color=\"blue\">  ACTOPOLIS SARAJEVO LABORATORIJA </font>, U ORGANIZACIJI GOETHE INSTITUTA U BIH") );
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.goethe.de/ins/ba/bs/sar/ver.cfm?fuseaction=events.detail&event_id=20764379") ));
            }
        });*/
        ImageView imgview = (ImageView) rootView.findViewById(R.id.ivOK);
        imgview.setScaleType(ImageView.ScaleType.FIT_XY);

        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArActivity.startWithSetup(getActivity(), new ModelLoaderSetup(
                        new Command() {
                            @Override
                            public boolean execute() {
                                startActivity(new Intent(getActivity(), AboutActivity.class));
                                return true;
                            }
                        }));
            }
        });

        return rootView;
    }
}