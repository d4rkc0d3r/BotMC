package d4rk.mc.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import d4rk.mc.ChatColor;
import d4rk.mc.PlayerString;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class EventSpecifier implements EventListener {
	public EventSpecifier() {
		
	}
	
	public void onPrePacketSendEvent(PreSendPacketEvent event) {
		if(event.getPacket() instanceof C01PacketChatMessage) {
			String msg = ((C01PacketChatMessage)event.getPacket()).func_149439_c();
			SendChatMessageEvent e = new SendChatMessageEvent((C01PacketChatMessage)event.getPacket());
			EventManager.fireEvent(e);
			if(e.isDisabled()) {
				event.setDisabled(true);
			} else if(e.isModified()) {
				event.setDisabled(true);
				EventManager.sendPacketWithoutEvent(e.getPacket());
			}
		}
	}
	
	public void onSendChatMessage(SendChatMessageEvent event) {
		if(event.getMessage().startsWith("/")) {
			System.out.println("Send Command?: " + event.getMessage());
			CommandEvent e = new CommandEvent(event.getMessage().substring(1).split(" "), PlayerString.ME);
			EventManager.fireEvent(e);
			event.setDisabled(e.isDisabled());
		}
	}
	
	public void onChatEvent(ChatEvent event) {
		String strColor = event.message;
		String str = ChatColor.stripColor(strColor);
		try {
			if(firstGlobalLocalPattern.matcher(str).matches()) {
				if(globalPattern.matcher(strColor).matches()) {
					int strIndexOfDoubbleDot = str.indexOf(": ");
					String s = str.substring(0, strIndexOfDoubbleDot).replaceAll("<3", "") + str.substring(strIndexOfDoubbleDot);
					String player = s.substring(s.indexOf(']') + 1, s.indexOf(':'));
					String rank = s.substring(1, s.indexOf(']'));
					int index2 = strColor.indexOf(": " + ChatColor.YELLOW) + 4;
					GlobalMessageEvent gmEvent = new GlobalMessageEvent(new PlayerString(player, rank), strColor.substring(index2));
					event.setDisabled(EventManager.fireEvent(gmEvent) || event.isDisabled());
					event.message = strColor.substring(0, index2) + gmEvent.message;
				} else {
					int strIndexOfDoubbleDot = str.indexOf(": ");
					String s = str.substring(0, strIndexOfDoubbleDot).replaceAll("<3", "") + str.substring(strIndexOfDoubbleDot);
					String player = s.substring(s.indexOf(']') + 1, s.indexOf(':'));
					String rank = s.substring(1, s.indexOf(']'));
					int index2 = strColor.indexOf(": " + ChatColor.WHITE) + 4;
					LocaleMessageEvent lmEvent = new LocaleMessageEvent(new PlayerString(player, rank), str.substring(index2 + 2));
					event.setDisabled(EventManager.fireEvent(lmEvent) || event.isDisabled());
					event.message = strColor.substring(0, index2) + lmEvent.message;
				}
			} else if(privateMessagePattern.matcher(str).matches()) {
				String[] user = str.substring(1, str.indexOf("] ")).split(" -> ", 2);
				int index2 = strColor.indexOf("] " + ChatColor.WHITE);
				PrivateMessageEvent pmEvent = new PrivateMessageEvent(strColor.substring(index2 + 4),
						new PlayerString(user[0]), new PlayerString(user[1])); 
				event.setDisabled(EventManager.fireEvent(pmEvent) || event.isDisabled());
				event.message = strColor.substring(0, index2 + 4) + pmEvent.message;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
	
	private static Pattern firstGlobalLocalPattern = Pattern.compile("^\\[[a-zA-Z1-9_+]+\\](<3)*[a-zA-Z1-9_]+: ");
	private static Pattern globalPattern = Pattern.compile(": " + ChatColor.COLOR_CHAR + "e");
	private static Pattern privateMessagePattern = Pattern.compile(
			"^\\[(mir -> [a-zA-Z1-9_+]+\\](<3)*[a-zA-Z1-9_]+|[a-zA-Z1-9_+]+\\](<3)*[a-zA-Z1-9_]+ -> mir)\\] ");
}
