package de.rwth;

import android.content.Context;
import android.widget.Button;

/**
 * Created by MiniP on 3/24/2016.
 */
public class ListItemButton extends Button {

    private String _text;

    public String getText(){
        return _text;
    }
    public void setText(String text){
        _text = text;
    }

    public ListItemButton(Context context, String text) {
        super(context);
        _text = text;
    }

    @Override
    public String toString() {
        return _text;
    }
}
