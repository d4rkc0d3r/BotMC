package d4rk.mc.gui;

import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import d4rk.mc.ChatColor;
import d4rk.mc.Config;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.LoadConfigEvent;
import d4rk.mc.event.PreSendPacketEvent;

public class ItemNameIdFinder extends BasicGuiScreen implements EventListener {
	
	private Item boundItem = Items.paper;
	private GuiTextField itemTextInput = null;
	private int inputGuiTop = 35;
	private Set<String> itemNameResultSet = new TreeSet();
	
	public ItemNameIdFinder() {
		mc = Minecraft.getMinecraft();
	}
	
	@Override
	public void initGui() {
		itemTextInput = new GuiTextField(fontRendererObj, width / 2 - 160, inputGuiTop, 92, fontRendererObj.FONT_HEIGHT);
        itemTextInput.setMaxStringLength(20);
        itemTextInput.setEnableBackgroundDrawing(true);
        itemTextInput.setVisible(true);
        itemTextInput.setTextColor(16777215);
        itemTextInput.setText("");
		
		Keyboard.enableRepeatEvents(true);
	}
	
	public void onLoadConfig(LoadConfigEvent event) {
		Config cfg = event.config;
		cfg.setDefault("ItemNameIdFinder.openWithItem", "paper");
		
		boundItem = (Item)Item.itemRegistry.getObject(cfg.getString("ItemNameIdFinder.openWithItem"));
	}
	
	public void onRightClick(PreSendPacketEvent event) {
		if(event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
			C08PacketPlayerBlockPlacement p = (C08PacketPlayerBlockPlacement)event.getPacket();
			try {
				if(p.func_149574_g().getItem().equals(boundItem)) {
					mc.displayGuiScreen(this);
					event.setDisabled(true);
				}
			} catch(NullPointerException npe) {
				// no item held in hand, do nothing
			}
		}
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		itemTextInput.mouseClicked(par1, par2, par3);
	}
	
	@Override
	protected void keyTyped(char par1, int par2) {
		if(itemTextInput.textboxKeyTyped(par1, par2)) {
			itemNameResultSet.clear();
			String input = itemTextInput.getText().toLowerCase();
			for(Object o : Item.itemRegistry.getKeys()) {
				String toTest = "" + o;
				if(toTest.startsWith("minecraft:")) {
					toTest = toTest.substring(10);
				}
				if(toTest.contains(input)) {
					int i = toTest.toLowerCase().indexOf(input.toLowerCase());
			        StringBuilder sb = new StringBuilder();
			        if(i > 0) {
			        	sb.append(ChatColor.WHITE);
			        	sb.append(toTest.substring(0, i));
			        }
		        	sb.append(ChatColor.DARK_GREEN);
		        	sb.append(toTest.substring(i, i + input.length()));
		        	if(i + input.length() < toTest.length()) {
			        	sb.append(ChatColor.WHITE);
			        	sb.append(toTest.substring(i + input.length()));
		        	}
					itemNameResultSet.add(sb.toString());
				}
			}
		}
		super.keyTyped(par1, par2);
	}
	
	@Override
	public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		
		itemTextInput.drawTextBox();
		
		drawString(fontRendererObj, "Search for:", width / 2 - 160, inputGuiTop - 12, 16777215);
		
		int i = 0;
		for(String s : itemNameResultSet) {
			drawString(fontRendererObj, s, width / 2 - 60 , inputGuiTop - 12 + i * 12, 16777215);
			++i;
		}
		
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public boolean isDestroyed() {
		// TODO Auto-generated method stub
		return false;
	}

}
