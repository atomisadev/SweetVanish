package me.atomisadev.sweetvanish.handlers;

import me.atomisadev.sweetvanish.SweetVanish;
import me.atomisadev.sweetvanish.commands.MainCommand;
import me.atomisadev.sweetvanish.commands.VanishCommand;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class VanishHandler implements Listener {
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        VanishCommand vanishCommand = SweetVanish.getInstance().getVanishCommand();
        MainCommand mainCommand = SweetVanish.getInstance().getMainCommand();

        if (vanishCommand.isPlayerVanished(event.getPlayer().getUniqueId()) && !mainCommand.canPlayerPickupItems(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        VanishCommand vanishCommand = SweetVanish.getInstance().getVanishCommand();

        new BukkitRunnable() {
            @Override
            public void run() {
                vanishCommand.sendActionBarMessage(player);
            }
        }.runTaskLater(SweetVanish.getInstance(), 20L);

        if (vanishCommand.isPlayerVanished(player.getUniqueId())) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        VanishCommand vanishCommand = SweetVanish.getInstance().getVanishCommand();

        Player player = event.getPlayer();
        if (vanishCommand.isPlayerVanished(player.getUniqueId())) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        MainCommand mainCommand = SweetVanish.getInstance().getMainCommand();

        if (SweetVanish.getInstance().getVanishCommand().isPlayerVanished(player.getUniqueId()) && !mainCommand.canPlayerBreakBlocks(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You have breaking blocks disabled. Turn it back on using " + ChatColor.YELLOW + "/sv break" + ChatColor.RED + ".");
            player.getWorld().playEffect(event.getBlock().getLocation().add(0, 1, 0), Effect.SMOKE, 0);
        }
    }
}
