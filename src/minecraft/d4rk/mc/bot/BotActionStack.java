package d4rk.mc.bot;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

import d4rk.mc.BotMC;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.TickEvent;

public class BotActionStack implements EventListener {
	private Deque<BotAction> stack = new ArrayDeque();
	private BotAction currentAction;
	private boolean isDestroyed = false;
	
	public BotActionStack() {
		if(instance != null) {
			instance.destroy();
		}
		instance = this;
		EventManager.registerEvents(this);
	}
	
	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public void push(BotAction action) {
		stopTop();
		stack.push(action);
		activateTop();
	}
	
	private void stopTop() {
		if(stack.isEmpty()) {
			return;
		}
		stack.peek().disableEvents = true;
	}
	
	private void activateTop() {
		if(stack.isEmpty() || !stack.peek().disableEvents) {
			return;
		}
		stack.peek().disableEvents = false;
		EventManager.registerEvents(stack.peek());
	}
	
	public void onTick(TickEvent event) {
		if(stack.isEmpty()) {
			return;
		}
		
		if(stack.peek().isFinished()) {
			BotAction action = stack.pop();
			BotMC.log("[BotActionStack] " + action.getClass().getSimpleName() + " finished with " + action.getResult());
			activateTop();
		}
	}
	
	private static BotActionStack instance;
	public static BotActionStack getInstance() {
		if(instance == null) {
			new BotActionStack();
		}
		return instance;
	}
	
	public void destroy() {
		while(!stack.isEmpty()) {
			stopTop();
			stack.pop();
		}
		isDestroyed = true;
	}
	
	@Override
	public boolean isDestroyed() {
		return false;
	}
}
