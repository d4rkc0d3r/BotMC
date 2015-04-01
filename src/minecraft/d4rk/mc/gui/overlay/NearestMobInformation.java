package d4rk.mc.gui.overlay;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import d4rk.mc.BlockWrapper;
import d4rk.mc.BotMC;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.TickEvent;
import d4rk.mc.gui.BasicGuiOverlay;
import d4rk.mc.util.Vec3D;

public class NearestMobInformation extends BasicGuiOverlay implements EventListener {
	private ArrayList<String> displayText = new ArrayList();

	public NearestMobInformation() {
		isVisible = true;
		EventManager.registerEvents(this);
	}
	
	public void onTick(TickEvent event) {
		displayText.clear();
		
		Minecraft mc = Minecraft.getMinecraft();
		
		Vec3D playerPos = Vec3D.getPlayerFootPos(mc.thePlayer);
		List<Entity> entities = mc.theWorld.loadedEntityList;
		
		Entity nearest = null;
		double dist = Double.MAX_VALUE;
		
		for(Entity e : entities) {
			if(e == mc.thePlayer) {
				continue;
			}
			Vec3D pos = new Vec3D(e);
			double d = pos.dist(playerPos);
			if(d < dist) {
				nearest = e;
				dist = d;
			}
		}
		
		if(nearest != null) {
			displayText.add(EntityList.getEntityString(nearest));
			displayText.add(dist + "m");
		}
		
		width = 0;
		for(String s : displayText) {
			width = Math.max(width, fontRenderer.getStringWidth(s));
		}
		width += 8;
		height = (displayText.size() == 0) ? 0 : (fontRenderer.FONT_HEIGHT + 3) * displayText.size() + 3;
	}
	
	@Override
	public String getName() {
		return "Nearest Mob Information";
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
