package de.rwth;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        return rootView;
    }
}