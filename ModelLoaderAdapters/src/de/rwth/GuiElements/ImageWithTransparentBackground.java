package de.rwth.GuiElements;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import commands.Command;
import commands.system.CommandDeviceVibrate;

/**
 * Created by MiniP on 6/11/2016.
 */
public class ImageWithTransparentBackground extends ImageView {

    private static final long VIBRATION_DURATION_IN_MS = 20;
    //region == Fields ==
    private final CommandDeviceVibrate _vibrateCommand;
    private final List<Command> _preExecuteCommands,
            _postExecutedComands;
    private Command _command;
    //endregion

    //region == Constructors ==

    public ImageWithTransparentBackground(final Context context, @DrawableRes final int normalImageId,
                                          @DrawableRes final int clickedImageId, final Command command) {
        super(context);
        _preExecuteCommands = new ArrayList<>();
        _postExecutedComands = new ArrayList<>();
        _vibrateCommand = new CommandDeviceVibrate(context, VIBRATION_DURATION_IN_MS);
        if(command!=null)_command = command;
        this.setBackgroundColor(0);
        this.setImageResource(normalImageId);
        super.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImageResource(clickedImageId);
                if (_vibrateCommand != null) _vibrateCommand.execute();

                for (Command command : _preExecuteCommands) { command.execute(); }

                if (_command != null) _command.execute();
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                ((Activity) context).runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                setImageResource(normalImageId);
                                                for (Command command : _postExecutedComands) { command.execute(); }
                                            }
                                        });
                            }
                        }, 150);
            }
        });
    }

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

    //region == Methods ==

    //endregion
}
