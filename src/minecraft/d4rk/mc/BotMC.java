package d4rk.mc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.gui.OverlayManager;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class BotMC implements EventListener {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static Config cfg;
	
	public BotMC() {
		instance = this;
		(new File(getBotMCDir() + "/log/" + getPlayerName())).mkdirs();
		Permission.init();
		EventManager.init();
		
		// ensure, that there is an instance of the OverlayManager so that the config reload will take effect.
		OverlayManager.getInstance();
		
		cfg = new ConfigManager(getBotMCDir() + "/config.cfg");
		
		cfg.reload();
	}

	static public String getBotMCDir() {
		return getMCDir() + "/botmc";
	}
	
	static public String getMCDir() {
		return mc.mcDataDir.getPath();
	}
	
	static public String getCurrentDateAndTime() {
	    return (new SimpleDateFormat("yyyy.MM.dd-hh:mm:ss")).format(Calendar.getInstance().getTime());
	}
	
	/**
	 * Logs the String given as parameter to STDOUT.<br>
	 * If a prefix like "[prefix] " exists, then it will also be saved to a file
	 * (prefix.log) with the current time stamp.
	 */
	static public void log(String str) {
		System.out.println(str);
		logToFile(str);
	}
	
	/**
	 * Does the same as log(str); just without putting str on STDOUT.
	 */
	static public void logToFile(String str) {
		if(str.startsWith("[") && str.indexOf("] ")!=-1) {
			try {
				String type = str.substring(1, str.indexOf("] ")).toLowerCase();
				String content = str.substring(str.indexOf("] ")+2);
				Writer output = new BufferedWriter(new FileWriter(getBotMCDir()+"/log/"+type+".log", true));
				output.append(getCurrentDateAndTime()+" "+content+"\r\n");
				output.close();
			} catch (IOException e) {}
		}
	}
	
	static public void logInfo(String str) {
		log("[Info] " + str);
	}
	
	static public void logWarning(String str) {
		System.err.println(str);
		logToFile("[Warning] " + str);
	}
	
	static public void logSevere(String str) {
		System.err.println(str);
		logToFile("[Severe] " + str);
	}

	public static void sendChatMessage(String string) {
		mc.thePlayer.sendChatMessage(string);
	}

	public static void addToChatGui(String msg) {
		mc.ingameGUI.getChatGUI().func_146227_a(new ChatComponentText(msg));
	}

	public static String getPlayerName() {
		return mc.getSession().getUsername();
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
	
	public static BotMC getInstance() {
		return instance;
	}
	
	private static BotMC instance = null;
}
