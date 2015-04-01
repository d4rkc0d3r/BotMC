package d4rk.mc.bot;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import d4rk.mc.BotMC;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.TickEvent;
import d4rk.mc.util.Vec2D;
import d4rk.mc.util.Vec3D;

public class SimpleTestBot implements EventListener {
	private boolean isDestroyed = false;
	private int slot = 0;
	private int sleepTicks = 10 * 20;
	
	public SimpleTestBot() {
		EventManager.registerEvents(this);
	}
	
	public void onTick(TickEvent event) {
		if(BotMC.player.player.getHeldItem() != null && BotMC.player.player.getHeldItem().getItem().equals(Items.bow)) {
			Minecraft mc = Minecraft.getMinecraft();
			
			Vec3D playerPos = Vec3D.getPlayerFootPos(mc.thePlayer);
			List<Entity> entities = mc.theWorld.loadedEntityList;
			
			Entity nearest = null;
			double dist = Double.MAX_VALUE;
			
			for(Entity e : entities) {
				if(e == mc.thePlayer) {
					continue;
				}
				if(!(e instanceof EntityLiving)) {
					continue;
				}
				Vec3D pos = new Vec3D(e);
				double d = pos.dist(playerPos);
				if(d < dist && d <= 25) {
					nearest = e;
					dist = d;
				}
			}
			
			if(nearest != null) {
				Vec3D dir = new Vec3D(nearest).sub(playerPos).setLen(1);
				Vec2D d = new Vec2D(dir.x, dir.z);
				double a = 180 / Math.PI * d.angle(new Vec2D(0, 1));
				if(d.x > 0) {
					a =  -a;
				}
				BotMC.player.player.rotationYaw = (float)a;
			}
		}
		
		
		/*
		if(sleepTicks-- > 0) {
			return;
		}
		
		if(BotMC.player == null || !BotMC.player.hasOpenInventoryGUI()) {
			return;
		}
		
		BotActionStack bas = BotActionStack.getInstance();
		
		if(!BotMC.player.hasItemsInInventory(Item.getItemFromBlock(Blocks.torch), 16) && bas.isEmpty()) {
			bas.push(new CraftingAction(Blocks.torch, 16));
		}
		
		sleepTicks = (int) (20 * (5 + Math.random() * 5));*/
	}
	
	public void destroy() {
		isDestroyed = true;
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}
}
