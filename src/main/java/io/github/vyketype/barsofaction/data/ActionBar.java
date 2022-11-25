package io.github.vyketype.barsofaction.data;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a saveable ActionBar.
 *
 * @author vyketype
 */
public record ActionBar(UUID creator, String name, String content) {
    /**
     * Sends the ActionBar packet to a player.
     *
     * @param player The player to whom the ActionBar should be sent.
     * @param sound The sound to play when the ActionBar is sent.
     * @param pitch The pitch to play the sound.
     */
    public void send(Player player, String sound, float pitch) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(content));
        player.playSound(player.getLocation(), Sound.valueOf(sound.toUpperCase()), 100F, pitch);
    }

    @Override
    public String toString() {
        // > template : by vPrototype_
        return ChatColor.DARK_GRAY + "> " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + " : by " +
                ChatColor.GRAY + Bukkit.getOfflinePlayer(creator).getName();
    }
}
