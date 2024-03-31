package ru.olejka.gamezonesplugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Logger;

public class EventManager implements Listener {
	private static final Logger logger = GameZones.getPluginLogger();

	// Cancel entity explosion
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		for (var zone : ConfigManager.getGreenZones()) {
			if (zone.contains(event.getLocation().getBlockX(), event.getLocation().getBlockZ())) {
				event.setCancelled(true);
			}
		}
	}

	// Cancel explosion
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockExplode(BlockExplodeEvent event) {
		for (var zone : ConfigManager.getGreenZones()) {
			if (zone.contains(event.getBlock().getX(), event.getBlock().getZ())) {
				event.setCancelled(true);
			}
		}
	}

	//Cancel damage only to players in green zones
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			var player = (Player) event.getEntity();
			var location = player.getLocation();

//			logger.info(event.getDamager().getType() + " attacked " + event.getEntity().getType());

			for (var zone : ConfigManager.getGreenZones()) {
				if (zone.contains(location.getBlockX(), location.getBlockZ())) {
					event.setCancelled(true);
				}
			}
		}
	}

	// Left and right click by wand
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		var player = event.getPlayer();
		var item = event.getItem();
		var block = event.getClickedBlock();

		if (item == null || block == null || item.getType() != Material.WOODEN_AXE || !player.hasPermission("ocgz.wand"))
			return;

		var meta = item.getItemMeta();
		if (meta == null || !meta.getDisplayName().equals("gz-wand"))
			return;

		event.setCancelled(true);

		// get z and x click coordinates
		var x = block.getX();
		var z = block.getZ();

		// Check overlap with existing zones
		for (var zone : ConfigManager.getGreenZones()) {
			if (zone.contains(x, z)) {
				player.sendMessage(ConfigManager.getTranslation("select-overlap-green"));
				return;
			}
		}
		for (var zone : ConfigManager.getRedZones()) {
			if (zone.contains(x, z)) {
				player.sendMessage(ConfigManager.getTranslation("select-overlap-red"));
				return;
			}
		}

		// If left click then its x0 & z0, if right click then its x1 & z1
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			SelectionManager.addFirst(player.getName(), x, z);
			player.sendMessage(ConfigManager.getTranslation("selected-first") + " x: " + x + " z: " + z);
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			SelectionManager.addSecond(player.getName(), x, z);
			player.sendMessage(ConfigManager.getTranslation("selected-second") + " x: " + x + " z: " + z);
		}
	}
}
