package ba.cloud.sarajevo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dinok on 5/4/2016.
 */
public class ScreenSlidePageFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tvPrvi = (TextView) getActivity().findViewById(R.id.tvPrvi);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");

        if(tvPrvi != null) tvPrvi.setTypeface(type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        TextView tvPrvi = (TextView) rootView.findViewById(R.id.tvPrvi);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ACTOPOLIS.otf");
        if(tvPrvi != null) tvPrvi.setTypeface(type);

        return rootView;
    }
}