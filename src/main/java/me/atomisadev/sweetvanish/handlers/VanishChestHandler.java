package me.atomisadev.sweetvanish.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.atomisadev.sweetvanish.SweetVanish;
import me.atomisadev.sweetvanish.commands.VanishCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VanishChestHandler extends PacketAdapter {
    JavaPlugin plugin;

    public VanishChestHandler(JavaPlugin plugin) {
        super(plugin, PacketType.Play.Server.BLOCK_ACTION);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        VanishCommand vanishCommand = SweetVanish.getInstance().getVanishCommand();

        if (vanishCommand.isPlayerVanished(player.getUniqueId())) {
            BlockPosition blockPosition = event.getPacket().getBlockPositionModifier().read(0);
            Block block = blockPosition.toLocation(player.getWorld()).getBlock();

            if (block.getType() == Material.CHEST) {
                event.setCancelled(true);
            }
        }
    }

    public void register() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(this);
    }
}
