package d4rk.mc.event;

import d4rk.mc.Config;

public class LoadConfigEvent extends BaseEvent {
	public final Config config;
	
	public LoadConfigEvent(Config config) {
		this.config = config;
	}
}
