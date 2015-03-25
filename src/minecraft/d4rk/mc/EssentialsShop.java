package d4rk.mc;

public class EssentialsShop extends Shop {
	public static Shop parse(BlockWrapper block) {
		if (!block.isSign()
				|| block.getSignLine(0).isEmpty()
				|| block.getSignLine(1).isEmpty()
				|| block.getSignLine(2).isEmpty()
				|| block.getSignLine(3).isEmpty())
			return null;
		
		Shop shop = null;
		String firstLine = ChatColor.remove(block.getSignLine(0));
		
		if(firstLine.equals("[Trade]")) {
			shop = new EssentialsShop();
			shop.block = block;
			shop.isBuy = true;
			if(!block.getSignLine(1).startsWith("$")) {
				return null;
			}
			String secondLine = block.getSignLine(1).substring(1);
			int indexOfDoublePoint = secondLine.indexOf(':');
			if(indexOfDoublePoint != -1) {
				secondLine = secondLine.substring(0, indexOfDoublePoint);
			}
			try {
				shop.priceBuy = Integer.parseInt(secondLine);
			} catch(NumberFormatException nfe) {
				return null;
			}
			
			String[] thirdSplit = block.getSignLine(2).split(" ", 2);
			if(thirdSplit.length != 2) {
				return null;
			}
			try {
				shop.count = Integer.parseInt(thirdSplit[0]);
			} catch(NumberFormatException nfe) {
				return null;
			}
			thirdSplit = thirdSplit[1].split(":", 2);
			if(thirdSplit.length != 2) {
				return null;
			}
			try {
				if(Integer.parseInt(thirdSplit[1]) < 1) {
					return null;
				}
			} catch(NumberFormatException nfe) {
				return null;
			}
			shop.itemName = thirdSplit[0];
			shop.userName = ChatColor.remove(block.getSignLine(3));
		}
		else if(firstLine.equals("[Buy]") || firstLine.equals("[Sell]")) {
			shop = new EssentialsShop();
			shop.block = block;
			shop.isBuy = firstLine.equals("[Buy]");
			shop.isSell = !shop.isBuy;
			
			shop.userName = "Server";
			
			try {
				shop.count = Integer.parseInt(block.getSignLine(1));
			} catch(NumberFormatException nfe) {
				return null;
			}
			
			shop.itemName = block.getSignLine(2);
			
			if(!block.getSignLine(3).startsWith("$")) {
				return null;
			}
			
			try {
				int price = Integer.parseInt(block.getSignLine(3).substring(1));
				if(shop.isBuy) {
					shop.priceBuy = price;
				}
				else {
					shop.priceSell = price;
				}
			} catch(NumberFormatException nfe) {
				return null;
			}
		}
		
		return shop;
	}
}
