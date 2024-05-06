package ru.olejka.gamezonesplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.olejka.gamezonesplugin.ConfigManager;
import ru.olejka.gamezonesplugin.GameZones;
import ru.olejka.gamezonesplugin.SelectionManager;

import java.util.Objects;

public class DeleteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ConfigManager.getTranslation("players-only"));
			return true;
		}

		var player = (Player) sender;
		var world = player.getWorld().getEnvironment();
		var location = player.getLocation();

		if (!player.hasPermission("ocgz.zones")) {
			player.sendMessage(ConfigManager.getTranslation("no-permission"));
			return true;
		}

		var deleted = ConfigManager.deleteZone(location.getBlockX(), location.getBlockZ(), world);

		if (!deleted) {
			player.sendMessage(ConfigManager.getTranslation("not-in-zone"));
			return true;
		}

		GameZones.getInstance().updateConfig();
		player.sendMessage(ConfigManager.getTranslation("deleted"));

		return true;
	}
}
