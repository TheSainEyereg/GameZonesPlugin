package ru.olejka.gamezonesplugin;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class SelectionManager {
	// map with coords connected to name
	private static final Map<String, Coordinates> coordinates = new HashMap<>();
	// map with BukkitRunner task id's connected to name
	private static final Map<String, Integer> tasks = new HashMap<>();

	public static void clearSelection(String name) {
		coordinates.remove(name);
		if (tasks.containsKey(name)) {
			Bukkit.getScheduler().cancelTask(tasks.get(name));
		}
		tasks.remove(name);
	}

	private static void prepare(String name) {
		// If not exists in coordinates map then create
		if (!coordinates.containsKey(name)) {
			coordinates.put(name, new Coordinates());
		}

		if (tasks.containsKey(name)) {
			Bukkit.getScheduler().cancelTask(tasks.get(name));
		}

		var task = new BukkitRunnable() {
			@Override
			public void run() {
				clearSelection(name);
			}
		}.runTaskLaterAsynchronously(GameZones.getInstance(), 20 * 60 * 5);

		tasks.put(name, task.getTaskId());
	}

	public static void addFirst(String name, int x, int z) {
		prepare(name);
		coordinates.get(name).setFirst(x, z);
	}

	public static void addSecond(String name, int x, int z) {
		prepare(name);
		coordinates.get(name).setSecond(x, z);
	}

	public static Coordinates getSelection(String name) {
		return coordinates.get(name);
	}
}
