package ru.olejka.gamezonesplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.olejka.gamezonesplugin.ConfigManager;
import ru.olejka.gamezonesplugin.GameZones;
import ru.olejka.gamezonesplugin.SelectionManager;

import java.util.Objects;

public class SaveCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ConfigManager.getTranslation("players-only"));
			return true;
		}

		var player = (Player) sender;
		var world = player.getWorld().getEnvironment();

		if (!player.hasPermission("ocgz.zones")) {
			player.sendMessage(ConfigManager.getTranslation("no-permission"));
			return true;
		}

		var selection = SelectionManager.getSelection(player.getName());

		if (selection == null) {
			player.sendMessage(ConfigManager.getTranslation("select-no-selection"));
			return true;
		}

		var first = selection.getFirst();
		var second = selection.getSecond();

		if (first.isEmpty() || second.isEmpty()) {
			player.sendMessage(ConfigManager.getTranslation("select-two-points"));
			return true;
		}

		if (Objects.equals(first.get("x"), second.get("x")) && Objects.equals(first.get("z"), second.get("z"))) {
			player.sendMessage(ConfigManager.getTranslation("select-two-different-points"));
			return true;
		}

		var zone = args[0];

		if (zone.equals("green")) {
			ConfigManager.setGreenZone(first.get("x"), first.get("z"), second.get("x"), second.get("z"), world);
			player.sendMessage(ConfigManager.getTranslation("created-green"));
		} else if (zone.equals("red")) {
			ConfigManager.setRedZone(first.get("x"), first.get("z"), second.get("x"), second.get("z"), world);
			player.sendMessage(ConfigManager.getTranslation("created-red"));
		} else {
			player.sendMessage(ConfigManager.getTranslation("wrong-type"));
			return true;
		}

		SelectionManager.clearSelection(player.getName());
		GameZones.getInstance().updateConfig();


		return true;
	}
}
