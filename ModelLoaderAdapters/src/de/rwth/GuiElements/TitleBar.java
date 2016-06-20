package de.rwth.GuiElements;

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
    private final ImageWithTransparentBackground _leftButton, _rightButton;
    private final TextView _txtUserName, _txtPiktogramChooserInfo;
    private final ClickableTextView _txtNaslov;
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
        _leftButton = new ImageWithTransparentBackground(context, R.drawable.gornji_lijevi_button_zuto, R.drawable.gornji_lijevi_button_zeleno, null);
        _leftButton.setPadding(15, 15, 15, 15);

        _txtNaslov = new ClickableTextView(context, "SARAJEVO CLOUD", null, _defaultFont, R.color.zuta, R.color.zelena);
        _txtNaslov.setTypeface(_defaultFont);
        _txtNaslov.setTextColor(getResources().getColor(R.color.zuta));
        _txtNaslov.setTextSize(19);

        _leftButton.registerPreExecutedCommand(new Command() {
            @Override
            public boolean execute() {
                _txtNaslov.setTextColor(getResources().getColor(R.color.zelena));
                return false;
            }
        });
        _leftButton.registerPostExecutedCommand(new Command() {
            @Override
            public boolean execute() {
                _txtNaslov.setTextColor(getResources().getColor(R.color.zuta));
                return false;
            }
        });
        _txtNaslov.registerPreExecutedCommand(new Command() {
            @Override
            public boolean execute() {
                _leftButton.setImageResource(R.drawable.gornji_lijevi_button_zeleno);
                return false;
            }
        });
        _txtNaslov.registerPostExecutedCommand(new Command() {
            @Override
            public boolean execute() {
                _leftButton.setImageResource(R.drawable.gornji_lijevi_button_zuto);
                return false;
            }
        });


        LinearLayout pomLyt = new LinearLayout(context);
        pomLyt.setOrientation(HORIZONTAL);
        pomLyt.addView(_leftButton);
        pomLyt.addView(_txtNaslov);
        RelativeLayout.LayoutParams lp_l = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        pomLyt.setLayoutParams(lp_l);

        _rightButton = new ImageWithTransparentBackground(context, R.drawable.gornji_desni_meni_zuto,
                R.drawable.gornji_desni_meni_zelen, null);
        _rightButton.setPadding(15, 15, 15, 15);
        lp_l = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_l.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        _rightButton.setLayoutParams(lp_l);

        _firstRow.addView(pomLyt);
        _firstRow.addView(_rightButton);

        _txtUserName = new TextView(context);
        _txtUserName.setTypeface(_defaultFont);
        _txtUserName.setTextColor(getResources().getColor(R.color.zuta));
        _txtUserName.setTextSize(17);
        _txtUserName.setPadding(105, 9, 0, 11);
        _txtUserName.setVisibility(View.GONE);
        _txtUserName.setText(Spremnik.getInstance().getUserName());
        addView(_txtUserName);

        _txtPiktogramChooserInfo = new TextView(context);
        _txtPiktogramChooserInfo.setTypeface(_defaultFont);
        _txtPiktogramChooserInfo.setTextSize(19);
        _txtPiktogramChooserInfo.setText("ODABERITE OBJEKAT");
        _txtPiktogramChooserInfo.setTextColor(Color.rgb(242, 229, 0));
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

    public void setLeftButtonCommand(Command command) {
        _leftButton.setCommand(command);
        _txtNaslov.setCommand(command);
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
    public  void setTitle(String text){
        _txtNaslov.setText(text);
        if(text.toUpperCase().equals("SARAJEVO CLOUD"))
            _txtUserName.setVisibility(GONE);
        else
            _txtUserName.setVisibility(VISIBLE);
    }
    public void showPiktogramChooserInfo(boolean show){
        if(show) {
            _txtPiktogramChooserInfo.setVisibility(VISIBLE);
            hideBackground();
            setEnabled(false);
        }else {
            _txtPiktogramChooserInfo.setVisibility(GONE);
            showBackground();
            setEnabled(true);
        }
    }

    //region ==== Overrides ====


    @Override
    public void setEnabled(boolean enabled) {
        _leftButton.setEnabled(enabled);
        _txtNaslov.setEnabled(enabled);
        _rightButton.setEnabled(enabled);
        super.setEnabled(enabled);
    }

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