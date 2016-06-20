package de.rwth.GuiElements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import commands.Command;
import de.rwth.R;

/**
 * Created by MiniP on 6/12/2016.
 */
public class LoaderBar extends LinearLayout implements IGuiElement {

    //region == Fields ==

    private final List<Command> _onShownCommands, _onHideCommands;
    private final ProgressBar _loader;
    private final TextView _txtBox;

    //endregion

    //region == Constructors ==

    public LoaderBar(Context context, Typeface typeFace) {
        super(context);

        _onShownCommands=new ArrayList<>();
        _onHideCommands = new ArrayList<>();

        _loader = new ProgressBar(context);
        _txtBox = new TextView(context);
        _txtBox.setTypeface(typeFace);
        _txtBox.setTextColor(getResources().getColor(R.color.zuta));
        _txtBox.setPadding(10,13,0,15);

        setBackgroundColor(Color.argb(128, 0, 0, 0));
        setOrientation(LinearLayout.HORIZONTAL);
        addView(_loader);
        addView(_txtBox);
        hideLoader();
    }

    //endregion

    //region == Methods ==


    /**
     * @deprecated Use {@link #showLoader(String)}
     */
    @Deprecated
    @Override
    public void setVisibility(int visibility) {
        //super.setVisibility(visibility);
    }

    public void hideLoader() {
        for (Command c : _onHideCommands) {
            c.execute();
        }
        super.setVisibility(GONE);
    }

    public void showLoader(String text){
        for (Command c : _onShownCommands) {
            c.execute();
        }
        _txtBox.setText(text);
        super.setVisibility(VISIBLE);
    }

    @Override
    public void registerOnShowCommand(Command command) {
        if (command != null)
            _onShownCommands.add(command);
    }
    @Override
    public void unRegisterAllOnShowCommands() {
        _onShownCommands.clear();
    }
    @Override
    public void unRegisterOnShowCommand(Command command) {
        if (command != null && _onShownCommands.contains(command))
            _onShownCommands.remove(command);
    }

    @Override
    public void registerOnHideCommand(Command command) {
        if (command != null)
            _onHideCommands.add(command);
    }
    @Override
    public void unRegisterAllOnHideCommands() {
        _onHideCommands.clear();
    }
    @Override
    public void unRegisterOnHideCommand(Command command) {
        if (command != null && _onHideCommands.contains(command))
            _onHideCommands.remove(command);
    }

}
