package io.github.vyketype.barsofaction.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.data.ActionBar;
import io.github.vyketype.barsofaction.util.ErrorUtil;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

@CommandAlias("actionbar|ab")
public class ActionBarCommand extends BaseCommand {
    private final BarsOfAction plugin;

    public ActionBarCommand(BarsOfAction plugin) {
        this.plugin = plugin;
    }

    // --------------------------------------------------------------------------------
    // COMMAND/SUBCOMMAND HANDLERS
    // --------------------------------------------------------------------------------

    @HelpCommand
    @Default
    public void onActionBar(CommandHelp commandHelp) {
        commandHelp.showHelp();
    }

    @Subcommand("permissions|perms")
    @Description("List all permissions for this plugin.")
    public void onActionBarPerms(CommandSender sender) {
        sender.sendMessage(BarsOfAction.NAMESPACE + "Getting the permissions for this plugin...");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + "actionbar.broadcast" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab broadcast");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + "actionbar.delete" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab delete");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + "actionbar.save" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab save" + ChatColor.WHITE + " and " +
                ChatColor.AQUA + "/ab saverecent");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + "actionbar.send.self" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab send" + ChatColor.WHITE + " to yourself");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + "actionbar.send.others" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab send" + ChatColor.WHITE + " to others");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + "actionbar.prefix" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab prefix");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + "actionbar.consoletoggle" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab sendtoconsole");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + "actionbar.sound" +
                ChatColor.WHITE + " : allows the use of the -sound argument");
    }
    
    @Subcommand("plugin")
    @Description("Information about the plugin.")
    public void onActionBarPlugin(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "BarsOfAction" + ChatColor.RESET + " " +
                ChatColor.GREEN + BarsOfAction.VERSION);
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GRAY + "Create and send " + BarsOfAction.ACTIONBAR +
                ChatColor.GRAY + "s with ease!");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GRAY + "Developed by " + ChatColor.GOLD + "vyketype");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GRAY + "GitHub" + ChatColor.RED + "vyketype");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GRAY + "Discord" + ChatColor.BLUE + "vyketype" +
                ChatColor.GRAY + "#3472");
    }

    @Subcommand("list")
    @Syntax("[page]")
    @Description("List all saved ActionBars.")
    public void onActionBarList(CommandSender sender, @Optional Integer page) {
        // CHECKING IF SAVED BARS EXIST AT ALL
        if (plugin.getFileManager().getSavedBars().isEmpty()) {
            ErrorUtil.error(sender, "There are no saved ActionBars.");
            return;
        }

        // CHECKING IF NO PAGE ARGUMENT IS GIVEN
        if (page == null) {
            showListPage(sender, 1);
            return;
        }

        // CHECKING IF THE PAGE EXISTS
        int size = plugin.getFileManager().getSavedBars().size();
        int pages = (int) Math.ceil(size / 7.0);

        if (page > pages || page < 1) {
            ErrorUtil.error(sender, "This page does not exist!");
            return;
        }

        showListPage(sender, page);
    }

    @Subcommand("broadcast|bc")
    @Syntax("<text || -get [savename]> [-sound <sound> [pitch]] [-noprefix]")
    @Description("Broadcast an ActionBar.")
    @CommandPermission("actionbar.broadcast")
    public void onActionBarBroadcast(Player player, String strArgs) {
        ActionBar.register(plugin, player, strArgs, null);
    }

    @Subcommand("send")
    @Syntax("<target> <text || -get [savename]> [-sound <sound> [pitch]] [-noprefix]")
    @CommandCompletion("@players")
    @Description("Send an ActionBar to a player.")
    public void onActionBarSend(Player player, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // CHECKING IF PLAYER IS ONLINE
        if (!target.isOnline()) {
            ErrorUtil.error(player, "This player isn't online!");
            return;
        }

        // CHECKING PERMISSION FOR OTHERS
        if (!player.hasPermission("actionbar.send.others") && !Objects.equals(target.getName(), player.getName())) {
            ErrorUtil.error(player, "You do not have the permission to send ActionBar messages to others!");
            return;
        }

        // CHECKING PERMISSION FOR SELF
        if (!player.hasPermission("actionbar.send.self") && Objects.equals(target.getName(), player.getName())) {
            ErrorUtil.error(player, "You do not have the permission to send ActionBar messages to yourself!");
            return;
        }

        String[] newArgs = strArgs.split(" ", 2);
        ActionBar.register(plugin, player, newArgs[1], target.getPlayer());
    }

    @Subcommand("save")
    @Syntax("<message> <name>")
    @Description("Save a custom ActionBar.")
    @CommandPermission("actionbar.save")
    public void onActionBarSave(Player player, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        String message = StringUtils.join(args, " ", 0, args.length - 1);
        String name = args[args.length - 1];
        String prefix = plugin.getConfig().getString("prefix");

        if (!ActionBar.checkIfExists(plugin, player, name)) return;

        plugin.getFileManager().saveBar(new ActionBar(plugin, player.getUniqueId(), prefix + name, message));
        player.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GREEN + "Successfully saved " + ChatColor.GRAY +
                "this ActionBar with the name " + ChatColor.AQUA + "\"" + name + "\"" + ChatColor.GRAY + ".");
    }

    @Subcommand("delete")
    @Syntax("<name>")
    @Description("Delete a saved ActionBar.")
    @CommandPermission("actionbar.delete")
    public void onActionBarDelete(CommandSender sender, String name) {
        if (plugin.getFileManager().deleteBar(name)) {
            sender.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GOLD + "Successfully deleted " + ChatColor.GRAY +
                    "the ActionBar with the name " + ChatColor.RED + "\"" + name + "\"" + ChatColor.GRAY + ".");
        } else {
            ErrorUtil.error(sender, "No such ActionBar exists!");
        }
    }

    @Subcommand("saverecent")
    @Syntax("<name>")
    @Description("Save the most recent ActionBar you sent.")
    @CommandPermission("actionbar.save")
    public void onActionBarSaveRecent(Player player, String name) {
        UUID uuid = player.getUniqueId();

        // CHECKING IF THERE ARE ANY RECENT BARS BY THE PLAYER
        if (!plugin.getHandler().getRecents().containsKey(uuid)) {
            ErrorUtil.error(player, "You haven't sent an ActionBar recently!");
            return;
        }

        if (!ActionBar.checkIfExists(plugin, player, name)) return;
    
        plugin.getFileManager().saveBar(new ActionBar(plugin, uuid, name, plugin.getHandler().getRecents().get(uuid)));
        player.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GREEN + "Successfully saved " + ChatColor.GRAY +
                "your recent ActionBar with the name " + ChatColor.AQUA + "\"" + name + "\"" + ChatColor.GRAY + ".");
    }
    
    @Subcommand("sendtoconsole")
    @Description("Toggle if sent ActionBars get sent to console.")
    @CommandPermission("actionbar.consoletoggle")
    public void onActionBarSendToConsole(CommandSender sender) {
        if (plugin.getConfig().getBoolean("sendToConsole")) {
            plugin.getConfig().set("sendToConsole", false);
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(), "block.note_block.bass", 100, 1F);
            }
        } else {
            plugin.getConfig().set("sendToConsole", true);
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(), "block.note_block.bass", 100, 2F);
            }
        }
        plugin.getConfig().save();
        
        sender.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GRAY + "Toggled sending ActionBars to console to " +
                (plugin.getConfig().getBoolean("sendToConsole") ? ChatColor.GREEN + "true" : ChatColor.RED + "false") +
                ChatColor.GRAY + ".");
    }

    // --------------------------------------------------------------------------------
    // DISPLAYING LISTS
    // --------------------------------------------------------------------------------

    private void showListPage(CommandSender sender, int page) {
        // EACH PAGE WILL HAVE 7 ENTRIES
        int size = plugin.getFileManager().getSavedBars().size();
        int pages = (int) Math.ceil(size / 7.0);
        int limit = Math.min(7 * page, size);

        sender.sendMessage(BarsOfAction.NAMESPACE + "Retrieving saved ActionBars...");
        for (int i = (page - 1) * 7 + 1; i <= limit; i++) {
            sender.sendMessage(plugin.getFileManager().getSavedBars().get(i - 1).toString());
        }
        sender.sendMessage(BarsOfAction.NAMESPACE + "Page " + ChatColor.GREEN + page + ChatColor.GRAY + "/" + pages);
    }
}
