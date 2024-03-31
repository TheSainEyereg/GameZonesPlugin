package ru.olejka.gamezonesplugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

enum NotifiedType {
	ENTERED_GREEN,
	ENTERED_RED,
	LEFT
}

public class PlayerNotifier {
	// Map with names of players that was notified
	private final Map<String, NotifiedType> notifications = new HashMap<>();

	private int taskId;
	public void enable() {
		taskId = new BukkitRunnable() {
			@Override
			public void run() {
				for(var p : Bukkit.getOnlinePlayers()) {
					var location = p.getLocation();
					var spigot = p.spigot();
					var notified = notifications.getOrDefault(p.getName(), NotifiedType.LEFT);

					var notInZone = true;
					for (var zone : ConfigManager.getRedZones()) {
						if (zone.contains(location.getBlockX(), location.getBlockZ())) {
							notInZone = false;

							// if hase invisibility take it
							if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
								p.removePotionEffect(PotionEffectType.INVISIBILITY);
							}

							if (!notified.equals(NotifiedType.ENTERED_RED)) {
								spigot.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ConfigManager.getTranslation("entered-red")));
								notifications.put(p.getName(), NotifiedType.ENTERED_RED);
							}
						}
					}

					for (var zone : ConfigManager.getGreenZones()) {
						if (zone.contains(location.getBlockX(), location.getBlockZ())) {
							notInZone = false;
							if (!notified.equals(NotifiedType.ENTERED_GREEN)) {
								spigot.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ConfigManager.getTranslation("entered-green")));
								notifications.put(p.getName(), NotifiedType.ENTERED_GREEN);
							}
						}
					}

					if (notInZone) {
						if (!notified.equals(NotifiedType.LEFT)) {
							spigot.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ConfigManager.getTranslation(notified.equals(NotifiedType.ENTERED_GREEN) ? "left-green" : "left-red")));
						}
						notifications.put(p.getName(), NotifiedType.LEFT);
					}
				}
			}
		}.runTaskTimer(GameZones.getInstance(), 0, 1).getTaskId();
	}

	public void disable() {
		Bukkit.getScheduler().cancelTask(taskId);
	}
}
