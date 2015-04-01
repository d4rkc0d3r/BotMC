package d4rk.mc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import d4rk.mc.bot.PlayerHelper;
import d4rk.mc.bot.SimpleTestBot;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PostSendPacketEvent;
import d4rk.mc.event.TickEvent;
import d4rk.mc.gui.OverlayManager;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.ChatComponentText;

public class BotMC implements EventListener {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static Config cfg;
	public static PlayerHelper player;
	
	public BotMC() {
		instance = this;
		(new File(getBotMCDir() + "/log/" + getPlayerName())).mkdirs();
		Permission.init();
		EventManager.init();
		
		new SimpleTestBot();
		
		// ensure, that there is an instance of the OverlayManager so that the config reload will take effect.
		OverlayManager.getInstance();
		ClientHacks.getInstance();
		
		cfg = new ConfigManager(getBotMCDir() + "/config.cfg");
		
		cfg.reload();
	}
	
	public void onTick(TickEvent event) {
		if(player == null && mc.thePlayer != null) {
			player = new PlayerHelper(mc.thePlayer, mc.playerController);
		}
	}
	
	public void onWindowClick(PostSendPacketEvent event) {
		if(!(event.getPacket() instanceof C0EPacketClickWindow)) {
			return;
		}
		
		C0EPacketClickWindow packet = (C0EPacketClickWindow) event.getPacket();
		if(event.getPacket() != null) return;
		
		log("clicked slot " + packet.func_149544_d() + " with " + packet.func_149546_g());
		
		ItemStack item = packet.func_149546_g();
		if(item != null) {
			for(IRecipe recipe : player.getRecipesFor(item)) {
				List<ItemStack> items = null;
				if(recipe instanceof ShapedRecipes) {
					items = ((ShapedRecipes)recipe).getRecipeInput();
				}
				if(recipe instanceof ShapelessRecipes) {
					items = ((ShapelessRecipes)recipe).getRecipeInput();
				}
				log("craft " + recipe.getRecipeOutput() + " with " + format(items));
			}
		}
	}
	
	static public String format(List<ItemStack> items) {
		if(items == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		for(int i = 0; i < items.size(); i++) {
			sb.append(""+items.get(i));
			if(i < items.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append(" }");
		return sb.toString();
	}

	static public String getBotMCDir() {
		return getMCDir() + "/botmc";
	}
	
	static public String getMCDir() {
		return mc.mcDataDir.getPath();
	}
	
	static public void sendPacket(Packet p) {
		mc.getNetHandler().addToSendQueue(p);
	}
	
	static public void sendPacketNoEvent(Packet p) {
		mc.getNetHandler().addToSendQueueNoEvent(p);
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
