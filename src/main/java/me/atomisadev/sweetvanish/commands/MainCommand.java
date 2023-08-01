package me.atomisadev.sweetvanish.commands;

import me.atomisadev.sweetvanish.SweetVanish;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class MainCommand implements CommandExecutor {

    private HashSet<UUID> noPickupPlayers = new HashSet<>();
    private HashSet<UUID> noBreakPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (sender.hasPermission("sweetvanish.reload")) {
                        SweetVanish.getInstance().reloadConfig();
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + "SweetVanish configuration has been reloaded.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    }
                    break;
                case "tipu":
                    if (sender.hasPermission("sweetvanish.tipu")) {
                        if (!noPickupPlayers.contains(player.getUniqueId())) {
                            noPickupPlayers.add(player.getUniqueId());
                            player.sendMessage(ChatColor.GREEN + "You will not be able to pick up items in vanish.");
                        } else {
                            noPickupPlayers.remove(player.getUniqueId());
                            player.sendMessage(ChatColor.GREEN + "You will be able to pick up items in vanish.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    }
                    break;
                case "login":
                    if (sender.hasPermission("sweetvanish.login")) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', SweetVanish.getInstance().getConfig().getString("join_message").replace("{PLAYER}", player.getName())));
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    }
                    break;
                case "logout":
                    if (sender.hasPermission("sweetvanish.logout")) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', SweetVanish.getInstance().getConfig().getString("quit_message").replace("{PLAYER}", player.getName())));
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    }
                    break;
                case "break":
                    if (!sender.hasPermission("sweetvanish.break")) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    }
                    if (!noBreakPlayers.contains(player.getUniqueId())) {
                        noBreakPlayers.add(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "You will not be able to break blocks in vanish.");
                    } else {
                        noBreakPlayers.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "YOu will be able to break blocks in vanish.");
                    }
                    break;
            }
        }
        return false;
    }

    public boolean canPlayerPickupItems(UUID uuid) {
        return !noPickupPlayers.contains(uuid);
    }

    public boolean canPlayerBreakBlocks(UUID uuid) {
        return !noBreakPlayers.contains(uuid);
    }
}
