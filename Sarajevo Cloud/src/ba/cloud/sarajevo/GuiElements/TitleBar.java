package ba.cloud.sarajevo.GuiElements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import commands.Command;
import de.rwth.R;
import de.rwth.Spremnik;

/**
 * Created by MiniP on 6/12/2016.
 */
public class TitleBar extends LinearLayout implements IGuiElement {

    //region == Fields ==

    private final Typeface _defaultFont;
    private final List<Command> _onShownCommands, _onHidenCommands;
    private final RelativeLayout _firstRow;
    private final ImageWithTransparentBackground _rightButton;
    private final TextView _txtUserName, _txtPiktogramChooserInfo, _txtNaslov;
    boolean _backgroundShown;
    //endregion

    //region == Constructors ==

    public TitleBar(final Context context, int screenWidth) {
        super(context);

        _onShownCommands = new ArrayList<>();
        _onHidenCommands = new ArrayList<>();

        _defaultFont = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/ACTOPOLIS.otf");
        _firstRow = new RelativeLayout(context);
        _firstRow.setMinimumWidth(screenWidth);
        setOrientation(VERTICAL);
        addView(_firstRow);

        _txtNaslov = new TextView(context);
        _txtNaslov.setText("SARAJEVO CLOUD");
        _txtNaslov.setTypeface(_defaultFont);
        _txtNaslov.setTextColor(getResources().getColor(R.color.zuta));
        _txtNaslov.setTextSize(19);

        RelativeLayout.LayoutParams lp_l = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        _txtNaslov.setLayoutParams(lp_l);

        _rightButton = new ImageWithTransparentBackground(context, R.drawable.gornji_desni_meni_zuto,
                R.drawable.gornji_desni_meni_zelen, null);
        _rightButton.setPadding(15, 15, 15, 15);
        lp_l = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_l.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        _rightButton.setLayoutParams(lp_l);

        _txtUserName = new TextView(context);
        _txtUserName.setTextColor(getResources().getColor(R.color.zuta));
        _txtUserName.setTextSize(17);
        _txtUserName.setPadding(105, 9, 0, 11);
        _txtUserName.setVisibility(View.GONE);
        _txtUserName.setText(Spremnik.getInstance().getUserName());

        _firstRow.addView(_txtNaslov);
        _firstRow.addView(_rightButton);
        _firstRow.addView(_txtUserName);

        _txtPiktogramChooserInfo = new TextView(context);
        _txtPiktogramChooserInfo.setTextSize(17);
        _txtPiktogramChooserInfo.setText("ODABERITE OBJEKAT IZ GALERIJE");
        _txtPiktogramChooserInfo.setTextColor(context.getResources().getColor(R.color.zuta));
        _txtPiktogramChooserInfo.setPadding(45, 19, 0, 105);
        _txtPiktogramChooserInfo.setVisibility(View.GONE);
        addView(_txtPiktogramChooserInfo);
        }

    //endregion

    //region == Methods ==

    public void showBar() {
        for (Command command : _onShownCommands) {
            command.execute();
        }
        super.setVisibility(VISIBLE);
    }

    public void hideBar() {
        for (Command command : _onHidenCommands) {
            command.execute();
        }
        super.setVisibility(GONE);
    }

    public void setRightButtonCommand(Command command) { _rightButton.setCommand(command); }

    public void showBackground() {
        super.setBackgroundColor(Color.argb(128, 0, 0, 0));
        _backgroundShown = true;
    }
    public void hideBackground() {
        super.setBackgroundColor(Color.argb(0, 0, 0, 0));
    _backgroundShown=false;
    }

    public boolean isBackgroundShown() {
        return _backgroundShown;
    }

    public  void setTitle(String text) {
        _txtNaslov.setText(text);
        if (text.toUpperCase().equals("SARAJEVO CLOUD")) {
            _txtUserName.setVisibility(VISIBLE);
            _rightButton.setVisibility(VISIBLE);
        } else {
            _txtUserName.setVisibility(GONE);
            _rightButton.setVisibility(GONE);
        }
    }

    public void showPiktogramChooserInfo(boolean show) {
        if (show) {
            _txtPiktogramChooserInfo.setVisibility(VISIBLE);
            setTitle("GALERIJA OBJEKATA");
            hideBackground();
        } else {
            _txtPiktogramChooserInfo.setVisibility(GONE);
            setTitle("SARAJEVO CLOUD");
            showBackground();
        }
    }

    //region ==== Overrides ====

    /**
    * @deprecated use {@link #showBackground()} and {@link #hideBackground()} instead
    */
    @Deprecated
    @Override
    public void setBackgroundColor(int color) { }

    /*
        * @deprecated use {@link #showBar()} and {@link #hideBar()} instead
        * */
    @Deprecated
    @Override
    public void setVisibility(int visibility) { }

    @Override
    public void registerOnShowCommand(Command command) {
        if (command != null)
            _onShownCommands.add(command);
    }

    @Override
    public void unRegisterAllOnShowCommands() { _onShownCommands.clear(); }

    @Override
    public void unRegisterOnShowCommand(Command command) {
        if (command != null && _onShownCommands.contains(command)) _onShownCommands.remove(command);
    }

    @Override
    public void registerOnHideCommand(Command command) { if (command != null) _onHidenCommands.add(command); }

    @Override
    public void unRegisterAllOnHideCommands() { _onHidenCommands.clear(); }

    @Override
    public void unRegisterOnHideCommand(Command command) {
        if (command != null && _onHidenCommands.contains(command))
            _onHidenCommands.remove(command);
    }
    //endregion

    //endregion
}