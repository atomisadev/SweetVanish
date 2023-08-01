package me.atomisadev.sweetvanish.commands;

import me.atomisadev.sweetvanish.SweetVanish;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class VanishCommand implements CommandExecutor {

    private Set<UUID> vanishedPlayers = new HashSet<>();
    private Map<UUID, BukkitRunnable> actionBarRunnables = new HashMap<>();
    private static final long VANISH_COMMAND_COOLDOWN = 5;
    private Map<UUID, Long> commandCooldowns = new HashMap<>();
    private File vanishedPlayersFile;

    public VanishCommand() {
        vanishedPlayersFile = new File(SweetVanish.getInstance().getDataFolder(), "data.yml");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (commandCooldowns.containsKey(playerUUID)) {
            long secondsLeft = ((commandCooldowns.get(playerUUID) / 1000) + VANISH_COMMAND_COOLDOWN) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot use this command for another &e" + secondsLeft + "&c seconds!"));
                return true;
            }
        }

        if (!player.hasPermission("sweetvanish.vanish")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to run this!");
        }

        if (!isPlayerVanished(player.getUniqueId())) {
            vanishPlayer(player);
        } else {
            unvanishPlayer(player);
        }

        return true;
    }

    public void vanishPlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("sweetvanish.bypass")) {
                onlinePlayer.hidePlayer(SweetVanish.getInstance(), player);
            } else {
                onlinePlayer.showPlayer(SweetVanish.getInstance(), player);
            }
        }
        vanishedPlayers.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You are now invisible to other players.");
        String quitMessage = SweetVanish.getInstance().getConfig().getString("quit_message").replace("{PLAYER}", player.getName());
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            quitMessage = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, quitMessage);
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', quitMessage));
        sendActionBarMessage(player);
    }

    public void unvanishPlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(SweetVanish.getInstance(), player);
        }
        vanishedPlayers.remove(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You are now visible to other players.");
        String joinMessage = SweetVanish.getInstance().getConfig().getString("join_message").replace("{PLAYER}", player.getName());
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            joinMessage = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, joinMessage);
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
        sendActionBarMessage(player);
    }

    public void saveVanishedPlayers() {
        FileConfiguration config = new YamlConfiguration();
        List<String> uuidStrings = new ArrayList<>();

        for (UUID uuid : vanishedPlayers) {
            uuidStrings.add(uuid.toString());
        }

        config.set("vanishedPlayers", uuidStrings);

        try {
            config.save(vanishedPlayersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadVanishedPlayers() {
        if (!vanishedPlayersFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(vanishedPlayersFile);
        List<String> uuidStrings = config.getStringList("vanishedPlayers");

        for (String uuidString : uuidStrings) {
            vanishedPlayers.add(UUID.fromString(uuidString));
        }
    }

    public boolean isPlayerVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

    public void sendActionBarMessage(Player player) {
        if (isPlayerVanished(player.getUniqueId())) {
            // If there is already a runnable for the player, cancel it
            BukkitRunnable oldRunnable = actionBarRunnables.get(player.getUniqueId());
            if (oldRunnable != null) {
                oldRunnable.cancel();
            }

            // Create a new runnable that sends the action bar message every 20 ticks
            BukkitRunnable newRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aYou are now invisible to other players."));
                }
            };
            newRunnable.runTaskTimer(SweetVanish.getInstance(), 0L, 20L);

            // Store the runnable in the map
            actionBarRunnables.put(player.getUniqueId(), newRunnable);
        } else {
            // If the player is not vanished, cancel the runnable
            BukkitRunnable runnable = actionBarRunnables.remove(player.getUniqueId());
            if (runnable != null) {
                runnable.cancel();
            }
        }
    }



}
