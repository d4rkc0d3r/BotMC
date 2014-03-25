package d4rk.mc.chat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import d4rk.mc.BotMC;
import d4rk.mc.ChatColor;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.GlobalMessageEvent;
import d4rk.mc.event.LocaleMessageEvent;
import d4rk.mc.event.PrivateMessageEvent;

public class LocaleGlobalPrivateMessageLogger  implements EventListener {
	public LocaleGlobalPrivateMessageLogger() {
		
	}
	
	public void onPMEvent(PrivateMessageEvent event) {
		log(event.toString(), (event.sender.toString().equals("mir")) ? event.receiver.getName() : event.sender.getName());
	}
	
	public void onLMEvent(LocaleMessageEvent event) {
		log(event.toString(), "locale");
	}
	
	public void onGMEvent(GlobalMessageEvent event) {
		log(event.toString(), "global");
	}
	
	private void log(String toLog, String logFileName) {
		toLog = ChatColor.stripColor(toLog);
		try {
			Writer output = new BufferedWriter(new FileWriter(
					BotMC.getBotMCDir() + "/log/" + BotMC.getPlayerName() + "/" + logFileName + ".log", true));
			output.append(BotMC.getCurrentDateAndTime() + " " + toLog + "\r\n");
			output.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean isDestroyed() {
		return false;
	}
}
