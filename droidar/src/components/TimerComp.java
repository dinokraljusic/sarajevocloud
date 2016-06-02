package components;

import commands.Command;

import worldData.Entity;
import worldData.UpdateTimer;
import worldData.Updateable;
import worldData.Visitor;

public class TimerComp implements Entity {

	private UpdateTimer timer;
	private Updateable myParent;

	public TimerComp(float countdownTimeInSeconds, Command commandToExecute) {
		timer = new UpdateTimer(countdownTimeInSeconds, commandToExecute);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		setMyParent(parent);
		return !timer.update(timeDelta, this);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Updateable getMyParent() {
		return myParent;
	}

	@Override
	public void setMyParent(Updateable parent) {
		myParent = parent;
	}

}
