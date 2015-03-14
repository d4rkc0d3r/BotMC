package d4rk.mc.bot;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import d4rk.mc.BotMC;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.TickEvent;

public class SimpleTestBot implements EventListener {
	private boolean isDestroyed = false;
	private int slot = 0;
	private int sleepTicks = 10 * 20;
	
	public SimpleTestBot() {
		EventManager.registerEvents(this);
	}
	
	public void onTick(TickEvent event) {
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
		
		sleepTicks = (int) (20 * (5 + Math.random() * 5));
	}
	
	public void destroy() {
		isDestroyed = true;
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}
}
