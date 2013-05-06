package nl.imesiacraft.nlundeadoverrun;

import org.bukkit.Material;

public enum ItemTypes {
	WOODENS_WORD(6, Material.WOOD_SWORD, 1, "Wooden Sword"),
	STONE_SWORD(16, Material.STONE_SWORD, 1, "Stone Sword"),
	IRON_SWORD(25, Material.IRON_SWORD, 1, "Iron Sword"),
	DIAMOND_SWORD(50, Material.DIAMOND_SWORD, 1, "Diamond Sword"),
	LEATHER_HELMET(4, Material.LEATHER_HELMET, 1, "Leather Helmet"),
	LEATHER_ARMOR(6, Material.LEATHER_CHESTPLATE, 1, "Leather Chestplate"),
	LEATHER_LEGGINGS(5, Material.LEATHER_LEGGINGS, 1, "Leather Leggings"),
	LEATHER_BOOTS(4, Material.LEATHER_BOOTS, 1, "Leather Boots"),
	CHAIN_HELMET(16, Material.CHAINMAIL_HELMET, 1, "Chain Helmet"),
	CHAIN_ARMOR(20, Material.CHAINMAIL_CHESTPLATE, 1, "Chain Chestplate"),
	CHAIM_LEGGINGS(18, Material.CHAINMAIL_LEGGINGS, 1, "Chain Leggings"),
	CHAIN_BOOTS(16, Material.CHAINMAIL_BOOTS, 1, "Chain Boots"),
	IRON_HELMET(25, Material.IRON_HELMET, 1, "Iron Helmet"),
	IRON_ARMOR(28, Material.IRON_CHESTPLATE, 1, "Iron Chestplate"),
	IRON_LEGGINGS(26, Material.IRON_LEGGINGS, 1, "Iron Leggings"),
	IRON_BOOTS(25, Material.IRON_BOOTS, 1, "Iron Boots"),
	DIAMOND_HELMET(45, Material.DIAMOND_HELMET, 1, "Diamond Helmet"),
	DIAMOND_ARMOR(50, Material.DIAMOND_CHESTPLATE, 1, "Diamond Chestplate"),
	DIAMOND_LEGGINGS(46, Material.DIAMOND_LEGGINGS, 1, "Diamond Leggings"),
	DIAMOND_BOOTS(45, Material.DIAMOND_BOOTS, 1, "Diamond Boots"),
	BREAD(2, Material.BREAD, 4, "Bread"),
	BEEF(2, Material.COOKED_BEEF, 4, "Beef"),
	ARROW(4, Material.ARROW, 8, "Arrow"),
	BOW(20, Material.BOW, 1, "Bow");
	
	private int price = 0;
	private Material material = null;
	private int amount = 0;
	private String name = null;
	
	ItemTypes(int price, Material material, int amount, String name) {
		
	}
	
	public int getPrice() {
		return price;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public String getName() {
		return name;
	}
	
	

}
