package nl.imesiacraft.nlundeadoverrun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class UndeadOverrun extends JavaPlugin {
/* ToDo:
 * firework effects
 */	
	//list for players that are ingame
	public List<Player> playing = new ArrayList<Player>();
	//Logger
	private Logger log = null;
	//player inventory store
	private HashMap<Player, ItemStack[][]> inv_store = new HashMap<Player, ItemStack[][]>();
	//player location store
	private HashMap<Player, Location> loc_store = new HashMap<Player, Location>();
	//player gamemode store
	private HashMap<Player, String> gm_store = new HashMap<Player, String>();
	//Ready players store
	private List<Player> ready = new ArrayList<Player>();
	//notReady players store
	private List<Player> notReady = new ArrayList<Player>();
	//boolean if the game was started
	public boolean started = false;
	//Random generator
	private Random randomGenerator = new Random();
	//wave
	public int wave = 0;
	//kills to go up waves
	public int kills = 0;
	//needed kills for next wave
	public int needKills = 0;
	//Boolean if wave is ready
	public boolean waveReady = false;
	//score
	public HashMap<String, Integer> score = new HashMap<String, Integer>();
	//IconMenu
	public static IconMenu im;
    //FireworkEffectPlayer
    FireworkEffectPlayer fplayer = new FireworkEffectPlayer();
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
    //blocks above method
    public static List<Location> above (Location loc, Integer h) {
    	List<Location> aboveblocks = new ArrayList<Location>();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
    	for(int i = 0; i < h; i++) {
    		int ny = y + i;
    		Location l = new Location(loc.getWorld(), x, ny, z);
    		aboveblocks.add(l);
    	} 	
    	return aboveblocks;
    }
	//Disable
    public void onDisable() {
    	log.info("UndeadOverrun disabled!");	
    }
    
    //Enable
    public void onEnable() {
    	//Listener class
    	EventListener listener = new EventListener(this);
    	
    	//logger
    	log = Logger.getLogger("Minecraft");
    	
    	log.info("UndeadOverrun enabled!");
    	Bukkit.getPluginManager().registerEvents(listener, this);
    }

    //Commands
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	Player player = (Player) sender;
    	if(commandLabel.equalsIgnoreCase("uo")) {
    		if(args.length == 0) {
               showHelpMenu(player);
    		} else if(args.length == 1) {
    			if(args[0].equalsIgnoreCase("join")) {
    	        	if(!started == true) {
    	        		if(!playing.contains(player)) {
	    		    		   player.sendMessage(ChatColor.GRAY + "Successfully joined!");
	      	        			for(Player p : playing) {
	    	        				p.sendMessage(ChatColor.GRAY + player.getName() + " joined!");
	    	        			}
	      	        			playing.add(player);
	      	        			notReady.add(player);
	      	        			return true;
    	        		} else {
    	        			player.sendMessage(ChatColor.GRAY + "You already joined!!");
    	        			return true;
    	        		}
    	        	} else {
    	        		player.sendMessage(ChatColor.GRAY + "Game already started!");
    	        		return true;
    	        	}
    			} else if(args[0].equalsIgnoreCase("leave")) {
   	        		if(playing.contains(player)) {
	        			playing.remove(player);
	        			player.sendMessage(ChatColor.GRAY + "You left the game!");
	        			if(ready.contains(player)) {
	        				ready.remove(player);
	        			}
	        			if(notReady.contains(player)) {
	        				notReady.remove(player);
	        			}
	        			for(Player p : playing) {
	        				p.sendMessage(player.getName() + " has left!");
	        			}
	        			return true;
	        		} else {
	        			player.sendMessage(ChatColor.GRAY + "You are not playing!");
	        			return true;
	        		}
    			} else if(args[0].equalsIgnoreCase("players")) {
    				player.sendMessage(ChatColor.GRAY + "Players: " + playing.size());
    				for(Player p : playing) {
    					player.sendMessage(ChatColor.GRAY + p.getName());
    				}
    			} else if(args[0].equalsIgnoreCase("stats")) {
    				if(started == false) {
    					player.sendMessage(ChatColor.GRAY + "The game hasn't started yet!");
    					return true;
    				} else {
    					player.sendMessage(ChatColor.GRAY + "Wave: " + wave);
	    				player.sendMessage(ChatColor.GRAY + "Kills : " + kills);
	    				player.sendMessage(ChatColor.GRAY + "Kills needed : " + needKills);
    					return true;
    				}
    			} else if(args[0].equalsIgnoreCase("ready")) {
    				ready.add(player);
    				notReady.remove(player);
    				for(Player p : playing) {
    					p.sendMessage(ChatColor.GRAY + player.getName() + " is ready to kill some zombies!");
    				}
    			} else if(args[0].equalsIgnoreCase("start")) {
    				if(!started == true) {
 	        			if(playing.contains(player)) {
 	        				if(notReady.size() == 0) {
	        				//World world = Bukkit.getWorld("Imesia");
	        				Location startLoc = new Location(player.getWorld(), 2790, 87, -4318);
	            			for(Player p : playing) {
	            			//save gamemode
	            			saveGamemode(p);
	            			p.setGameMode(GameMode.ADVENTURE);
	            			//Inventory stuff
	            			PlayerInventory inv = p.getInventory();
	             			saveInv(p);
	            			safeLoc(p);
	            			ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
	            			ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
	            			ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
	            			ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
	            			ItemStack sword = new ItemStack(Material.WOOD_SWORD, 1);
	            			ItemStack bow = new ItemStack(Material.BOW, 1);
	            			ItemStack arrows = new ItemStack(Material.ARROW, 30);
	            			ItemStack food = new ItemStack(Material.BREAD,4);
	            			inv.clear();
	            			inv.setBoots(boots);
	            			inv.setLeggings(leggings);
	            			inv.setChestplate(chestplate);
	            			inv.setHelmet(helmet);
	            			inv.addItem(sword);
	            			inv.addItem(bow);
	            			inv.addItem(food);
	            			inv.addItem(arrows);
	            			//create player in score hashmap
	            			score.put(p.getName(), 0);
	            			//teleport
	            			p.teleport(startLoc);
	            			//wish the player luck!
	            			player.sendMessage(ChatColor.GRAY + "Goodluck!");
	            			//start the next wave
	            			started = true;
	            			}
	            			nextWave();
	            			} else {
	            				player.sendMessage(ChatColor.GRAY + "Not all players are ready!");
	            				for(Player p : notReady) {
	            					player.sendMessage(ChatColor.GRAY + p.getName());
	            				}
	            			}
 	        			}
	        		} else {
	        			player.sendMessage(ChatColor.GRAY + "Game already started!");
	        		}
    			} else if(args[0].equalsIgnoreCase("coins")) {
	        		if(playing.contains(player)) {
    	        		int coins = score.get(player.getName());
    	        		player.sendMessage(ChatColor.GRAY + "Your coins: " + coins);
	        		} else {
	        			player.sendMessage(ChatColor.GRAY + "You are not playing!");
	        		}
    			} else if(args[0].equalsIgnoreCase("quit")) {
	        		if(playing.contains(player)) {
	        			PlayerInventory inv = player.getInventory();
	        			inv.clear();
	        			restoreInv(player);	
	        			restoreLoc(player);
	        			restoreGamemode(player);
	        			playing.remove(player);
	        			if(playing.isEmpty()) {
	        				started = false;
	        				Bukkit.broadcastMessage(ChatColor.GRAY + "UndeadOverrun game finished!");
	        				Bukkit.broadcastMessage(ChatColor.GRAY + "Wave reached: " + wave);
	        				wave = 0;
	        				score.clear();
	        				removeMobs();
	        			}	
	        			return true;
	        		} else {
	        			player.sendMessage(ChatColor.GRAY + "You are not playing!");
	        			return true;
	        		}
    			} else if(args[0].equalsIgnoreCase("")) {
    				
    			}
    		} else if(args.length == 2 || args.length > 2) {
    			player.sendMessage(ChatColor.GRAY + "Too many arguments! Use /uo");
    		}
    	}
    	return false;
}
    public void saveInv(Player p){
        ItemStack[] [] store = new ItemStack[2][1];
        store[0] = p.getInventory().getContents();
        store[1] = p.getInventory().getArmorContents();
        this.inv_store.put(p, store);
    }
   

	public void restoreInv(Player p){
        p.getInventory().clear();
        p.getInventory().setContents(this.inv_store.get(p)[0]);
        p.getInventory().setArmorContents(this.inv_store.get(p)[1]);
        this.inv_store.remove(p);
        //Isn't needed anymore.
        //p.updateInventory();
    }
    public void safeLoc(Player p){
    	Location loc = p.getLocation();
    	this.loc_store.put(p, loc);
    }
    public void restoreLoc(Player p){
    	Location loc = this.loc_store.get(p);
    	p.teleport(loc);
    }
    public void saveGamemode(Player p) {
    	if(p.getGameMode() == GameMode.SURVIVAL) {
    		gm_store.put(p, "s");
    	} else if(p.getGameMode() == GameMode.CREATIVE) {
    		gm_store.put(p, "c");
    	}
    }
    public void restoreGamemode(Player p) {
    	String gm = gm_store.get(p);
    	if (gm.equals("s")) {
    		p.setGameMode(GameMode.SURVIVAL);
    	} else if(gm.equals("c")) {
    		p.setGameMode(GameMode.CREATIVE);
    	}
    }
    
	public void nextWave() {
		waveReady = false;
		kills = 0;
		needKills = 0;
    	World world = Bukkit.getWorld("Imesia");
    	world.setTime(13000);
    	/*Zombie spawn locations:
    	 *  dichtbij kasteel:
    	 *     2782, 83, -4340
    	 *     2803, 83, -4342
    	 *     2832, 81, -4340
    	 *     2836, 80, -4310
    	 *     2828, 82, -4292
    	 *     2805, 86, -4289
    	 *     2790, 91, -4300
    	 *     2786, 87, -4326   
    	 */
    	Location l1 = new Location(world, 2782, 83, -4340);
    	Location l2 = new Location(world, 2803, 83, -4342);
    	Location l3 = new Location(world, 2832, 81, -4340);
    	Location l4 = new Location(world, 2836, 80, -4310);
    	Location l5 = new Location(world, 2828, 82, -4292);
    	Location l6 = new Location(world, 2805, 86, -4289);
    	Location l7 = new Location(world, 2790, 91, -4300);
    	Location l8 = new Location(world, 2786, 87, -4326);	
    	Location spec = new Location(world, 2806, 75, -4341);
    	ArrayList<Location> locs = new ArrayList<Location>();
    	locs.add(l1);
    	locs.add(l2);
    	locs.add(l3);
    	locs.add(l4);
    	locs.add(l5);
    	locs.add(l6);
    	locs.add(l7);
    	locs.add(l8);
 	
    	int oldwave = wave;
    	int newwave = oldwave + 1;
    	int am = 6;
    	if(playing.size() < 2) {
    		am = 4;
    	} else if(playing.size() > 2 && playing.size() < 4) {
    		am = 6;
    	} else if(playing.size() > 4) {
    		am = 8;
    	}
 	
    	wave = newwave;
    	int amount = wave * am;
    	if(wave < 10) {
    		needKills = amount - 2;
    	} else if(wave > 10 && wave < 25 || wave == 10 && wave < 35) {
    		needKills = amount - 4;
    	} else if(wave > 25) {
    		needKills = amount - 6;
    	} 	
    	for(Player pl : playing) {
    		if(!score.containsKey(pl.getName())) {
    			score.put(pl.getName(), 0);
    		}
    		int coins = score.get(pl.getName());
    		pl.sendMessage(ChatColor.GRAY + "Wave " + wave);
    		pl.sendMessage(ChatColor.GRAY + "Kills needed for next wave: " + needKills);
    		pl.sendMessage(ChatColor.GRAY + "Coins: " + coins);
    	}
    	if(newwave == 5 || newwave == 10 || newwave == 15 || newwave == 20) {
    		for(Player player : playing) {
    		player.sendMessage(ChatColor.GRAY + "Special Wave!");
    		}
    		world.spawnEntity(spec, EntityType.WITCH);
    	}
    	for (int i = 0; i < amount; i++) {
    		int r = randomGenerator.nextInt(8);
    		Location loc = locs.get(r);
    	    world.spawnEntity(loc, EntityType.ZOMBIE);
    	}   	
    	for(Location l : locs) {
			try {
				fplayer.playFirework(world, l, FireworkEffect.builder().with(Type.BURST).withColor(Color.BLACK).build());
		   } catch (Exception exc) {	
       	}	
    	}
    }	
	
	public void giveReward(Player p) {
          if(wave < 3 || wave == 3) {
        	  p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
        	  p.sendMessage(ChatColor.GRAY + "You earned a gold ingot!");
          } else if(wave > 3 && wave < 5 || wave == 5) {
        	  p.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
        	  p.sendMessage(ChatColor.GRAY + "You earned a iron ingot!"); 
          } else if(wave > 5 && wave < 10 || wave == 10) {
        	  p.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 4));
        	  p.sendMessage(ChatColor.GRAY + "You earned four iron ingots!"); 
          } else if(wave > 10) {
        	  p.getInventory().addItem(new ItemStack(Material.DIAMOND, 2));
        	  p.sendMessage(ChatColor.GRAY + "You earned two diamonds!"); 
          }	
	}
	public void removeMobs() {
		Location loc = new Location(Bukkit.getWorld("Imesia"), 2812, 89, -4315);
		List<Entity> entities = loc.getWorld().getEntities();
		Vector ownVector = loc.toVector();
    	for (Entity entity : entities) {
    		if(entity.getLocation().toVector().distance(ownVector) < 80) {
    		if(entity instanceof LivingEntity) {
     			if(entity instanceof Player){	
    			} else {
    			 ((LivingEntity) entity).damage(40);
    			}
    		}
    		}
    	}		
	}
	
	public void showHelpMenu(Player player) {
		player.sendMessage(ChatColor.GRAY + "--------UndeadOverrun--------");
		player.sendMessage(ChatColor.GRAY + "Commands:");
		player.sendMessage(ChatColor.GRAY + "/uo join - To join the game");
		player.sendMessage(ChatColor.GRAY + "/uo leave - To leave the game when you joined");
		player.sendMessage(ChatColor.GRAY + "/uo players - To see the players that are in the game");
		player.sendMessage(ChatColor.GRAY + "/uo stats - To get the stats of the current game");
		player.sendMessage(ChatColor.GRAY + "/uo ready - To set yourself ready");
		player.sendMessage(ChatColor.GRAY + "/uo start - To start the game");
		player.sendMessage(ChatColor.GRAY + "Commands while playing:");
		player.sendMessage(ChatColor.GRAY + "/uo coins - To get your amount of coins");
		player.sendMessage(ChatColor.GRAY + "/uo quit - To quit the game");	
	}
}