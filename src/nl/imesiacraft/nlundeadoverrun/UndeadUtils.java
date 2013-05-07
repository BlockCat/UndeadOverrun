package nl.imesiacraft.nlundeadoverrun;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class UndeadUtils {
	
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
}
