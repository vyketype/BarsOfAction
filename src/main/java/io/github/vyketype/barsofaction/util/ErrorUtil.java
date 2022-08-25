package io.github.vyketype.barsofaction.util;

import io.github.vyketype.barsofaction.BarsOfAction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ErrorUtil {

    public static void error(CommandSender sender, String message) {
        sender.sendMessage(BarsOfAction.PREFIX + ChatColor.RED + message);
        if (sender instanceof Player player)
            player.playSound(player.getLocation(), "entity.enderman.teleport", 100, 0.5F);
    }

}
