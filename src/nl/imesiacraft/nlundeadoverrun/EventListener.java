package nl.imesiacraft.nlundeadoverrun;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener{
	public UndeadOverrun plugin;
	public EventListener(UndeadOverrun plugin) {
		this.plugin = plugin;
	}
	//logger
	Logger log = Logger.getLogger("Minecraft");
    //circle method
    public static List<Location> circle (Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx +r; x++)
            for (int z = cz - r; z <= cz +r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r*r && !(hollow && dist < (r-1)*(r-1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                        }
                    }
     
        return circleblocks;
    }
    //FireworkEffectPlayer
    FireworkEffectPlayer fplayer = new FireworkEffectPlayer();
    
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if(plugin.playing.contains(player)) {
			//Location deathLoc = new Location(player.getWorld(), 2805, 103, -4320);
			player.sendMessage(ChatColor.GRAY + "You have died!");
			//remove player from game
			plugin.playing.remove(player);
			//check if game was finished
			if(plugin.playing.isEmpty()) {
				plugin.giveReward(player);
				plugin.started = false;
				Bukkit.broadcastMessage(ChatColor.GRAY + "UndeadOverrun game finished!");
				Bukkit.broadcastMessage(ChatColor.GRAY + "Wave reached: " + plugin.wave);
				plugin.wave = 0;
				plugin.score.clear();
				plugin.removeMobs();
			}
		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if(plugin.playing.contains(player)) {
			if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() ==  Action.RIGHT_CLICK_BLOCK) {
				if(event.getClickedBlock().getType() == Material.EMERALD_BLOCK) {
					if(plugin.kills == plugin.needKills || plugin.kills > plugin.needKills) {
						plugin.nextWave();
					} else {
						int needed = plugin.needKills - plugin.kills;
						player.sendMessage(ChatColor.GRAY + "Not enough kills! You need " + needed + " more kills!");
					}					
				}		
				}		
			if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() ==  Action.RIGHT_CLICK_BLOCK) {
				if(event.getClickedBlock().getType() == Material.GOLD_BLOCK) {
			        IconMenu shop = new IconMenu("Shop", 36, new IconMenu.OptionClickEventHandler() {
			            @Override
			            public void onOptionClick(IconMenu.OptionClickEvent event) {         	
			            	String item = event.getName();
			            	int money = plugin.score.get(player.getName());
			            	int price = 0;            	
			            	if(item == "Wooden Sword") {	
                              price = 6;
                              if(money > price || money == price) {
                                  plugin.score.put(player.getName(), money - price);
                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
                                  player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
                              } else {
                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
                              } 
			            	}
			            	if(item == "Stone Sword") {	
	                              price = 16;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Iron Sword") {	
	                              price = 25;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Diamond Sword") {	
	                              price = 50;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Leather Helmet") {
	                              price = 4;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Leather Chestplate") {
	                              price = 6;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
	                                  
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Leather Leggings") {
	                              price = 5;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Leather Boots") {
	                              price = 4;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Chain Helmet") {
	                              price = 16;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Chain Chestplate") {
	                              price = 20;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Chain Leggings") {
	                              price = 18;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Chain Boots") {
	                              price = 16;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Iron Helmet") {
	                              price = 25;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Iron Chestplate") {
	                              price = 28;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Iron Leggings") {
	                              price = 26;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Iron Boots") {
	                              price = 25;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Diamond Helmet") {
	                              price = 45;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Diamond Chestplate") {
	                              price = 50;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Diamond Leggings") {
	                              price = 46;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Diamond Boots") {
	                              price = 45;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Bread") {
	                              price = 2;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().addItem(new ItemStack(Material.BREAD, 4));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Beef") {
	                              price = 2;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 4));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Arrow") {
	                              price = 4;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().addItem(new ItemStack(Material.ARROW, 8));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	if(item == "Bow") {
	                              price = 20;
	                              if(money > price || money == price) {
	                                  plugin.score.put(player.getName(), money - price);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "You brought " + item);
	                                  event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
	                                  player.getInventory().addItem(new ItemStack(Material.BOW, 1));
	                              } else {
	                            	  player.sendMessage(ChatColor.GRAY + "Not enough money!");
	                              } 
				            	}
			            	
			            	event.setWillClose(true);
			                event.setWillDestroy(true);
			                
			            }
			            
			        }, plugin)
			        //Swords
			        .setOption(0, new ItemStack(Material.WOOD_SWORD, 1), "Wooden Sword", "Price: 6")
			        .setOption(9, new ItemStack(Material.STONE_SWORD, 1), "Stone Sword", "Price: 16")
			        .setOption(18, new ItemStack(Material.IRON_SWORD, 1), "Iron Sword", "Price: 25")
			        .setOption(27, new ItemStack(Material.DIAMOND_SWORD, 1), "Diamond Sword", "Price: 50")
			        //Leather Armor
			        .setOption(1, new ItemStack(Material.LEATHER_HELMET, 1), "Leather Helmet", "Price: 4")
			        .setOption(10, new ItemStack(Material.LEATHER_CHESTPLATE, 1), "Leather Chestplate", "Price: 6")
			        .setOption(19, new ItemStack(Material.LEATHER_LEGGINGS, 1), "Leather Leggings", "Price: 5")
			        .setOption(28, new ItemStack(Material.LEATHER_BOOTS, 1), "Leather Boots", "Price: 4")
			        //Chain Armor
			        .setOption(2, new ItemStack(Material.CHAINMAIL_HELMET), "Chain Helmet", "Price: 16")
			        .setOption(11, new ItemStack(Material.CHAINMAIL_CHESTPLATE), "Chain Chestplate", "Price: 20")
			        .setOption(20, new ItemStack(Material.CHAINMAIL_LEGGINGS), "Chain Leggings", "Price: 18")
			        .setOption(29, new ItemStack(Material.CHAINMAIL_BOOTS), "Chain Boots", "Price: 16")
			        //Iron Armor
			        .setOption(3, new ItemStack(Material.IRON_HELMET), "Iron Helmet", "Price: 25")
			        .setOption(12, new ItemStack(Material.IRON_CHESTPLATE), "Iron Chestplate", "Price: 28")
			        .setOption(21, new ItemStack(Material.IRON_LEGGINGS), "Iron Leggings", "Price: 26")
			        .setOption(30, new ItemStack(Material.IRON_BOOTS), "Iron Boots", "Price: 25")
			        //Diamond Armor
			        .setOption(4, new ItemStack(Material.DIAMOND_HELMET), "Diamond Helmet", "Price: 45")
			        .setOption(13, new ItemStack(Material.DIAMOND_CHESTPLATE), "Diamond Chestplate", "Price: 50")
			        .setOption(22, new ItemStack(Material.DIAMOND_LEGGINGS), "Diamond Leggings", "Price: 46")
			        .setOption(31, new ItemStack(Material.DIAMOND_BOOTS), "Diamond Boots", "Price: 45")        
			        //Food
			        .setOption(5, new ItemStack(Material.BREAD, 4), "Bread", "Price: 2")
			        .setOption(6, new ItemStack(Material.COOKED_BEEF, 4), "Beef", "Price: 2")
			        //Bow/Arrow	         
			        .setOption(7, new ItemStack(Material.ARROW, 8), "Arrow", "Price: 4")
			        .setOption(8, new ItemStack(Material.BOW, 1), "Bow", "Price: 20");
			        shop.open(player);
				}
			}
		}

	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity zombie = event.getEntity();
		Player player = event.getEntity().getKiller();	
		if(zombie instanceof Zombie) {
			if(player instanceof Player) {
				final Player p = event.getEntity().getKiller();
				if(plugin.playing.contains(p)) {		
					if(!plugin.score.containsKey(player.getName())) {
						plugin.score.put(player.getName(), 0);
					}
					int oldscore = plugin.score.get(player.getName());
					int newscore = oldscore + 2;
					plugin.kills++;
					plugin.score.put(player.getName(), newscore);
					if(!plugin.waveReady == true) {
					if(plugin.kills == plugin.needKills || plugin.kills > plugin.needKills) {
						plugin.waveReady = true;
						//plugin.kills = 0;
						//plugin.nextWave();
						for(Player speler : plugin.playing) {
							speler.sendMessage(ChatColor.GRAY + "New wave is ready! Next wave is starting automaticly in 20 seconds");
							speler.sendMessage(ChatColor.GRAY + "Your coins: " + plugin.score.get(speler.getName()));
						}
						Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								plugin.nextWave();
							}
							
						}, 20 * 20);
		
				}
				}	
			}		
		}
	}

	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if(plugin.playing.contains(player)) {
			event.getDrops().clear();
			event.setDeathMessage(player.getName() + " has died in UndeadOverrun");
		}
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(plugin.playing.contains(player)) {
	         boolean day = day();
	         if(day == true) {
	         	World world = Bukkit.getWorld("Imesia");
	        	world.setTime(13000);
	         }	
		}
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(plugin.playing.contains(player)) {
			//remove player from game
			plugin.playing.remove(player);
			//check if game was finished
			if(plugin.playing.isEmpty()) {
				plugin.giveReward(player);
				plugin.started = false;
				Bukkit.broadcastMessage(ChatColor.GRAY + "UndeadOverrun game finished!");
				Bukkit.broadcastMessage(ChatColor.GRAY + "Wave reached: " + plugin.wave);
				plugin.wave = 0;
				plugin.score.clear();
				plugin.removeMobs();
			}
		}
	}
	public boolean day() {
	    Server server = Bukkit.getServer();
	    long time = server.getWorld("Imesia").getTime();

	    if(time > 0 && time < 12300) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	
}