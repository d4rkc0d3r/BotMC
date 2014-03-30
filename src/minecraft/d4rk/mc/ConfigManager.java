package d4rk.mc;

import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.LoadConfigEvent;
import d4rk.mc.util.Pair;

public class ConfigManager extends Config implements EventListener {
	private boolean isDestroyed = false;
	
	public ConfigManager(String fileName) {
		super(fileName);
		EventManager.registerEvents(this);
		if(instance != null) {
			instance.isDestroyed = true;
		}
		instance = this;
	}
	
	public void onCommand(CommandEvent event) {
		if(!event.getCommand().equalsIgnoreCase("config")) {
			return;
		}
		if(!event.getSender().hasPermission(Permission.LOCALE)) {
			event.getSender().sendSilent(Permission.NO_PERMISSION);
		}
		if(event.getArg(1).equalsIgnoreCase("reload")) {
			this.reload();
		} else if(event.getArg(1).equalsIgnoreCase("get")) {
			if(data.containsKey(event.getArg(2))) {
				event.getSender().sendSilent(ChatColor.GRAY + event.getArg(2) + " : " + ChatColor.WHITE + getString(event.getArg(2)));
			} else {
				event.getSender().sendSilent(ChatColor.RED + "There is no entry with the key " + event.getArg(2));
			}
		} else if(event.getArg(1).equalsIgnoreCase("rget")) {
			Pattern p = Pattern.compile(event.getArg(2));
			event.getSender().sendSilent(ChatColor.DARK_PURPLE + "========== " + event.getArg(2) + " ==========");
			for(Entry<String, Pair<String, String>> e : data.entrySet()) {
				if(p.matcher(e.getKey()).matches()) {
					event.getSender().sendSilent(ChatColor.GRAY + e.getKey() + " : " + ChatColor.WHITE + e.getValue().getFirst());
				}
			}
		} else if(event.getArg(1).equalsIgnoreCase("set")) {
			if(event.getArg(2).isEmpty()) {
				event.getSender().sendSilent(ChatColor.GRAY + "Usage: /config set <key> <value>");
			} else {
				this.set(event.getArg(2), event.getArg(3));
				EventManager.fireEvent(new LoadConfigEvent(this));
				this.save();
				event.getSender().sendSilent(ChatColor.GRAY + "Set " + ChatColor.WHITE + event.getArg(2) + ChatColor.GRAY
						 + " to \"" + ChatColor.WHITE + event.getArg(3) + ChatColor.GRAY + "\"");
			}
		} else if(event.getArg(1).equalsIgnoreCase("rset")) {
			if(event.getArg(2).isEmpty()) {
				event.getSender().sendSilent(ChatColor.GRAY + "Usage: /config rset <keyregex> <value>");
			} else {
				try {
					String key = null;
					Pattern p = Pattern.compile(event.getArg(2));
					for(Entry<String, Pair<String, String>> e : data.entrySet()) {
						if(p.matcher(e.getKey()).matches()) {
							key = e.getKey();
							break;
						}
					}
					this.set(key, event.getArg(3));
					EventManager.fireEvent(new LoadConfigEvent(this));
					this.save();
					event.getSender().sendSilent(ChatColor.GRAY + "Set " + ChatColor.WHITE + key + ChatColor.GRAY
							 + " to \"" + ChatColor.WHITE + event.getArg(3) + ChatColor.GRAY + "\"");
				} catch(PatternSyntaxException pse) {
					event.getSender().sendSilent(ChatColor.RED + event.getArg(2) + " is not a valid regex.");
				}
			}
		} else if(event.getArg(1).equalsIgnoreCase("name")) {
			event.getSender().sendSilent(ChatColor.GRAY + "Config name: " + ChatColor.WHITE + getFileName());
		} else {
			event.getSender().sendSilent(ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/config (reload | get | rget | set | rset | name)");
		}
		event.setDisabled(true);
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	private static ConfigManager instance = null;
}
