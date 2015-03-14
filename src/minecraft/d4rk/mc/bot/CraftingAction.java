package d4rk.mc.bot;

import java.util.List;

import d4rk.mc.BotMC;
import d4rk.mc.event.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

public class CraftingAction extends BotAction {
	private ItemStack toCraft;
	private boolean isCrafting = false;
	
	public CraftingAction(Block toCraft, int amount) {
		this(Item.getItemFromBlock(toCraft), amount);
	}
	
	public CraftingAction(Item toCraft, int amount) {
		this(new ItemStack(toCraft, amount));
	}
	
	public CraftingAction(ItemStack toCraft) {
		super();
		if(toCraft == null) {
			finish("PARAMETER_IS_NULL");
		}
		this.toCraft = toCraft;
	}
	
	private boolean hasPlayerItems(List<ItemStack> items, int craftAmount) {
		//TODO: improve player item check
		for(ItemStack item : items) {
			if(BotMC.player.findItemStack(item) == -1) {
				return false;
			}
		}
		return true;
	}
	
	public void onTick(TickEvent event) {
		if(isCrafting) {
			if(!BotMC.player.hasWindowClicksQueued()) {
				finish("true");
			}
			return;
		}
		
		PlayerHelper p = BotMC.player;
		
		Object lastError = null;
		
		List<IRecipe> recipes = p.getRecipesFor(toCraft);
		
		if(recipes.isEmpty()) {
			finish("NO_RECIPES_FOUND");
			return;
		}
		
		for(IRecipe recipe : recipes) {
			int width = 2;
			List<ItemStack> items = null;
			
			if(recipe instanceof ShapedRecipes) {
				items = ((ShapedRecipes)recipe).getRecipeInput();
				width = ((ShapedRecipes)recipe).getRecipeWidth();
			}
			
			if(recipe instanceof ShapelessRecipes) {
				items = ((ShapelessRecipes)recipe).getRecipeInput();
			}
			
			if(items == null) {
				lastError = "???";
				continue;
			}
			
			if(!hasPlayerItems(items, recipe.getRecipeOutput().stackSize)) {
				lastError = "NOT_ENOUGH_ITEMS_IN_INVENTORY";
				continue;
			}
			
			if(p.player.inventoryContainer.getInventory().size() == 5 * 9) { // 2*2 crafting grid
				if(recipe.getRecipeSize() > 4 || (width == 1 && recipe.getRecipeSize() == 3) || width == 3) {
					lastError = "RECIPE_NEEDS_CRAFTING_TABLE";
					continue;
				}
				
				//TODO: craft actually requested amount and not just one
				int craftingIndex = 1;
				for(int i = 0; i < items.size(); i++) {
					ItemStack is = items.get(i);
					if(is == null) {
						continue;
					}
					int slotIndex = p.findItemStack(is);
					p.queueWindowClick(slotIndex, 0, false);
					p.queueWindowClick(craftingIndex, 0, false);
					
					craftingIndex += (width == 1) ? 2 : 1;
				}
				
				p.queueWindowClick(0, 0, false);
				p.queueWindowClick(p.findItemStack(null), 0, false);
				
				p.queueWindowClick(1, 0, true);
				p.queueWindowClick(2, 0, true);
				p.queueWindowClick(3, 0, true);
				p.queueWindowClick(4, 0, true);
				
				lastError = null;
				break;
			}
			
			if(p.player.inventoryContainer.getInventory().size() == 4 * 9 + 10) { // 3*3 crafting table
				if(recipe instanceof ShapelessRecipes) {
					width = 3;
				}
				
				//TODO: implement crafting action in crafting table
				lastError = "CRAFTING_TABLE_NOT_YET_IMPLEMENTED";
				continue;
			}
		}
		
		if(lastError != null) {
			finish(lastError);
			return;
		}
		
		isCrafting = true;
	}
}
