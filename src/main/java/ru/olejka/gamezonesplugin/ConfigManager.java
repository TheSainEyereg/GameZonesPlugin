package ru.olejka.gamezonesplugin;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

class ZoneDifinition {
	private final Map<String, Integer> coordinates;

	public ZoneDifinition(int x0, int z0, int x1, int z1) {
		this.coordinates = Map.of("x0", Math.min(x0, x1), "z0", Math.min(z0, z1), "x1", Math.max(x0, x1), "z1", Math.max(z0, z1));
	}

	public Map<String, Integer> getCoordinates() {
		return coordinates;
	}

	public boolean contains(int x, int z) {
		return x >= coordinates.get("x0") && x <= coordinates.get("x1") && z >= coordinates.get("z0") && z <= coordinates.get("z1");
	}
}

public class ConfigManager {
	private static final List<ZoneDifinition> greenZones = new ArrayList<>();
	private static final List<ZoneDifinition> redZones = new ArrayList<>();
	private static final Map<String, String> translation = new HashMap<>();

	public static void parseConfig(FileConfiguration config) {
		greenZones.clear();
		redZones.clear();

		for (var zone : config.getMapList("zones.green")) {
			greenZones.add(new ZoneDifinition((int) zone.get("x0"), (int) zone.get("z0"), (int) zone.get("x1"), (int) zone.get("z1")));
		}
		for (var zone : config.getMapList("zones.red")) {
			redZones.add(new ZoneDifinition((int) zone.get("x0"), (int) zone.get("z0"), (int) zone.get("x1"), (int) zone.get("z1")));
		}

		for (var key : Objects.requireNonNull(config.getConfigurationSection("translation")).getKeys(false)) {
			translation.put(key, config.getString("translation." + key));
		}
	}

	public static void saveConfig(FileConfiguration config) {
		var greenList  = greenZones.stream().map(ZoneDifinition::getCoordinates).toList();
		var redList  = redZones.stream().map(ZoneDifinition::getCoordinates).toList();
		config.set("zones.green", greenList);
		config.set("zones.red", redList);
	}

	public static void setGreenZone(int x0, int z0, int x1, int z1) {
		greenZones.add(new ZoneDifinition(x0, z0, x1, z1));
	}

	public static void setRedZone(int x0, int z0, int x1, int z1) {
		redZones.add(new ZoneDifinition(x0, z0, x1, z1));
	}

	public static boolean deleteZone(int x, int z) {
		return greenZones.removeIf(zone -> zone.contains(x, z)) || redZones.removeIf(zone -> zone.contains(x, z));
	}

	public static List<ZoneDifinition> getGreenZones() {
		return greenZones;
	}

	public static List<ZoneDifinition> getRedZones() {
		return redZones;
	}

	public static String getTranslation(String key) {
		return translation.getOrDefault(key, key);
	}
}
