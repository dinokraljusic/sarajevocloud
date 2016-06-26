package de.rwth.GuiElements;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import commands.Command;
import de.rwth.R;

/**
 * Created by MiniP on 6/25/2016.
 */
public class CommandBar extends LinearLayout implements IGuiElement {

    //region == Fields ==

    private final List<Command> _onShownCommands, _onHideCommands;
    private final ImageWithTransparentBackground _rotirajButton, _albumButton, _restartButton,
            _fotoButton, _newPiktogramButton, _postaviPiktogramButton;

    //endregion

    //region == Constructors ==

    public CommandBar(Context context, int width) {
        super(context);

        _onShownCommands = new ArrayList<>();
        _onHideCommands = new ArrayList<>();
        setOrientation(LinearLayout.HORIZONTAL);
        setMinimumWidth(width);
        setGravity(Gravity.RIGHT);
        setWeightSum(4f);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
        //lp.gravity = Gravity.RIGHT;

        _rotirajButton = new ImageWithTransparentBackground(context, R.drawable.rotiraj_zuto, R.drawable.rotiraj_zeleno, null);
        _rotirajButton.setPadding(15, 15, 15, 15);
        _rotirajButton.setLayoutParams(lp);

        _albumButton = new ImageWithTransparentBackground(context, R.drawable.album_zuto, R.drawable.album_zeleno, null);
        _albumButton.setPadding(15, 15, 15, 15);
        _albumButton.setLayoutParams(lp);

        _restartButton = new ImageWithTransparentBackground(context, R.drawable.restart_zuto, R.drawable.restart_zeleno, null);
        _restartButton.setPadding(15, 15, 15, 15);
        _restartButton.setLayoutParams(lp);

        _fotoButton = new ImageWithTransparentBackground(context, R.drawable.foto_zuto, R.drawable.foto_zeleno, null);
        _fotoButton.setPadding(15, 15, 15, 15);
        _fotoButton.setLayoutParams(lp);

        _newPiktogramButton = new ImageWithTransparentBackground(context, R.drawable.postavi_zuto, R.drawable.postavi_zeleno, null);
        _newPiktogramButton.setPadding(15, 15, 15, 15);
        _newPiktogramButton.setLayoutParams(lp);

        _postaviPiktogramButton = new ImageWithTransparentBackground(context, R.drawable.postavi_zuto, R.drawable.postavi_zeleno, null);
        _postaviPiktogramButton.setPadding(15, 15, 15, 15);
        _postaviPiktogramButton.setLayoutParams(lp);

        addView(_albumButton);
        addView(_restartButton);
        addView(_fotoButton);
        addView(_newPiktogramButton);
        addView(_rotirajButton);
        addView(_postaviPiktogramButton);

        setBackgroundColor(Color.argb(128, 0, 0, 0));
    }

    //endregion

    //region == Methods ==


    /**
     * @deprecated Use {@link #show(Boolean)} or {@link #hide()} instead
     */
    @Deprecated
    @Override
    public void setVisibility(int visibility) {
        //super.setVisibility(visibility);
    }

    public void hide() {
        for (Command c : _onHideCommands) {
            c.execute();
        }
        super.setVisibility(GONE);
    }

    public void show(Boolean sarajevoCloudMode) {
        for (Command c : _onShownCommands) {
            c.execute();
        }
        super.setVisibility(VISIBLE);
        if (sarajevoCloudMode) {
            _albumButton.setVisibility(VISIBLE);
            _restartButton.setVisibility(VISIBLE);
            _fotoButton.setVisibility(VISIBLE);
            _newPiktogramButton.setVisibility(VISIBLE);
            _rotirajButton.setVisibility(GONE);
            _postaviPiktogramButton.setVisibility(GONE);
        } else {
            _albumButton.setVisibility(INVISIBLE);
            _restartButton.setVisibility(INVISIBLE);
            _fotoButton.setVisibility(GONE);
            _newPiktogramButton.setVisibility(GONE);
            _rotirajButton.setVisibility(VISIBLE);
            _postaviPiktogramButton.setVisibility(VISIBLE);
        }
    }

    public void setAlbumCommand(Command command) {_albumButton.setCommand(command);}

    public void setRestartCommand(Command command) { _restartButton.setCommand(command); }

    public void setFotoCommand(Command command) { _fotoButton.setCommand(command); }

    public void setNewPiktogramCommand(Command command) { _newPiktogramButton.setCommand(command); }

    public void setRotirajCommand(Command command) { _rotirajButton.setCommand(command); }

    public void setPostaviCommand(Command command) { _postaviPiktogramButton.setCommand(command); }

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