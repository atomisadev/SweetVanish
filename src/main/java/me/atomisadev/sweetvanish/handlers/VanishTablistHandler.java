package me.atomisadev.sweetvanish.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import me.atomisadev.sweetvanish.SweetVanish;
import me.atomisadev.sweetvanish.commands.VanishCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class VanishTablistHandler extends PacketAdapter {
    JavaPlugin plugin;

    public VanishTablistHandler(JavaPlugin plugin) {
        super(plugin, PacketType.Play.Server.PLAYER_INFO);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        VanishCommand vanishCommand = SweetVanish.getInstance().getVanishCommand();

        List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
        playerInfoDataList = playerInfoDataList.stream().filter(data -> data != null && !vanishCommand.isPlayerVanished(data.getProfile().getUUID())).collect(Collectors.toList());
        event.getPacket().getPlayerInfoDataLists().write(0, playerInfoDataList);
    }

    public void register() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(this);
    }
}
