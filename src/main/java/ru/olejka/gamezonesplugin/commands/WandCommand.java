package ru.olejka.gamezonesplugin.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.olejka.gamezonesplugin.ConfigManager;

public class WandCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ConfigManager.getTranslation("players-only"));
			return true;
		}

		var player = (Player) sender;

		if (!player.hasPermission("ocgz.wand")) {
			player.sendMessage(ConfigManager.getTranslation("no-permission"));
			return true;
		}

		var item = new ItemStack(Material.WOODEN_AXE);
		item.setAmount(1);

		var meta = item.getItemMeta();
		assert meta != null;
		meta.setDisplayName("gz-wand");

		item.setItemMeta(meta);
		player.getInventory().addItem(item);

		return true;
	}

}
