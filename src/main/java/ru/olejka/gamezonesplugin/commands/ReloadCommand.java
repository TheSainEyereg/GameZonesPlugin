package ru.olejka.gamezonesplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.olejka.gamezonesplugin.ConfigManager;
import ru.olejka.gamezonesplugin.GameZones;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("ocgz.reload")) {
			sender.sendMessage(ConfigManager.getTranslation("no-permission"));
			return true;
		}

		var plugin = GameZones.getInstance();
		plugin.reloadConfig();
		ConfigManager.parseConfig(plugin.getConfig());

		sender.sendMessage(ConfigManager.getTranslation("reloaded"));

		return true;
	}
}
