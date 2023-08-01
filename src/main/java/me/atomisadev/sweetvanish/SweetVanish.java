package me.atomisadev.sweetvanish;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.atomisadev.sweetvanish.commands.MainCommand;
import me.atomisadev.sweetvanish.commands.VanishCommand;
import me.atomisadev.sweetvanish.expansions.SweetVanishExpansions;
import me.atomisadev.sweetvanish.handlers.VanishChestHandler;
import me.atomisadev.sweetvanish.handlers.VanishHandler;
import me.atomisadev.sweetvanish.handlers.VanishTablistHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SweetVanish extends JavaPlugin {

    private static SweetVanish instance;
    private VanishCommand vanishCommand;
    private MainCommand mainCommand;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.protocolManager = ProtocolLibrary.getProtocolManager();


        vanishCommand = new VanishCommand(protocolManager);
        mainCommand = new MainCommand();

        getCommand("vanish").setExecutor(vanishCommand);
        getCommand("sweetvanish").setExecutor(mainCommand);

        getServer().getPluginManager().registerEvents(new VanishHandler(), this);

        getConfig().addDefault("join_message", "&e{PLAYER} joined the game");
        getConfig().addDefault("quit_message", "&e{PLAYER} left the game");

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().warning("We recommend installing PlaceholderAPI in order to have more flexibility with the config.yml. Although, we have a default placeholder for registering player names, '{PLAYER}'");
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            getLogger().severe("Missing dependency 'Vault'. Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            getLogger().severe("Missing dependency 'ProtocolLib'. Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        VanishChestHandler vanishChestHandler = new VanishChestHandler(this);
//        VanishTablistHandler vanishTablistHandler = new VanishTablistHandler(this);

//        vanishTablistHandler.register();
        vanishChestHandler.register();

        new SweetVanishExpansions().register();

        vanishCommand.loadVanishedPlayers();
    }

    @Override
    public void onDisable() {
        vanishCommand.saveVanishedPlayers();
        getLogger().info("Successfully stored vanished players");
    }

    public static SweetVanish getInstance() {
        return instance;
    }

    public VanishCommand getVanishCommand() {
        return vanishCommand;
    }

    public MainCommand getMainCommand() {
        return mainCommand;
    }
}
