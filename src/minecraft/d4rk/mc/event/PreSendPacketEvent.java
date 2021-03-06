package d4rk.mc.event;

import net.minecraft.network.Packet;

public class PreSendPacketEvent extends DisableEvent {
	private final Packet packet;
	
	public PreSendPacketEvent(Packet packet) {
		this.packet = packet;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + packet.getClass().getSimpleName() + ")";
	}
}
