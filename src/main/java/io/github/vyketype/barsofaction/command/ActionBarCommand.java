package io.github.vyketype.barsofaction.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.vyketype.barsofaction.ActionBar;
import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.util.ErrorUtil;
import net.md_5.bungee.api.ChatColor;
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

    private static final BarsOfAction instance = BarsOfAction.getInstance();

    @HelpCommand
    @Default
    public void onActionBar(CommandHelp commandHelp) {
        commandHelp.showHelp();
    }

    @Subcommand("permissions|perms")
    @Description("List all permissions for this plugin. /ab perms")
    public void onActionBarPerms(CommandSender sender) {
        sender.sendMessage(BarsOfAction.PREFIX + "Getting the permissions for this plugin...");
        sender.sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.GREEN + "actionbar.broadcast" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab broadcast");
        sender.sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.GREEN + "actionbar.delete" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab delete");
        sender.sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.GREEN + "actionbar.save" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab save" + ChatColor.WHITE + " and " +
                ChatColor.AQUA + "/ab saverecent");
        sender.sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.GREEN + "actionbar.send.self" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab send" + ChatColor.WHITE + " to yourself");
        sender.sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.GREEN + "actionbar.send.others" +
                ChatColor.WHITE + " : allows " + ChatColor.AQUA + "/ab send" + ChatColor.WHITE + " to others");
        sender.sendMessage();
    }

    @Subcommand("list")
    @Description("List all saved ActionBars. /ab list [page]")
    public void onActionBarList(CommandSender sender, @Optional Integer page) {
        // CHECKING IF SAVED BARS EXIST AT ALL
        if (instance.getManager().getSavedBars().isEmpty()) {
            ErrorUtil.error(sender, "There are no saved ActionBars.");
            return;
        }

        // CHECKING IF NO PAGE ARGUMENT IS GIVEN
        if (page == null) {
            showListPage(sender, 1);
            return;
        }

        // CHECKING IF THE PAGE EXISTS
        int size = instance.getManager().getSavedBars().size();
        int pages = (int) Math.ceil(size / 7.0);

        if (page > pages || page < 1) {
            ErrorUtil.error(sender, "This page does not exist!");
            return;
        }

        int p = page;
        showListPage(sender, p);
    }

    @Subcommand("broadcast")
    @Description("Broadcast a custom ActionBar. /ab broadcast <message> OR ALTERNATIVELY TO RETRIEVE A SAVED ACTIONBAR: /ab broadcast -get <savename>")
    @CommandPermission("actionbar.broadcast")
    public void onActionBarBroadcast(Player player, String strArgs) {
        handleSending(player, strArgs, null);
    }

    @Subcommand("send")
    @CommandCompletion("@players")
    @Description("Send a custom ActionBar. /ab send <target> <message> OR ALTERNATIVELY TO RETRIEVE A SAVED ACTIONBAR: /ab send <player> -get <savename>")
    public void onActionBarSend(Player player, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // CHECKING IF PLAYER IS ONLINE
        if (!target.isOnline()) {
            ErrorUtil.error(player, "This player isn't online!");
            return;
        }

        // CHECK PERMISSION FOR OTHERS
        if (!player.hasPermission("actionbar.send.others") && !Objects.equals(target.getName(), player.getName())) {
            ErrorUtil.error(player, "You do not have the permission to send ActionBar messages to others!");
            return;
        }

        // CHECK PERMISSION FOR SELF
        if (!player.hasPermission("actionbar.send.self") && Objects.equals(target.getName(), player.getName())) {
            ErrorUtil.error(player, "You do not have the permission to send ActionBar messages to yourself!");
            return;
        }

        String[] newArgs = strArgs.split(" ", 2);
        handleSending(player, newArgs[1], target.getPlayer());
    }

    @Subcommand("save")
    @Description("Save a custom ActionBar. /ab save <message> <name>")
    @CommandPermission("actionbar.save")
    public void onActionBarSave(Player player, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        String message = StringUtils.join(args, " ", 0, args.length - 1);
        String name = args[args.length - 1];

        if (!checkIfExists(player, name)) return;

        instance.getManager().saveBar(new ActionBar(player.getUniqueId(), name, message));
        player.sendMessage(BarsOfAction.PREFIX + ChatColor.GREEN + "Successfully saved " + ChatColor.GRAY +
                "this ActionBar with the name " + ChatColor.AQUA + "\"" + name + "\"" + ChatColor.GRAY + ".");
    }

    @Subcommand("delete")
    @Description("Delete a saved ActionBar. /ab delete <name>")
    @CommandPermission("actionbar.delete")
    public void onActionBarDelete(CommandSender sender, String name) {
        if (instance.getManager().deleteBar(name)) {
            sender.sendMessage(BarsOfAction.PREFIX + ChatColor.GOLD + "Successfully deleted " + ChatColor.GRAY +
                    "the ActionBar with the name " + ChatColor.RED + "\"" + name + "\"" + ChatColor.GRAY + ".");
        } else {
            ErrorUtil.error(sender, "No such ActionBar exists!");
        }
    }

    @Subcommand("saverecent")
    @Description("Save the most recent ActionBar you sent. /ab saverecent <name>")
    @CommandPermission("actionbar.save")
    public void onActionBarSaveRecent(Player player, String name) {
        UUID uuid = player.getUniqueId();

        // CHECKING IF THERE ARE ANY RECENT BARS BY THE PLAYER
        if (!instance.getHandler().getRecents().containsKey(uuid)) {
            ErrorUtil.error(player, "You haven't sent an ActionBar recently!");
            return;
        }

        if (!checkIfExists(player, name)) return;

        instance.getManager().saveBar(new ActionBar(uuid, name, instance.getHandler().getRecents().get(uuid)));
        player.sendMessage(BarsOfAction.PREFIX + ChatColor.GREEN + "Successfully saved " + ChatColor.GRAY +
                "your recent ActionBar with the name " + ChatColor.AQUA + "\"" + name + "\"" + ChatColor.GRAY + ".");
    }

    // --------------------------------------------------------------------------------

    private boolean checkIfExists(Player player, String name) {
        // CHECKING IF AN ACTIONBAR WITH THAT NAME EXISTS
        // IF IT EXISTS, ONLY THE CREATOR PLAYER CAN OVERWRITE ITS SAVE
        @Nullable ActionBar maybeExists = instance.getManager().getBar(name);

        if (maybeExists != null && maybeExists.creator() == player.getUniqueId()) {
            ErrorUtil.error(player, "An ActionBar with that name already exists!");
            return false;
        }

        return true;
    }

    public void showListPage(CommandSender sender, int page) {
        // EACH PAGE WILL HAVE 7 ENTRIES
        int size = instance.getManager().getSavedBars().size();
        int pages = (int) Math.ceil(size / 7.0);
        int limit = Math.min(7 * page, size);

        sender.sendMessage(BarsOfAction.PREFIX + "Retrieving saved ActionBars...");
        for (int i = (page - 1) * 7 + 1; i <= limit; i++) {
            sender.sendMessage(instance.getManager().getSavedBars().get(i - 1).toString());
        }
        sender.sendMessage(BarsOfAction.PREFIX + "Page " + ChatColor.GREEN + page + ChatColor.GRAY + "/" + pages);
    }

    public void handleSending(Player sender, String strArgs, @Nullable Player target) {
        String[] args = StringUtils.split(strArgs, " ", -1);

        String content;

        // -GET ARGUMENT
        if (args[0].equalsIgnoreCase("-get")) {
            String name = args[1];
            @Nullable ActionBar bar = instance.getManager().getBar(name);

            if (bar == null) {
                ErrorUtil.error(sender, "No such ActionBar exists!");
                return;
            }

            content = bar.content();
        } else {
            content = strArgs;
        }

        // SEND TO CONSOLE
        instance.getLogger().info("ActionBar message by " + sender.getName() + " : " + content);

        // ADD TO HANDLER
        instance.getHandler().getRecents().put(sender.getUniqueId(), content);

        ActionBar actionBar = new ActionBar(sender.getUniqueId(), "nameDoesNotMatter",
                ChatColor.translateAlternateColorCodes('&', content));

        // IF TARGET IS NULL, DO BROADCAST
        if (target == null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                actionBar.send(p);
                p.playSound(p.getLocation(), "entity.experience_orb.pickup", 100, 0.5F);
            }
        }
        // OTHERWISE, SEND TO INDIVIDUAL
        else {
            actionBar.send(target);
            target.playSound(target.getLocation(), "entity.experience_orb.pickup", 100, 0.5F);
        }

        // SEND FEEDBACK MESSAGE
        sender.sendMessage(BarsOfAction.PREFIX + ChatColor.GRAY + "ActionBar message " + ChatColor.GREEN +
                "successfully sent" + ChatColor.GRAY  + ".");
    }

}
