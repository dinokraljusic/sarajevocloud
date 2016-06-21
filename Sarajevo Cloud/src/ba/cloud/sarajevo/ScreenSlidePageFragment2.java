package ba.cloud.sarajevo;

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
public class ScreenSlidePageFragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page2, container, false);

        TextView tvMojCloud = (TextView) rootView.findViewById(R.id.tvMojCloud);
        //TextView tvDrugiCloudovi = (TextView) rootView.findViewById(R.id.tvDrugiCloudovi);
        TextView tvSarajevoCloud = (TextView) rootView.findViewById(R.id.tvSarajevoCloud);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");

        if(tvMojCloud != null) tvMojCloud.setTypeface(type);
        //if(tvDrugiCloudovi != null) tvDrugiCloudovi.setTypeface(type);
        if(tvSarajevoCloud != null) tvSarajevoCloud.setTypeface(type);

        return rootView;
    }
}