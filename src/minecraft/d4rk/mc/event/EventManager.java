package d4rk.mc.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import d4rk.mc.BotMC;
import d4rk.mc.KeySim;
import d4rk.mc.chat.ChatRegexIgnore;
import d4rk.mc.chat.LocaleGlobalPrivateMessageLogger;
import d4rk.mc.chat.PMIgnore;
import d4rk.mc.debug.PacketDisplay;
import d4rk.mc.gui.ShopAnalyzer;
import d4rk.mc.util.Pair;

public class EventManager {
	private EventManager() {}

	public static void init() {
		BotMC.logToFile("[EventManager] ============================== init() ==============================");
		
		map = new HashMap();
		triggerEvents = true;
		
		registerEvents(new PMIgnore());
		registerEvents(new PacketDisplay());
		registerEvents(new EventSpecifier());
		registerEvents(new ShopAnalyzer());
		registerEvents(new LocaleGlobalPrivateMessageLogger());
		registerEvents(new ChatRegexIgnore());
		registerEvents(new KeySim());
		registerEvents(BotMC.getInstance());
	}

	/**
	 * Call this method to fire an event. All of its listeners are called.
	 * 
	 * @param event
	 *            The event to be fired.
	 * @return {@link DisableEvent#isDisabled() event.isDisabeled()}
	 */
	public static boolean fireEvent(BaseEvent event) {
		if(!triggerEvents)
			return false;
		Set<Pair<EventListener, Method>> set = map.get(event.getClass());
		if (set == null && event instanceof DisableEvent)
			return ((DisableEvent) event).isDisabled();
		if (set == null)
			return false;
		List<Pair<EventListener, Method>> toRemove = new ArrayList();
		Pair<EventListener, Method>[] array =  set.toArray(new Pair[0]);
		for (int i = 0; i < array.length; i++) {
			Pair<EventListener, Method> p = array[i];
			if(p.getFirst().isDestroyed()) {
				Class<?>[] params = p.getSecond().getParameterTypes();
				BotMC.logToFile("[EventManager] Successfully deregistered '"
						+ afterLastDot(p.getFirst().getClass().getName()) + "."
						+ p.getSecond().getName() + "(" + afterLastDot(params[0].getName()) + ")'");
				toRemove.add(p);
				continue;
			}
			try {
				p.getSecond().invoke(p.getFirst(), event);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		set.removeAll(toRemove);
		if(set.isEmpty()) {
			map.remove(event.getClass());
		}
		if (event instanceof DisableEvent)
			return ((DisableEvent) event).isDisabled();
		return false;
	}

	/**
	 * Registers all Methods of the listener with one parameter as an event
	 * listener method. The parameter's class must be a subclass of
	 * {@link BaseEvent}.
	 * 
	 * @param listener
	 */
	public static void registerEvents(EventListener listener) {
		Method[] methods = listener.getClass().getMethods();
		for (Method m : methods) {
			registerMethod(listener, m);
		}
	}
	
	public static boolean registerMethod(EventListener listener, Method m) {
		Class<?>[] params = m.getParameterTypes();
		if (params.length != 1 || params[0].getName().startsWith("[")
				|| !isSuperClassOf(params[0], BaseEvent.class)) {
			return false;
		}
	
		Set set = map.get(params[0]);
		if (set == null) {
			set = new HashSet<Pair<EventListener, Method>>();
			map.put(params[0], set);
		}
		set.add(new Pair(listener, m));
		BotMC.logToFile("[EventManager] Successfully registered '"
				+ afterLastDot(listener.getClass().getName()) + "."
				+ m.getName() + "(" + afterLastDot(params[0].getName()) + ")'");
		return true;
	}

	private static boolean isSuperClassOf(Class check, Class sup) {
		Class sup2 = check;
		while (!sup.equals(sup2)) {
			sup2 = sup2.getSuperclass();
			if (sup2 == null)
				return false;
		}
		return true;
	}

	private static String afterLastDot(String str) {
		return str.substring(str.lastIndexOf('.') + 1);
	}

	private static HashMap<Class, Set<Pair<EventListener, Method>>> map = new HashMap();
	public static boolean triggerEvents = true;
}
