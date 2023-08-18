package io.github.vyketype.barsofaction.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.util.ErrorUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("actionbar|ab")
@Subcommand("cooldown")
public class ActionBarCooldownCommand extends BaseCommand {
    private final BarsOfAction plugin;
    
    /*
    COMMANDS
/ab cooldown remove <playerName>
/ab cooldown remove -global

PERMISSIONS
actionbar.cooldown.bypass (bypass any cooldowns)

     */
    
    public ActionBarCooldownCommand(BarsOfAction plugin) {
        this.plugin = plugin;
    }
    
    @HelpCommand
    @Default
    public void onActionBarCooldown(CommandHelp commandHelp) {
        commandHelp.showHelp();
    }
    
    @Subcommand("query")
    @CommandPermission("actionbar.cooldown.query")
    @Description("View your current ActionBar cooldown, or the global ActionBar cooldown.")
    public void onActionBarCooldownQuery(Player player) {
        // TODO
    }
    
    @Subcommand("set")
    @Syntax("<playerName|-global> <timeSeconds>")
    @CommandPermission("actionbar.cooldown.set")
    @Description("Set a cooldown for sending ActionBars, globally or player-specific.")
    public void onActionBarCooldownSet(CommandSender sender, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        String targetName = args[0];
        
        // CHECKING IF INTERVAL NUMBER IS AN INTEGER
        try {
            Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            ErrorUtil.error(sender, "You must specify an interval in seconds (positive integer)!");
            return;
        }
        
        int seconds = Integer.parseInt(args[1]);
        
        if (targetName.equalsIgnoreCase("-global")) {
            if (!sender.hasPermission("actionbar.cooldown.global")) {
                ErrorUtil.error(sender, "Insufficient permissions!");
                return;
            }
            handleGlobalCooldown(sender, seconds);
            return;
        }
        
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
        handlePlayerCooldown(sender, offlineTarget, seconds);
    }
    
    private void handleGlobalCooldown(CommandSender sender, int seconds) {
        plugin.getCooldownHandler().setPublicCooldownSeconds(seconds);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (seconds <= 0) {
                onlinePlayer.sendMessage(BarsOfAction.NAMESPACE + "The global ActionBar cooldown has " +
                        "been turned " +
                        ChatColor.GREEN + "off" + ChatColor.GRAY + ".");
            } else {
                onlinePlayer.sendMessage(BarsOfAction.NAMESPACE + "A global ActionBar cooldown has been" +
                        " set for " +
                        ChatColor.RED + seconds + " seconds " + ChatColor.GRAY + "by " + ChatColor.GOLD +
                        sender.getName() + ChatColor.GRAY + ".");
            }
            onlinePlayer.playSound(onlinePlayer.getLocation(), "block.note_block.bass", 1, 1F);
        }
    }
    
    private void handlePlayerCooldown(CommandSender sender, OfflinePlayer offlineTarget, int seconds) {
        String targetName = offlineTarget.getName();
        if (seconds <= 0) {
            plugin.getCooldownHandler().getPlayerCooldowns().remove(offlineTarget.getUniqueId());
            sender.sendMessage(BarsOfAction.NAMESPACE + "The ActionBar cooldown for " + ChatColor.LIGHT_PURPLE +
                    targetName + ChatColor.GRAY + " was " + ChatColor.GREEN + "removed" + ChatColor.GRAY + ".");
            if (offlineTarget.isOnline()) {
                Player target = offlineTarget.getPlayer();
                sender.sendMessage(BarsOfAction.NAMESPACE + "Your ActionBar cooldown was " + ChatColor.GREEN +
                        "removed" + ChatColor.GRAY + ".");
                target.playSound(target.getLocation(), "block.note_block.bass", 1, 1F);
            }
        } else {
            plugin.getCooldownHandler().getPlayerCooldowns().put(offlineTarget.getUniqueId(), seconds);
            sender.sendMessage(BarsOfAction.NAMESPACE + "You placed an ActionBar " + ChatColor.RED +
                    "cooldown " + ChatColor.GRAY + "on " + ChatColor.LIGHT_PURPLE + targetName + " for " +
                    ChatColor.GOLD + seconds + " seconds" + ChatColor.GRAY + ".");
            if (offlineTarget.isOnline()) {
                Player target = offlineTarget.getPlayer();
                target.sendMessage(BarsOfAction.NAMESPACE + "You were placed on ActionBar " + ChatColor.RED +
                        "cooldown " + ChatColor.GRAY + "for " + ChatColor.GOLD + seconds + " seconds" +
                        ChatColor.GRAY + ".");
                target.playSound(target.getLocation(), "block.note_block.bass", 1, 1F);
            }
        }
    }
}
