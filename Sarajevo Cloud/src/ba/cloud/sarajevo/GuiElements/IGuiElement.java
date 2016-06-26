package ba.cloud.sarajevo.GuiElements;

import commands.Command;

/**
 * Created by MiniP on 6/11/2016.
 */
public interface IGuiElement {

    void registerOnShowCommand(Command command);
    void unRegisterAllOnShowCommands();
    void unRegisterOnShowCommand(Command command);

    void registerOnHideCommand(Command command);
    void unRegisterAllOnHideCommands();
    void unRegisterOnHideCommand(Command command);
}
