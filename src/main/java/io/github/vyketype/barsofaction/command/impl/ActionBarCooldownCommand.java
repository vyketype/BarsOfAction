package io.github.vyketype.barsofaction.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("actionbar|ab")
@Subcommand("cooldown")
public class ActionBarCooldownCommand extends BaseCommand {
    /*
    private static final BarsOfAction INSTANCE = BarsOfAction.getINSTANCE();
    
    @HelpCommand
    @Default
    public void onActionBarCooldown(CommandHelp commandHelp) {
        commandHelp.showHelp();
    }
    
    @Subcommand("query")
    @Syntax("[playerName]")
    @Description("View your current ActionBar cooldown and the global ActionBar cooldown. View another player's ActionBar cooldown by specifying their name.")
    public void onActionBarCooldownQuery(Player player, String targetName) {
        if (targetName != null) {
            if (!player.hasPermission(Permission.ACTIONBAR_COOLDOWN_QUERY_OTHERS.getName())) {
                ErrorUtil.error(player, "Insufficient permissions!");
                return;
            }
            
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
            
            @Nullable Integer playerSeconds = INSTANCE.getCooldownHandler().getPlayerCooldowns().get(offlineTarget.getUniqueId());
            if (playerSeconds == null) {
                ErrorUtil.error(player, "This player does not have any ActionBar cooldowns!");
                return;
            }
            
            player.sendMessage(BarsOfAction.NAMESPACE + targetName + "'s cooldown: " + playerSeconds);
            return;
        }
        
        String globalCooldown = ChatColor.GREEN + "none";
        int globalSeconds = INSTANCE.getCooldownHandler().getPublicCooldownSeconds();
        if (globalSeconds > 0) {
            globalCooldown = ChatColor.RED + "" + globalSeconds + " seconds";
        }
        
        String playerCooldown = ChatColor.GREEN + "none";
        @Nullable Integer playerSeconds = INSTANCE.getCooldownHandler().getPlayerCooldowns().get(player.getUniqueId());
        if (playerSeconds != null) {
            playerCooldown = ChatColor.RED + "" + playerSeconds + " seconds";
        }
        
        player.sendMessage(BarsOfAction.NAMESPACE + "Global Cooldown: " + globalCooldown);
        player.sendMessage(BarsOfAction.NAMESPACE + "Player Cooldown: " + playerCooldown);
    }
    
    @Subcommand("set")
    @Syntax("<playerName|-global> <timeSeconds>")
    @Description("Set a cooldown for sending ActionBars, globally or player-specific. Set the <timeSeconds> argument to 0 to remove the cooldown.")
    public void onActionBarCooldownSet(CommandSender sender, String strArgs) {
        if (!sender.hasPermission(Permission.ACTIONBAR_COOLDOWN_SET.getName())) {
            ErrorUtil.error(sender, "Insufficient permissions!");
            return;
        }
        
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
            if (!sender.hasPermission(Permission.ACTIONBAR_COOLDOWN_SET_GLOBAL.getName())) {
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
        INSTANCE.getCooldownHandler().setPublicCooldownSeconds(seconds);
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
            INSTANCE.getCooldownHandler().getPlayerCooldowns().remove(offlineTarget.getUniqueId());
            sender.sendMessage(BarsOfAction.NAMESPACE + "The ActionBar cooldown for " + ChatColor.LIGHT_PURPLE +
                    targetName + ChatColor.GRAY + " was " + ChatColor.GREEN + "removed" + ChatColor.GRAY + ".");
            if (offlineTarget.isOnline()) {
                Player target = offlineTarget.getPlayer();
                sender.sendMessage(BarsOfAction.NAMESPACE + "Your ActionBar cooldown was " + ChatColor.GREEN +
                        "removed" + ChatColor.GRAY + ".");
                target.playSound(target.getLocation(), "block.note_block.bass", 1, 1F);
            }
        } else {
            INSTANCE.getCooldownHandler().getPlayerCooldowns().put(offlineTarget.getUniqueId(), seconds);
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
     */
}
