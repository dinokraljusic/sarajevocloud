package de.rwth.GuiElements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import commands.Command;
import de.rwth.R;

/**
 * Created by MiniP on 6/11/2016.
 */
public class DialogBox extends LinearLayout implements IGuiElement {

    //region == Fields ==

    private final Command _yesButtonCommand, _noButtonCommand;
    private final List<Command> _onShownCommands, _onHideCommands;

    private final Context _context;
    private final Typeface _defaultFont;
    private final TextView _textBox;
    private final LinearLayout _buttons;
    private final ImageWithTransparentBackground _yesButton, _noButton;

    //endregion

    //region == Constructors ==

    public DialogBox(Context context, int normalYesButtonImageId, int clickedYesButtonImageId
            , int normalNoButtonImageId, int clickedNoButtonImageId) {
        super(context);
        _context = context;
        _yesButtonCommand = null;
        _noButtonCommand = null;
        _onShownCommands = new ArrayList<>();
        _onHideCommands = new ArrayList<>();

        _defaultFont = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/ACTOPOLIS.otf");

        _textBox = new TextView(context);
        //_textBox.setTypeface(_defaultFont);
        _textBox.setPadding(10, 13, 0, 17);
        _textBox.setTextColor(getResources().getColor(R.color.zuta));
        _textBox.setGravity(Gravity.CENTER);

        _buttons = new LinearLayout(context);
        _buttons.setOrientation(HORIZONTAL);
        _buttons.setGravity(Gravity.CENTER);

        _yesButton = new ImageWithTransparentBackground(context, normalYesButtonImageId, clickedYesButtonImageId, null);
        _yesButton.setPadding(25, 25, 35, 30);
        _noButton = new ImageWithTransparentBackground(context, normalNoButtonImageId, clickedNoButtonImageId, null);
        _noButton.setPadding(25, 25, 35, 30);

        _buttons.addView(_yesButton);
        _buttons.addView(_noButton);
        setOrientation(VERTICAL);
        addView(_textBox);
        addView(_buttons);
        super.setVisibility(GONE);
        setBackgroundColor(Color.argb(128, 0, 0, 0));
        hideDialog();
    }

    //endregion

    //region methods

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

    /**
     * @deprecated Use {@link #showDialog(String, Command, Command)}
     */
    @Deprecated
    @Override
    public void setVisibility(int visibility) {
        //super.setVisibility(visibility);
    }

    public void hideDialog() {
        for (Command c : _onHideCommands) {
            c.execute();
        }
        super.setVisibility(GONE);
    }

    public void showDialog(final String text, final Command yesCommand, final Command noCommand) {
        if (getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDialog(text, yesCommand, noCommand);
                }
            }, 500);
        } else {
            ViewGroup row = (ViewGroup)getParent();
            for (int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
                View view = row.getChildAt(itemPos);
                view.setVisibility(GONE);
            }
            _textBox.setText(text);
            for (Command r : _onShownCommands) {
                r.execute();
            }
            _yesButton.unRegisterAllPreExecutedCommands();
            _yesButton.unRegisterAllPostExecutedCommands();
            _yesButton.registerPostExecutedCommand(new Command() {
                @Override
                public boolean execute() {
                    hideDialog();
                    return false;
                }
            });
            _yesButton.setCommand(yesCommand);

            _noButton.unRegisterAllPreExecutedCommands();
            _noButton.unRegisterAllPostExecutedCommands();
            _noButton.registerPreExecutedCommand(noCommand);
            _noButton.registerPostExecutedCommand(new Command() {
                @Override
                public boolean execute() {
                    hideDialog();
                    return false;
                }
            });
            _noButton.setCommand(noCommand);
            super.setVisibility(VISIBLE);
        }

        //endregion
    }
}