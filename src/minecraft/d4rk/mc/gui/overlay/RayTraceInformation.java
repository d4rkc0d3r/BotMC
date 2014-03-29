package d4rk.mc.gui.overlay;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntitySkull;
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

	private boolean displayBlockName = true;
	private boolean displayBlockNamedID = true;
	private boolean displayBlockMetadata = true;
	private boolean displayBlockPosition = true;
	private boolean displayBlockSubInfo = true;
	
	private boolean displayEntityName = true;
	private boolean displayEntityHealth = true;
	private boolean displayEntityArmor = true;
	private boolean displayEntityIsInLove = true;
	private boolean displayEntitySubInfo = true;
	
	public RayTraceInformation() {
		displayText = new ArrayList();
		
		EventManager.registerEvents(this);
	}
	
	public void onLoadConfig(LoadConfigEvent event) {
		Config cfg = event.config;
		cfg.setDefault("RayTraceInformation.isVisible", false);
		
		cfg.setDefault("RayTraceInformation.displayBlockName", true);
		cfg.setDefault("RayTraceInformation.displayBlockNamedID", true);
		cfg.setDefault("RayTraceInformation.displayBlockMetadata", true);
		cfg.setDefault("RayTraceInformation.displayBlockPosition", true);
		cfg.setDefault("RayTraceInformation.displayBlockSubInfo", true);
		
		cfg.setDefault("RayTraceInformation.displayEntityName", true);
		cfg.setDefault("RayTraceInformation.displayEntityHealth", true);
		cfg.setDefault("RayTraceInformation.displayEntityArmor", true);
		cfg.setDefault("RayTraceInformation.displayEntityIsInLove", true);
		cfg.setDefault("RayTraceInformation.displayEntitySubInfo", true);
		
		
		setVisible(cfg.getBoolean("RayTraceInformation.isVisible"));
		
		displayBlockName = cfg.getBoolean("RayTraceInformation.displayBlockName");
		displayBlockNamedID = cfg.getBoolean("RayTraceInformation.displayBlockNamedID");
		displayBlockMetadata = cfg.getBoolean("RayTraceInformation.displayBlockMetadata");
		displayBlockPosition = cfg.getBoolean("RayTraceInformation.displayBlockPosition");
		displayBlockSubInfo = cfg.getBoolean("RayTraceInformation.displayBlockSubInfo");
		
		displayEntityName = cfg.getBoolean("RayTraceInformation.displayEntityName");
		displayEntityHealth = cfg.getBoolean("RayTraceInformation.displayEntityHealth");
		displayEntityArmor = cfg.getBoolean("RayTraceInformation.displayEntityArmor");
		displayEntityIsInLove = cfg.getBoolean("RayTraceInformation.displayEntityIsInLove");
		displayEntitySubInfo = cfg.getBoolean("RayTraceInformation.displayEntitySubInfo");
	}
	
	public void onTick(TickEvent event) {
		displayText.clear();
		
		Minecraft mc = Minecraft.getMinecraft();
		
		MovingObjectPosition rayTrace = Minecraft.getMinecraft().objectMouseOver;
		if(rayTrace.typeOfHit == MovingObjectType.BLOCK) {
			BlockWrapper block = new BlockWrapper(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
			String name;
			try {
				Item item = block.getItem();
				Block bl = (item instanceof ItemBlock && !block.getBlock().isFlowerPot()) ?
						Block.getBlockFromItem(item) : block.getBlock();
                int dmg = bl.getDamageValue(mc.theWorld, block.x, block.y, block.z);
                name = (new ItemStack(item, 1, dmg)).getDisplayName();
			} catch(NullPointerException npe) {
				name = block.getBlock().getLocalizedName();
			}
			if(displayBlockName) displayText.add("Name: " + name);
			if(displayBlockNamedID) displayText.add("NamedID: " + Block.blockRegistry.getNameForObject(block.getBlock()).replaceFirst("minecraft:", ""));
			if(displayBlockMetadata) displayText.add("Metadata: " + block.getMetadata());
			if(displayBlockPosition) displayText.add("Position: " + block);
			TileEntity te = block.getTileEntity();
			if(te instanceof TileEntitySkull && displayBlockSubInfo) {
				TileEntitySkull skull = (TileEntitySkull) te;
				if(!skull.func_145907_c().isEmpty()) {
					displayText.add(" SkullOwner: " + skull.func_145907_c());
				}
			}
		} else if(rayTrace.typeOfHit == MovingObjectType.ENTITY) {
			Entity entity = rayTrace.entityHit;
			if(entity instanceof EntityPlayer && displayEntityName) {
				displayText.add("Name: " + ((EntityPlayer)entity).getGameProfile().getName());
			} else if(displayEntityName) {
				displayText.add("Name: " + EntityList.getEntityString(entity));
			}
			if(entity instanceof EntityLivingBase) {
				EntityLivingBase e = (EntityLivingBase)entity;
				if(displayEntityHealth) displayText.add("Health: " + ((int)e.getHealth()) + "/" + ((int)e.getMaxHealth()));
				if(displayEntityArmor) displayText.add("Armor: " + e.getTotalArmorValue());
				if(entity instanceof EntityAnimal && displayEntityIsInLove) {
					EntityAnimal animal = (EntityAnimal)entity;
					displayText.add("IsInLove: " + animal.isInLove());
				}
			} else if(entity instanceof EntityItemFrame && displayEntitySubInfo) {
				EntityItemFrame frame = (EntityItemFrame)entity;
				ItemStack i = frame.getDisplayedItem();
				if(i != null) {
					displayText.add(" ItemName: " + i.getDisplayName());
					displayText.add(" ItemID: " + Item.itemRegistry.getNameForObject(i.getItem()).replace("minecraft:", "")
													  + ((i.getHasSubtypes()) ? ":" + i.getItemDamage() : ""));
				}
			}
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
