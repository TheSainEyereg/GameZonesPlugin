package ru.olejka.gamezonesplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.olejka.gamezonesplugin.commands.DeleteCommand;
import ru.olejka.gamezonesplugin.commands.ReloadCommand;
import ru.olejka.gamezonesplugin.commands.SaveCommand;
import ru.olejka.gamezonesplugin.commands.WandCommand;

import java.util.Objects;
import java.util.logging.Logger;

public final class GameZones extends JavaPlugin {
    private static GameZones instance;
    private static Logger logger;
    private static PlayerNotifier notifier;

    @Override
    public void onEnable() {
        // Plugin startup logic
        ConfigManager.parseConfig(getConfig());
        saveDefaultConfig();

        instance = this;
        logger = getLogger();
        notifier = new PlayerNotifier();

        Bukkit.getPluginManager().registerEvents(new EventManager(), this);

        Objects.requireNonNull(this.getCommand("gz-wand")).setExecutor(new WandCommand());
        Objects.requireNonNull(this.getCommand("gz-save")).setExecutor(new SaveCommand());
        Objects.requireNonNull(this.getCommand("gz-delete")).setExecutor(new DeleteCommand());
        Objects.requireNonNull(this.getCommand("gz-reload")).setExecutor(new ReloadCommand());

        notifier.enable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        notifier.disable();
        updateConfig();
    }

    public void updateConfig() {
        ConfigManager.saveConfig(getConfig());
        saveConfig();
    }

    public static Logger getPluginLogger() {
        return logger;
    }

    public static GameZones getInstance() {
        return instance;
    }
}
