package d4rk.mc.event;

import d4rk.mc.PlayerString;

public class LocaleMessageEvent extends DisableEvent {
	public PlayerString sender;
	public String message;

	public LocaleMessageEvent(PlayerString sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	public LocaleMessageEvent(LocaleMessageEvent e) {
		sender = e.sender;
		message = e.message;
	}

	@Override
	public String toString() {
		return sender + ": " + message;
	}
}