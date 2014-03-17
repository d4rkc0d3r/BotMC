package d4rk.mc.gui.overlay;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import d4rk.mc.BlockWrapper;
import d4rk.mc.Config;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.LoadConfigEvent;
import d4rk.mc.event.TickEvent;
import d4rk.mc.gui.BasicGuiOverlay;

public class RayTraceInformation extends BasicGuiOverlay implements EventListener {
	private ArrayList<String> displayText;
	
	public RayTraceInformation() {
		displayText = new ArrayList();
		
		EventManager.registerEvents(this);
	}
	
	public void onLoadConfig(LoadConfigEvent event) {
		Config cfg = event.config;
		cfg.setDefault("isRayTraceInformationVisible", false);
		
		setVisible(cfg.getBoolean("isRayTraceInformationVisible"));
	}
	
	public void onTick(TickEvent event) {
		displayText.clear();
		
		MovingObjectPosition rayTrace = Minecraft.getMinecraft().objectMouseOver;
		if(rayTrace.typeOfHit == MovingObjectType.BLOCK) {
			BlockWrapper block = new BlockWrapper(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
			displayText.add("Position: " + block);
			displayText.add("Name: " + (new ItemStack(Item.getItemFromBlock(block.getBlock()), 1, block.getMetadata())).getDisplayName());
			displayText.add("NamedID: " + Block.blockRegistry.getNameForObject(block.getBlock()).replaceFirst("minecraft:", ""));
			displayText.add("Metadata: " + block.getMetadata());
		} else if(rayTrace.typeOfHit == MovingObjectType.ENTITY) {
			Entity entity = rayTrace.entityHit;
			displayText.add("Name: " + EntityList.getEntityString(entity));
			if(entity instanceof EntityLiving) {
				EntityLiving e = (EntityLiving)entity;
				displayText.add("Health: " + ((int)e.getHealth()) + "/" + ((int)e.getMaxHealth()));
			}
		}
		
		width = 0;
		for(String s : displayText) {
			width = Math.max(width, fontRenderer.getStringWidth(s));
		}
		width += 8;
		height = (fontRenderer.FONT_HEIGHT + 3) * displayText.size() + 3;
	}

	@Override
	public String getName() {
		return "Ray Trace Information";
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
