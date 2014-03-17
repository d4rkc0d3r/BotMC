package d4rk.mc.event;

import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class TimeUpdateEvent extends BaseEvent {
	private S03PacketTimeUpdate packet = null;
	
	public TimeUpdateEvent(S03PacketTimeUpdate packet) {
		this.packet = packet;
	}
	
	public long getTime() {
		return packet.func_149366_c();
	}
}
