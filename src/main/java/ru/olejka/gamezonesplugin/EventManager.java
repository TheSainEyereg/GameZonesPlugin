package ru.olejka.gamezonesplugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;
import java.util.logging.Logger;

public class EventManager implements Listener {
	private static final Logger logger = GameZones.getPluginLogger();

	// Cancel entity explosion
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		var world = Objects.requireNonNull(event.getLocation().getWorld()).getEnvironment();
		for (var zone : ConfigManager.getGreenZones()) {
			if (zone.contains(event.getLocation().getBlockX(), event.getLocation().getBlockZ(), world)) {
//				event.setCancelled(true);
				event.blockList().clear();
			}
		}
	}

	// Cancel explosion
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockExplode(BlockExplodeEvent event) {
		for (var zone : ConfigManager.getGreenZones()) {
			if (zone.contains(event.getBlock().getX(), event.getBlock().getZ(), event.getBlock().getWorld().getEnvironment())) {
//				event.setCancelled(true);
				event.blockList().clear();
			}
		}
	}

	//Cancel burning damage only to players in green zones
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void onEntityDamageByBlock(EntityDamageEvent event) {
//		if (event.getEntity() instanceof Player) {
//			var player = (Player) event.getEntity();
//			var cause = event.getCause();
//			var location = player.getLocation();
//			var world = Objects.requireNonNull(location.getWorld());
//
//			if (cause != EntityDamageEvent.DamageCause.FIRE && cause != EntityDamageEvent.DamageCause.FIRE_TICK && cause != EntityDamageEvent.DamageCause.LAVA)
//				return;
//
//			for (var zone : ConfigManager.getGreenZones()) {
//				if (zone.contains(location.getBlockX(), location.getBlockZ(), world.getEnvironment())) {
//					event.setCancelled(true);
//				}
//			}
//		}
//	}

	// Limit fire spread in green zone
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFireSpread(BlockSpreadEvent event) {
		var block = event.getSource();
		for (var zone : ConfigManager.getGreenZones()) {
			if (zone.contains(block.getX(), block.getZ(), block.getWorld().getEnvironment())) {
				event.setCancelled(true);
			}
		}
	}

	// Limit block burn in green zone
	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockBurn(BlockBurnEvent event) {
		var block = event.getBlock();
		for (var zone : ConfigManager.getGreenZones()) {
			if (zone.contains(block.getX(), block.getZ(), block.getWorld().getEnvironment())) {
				event.setCancelled(true);
			}
		}
	}

	// Limit player's elytra speed in red zone
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		var player = event.getPlayer();
		if (player.isGliding()) {
			var location = player.getLocation();
			var world = Objects.requireNonNull(location.getWorld());
			for (var zone : ConfigManager.getRedZones()) {
				if (zone.contains(location.getBlockX(), location.getBlockZ(), world.getEnvironment())) {
					var velocity = player.getVelocity();

					player.setVelocity(velocity.multiply(0.9f));
				}
			}
		}
	}

	// Fireworks use in red zone
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onElytaFireworks(PlayerInteractEvent event) {
		var player = event.getPlayer();
		var item = event.getItem();
		var position = player.getLocation();
		var world = Objects.requireNonNull(position.getWorld());

		if (!player.isGliding() || item == null || item.getType() != Material.FIREWORK_ROCKET)
			return;

		for (var zone : ConfigManager.getRedZones()) {
			if (zone.contains(position.getBlockX(), position.getBlockZ(), world.getEnvironment())) {
				event.setCancelled(true);
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
		var world = block.getWorld().getEnvironment();

		// Check overlap with existing zones
		for (var zone : ConfigManager.getGreenZones()) {
			if (zone.contains(x, z, world)) {
				player.sendMessage(ConfigManager.getTranslation("select-overlap-green"));
				return;
			}
		}
		for (var zone : ConfigManager.getRedZones()) {
			if (zone.contains(x, z, world)) {
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
