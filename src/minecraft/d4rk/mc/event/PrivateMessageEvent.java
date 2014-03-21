package d4rk.mc.event;

import d4rk.mc.PlayerString;

public class PrivateMessageEvent extends DisableEvent {
	public String message;
	public PlayerString sender;
	public PlayerString receiver;

	public PrivateMessageEvent(String msg, PlayerString sender, PlayerString receiver) {
		this.message = msg;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public PrivateMessageEvent(PrivateMessageEvent e) {
		this.message = e.message;
		this.sender = e.sender;
		this.receiver = e.receiver;
	}
	
	@Override
	public String toString() {
		return "[" + sender + " -> " + receiver + "] " + message;
	}
}
