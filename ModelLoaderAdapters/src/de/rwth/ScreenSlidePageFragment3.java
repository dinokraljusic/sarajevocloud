package de.rwth;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import system.ArActivity;

/**
 * Created by dinok on 5/4/2016.
 */
public class ScreenSlidePageFragment3 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page3, container, false);

        TextView tvFotografisanje = (TextView) rootView.findViewById(R.id.tvFotografisanje);
        TextView tvDodavanjeObjekta = (TextView) rootView.findViewById(R.id.tvDodavanjeObjekta);
        TextView tvUklanjanjeObjekata = (TextView) rootView.findViewById(R.id.tvUklanjanjeObjekata);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");

        if(tvFotografisanje != null) tvFotografisanje.setTypeface(type);
        if(tvDodavanjeObjekta != null) tvDodavanjeObjekta.setTypeface(type);
        if(tvUklanjanjeObjekata != null) tvUklanjanjeObjekata.setTypeface(type);

        ImageView ivOK = (ImageView) rootView.findViewById(R.id.ivOK);
        ivOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArActivity.startWithSetup(getActivity(), new ModelLoaderSetup(null));
                //Intent i = new Intent(getContext(), Login.class);
                //startActivity(i);
            }
        });

        return rootView;
    }

    public void StartAR(View view){
        ArActivity.startWithSetup(getActivity(), new ModelLoaderSetup(null));
    }
}