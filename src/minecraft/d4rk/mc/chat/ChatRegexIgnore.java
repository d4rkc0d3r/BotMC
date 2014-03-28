package d4rk.mc.chat;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import d4rk.mc.BotMC;
import d4rk.mc.Config;
import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.LoadConfigEvent;

public class ChatRegexIgnore implements EventListener {
	private ArrayList<Pattern> ignoreOnMatch = new ArrayList();
	private Config saveListConfig = new Config(BotMC.getBotMCDir() + "/chatRegexIgnore.cfg");
	
	public ChatRegexIgnore() {
		
	}
	
	public void onLoadConfig(LoadConfigEvent event) {
		event.config.setDefault("ChatRegexIgnore.configFile", "chatRegexIgnore.cfg");
		
		saveListConfig = new Config(BotMC.getBotMCDir() + "/" + event.config.getString("ChatRegexIgnore.configFile"));
		loadFromConfig();
	}
	
	public void onChatEvent(ChatEvent event) {
		for(int i = 0; i < ignoreOnMatch.size(); ++i) {
			if(ignoreOnMatch.get(i).matcher(event.getSrc()).matches()) {
				event.setDisabled(true);
				break;
			}
		}
	}
	
	private void loadFromConfig() {
		if(!saveListConfig.exists()) {
			saveListConfig.setDefault("length", 4);
			saveListConfig.setDefault("0", "^\\Q[Message]\\E.*");
			saveListConfig.setDefault("1", "^\\Q[Rundruf]\\E.*");
			saveListConfig.setDefault("2", "^\\Q[LOTTERY]\\E.*\\Qkaufte\\E.*");
			saveListConfig.setDefault("3", "^\\QWillkommen auf Secretcraft \\E.*");
			saveListConfig.save();
		}
		
		int length = saveListConfig.getInteger("length");
		ignoreOnMatch = new ArrayList(length);
		for(int i = 0; i < length; ++i) {
			String s = saveListConfig.getString(String.valueOf(i));
			try {
				ignoreOnMatch.add(Pattern.compile(s));
			} catch(PatternSyntaxException pse) {
				BotMC.logWarning("Couldn't compile pattern: " + s);
			}
		}
	}
	
	private void saveToConfig() {
		saveListConfig = new Config(saveListConfig.getFileName());
		saveListConfig.set("length", ignoreOnMatch.size());
		for(int i = 0; i < ignoreOnMatch.size(); ++i) {
			saveListConfig.set(String.valueOf(i), ignoreOnMatch.get(i).toString());
		}
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
