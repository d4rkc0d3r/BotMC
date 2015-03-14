package d4rk.mc.bot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.TickEvent;
import d4rk.mc.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.network.Packet;

public class PlayerHelper implements EventListener {
	private boolean isDestroyed = false;
	
	public Minecraft mc = null;
	public PlayerControllerMP ctrl = null;
	public EntityPlayer player = null;
	
	public PlayerHelper(EntityPlayer player, PlayerControllerMP ctrl) {
		this.player = player;
		this.ctrl = ctrl;
		this.mc = Minecraft.getMinecraft();
		
		EventManager.registerEvents(this);
	}
	
	public List<IRecipe> getRecipesFor(ItemStack item) {
		List<IRecipe> result = new ArrayList();
		
		for(IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList()) {
			ItemStack r = recipe.getRecipeOutput();
			if(r != null && r.getItem().equals(item.getItem()) && r.getItemDamage() == item.getItemDamage()) {
				result.add(recipe);
			}
		}
		
		return result;
	}
	
	public boolean hasOpenInventoryGUI() {
		return player.openContainer != null;
	}
	
	public int getOpenWindowId() {
		return (hasOpenInventoryGUI()) ? player.openContainer.windowId : 0;
	}
	
	public boolean hasWindowClicksQueued() {
		return !windowClickQueue.isEmpty();
	}
	
	private Queue<Pair<Integer, Integer>> windowClickQueue = new LinkedList<Pair<Integer, Integer>>();
	private int ticksToDequeueWindowClicks = 0;
	public void dequeueWindowClicks(TickEvent event) {
		if(ticksToDequeueWindowClicks-- > 0 || windowClickQueue.isEmpty()) {
			return;
		}
		
		Pair<Integer, Integer> p = windowClickQueue.remove();
		
		windowClick(p.getFirst(), p.getSecond() >> 1, (p.getSecond() & 1) == 1);
		
		ticksToDequeueWindowClicks = 1;
	}
	
	/**
	 * @param inventorySlot
	 * @param mouseClick 0 = left, 1 = right
	 * @param holdingShift
	 */
	public void queueWindowClick(int inventorySlot, int mouseClick, boolean holdingShift) {
		windowClickQueue.add(new Pair(inventorySlot, (mouseClick << 1) | (holdingShift ? 1 : 0)));
	}
	
	/**
	 * @param inventorySlot
	 * @param mouseClick 0 = left, 1 = right
	 * @param holdingShift
	 */
	public void windowClick(int inventorySlot, int mouseClick, boolean holdingShift) {
		ctrl.windowClick(getOpenWindowId(), inventorySlot, mouseClick, (holdingShift) ? 1 : 0, player);
	}
	
	public int findItemStack(Item item, int minSize) {
		List<ItemStack> inventory = player.inventoryContainer.getInventory();
		for(int i = inventory.size() - 1; i >= 0; i--) {
			ItemStack is = inventory.get(i);
			if(is != null && is.getItem().equals(item) && is.stackSize >= minSize) {
				return i;
			}
		}
		return -1;
	}
	
	public int findItemStack(ItemStack item) {
		List<ItemStack> inventory = player.inventoryContainer.getInventory();
		for(int i = inventory.size() - 1; i >= 0; i--) {
			ItemStack is = inventory.get(i);
			if((item == null && is == null) ||
					(is != null && item != null &&
					is.getItem().equals(item.getItem()) &&
					is.getItemDamage() == item.getItemDamage() &&
					is.stackSize >= item.stackSize)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean hasItemsInInventory(ItemStack item) {
		int amount = item.stackSize;
		for(ItemStack i : player.inventory.mainInventory) {
			if(i != null && i.getItem().equals(item.getItem()) && i.getItemDamage() == item.getItemDamage()) {
				amount -= i.stackSize;
			}
			if(amount <= 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasItemsInInventory(Item item, int amount) {
		for(ItemStack i : player.inventory.mainInventory) {
			if(i != null && i.getItem().equals(item)) {
				amount -= i.stackSize;
			}
			if(amount <= 0) {
				return true;
			}
		}
		return false;
	}
	
	public void destroy() {
		isDestroyed = true;
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}
}
