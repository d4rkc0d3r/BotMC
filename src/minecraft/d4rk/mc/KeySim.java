package d4rk.mc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.TickEvent;

public class KeySim implements EventListener {
	private boolean isDestroyed = false;
	
	public KeySim() {
		if(instance != null) {
			instance.isDestroyed = true;
		}
		instance = this;
	}

	public void onTick(TickEvent event) {
		Iterator<Entry<Integer, Integer>> iter = map.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<Integer, Integer> e = iter.next();
			if(e.getValue() <= 0) {
				release(e.getKey());
				iter.remove();
			}
			e.setValue(e.getValue() - 1);
		}
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	public static void klick(Integer key) {
		press(key, 2);
	}
	
	public static void press(Integer key, int ticks) {
		KeyBinding.setKeyBindState(key, true);
		map.put(key, ticks);
	}
	
	public static void release(Integer key) {
		KeyBinding.setKeyBindState(key, false);
	}
	
	private static Map<Integer, Integer> map = new HashMap();
	
	private static KeySim instance = null;
}
