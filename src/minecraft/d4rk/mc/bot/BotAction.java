package d4rk.mc.bot;

import d4rk.mc.event.EventListener;

public abstract class BotAction implements EventListener {
	protected boolean isFinished = false;
	protected boolean disableEvents = true;
	protected Object result = null;
	
	public BotAction() {
		
	}
	
	protected void finish(Object result) {
		this.result = result;
		isFinished = true;
	}
	
	public Object getResult() {
		return result;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	@Override
	public boolean isDestroyed() {
		return isFinished() || disableEvents;
	}
}
