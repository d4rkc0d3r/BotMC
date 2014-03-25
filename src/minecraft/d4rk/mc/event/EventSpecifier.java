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
		//System.out.println("ChatEvent: " + str);
		//System.out.println("  firstGlobalLocalPattern.matcher(str).lookingAt(): " + firstGlobalLocalPattern.matcher(str).lookingAt());
		//System.out.println("  globalPattern.matcher(strColor).lookingAt():      " + globalPattern.matcher(strColor).lookingAt());
		//System.out.println("  privateMessagePattern.matcher(str).lookingAt():   " + privateMessagePattern.matcher(str).lookingAt());
		try {
			if(firstGlobalLocalPattern.matcher(str).lookingAt()) {
				if(globalPattern.matcher(strColor).lookingAt()) {
					String s = str;
					String player = s.substring(s.indexOf(']') + 1, s.indexOf(':'));
					String rank = s.substring(1, s.indexOf(']'));
					int index2 = strColor.indexOf(": " + ChatColor.YELLOW) + 4;
					GlobalMessageEvent gmEvent = new GlobalMessageEvent(new PlayerString(player, rank), strColor.substring(index2));
					//System.out.println("  ==> GlobalMessageEvent: " + gmEvent);
					EventManager.fireEvent(gmEvent);
					event.setDisabled(gmEvent.isDisabled() || event.isDisabled());
					event.message = strColor.substring(0, index2) + gmEvent.message;
				} else {
					String s = str;
					String player = s.substring(s.indexOf(']') + 1, s.indexOf(':'));
					String rank = s.substring(1, s.indexOf(']'));
					int index2 = strColor.indexOf(": ") + 2;
					String message = strColor.substring(index2);
					boolean hasColorCode = message.startsWith("" + ChatColor.WHITE);
					message = message.substring((hasColorCode) ? 2 : 0);
					LocaleMessageEvent lmEvent = new LocaleMessageEvent(new PlayerString(player, rank), message);
					//System.out.println("  ==> LocaleMessageEvent: " + lmEvent);
					EventManager.fireEvent(lmEvent);
					event.setDisabled(lmEvent.isDisabled() || event.isDisabled());
					event.message = strColor.substring(0, index2) + ((hasColorCode) ? ChatColor.WHITE : "") + lmEvent.message;
				}
			} else if(privateMessagePattern.matcher(str).lookingAt()) {
				String[] user = str.substring(1, str.indexOf("] ")).split(" -> ", 2);
				int index2 = strColor.indexOf("] ");
				String message = strColor.substring(index2 + 2);
				boolean hasColorCode = message.startsWith("" + ChatColor.WHITE);
				message = message.substring((hasColorCode) ? 2 : 0);
				PrivateMessageEvent pmEvent = new PrivateMessageEvent(message,
						new PlayerString(user[0]), new PlayerString(user[1])); 
				//System.out.println("  ==> PrivateMessageEvent: " + pmEvent);
				EventManager.fireEvent(pmEvent);
				event.setDisabled(pmEvent.isDisabled() || event.isDisabled());
				event.message = strColor.substring(0, index2 + 2) + ((hasColorCode) ? ChatColor.WHITE : "") + pmEvent.message;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
	
	private static Pattern firstGlobalLocalPattern = Pattern.compile("^\\[[a-zA-Z1-9_+]+\\][a-zA-Z1-9_]+(<3)*: ");
	private static Pattern globalPattern = Pattern.compile("^.*: \\u00a7e");
	private static Pattern privateMessagePattern = Pattern.compile(
			"^\\[(mir -> \\[[a-zA-Z1-9_+]+\\][a-zA-Z1-9_]+(<3)*|\\[[a-zA-Z1-9_+]+\\][a-zA-Z1-9_]+(<3)* -> mir)\\] ");
}
