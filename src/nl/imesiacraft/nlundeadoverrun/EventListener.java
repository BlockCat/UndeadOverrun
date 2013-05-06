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
						
						@EventHandler
						public void onOptionClick(IconMenu.OptionClickEvent event) {         	
							String item = event.getName();
							int money = plugin.score.get(player.getName());
							int price = 0;

							for (ItemTypes it : ItemTypes.values()) {
								if (item.equalsIgnoreCase(it.getName())) {
									price = it.getPrice();
									if(money >= price) {
										plugin.score.put(player.getName(), money - price);
										event.getPlayer().sendMessage(ChatColor.GRAY + "You bought " + item);
										event.getPlayer().sendMessage(ChatColor.GRAY + "Your money: " +  plugin.score.get(player.getName()));
										player.getInventory().addItem(new ItemStack(it.getMaterial(), it.getAmount()));
									} else {
										player.sendMessage(ChatColor.GRAY + "Not enough money!");
									}
								}
							}

							event.setWillClose(true);
							event.setWillDestroy(true);

						}

					}, plugin);
					int i = 0;
					int j = 0;
					for (ItemTypes it : ItemTypes.values()) {
						int slot = i * 9 + j;
						i++;
						if (i > 3) {
							i = 0;
							j++;
						}				
						shop.setOption(slot, new ItemStack(it.getMaterial(), it.getAmount()), it.getName(), "Price: " + it.getPrice());
					}
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
		plugin.restoreGamemode(player);
		plugin.restoreInv(player);
		plugin.restoreLoc(player);
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