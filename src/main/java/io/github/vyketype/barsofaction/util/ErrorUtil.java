package io.github.vyketype.barsofaction.util;

import io.github.vyketype.barsofaction.BarsOfAction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Utility class for sending error messages to players.
 */
public class ErrorUtil {
    /**
     * Sends an error message to a player and plays a sound.
     *
     * @param sender  The player to send the message.
     * @param message The message to send.
     */
    public static void error(CommandSender sender, String message) {
        sender.sendMessage(BarsOfAction.NAMESPACE + ChatColor.RED + message);
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 0.5F);
        }
    }
}
