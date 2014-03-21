package d4rk.mc.gui.overlay;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.world.biome.BiomeGenBase;

import org.lwjgl.opengl.GL11;

import d4rk.mc.Config;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.LoadConfigEvent;
import d4rk.mc.event.PostSendPacketEvent;
import d4rk.mc.gui.BasicGuiOverlay;

public class OreLevelIndicator extends BasicGuiOverlay implements EventListener {
	public List<ItemStack> oreList = new ArrayList();
    protected static RenderItem itemRenderer = new RenderItem();
    private ItemStack quarz;
    private ItemStack emerald;
    private ItemStack diamond;
    private ItemStack redstone;
    private ItemStack lapis;
    private ItemStack gold;
    private ItemStack iron;
	
	public OreLevelIndicator() {
		super();
		quarz = new ItemStack((Item)Item.itemRegistry.getObject("quartz"));
		emerald = new ItemStack((Item)Item.itemRegistry.getObject("emerald"));
		diamond = new ItemStack((Item)Item.itemRegistry.getObject("diamond"));
		redstone = new ItemStack((Item)Item.itemRegistry.getObject("redstone"));
		lapis = new ItemStack((Item)Item.itemRegistry.getObject("dye"), 1, 4);
		gold = new ItemStack((Item)Item.itemRegistry.getObject("gold_ingot"));
		iron = new ItemStack((Item)Item.itemRegistry.getObject("iron_ingot"));
		EventManager.registerEvents(this);
	}
	
	public void onPlayerMoveEvent(PostSendPacketEvent event) {
		if(!(event.getPacket() instanceof C04PacketPlayerPosition || event.getPacket() instanceof C06PacketPlayerPosLook)) {
			return;
		}
		BiomeGenBase biome = mc.theWorld.getBiomeGenForCoords(
				(int)Math.floor(mc.thePlayer.posX), (int)Math.floor(mc.thePlayer.posZ));
		
		oreList.clear();
		
		if(biome == BiomeGenBase.sky) {
			
		} else if(biome == BiomeGenBase.hell) {
			if(mc.thePlayer.posY <= 128) {
				oreList.add(quarz);
			}
		} else {
			if(mc.thePlayer.posY <= 30 && (biome == BiomeGenBase.extremeHills || biome == BiomeGenBase.extremeHills.extremeHillsEdge)) {
				oreList.add(emerald);
			}
			if(mc.thePlayer.posY <= 15) {
				oreList.add(diamond);
				oreList.add(redstone);
			}
			if(mc.thePlayer.posY <= 23) {
				oreList.add(lapis);
			}
			if(mc.thePlayer.posY <= 32) {
				oreList.add(gold);
			}
			if(mc.thePlayer.posY <= 64) {
				oreList.add(iron);
			}
		}
		
		width = 8 + 20 * oreList.size();
		height = 24;
	}
	
	public void onLoadConfig(LoadConfigEvent event) {
		Config cfg = event.config;
		cfg.setDefault("OreLevelIndicator.isVisible", true);
		
		setVisible(cfg.getBoolean("OreLevelIndicator.isVisible"));
	}
	
	@Override
	public String getName() {
		return "Ore level indicator";
	}

	@Override
	public void draw() {
		for(int i = 0; i < oreList.size(); ++i) {
			ItemStack item = oreList.get(i);
	        zLevel = 200.0F;
	        itemRenderer.zLevel = 200.0F;
	        itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), item, i * 20 + 8 , 8);
	        zLevel = 0.0F;
	        itemRenderer.zLevel = 0.0F;
		}
        GL11.glDisable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
