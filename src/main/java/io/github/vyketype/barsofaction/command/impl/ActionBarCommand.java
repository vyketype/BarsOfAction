package io.github.vyketype.barsofaction.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.data.ActionBar;
import io.github.vyketype.barsofaction.data.ActionBarInformation;
import io.github.vyketype.barsofaction.util.ActionBarUtil;
import io.github.vyketype.barsofaction.util.ErrorUtil;
import io.github.vyketype.barsofaction.util.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@CommandAlias("actionbar|ab")
public class ActionBarCommand extends BaseCommand {
    private static final BarsOfAction INSTANCE = BarsOfAction.getINSTANCE();
    
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
        sender.sendMessage(BarsOfAction.NAMESPACE + "Here are the permissions used in this plugin.");
        for (Permission permission : Permission.values()) {
            sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GREEN + permission.getName() +
                    ChatColor.WHITE + " : allows " + permission.getDescription());
        }
    }
    
    @Subcommand("plugin")
    @Description("Information about the plugin.")
    public void onActionBarPlugin(CommandSender sender) {
        TextComponent website = new TextComponent(ChatColor.DARK_GRAY + "› " + ChatColor.GRAY + "GitHub: " +
                ChatColor.LIGHT_PURPLE + "https://github.com/vyketype/BarsOfAction");
        website.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/vyketype/BarsOfAction"));
        
        sender.sendMessage(ChatColor.DARK_AQUA + "BarsOfAction" + ChatColor.RESET + " " + ChatColor.GREEN +
                BarsOfAction.VERSION);
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GRAY + "Create and send " + BarsOfAction.ACTIONBAR +
                ChatColor.GRAY + "s with ease!");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GRAY + "Developed by " + ChatColor.GOLD + "vyketype");
        sender.sendMessage(ChatColor.DARK_GRAY + "› " + ChatColor.GRAY + "Discord: " + ChatColor.AQUA + "vyketype");
        sender.spigot().sendMessage(website);
    }
    
    @Subcommand("list")
    @Syntax("[page]")
    @Description("List all saved ActionBars.")
    public void onActionBarList(CommandSender sender, @Optional Integer page) {
        if (INSTANCE.getFileManager().getSavedBars().isEmpty()) {
            ErrorUtil.error(sender, "There are no saved ActionBars.");
            return;
        }
        
        // Check if no page argument is even -> go to default page 1
        if (page == null) {
            showListPage(sender, 1);
            return;
        }
        
        int size = INSTANCE.getFileManager().getSavedBars().size();
        int pages = (int) Math.ceil(size / 7.0);
        
        if (page > pages || page < 1) {
            ErrorUtil.error(sender, "This page does not exist!");
            return;
        }
        
        showListPage(sender, page);
    }
    
    @Subcommand("broadcast|bc")
    @Syntax("<text || -get [savename]> [-sound <sound> [pitch]]")
    @Description("Broadcast an ActionBar.")
    public void onActionBarBroadcast(Player player, String strArgs) {
        if (!player.hasPermission(Permission.ACTIONBAR_BROADCAST.getName())) {
            ErrorUtil.error(player, "Insufficient permissions!");
            return;
        }
        
        handleSendAndBroadcast(player, strArgs, null, true);
    }
    
    @Subcommand("broadcastwithoutprefix|bcnopref")
    @Syntax("<text || -get [savename]> [-sound <sound> [pitch]]")
    @Description("Broadcast an ActionBar without the prefix.")
    public void onActionBarBroadcastNoPrefix(Player player, String strArgs) {
        if (!player.hasPermission(Permission.ACTIONBAR_BROADCAST_NOPREFIX.getName())) {
            ErrorUtil.error(player, "Insufficient permissions!");
            return;
        }
        
        handleSendAndBroadcast(player, strArgs, null, false);
    }
    
    @Subcommand("send")
    @Syntax("<target> <text || -get [savename]> [-sound <sound> [pitch]]")
    @CommandCompletion("@players")
    @Description("Send an ActionBar to a player.")
    public void onActionBarSend(Player player, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        handleSendAndBroadcast(player, strArgs, args[0], true);
    }
    
    @Subcommand("sendnoprefix|sendnopref")
    @Syntax("<target> <text || -get [savename]> [-sound <sound> [pitch]]")
    @CommandCompletion("@players")
    @Description("Send an ActionBar to a player without the prefix.")
    public void onActionBarSendNoPrefix(Player player, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        handleSendAndBroadcast(player, strArgs, args[0], false);
    }
    
    @Subcommand("save")
    @Syntax("<message> <name>")
    @Description("Save a custom ActionBar.")
    public void onActionBarSave(Player player, String strArgs) {
        if (!player.hasPermission(Permission.ACTIONBAR_SAVE.getName())) {
            ErrorUtil.error(player, "Insufficient permissions!");
            return;
        }
        
        String[] args = StringUtils.split(strArgs, " ", -1);
        String message = StringUtils.join(args, " ", 0, args.length - 1);
        String name = args[args.length - 1];
        
        if (!ActionBar.checkIfExists(INSTANCE, player, name))
              return;
        
        // Save to file
        INSTANCE.getFileManager().saveBar(new ActionBar(player.getUniqueId(), name, message));
        
        player.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GREEN + "Successfully saved " + ChatColor.GRAY +
                "this ActionBar with the name " + ChatColor.AQUA + "\"" + name + "\"" + ChatColor.GRAY + ".");
    }
    
    @Subcommand("delete")
    @Syntax("<name>")
    @Description("Delete a saved ActionBar.")
    @CommandPermission("actionbar.delete")
    public void onActionBarDelete(CommandSender sender, String name) {
        if (!sender.hasPermission(Permission.ACTIONBAR_DELETE.getName())) {
            ErrorUtil.error(sender, "Insufficient permissions!");
            return;
        }
        
        if (INSTANCE.getFileManager().deleteBar(name)) {
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
        if (!player.hasPermission(Permission.ACTIONBAR_SAVE.getName())) {
            ErrorUtil.error(player, "Insufficient permissions!");
            return;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Check if the player has sent an ActionBar recently
        if (!INSTANCE.getRecentsHandler().getRecents().containsKey(uuid)) {
            ErrorUtil.error(player, "You haven't sent an ActionBar recently!");
            return;
        }
        
        if (!ActionBar.checkIfExists(INSTANCE, player, name))
              return;
        
        // Save to file
        ActionBar actionBar = new ActionBar(uuid, name, INSTANCE.getRecentsHandler().getRecents().get(uuid));
        INSTANCE.getFileManager().saveBar(actionBar);
        
        player.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GREEN + "Successfully saved " + ChatColor.GRAY +
                "your recent ActionBar with the name " + ChatColor.AQUA + "\"" + name + "\"" + ChatColor.GRAY + ".");
    }
    
    @Subcommand("sendtoconsole")
    @Description("Toggle if sent ActionBars get sent to console.")
    @CommandPermission("actionbar.consoletoggle")
    public void onActionBarSendToConsole(CommandSender sender) {
        if (!sender.hasPermission(Permission.ACTIONBAR_CONSOLETOGGLE.getName())) {
            ErrorUtil.error(sender, "Insufficient permissions!");
            return;
        }
        
        if (INSTANCE.getConfig().getBoolean("sendToConsole")) {
            INSTANCE.getConfig().set("sendToConsole", false);
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(), "block.note_block.bass", 1, 1F);
            }
        } else {
            INSTANCE.getConfig().set("sendToConsole", true);
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(), "block.note_block.bass", 1, 2F);
            }
        }
        INSTANCE.getConfig().save();
        
        sender.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GRAY + "Toggled sending ActionBars to console to " +
                (INSTANCE.getConfig().getBoolean("sendToConsole") ? ChatColor.GREEN + "true" : ChatColor.RED + "false") +
                ChatColor.GRAY + ".");
    }
    
    // --------------------------------------------------------------------------------
    // DISPLAYING LISTS
    // --------------------------------------------------------------------------------
    
    private void showListPage(CommandSender sender, int page) {
        // Each page will have seven entries
        int size = INSTANCE.getFileManager().getSavedBars().size();
        int pages = (int) Math.ceil(size / 7.0);
        int limit = Math.min(7 * page, size);
        
        sender.sendMessage(BarsOfAction.NAMESPACE + "Retrieving saved ActionBars...");
        for (int i = (page - 1) * 7 + 1; i <= limit; i++) {
            sender.sendMessage(INSTANCE.getFileManager().getSavedBars().get(i - 1).toString());
        }
        sender.sendMessage(BarsOfAction.NAMESPACE + "Page " + ChatColor.GREEN + page + ChatColor.GRAY + "/" + pages);
    }
    
    // --------------------------------------------------------------------------------
    // ACTIONBAR SENDING AND BROADCASTING
    // --------------------------------------------------------------------------------
    
    private void handleSendAndBroadcast(Player player, String strArgs, @Nullable String targetName, boolean prefix) {
        @Nullable Player target = null;
        
        if (targetName != null) {
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
            
            if (!offlineTarget.isOnline()) {
                ErrorUtil.error(player, "This player isn't online!");
                return;
            }
            
            target = offlineTarget.getPlayer();
            
            boolean isPlayerTheTarget = Objects.equals(offlineTarget.getName(), player.getName());
            Permission othersPermission = prefix ? Permission.ACTIONBAR_SEND_OTHERS : Permission.ACTIONBAR_SEND_OTHERS_NOPREFIX;
            Permission selfPermission = prefix ? Permission.ACTIONBAR_SEND_SELF : Permission.ACTIONBAR_SEND_SELF_NOPREFIX;
            
            // Check permission for sending to others
            if (!player.hasPermission(othersPermission.getName()) && !isPlayerTheTarget) {
                ErrorUtil.error(player, "Insufficient permissions!");
                return;
            }
            
            // Check permission for sending to self
            if (!player.hasPermission(selfPermission.getName()) && isPlayerTheTarget) {
                ErrorUtil.error(player, "Insufficient permissions!");
                return;
            }
        }
        
        String content = target == null ? strArgs : strArgs.split(" ", 2)[1];
        @Nullable ActionBarInformation actionBarInfo = ActionBarUtil.getActionBarInformation(player, content);
        if (actionBarInfo == null)
            return;
        
        ActionBar actionBar = actionBarInfo.actionBar();
        actionBar.handleSending(player, actionBarInfo.soundInfo(), prefix, target);
    }
}
