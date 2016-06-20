package de.rwth.GuiElements;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import commands.Command;
import commands.system.CommandDeviceVibrate;

/**
 * Created by MiniP on 6/12/2016.
 */
public class ClickableTextView extends TextView {
    private static final long VIBRATION_DURATION_IN_MS = 20;
    //region == Fields ==
    private final CommandDeviceVibrate _vibrateCommand;
    private final List<Command> _preExecuteCommands,
            _postExecutedComands;
    private Command _command;
    //endregion

    //region == Constructors ==

    public ClickableTextView(final Context context, final String text, final Command command, final Typeface typeface,
                             @ColorRes final int normalColor, @ColorRes final int clickedColor) {
        super(context);
        _preExecuteCommands = new ArrayList<>();
        _postExecutedComands = new ArrayList<>();
        _vibrateCommand = new CommandDeviceVibrate(context, VIBRATION_DURATION_IN_MS);
        if(command!=null)_command = command;
        setText(text);
        setTypeface(typeface);
        super.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextColor(getResources().getColor(clickedColor));
                if (_vibrateCommand != null) _vibrateCommand.execute();

                for (Command command : _preExecuteCommands) {
                    command.execute();
                }

                if (_command != null) _command.execute();
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                ((Activity) context).runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                setTextColor(getResources().getColor(normalColor));
                                                for (Command command : _postExecutedComands) {
                                                    command.execute();
                                                }
                                            }
                                        });
                            }
                        }, 150);
            }
        });
    }

    //endregion

    //region == Methods ==

    /*
        * @deprecated use {@link #setCommand} instead
        * */
    @Deprecated
    @Override
    public void setOnClickListener(OnClickListener l) {

    }

    public void setCommand(Command command){
        if(command != null) {
            _command = command;
        }
    }
    public void removeCommand(){
        _command = null;
    }
    public Command getCommand(){ return _command; }

    public void registerPreExecutedCommand(Command command) { if (command != null) _preExecuteCommands.add(command); }
    public void unRegisterAllPreExecutedCommands() { _preExecuteCommands.clear(); }

    public void registerPostExecutedCommand(Command command) { if (command != null) _postExecutedComands.add(command); }
    public void unRegisterAllPostExecutedCommands() { _postExecutedComands.clear(); }

    //endregion
}
