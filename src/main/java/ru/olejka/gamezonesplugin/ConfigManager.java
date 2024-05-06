package ru.olejka.gamezonesplugin;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

class ZoneDefinition {
	private final Map<String, String> definition;
	private int x0, z0, x1, z1;
	private World.Environment world;

	public ZoneDefinition(int x0, int z0, int x1, int z1, World.Environment world) {
		this.x0 = Math.min(x0, x1);
		this.z0 = Math.min(z0, z1);
		this.x1 = Math.max(x0, x1);
		this.z1 = Math.max(z0, z1);
		this.world = world;

		this.definition = Map.of(
			"x0", String.valueOf(this.x0),
			"z0", String.valueOf(this.z0),
			"x1", String.valueOf(this.x1),
			"z1", String.valueOf(this.z1),
			"world", world.toString()
		);
	}

	public Map<String, String> getDefinition() {
		return definition;
	}

	public boolean contains(int x, int z, World.Environment world) {
		return x >= this.x0
			&& x <= this.x1
			&& z >= this.z0
			&& z <= this.z1
			&& world.equals(this.world);
	}
}

public class ConfigManager {
	private static final List<ZoneDefinition> greenZones = new ArrayList<>();
	private static final List<ZoneDefinition> redZones = new ArrayList<>();
	private static final Map<String, String> translation = new HashMap<>();

	public static void parseConfig(FileConfiguration config) {
		greenZones.clear();
		redZones.clear();

		for (var zone : config.getMapList("zones.green")) {
			var x0 = zone.get("x0");
			var z0 = zone.get("z0");
			var x1 = zone.get("x1");
			var z1 = zone.get("z1");
			var world = zone.get("world");

			greenZones.add(new ZoneDefinition(
				x0.getClass().equals(String.class) ? Integer.parseInt((String) x0) : (int) x0,
				z0.getClass().equals(String.class) ? Integer.parseInt((String) z0) : (int) z0,
				x1.getClass().equals(String.class) ? Integer.parseInt((String) x1) : (int) x1,
				z1.getClass().equals(String.class) ? Integer.parseInt((String) z1) : (int) z1,
				world != null ? World.Environment.valueOf((String) world) : World.Environment.NORMAL
			));
		}
		for (var zone : config.getMapList("zones.red")) {
			var x0 = zone.get("x0");
			var z0 = zone.get("z0");
			var x1 = zone.get("x1");
			var z1 = zone.get("z1");
			var world = zone.get("world");

			redZones.add(new ZoneDefinition(
				x0.getClass().equals(String.class) ? Integer.parseInt((String) x0) : (int) x0,
				z0.getClass().equals(String.class) ? Integer.parseInt((String) z0) : (int) z0,
				x1.getClass().equals(String.class) ? Integer.parseInt((String) x1) : (int) x1,
				z1.getClass().equals(String.class) ? Integer.parseInt((String) z1) : (int) z1,
				world != null ? World.Environment.valueOf((String) world) : World.Environment.NORMAL
			));
		}

		for (var key : Objects.requireNonNull(config.getConfigurationSection("translation")).getKeys(false)) {
			translation.put(key, config.getString("translation." + key));
		}
	}

	public static void saveConfig(FileConfiguration config) {
		var greenList  = greenZones.stream().map(ZoneDefinition::getDefinition).toList();
		var redList  = redZones.stream().map(ZoneDefinition::getDefinition).toList();
		config.set("zones.green", greenList);
		config.set("zones.red", redList);
	}

	public static void setGreenZone(int x0, int z0, int x1, int z1, World.Environment world) {
		greenZones.add(new ZoneDefinition(x0, z0, x1, z1, world));
	}

	public static void setRedZone(int x0, int z0, int x1, int z1, World.Environment world) {
		redZones.add(new ZoneDefinition(x0, z0, x1, z1, world));
	}

	public static boolean deleteZone(int x, int z, World.Environment world) {
		return greenZones.removeIf(zone -> zone.contains(x, z, world)) || redZones.removeIf(zone -> zone.contains(x, z, world));
	}

	public static List<ZoneDefinition> getGreenZones() {
		return greenZones;
	}

	public static List<ZoneDefinition> getRedZones() {
		return redZones;
	}

	public static String getTranslation(String key) {
		return translation.getOrDefault(key, key);
	}
}
