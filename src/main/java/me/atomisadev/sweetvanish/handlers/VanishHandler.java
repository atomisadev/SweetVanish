package me.atomisadev.sweetvanish.handlers;

import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.atomisadev.sweetvanish.SweetVanish;
import me.atomisadev.sweetvanish.commands.MainCommand;
import me.atomisadev.sweetvanish.commands.VanishCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
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
}
