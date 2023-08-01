package me.atomisadev.sweetvanish.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SweetVanishExpansions extends PlaceholderExpansion {

    private Chat chat = null;
    private Permission perms = null;

    public SweetVanishExpansions() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            chat = rsp.getProvider();
        }

        RegisteredServiceProvider<Permission> rspPerms = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rspPerms != null) {
            perms = rspPerms.getProvider();
        }
    }

    @Override
    public String getIdentifier() {
        return "sweetvanish";
    }

    @Override
    public String getAuthor() {
        return "atomisadev";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        switch (identifier) {
            case "p":
                return player.getName();
            case "displayname":
                return player.getDisplayName();
            case "prefix":
                return chat.getPlayerPrefix(player);
            case "suffix":
                return chat.getPlayerSuffix(player);
            case "group":
                return perms.getPrimaryGroup(player);
            default:
                return null;
        }
    }
}
