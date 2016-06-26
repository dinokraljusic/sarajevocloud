package ba.cloud.sarajevo.GuiElements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import commands.Command;
import de.rwth.R;

/**
 * Created by MiniP on 6/11/2016.
 */
public class MessageBox extends LinearLayout implements IGuiElement {

    // region -- Fields --

    private final int _width;
    private final Typeface _defaultFont;
    private final TextView _txtBox;
    List<Command> _onShownEvents, _onHideCommands;

    //endregion

    //region == Constructors ==

    public MessageBox(Context context, int width) {
        super(context);

        _width = width;

        _onShownEvents = new ArrayList<>();
        _onHideCommands = new ArrayList<>();
        _defaultFont = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/ACTOPOLIS.otf");

        _txtBox = new TextView(context);
        _txtBox.setPadding(0, 13, 0, 17);
        _txtBox.setTypeface(_defaultFont);
        _txtBox.setTextColor(getResources().getColor(R.color.zuta));
        _txtBox.setWidth(width);

        addView(_txtBox);
        setBackgroundColor(Color.argb(128, 0, 0, 0));
        hideMessage();
    }

    //endregion

    //region == Metods ==

    /**
     * @deprecated Use {@link #showMessage(String)}
     */
    @Deprecated
    @Override
    public void setVisibility(int visibility) {
    }

    private void hideMessage() {
        for (Command command :_onHideCommands) {
            command.execute();
        }
        super.setVisibility(GONE);
    }

    public void showMessage(final String text) {
        if (this.getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMessage(text);
                }
            }, 500);
        } else {
            for (Command command : _onShownEvents) {
                command.execute();
            }
            _txtBox.setText(text);
            super.setVisibility(VISIBLE);
            final MessageBox ths = this;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    hideMessage();
                }
            }, 4500);
        }
        _txtBox.setText(text);
    }

    @Override
    public void registerOnShowCommand(Command command) {
        if (command != null)
            _onShownEvents.add(command);
    }

    @Override
    public void unRegisterAllOnShowCommands() {
        _onShownEvents.clear();
    }

    @Override
    public void unRegisterOnShowCommand(Command command) {
        if (command != null && _onShownEvents.contains(command)) _onShownEvents.remove(command);
    }

    @Override
    public void registerOnHideCommand(Command command) {
        if (command != null) _onHideCommands.add(command);
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

    //endregion
}