package d4rk.mc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import d4rk.mc.event.EventManager;
import d4rk.mc.event.LoadConfigEvent;
import d4rk.mc.util.Pair;

public class Config {
	private final String name; 
	private String section = "";
	
	/**
	 * key, Pair(data, comment)
	 */
	protected Map<String, Pair<String, String>> data = new TreeMap();
	
	public Config(String fileName) {
		name = fileName;
		load();
	}
	
	public String getFileName() {
		return name;
	}
	
	public boolean exists() {
		return (new File(name)).exists();
	}
	
	public Config startSection(String sectionName) {
		section = section + sectionName + ".";
		return this;
	}
	
	public Config endSection() {
		int firstDot = section.indexOf('.');
		int lastDot = section.lastIndexOf('.');
		section = (firstDot == lastDot) ? "" : section.replace("\\.[^\\.]+\\.", ".");
		return this;
	}
	
	public String getSection() {
		return section;
	}
	
	/**
	 * Loads the file, triggers the {@link LoadConfigEvent} and then saves the file.
	 */
	public void reload() {
		load();
		EventManager.fireEvent(new LoadConfigEvent(this));
		save();
	}
	
	public void load() {
		File oFile = new File(name);
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(oFile));
			String line = "";
			String lastComment = "";
			while ((line = file.readLine()) != null) {
				if(line.startsWith("#")) {
					lastComment = line.substring(1);
					continue;
				}
				String[] str = line.split(":", 2);
				if(str.length == 2) {
					data.put(str[0].trim(), new Pair(str[1].trim(), lastComment));
				}
				lastComment = "";
	        }
			file.close();
		} catch (FileNotFoundException fnfe) {
			// will be filled with default values and then be saved anyways
			// so this is expected for the first run
			// --> no print of stack trace
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				file.close();
			} catch(Exception e) {}
		}
	}
	
	public void save() {
		File oFile = new File(name);
		BufferedWriter file = null;
		try {
			file = new BufferedWriter(new FileWriter(oFile));
			for(Entry<String, Pair<String, String>> entry : data.entrySet()) {
				if(!entry.getValue().getSecond().isEmpty()) {
					file.write("#" + entry.getValue().getSecond() + "\n");
				}
				file.write(entry.getKey() + ":" + entry.getValue().getFirst() + "\n");
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				file.close();
			} catch(Exception e) {}
		}
	}
	
	public Set<String> getKeys() {
		return data.keySet();
	}
	
	public void set(String key, Object value) {
		this.set(key, value, "");
	}
	
	public void set(String key, Object value, String comment) {
		data.put(section + key, new Pair(String.valueOf(value), (comment == null) ? "" : comment));
	}
	
	public void setComment(String key, String comment) {
		this.set(key, getString(key), comment);
	}
	
	public void setDefault(String key, Object value) {
		this.setDefault(key, value, "");
	}
	
	public void setDefault(String key, Object value, String comment) {
		if(!data.containsKey(section + key))
			this.set(key, value, comment);
	}
	
	public void setDefaultComment(String key, String comment) {
		if(data.containsKey(section + key) && data.get(section + key).getSecond().isEmpty())
			this.setComment(key, comment);
	}
	
	public int getInteger(String key) {
		try {
			return Integer.valueOf(getString(key));
		} catch(Exception e) {
			return 0;
		}
	}
	
	public float getFloat(String key) {
		try {
			return Float.valueOf(getString(key));
		} catch(Exception e) {
			return 0;
		}
	}
	
	public double getDouble(String key) {
		try {
			return Double.valueOf(getString(key));
		} catch(Exception e) {
			return 0;
		}
	}
	
	public boolean getBoolean(String key) {
		try {
			return Boolean.valueOf(getString(key));
		} catch(Exception e) {
			return false;
		}
	}
	
	public String getString(String key) {
		try {
			String val = data.get(section + key).getFirst();
			return (val == null) ? "" : val;
		} catch(Exception e) {
			return "";
		}
	}
	
	public String getComment(String key) {
		try {
			String val = data.get(section + key).getSecond();
			return (val == null) ? "" : val;
		} catch(Exception e) {
			return "";
		}
	}
}
