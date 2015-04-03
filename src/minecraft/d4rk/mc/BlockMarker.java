package d4rk.mc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.TickEvent;
import d4rk.mc.util.Pair;

public class BlockMarker implements EventListener {
	private static BlockMarker instance = null;
	private boolean isDestroyed = false;

	private HashMap<BlockWrapper, Block> markedOriginal = new HashMap();
	private HashMap<BlockWrapper, Integer> markedMetadata = new HashMap();
	private HashMap<BlockWrapper, Integer> markedTimer = new HashMap();
	
	public BlockMarker() {
		if(instance != null) {
			instance.isDestroyed = true;
		}
		instance = this;
	}
	
	public void onTick(TickEvent event) {
		List<BlockWrapper> toRemove = new ArrayList<BlockWrapper>();
		
		for(BlockWrapper block : markedTimer.keySet()) {
			int timer = markedTimer.get(block);
			markedTimer.put(block, timer - 1);
			if(timer - 1 <= 0) {
				toRemove.add(block);
				block.setBlockAndMetadata(markedOriginal.get(block), markedMetadata.get(block));
			}
		}
		
		for(BlockWrapper block : toRemove) {
			markedTimer.remove(block);
			markedOriginal.remove(block);
			markedMetadata.remove(block);
		}
	}
	
	public static void markBlockAs(BlockWrapper target, Block block, int metadata, int time) {
		if(!instance.markedOriginal.containsKey(target)) {
			instance.markedOriginal.put(target, target.getBlock());
			instance.markedMetadata.put(target, target.getMetadata());
		}
		instance.markedTimer.put(target, time);
		target.setBlockAndMetadata(block, metadata);
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}
}
