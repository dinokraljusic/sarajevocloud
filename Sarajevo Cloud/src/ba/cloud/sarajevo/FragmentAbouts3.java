package ba.cloud.sarajevo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.rwth.ModelLoaderSetup;
import system.ArActivity;

/**
 * Created by dinok on 6/21/2016.
 */
public class FragmentAbouts3 extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*TextView tvPrvi = (TextView) getActivity().findViewById(R.id.tvPrvi);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");

        if(tvPrvi != null) tvPrvi.setTypeface(type);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(de.rwth.R.layout.fragment_about3, container, false);

        /*TextView tvPrvi = (TextView) rootView.findViewById(R.id.tvPrvi);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");
        if(tvPrvi != null) tvPrvi.setTypeface(type);*/
        ImageView iv = (ImageView) rootView.findViewById(de.rwth.R.id.fragment_about3_iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArActivity.startWithSetup(getActivity(), new ModelLoaderSetup());
                getActivity().finish();
            }
        });

        return rootView;
    }
}