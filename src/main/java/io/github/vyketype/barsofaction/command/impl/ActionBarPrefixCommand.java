package io.github.vyketype.barsofaction.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.util.ErrorUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

@CommandAlias("actionbar|ab")
@Subcommand("prefix")
@CommandPermission("actionbar.prefix")
public class ActionBarPrefixCommand extends BaseCommand {
    private final BarsOfAction plugin;
    
    public ActionBarPrefixCommand(BarsOfAction plugin) {
        this.plugin = plugin;
    }
    
    @HelpCommand
    @Default
    public void onActionBar(CommandHelp commandHelp) {
        commandHelp.showHelp();
    }
    
    @Subcommand("query")
    @Description("See what the current ActionBar prefix is.")
    public void onActionBarPrefixQuery(CommandSender sender) {
        String prefix = plugin.getConfig().getString("prefix");
        if (Objects.equals(prefix, "")) {
            sender.sendMessage(BarsOfAction.NAMESPACE + "Currently, there is no ActionBar " + ChatColor.RED +
                    "prefix" + ChatColor.GRAY + ".");
        } else {
            sender.sendMessage(BarsOfAction.NAMESPACE + "The current ActionBar prefix is " +
                    ChatColor.LIGHT_PURPLE + "\"" + prefix + "\"" + ChatColor.GRAY + ".");
        }
    }
    
    @Subcommand("set")
    @Description("Set a new prefix for ActionBars. /ab prefix set <prefix>")
    public void onActionBarPrefixSet(CommandSender sender, String text) {
        int charLimit = plugin.getConfig().getInt("prefixCharLimit");
        if (text.length() > charLimit) {
            ErrorUtil.error(sender, "This prefix is too long! Prefix character limit: " + charLimit);
            return;
        }
        
        plugin.getFileManager().setPrefix(text);
        sender.sendMessage(BarsOfAction.NAMESPACE + "Set " + ChatColor.GREEN + "prefix " + ChatColor.GRAY + "to " +
                ChatColor.LIGHT_PURPLE + "\"" + text + "\"" + ChatColor.GRAY + ".");
    }
    
    @Subcommand("remove")
    @Description("Remove the ActionBar prefix. /ab prefix remove")
    public void onActionBarPrefixRemove(CommandSender sender) {
        plugin.getFileManager().setPrefix("");
        sender.sendMessage(BarsOfAction.NAMESPACE + ChatColor.RED + "Removed " + ChatColor.GRAY + "the ActionBar " +
                "prefix.");
    }
}
