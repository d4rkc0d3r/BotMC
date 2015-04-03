package d4rk.mc.bot;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import d4rk.mc.BlockMarker;
import d4rk.mc.BlockWrapper;
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
			
			EntityLiving nearest = null;
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
					nearest = (EntityLiving)e;
					dist = d;
				}
			}
			
			if(nearest != null) {
				//BotMC.player.aimAtWithBow(nearest);
			}
			
			Vec3D pos = new Vec3D(mc.thePlayer);
			Vec3D speed = new Vec3D();

			float PI = (float)Math.PI;
			
	        speed.x = (-MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * PI) * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * PI));
	        speed.z = (MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * PI) * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * PI));
	        speed.y = (-MathHelper.sin(mc.thePlayer.rotationPitch / 180.0F * PI));
			
	        speed.mul(3); // fully drawn bow
	        
	        outside:
	        
			for(int i = 0; i < 255; i++) {
				final int sampleSize = 16;
				for(int j = 0; j < sampleSize; j++) {
					BlockWrapper block = new BlockWrapper(pos.clone().add(speed.clone().mul(j / (double)sampleSize)));
					if(!block.isAir()) {
						BlockMarker.markBlockAs(block, Blocks.wool, 14, 3);
						break outside;
					}
				}
				
				pos.add(speed);
				speed.mul(0.99);
				speed.y -= 0.05;
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
