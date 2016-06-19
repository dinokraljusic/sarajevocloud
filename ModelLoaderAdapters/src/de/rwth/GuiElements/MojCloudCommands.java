package de.rwth.GuiElements;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import commands.Command;
import de.rwth.R;

/**
 * Created by MiniP on 6/12/2016.
 */
public class MojCloudCommands extends RelativeLayout implements IGuiElement {

    //region == Fields ==

    private final List<Command> _onShownCommands, _onHideCommands;
    private final LinearLayout _buttons, _background;
    private final ImageWithTransparentBackground _cameraButton, _newPiktogramButton, _reloadButton;

    //endregion

    //region == Constructors ==

    public MojCloudCommands(Context context, int height) {
        super(context);

        _onShownCommands = new ArrayList<>();
        _onHideCommands = new ArrayList<>();
        setMinimumHeight(height);
        setVerticalGravity(CENTER_VERTICAL);

        _buttons = new LinearLayout(context);
        _buttons.setOrientation(LinearLayout.VERTICAL);
        _buttons.setMinimumHeight(height);

        _background = new LinearLayout(context);
        _background.setMinimumHeight(height);

        _newPiktogramButton = new ImageWithTransparentBackground(context, R.drawable.plus_zuto, R.drawable.plus_zeleno, null);
        _reloadButton = new ImageWithTransparentBackground(context, R.drawable.reload_zuto, R.drawable.reload_zeleno, null);
        _cameraButton = new ImageWithTransparentBackground(context, R.drawable.cam_yellow, R.drawable.cam_green, null);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
        lp.gravity = Gravity.RIGHT;

        _newPiktogramButton.setLayoutParams(lp);
        _newPiktogramButton.setPadding(0, 0, 15, 0);
        _cameraButton.setLayoutParams(lp);
        _cameraButton.setPadding(0, 0, 15, 0);
        _reloadButton.setLayoutParams(lp);
        _reloadButton.setPadding(0,0,15, 0);
        _buttons.addView(_newPiktogramButton);
        _buttons.addView(_cameraButton);
        _buttons.addView(_reloadButton);

        RelativeLayout.LayoutParams rlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        addView(_background, rlp);
        addView(_buttons, rlp);
        _background.setBackgroundColor(Color.argb(128, 0, 0, 0));
        rlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlp.width = 55;
        _background.setLayoutParams(rlp);
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
        _buttons.setVisibility(VISIBLE);
        if (!sarajevoCloudMode) {
            _background.setBackgroundColor(Color.argb(128, 0, 0, 0));
            _newPiktogramButton.setVisibility(VISIBLE);
            _reloadButton.setVisibility(VISIBLE);
        } else {
            _background.setBackgroundColor(Color.argb(0, 0, 0, 0));
            _newPiktogramButton.setVisibility(GONE);
            _reloadButton.setVisibility(GONE);
        }
    }

    public void setNewPiktogramCommand(Command command) {
        _newPiktogramButton.setCommand(command);
    }

    public void setCameraCommand(Command command) {
        _cameraButton.setCommand(command);
    }

    public void setReloadCommand(Command command) {
        _reloadButton.setCommand(command);
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