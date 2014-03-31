package d4rk.mc;

import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.LoadConfigEvent;

public class ClientHacks implements EventListener {
	private boolean isDestroyed = false;
	
	public boolean waterDoesPushThePlayer = true;
	
	public ClientHacks() {
		if(instance != null) {
			instance.isDestroyed = true;
		}
		EventManager.registerEvents(this);
		instance = this;
	}
	
	public void onLoadConfig(LoadConfigEvent event) {
		Config cfg = event.config.startSection("ClientHacks");
		
		cfg.setDefault("waterDoesPushThePlayer", true);
		
		
		waterDoesPushThePlayer = cfg.getBoolean("waterDoesPushThePlayer");
		
		cfg.endSection();
	}
	
	@Override
	public boolean isDestroyed() {
		return false;
	}
	
	public static ClientHacks getInstance() {
		return (instance == null) ? new ClientHacks() : instance;
	}
	
	private static ClientHacks instance = null;
}
