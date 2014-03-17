package d4rk.mc.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.chunk.Chunk;

import d4rk.mc.BlockWrapper;
import d4rk.mc.Config;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.LoadConfigEvent;
import d4rk.mc.event.TickEvent;
import d4rk.mc.gui.BasicGuiOverlay;

public class EntityChunkCount extends BasicGuiOverlay implements EventListener {
	private ArrayList<String> displayText = new ArrayList();
	public static final Map<String,Integer> result = new TreeMap<String,Integer>();
	
	public EntityChunkCount() {
		EventManager.registerEvents(this);
	}
	
	public void onLoadConfig(LoadConfigEvent event) {
		Config cfg = event.config;
		cfg.setDefault("isEntityChunkCountVisible", false);
		
		setVisible(cfg.getBoolean("isEntityChunkCountVisible"));
	}
	
	public void onTick(TickEvent event) {
		displayText.clear();
		result.clear();
		
		Minecraft mc = Minecraft.getMinecraft();
		Chunk c = mc.theWorld.getChunkFromChunkCoords(mc.thePlayer.chunkCoordX, mc.thePlayer.chunkCoordZ);
		int entityCount = 0;
		for(List l : c.entityLists) { for(Entity e : (List<Entity>)l) {
			addOne(result, (e instanceof EntityPlayer) ? "Player" : EntityList.getEntityString(e));
			++entityCount;
		}}
		displayText.add("Chunk(" + c.xPosition + "," + c.zPosition + ") has "
				+ entityCount + ((entityCount == 1) ? " entity" : " entities"));
		for(Entry<String, Integer> e : result.entrySet()) {
			displayText.add(e.getKey() + ": " + e.getValue());
		}
		
		width = 0;
		for(String s : displayText) {
			width = Math.max(width, fontRenderer.getStringWidth(s));
		}
		width += 8;
		height = (fontRenderer.FONT_HEIGHT + 3) * displayText.size() + 3;
	}
    
	private static void addOne(Map<String, Integer> map, String entity) {
		Integer val = map.get(entity);
		map.put(entity, val == null ? 1 : 1 + val);
	}

	@Override
	public String getName() {
		return "Entity Chunk Count";
	}

	@Override
	public void draw() {
		for(int i = 0; i < displayText.size(); ++i) {
			fontRenderer.drawString(displayText.get(i), 8, 8 + i * (fontRenderer.FONT_HEIGHT + 3), 0xFFFFFF, true);
		}
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
