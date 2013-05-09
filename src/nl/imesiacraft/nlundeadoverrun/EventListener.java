package nl.imesiacraft.nlundeadoverrun;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener{

	public UndeadOverrun plugin;
	private FireworkEffectPlayer fplayer = new FireworkEffectPlayer();
	public EventListener(UndeadOverrun plugin) {
		this.plugin = plugin;
	}
	//logger
	Logger log = Logger.getLogger("Minecraft");

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
	public void onEntityDamage (EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			if (event.getDamage() >= player.getHealth() && plugin.playing.contains(player)) {
				
				event.setCancelled(true);
				UndeadPlayers.restorePlayer(player);

				plugin.playing.remove(player);
				plugin.giveReward(player);

				plugin.checkGame();
			}
		}
	}
	
	@EventHandler
	public void onEntityCombust(EntityCombustEvent event){
		if (event.getEntity() instanceof Zombie) {
			Zombie zombie = (Zombie) event.getEntity();
			String name = ((CraftZombie)zombie).getHandle().getCustomName();
			if (name.equalsIgnoreCase("Zombie") || name.equalsIgnoreCase("Strong Zombie") || name.equalsIgnoreCase("Death Zombie") || name.equalsIgnoreCase("Hell Zombie")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(plugin.playing.contains(player)) {
			//remove player from game
			plugin.playing.remove(player);
			UndeadPlayers.restorePlayer(player);
			//check if game was finished
			plugin.giveReward(player);
			plugin.checkGame();
		}
	}
}