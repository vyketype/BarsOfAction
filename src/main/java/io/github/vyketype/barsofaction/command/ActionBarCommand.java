package io.github.vyketype.barsofaction.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.vyketype.barsofaction.ActionBar;
import io.github.vyketype.barsofaction.BarsOfAction;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@CommandAlias("actionbar|ab")
@CommandPermission("actionbar.use")
public class ActionBarCommand extends BaseCommand {

    private static final BarsOfAction instance = BarsOfAction.getInstance();

    @HelpCommand
    @Default
    public void onActionBar(CommandHelp commandHelp) {
        commandHelp.showHelp();
    }

    @Subcommand("list")
    @Description("List all saved ActionBars. /ab list")
    public void onActionBarList(CommandSender sender) {
        // CHECKING IF SAVED BARS EXIST AT ALL
        if (instance.getManager().getSavedBars().isEmpty()) {
            sender.sendMessage(BarsOfAction.PREFIX + ChatColor.RED + "There are no saved ActionBars.");
            if (sender instanceof Player player)
                player.playSound(player.getLocation(), "entity.enderman.teleport", 100, 0.5F);
            return;
        }

        sender.sendMessage(BarsOfAction.PREFIX + "Retrieving all saved ActionBars...");
        for (ActionBar bar : instance.getManager().getSavedBars()) {
            sender.sendMessage(bar.toString());
        }
    }

    @Subcommand("broadcast")
    @Description("Broadcast a custom ActionBar. /ab broadcast <message> OR ALTERNATIVELY TO RETRIEVE A SAVED ACTIONBAR: /ab broadcast -get <savename>")
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
            player.sendMessage(BarsOfAction.PREFIX + ChatColor.RED + "This player isn't online!");
            player.playSound(player.getLocation(), "entity.enderman.teleport", 100, 0.5F);
            return;
        }

        String[] newArgs = strArgs.split(" ", 2);
        handleSending(player, newArgs[1], target.getPlayer());
    }

    @Subcommand("save")
    @Description("Save a custom ActionBar. /ab save <message> <name>")
    public void onActionBarSave(Player player, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        String message = StringUtils.join(args, " ", 0, args.length - 1);
        String name = args[args.length - 1];

        instance.getManager().saveBar(new ActionBar(player.getUniqueId(), name, message));
        player.sendMessage(BarsOfAction.PREFIX + ChatColor.GREEN + "Successfully saved this ActionBar with " +
                "the name " + ChatColor.AQUA + "\"" + name + "\"" + ChatColor.GREEN + ".");
    }

    @Subcommand("delete")
    @Description("Delete a saved ActionBar. /ab delete <name>")
    public void onActionBarDelete(CommandSender sender, String name) {
        if (instance.getManager().deleteBar(name)) {
            sender.sendMessage(BarsOfAction.PREFIX + ChatColor.GOLD + "Successfully deleted the ActionBar with the " +
                    "name " + ChatColor.RED + "\"" + name + "\"" + ChatColor.GOLD + ".");
        } else {
            sender.sendMessage(BarsOfAction.PREFIX + ChatColor.RED + "No such ActionBar exists!");
            if (sender instanceof Player player)
                player.playSound(player.getLocation(), "entity.enderman.teleport", 100, 0.5F);
        }
    }

    @Subcommand("saverecent")
    @Description("Save the most recent ActionBar you sent. /ab saverecent <name>")
    public void onActionBarSaveRecent(Player player, String name) {
        UUID uuid = player.getUniqueId();

        // CHECKING IF THERE ARE ANY RECENT BARS BY THE PLAYER
        if (!instance.getHandler().getRecents().containsKey(uuid)) {
            player.sendMessage(BarsOfAction.PREFIX + ChatColor.RED + "You haven't sent an ActionBar recently!");
            player.playSound(player.getLocation(), "entity.enderman.teleport", 100, 0.5F);
            return;
        }

        instance.getManager().saveBar(new ActionBar(uuid, name, instance.getHandler().getRecents().get(uuid)));
        player.sendMessage(BarsOfAction.PREFIX + ChatColor.GREEN + "Successfully saved your recent ActionBar with " +
                "the name " + ChatColor.AQUA + "\"" + name + "\"" + ChatColor.GREEN + ".");
    }

    // --------------------------------------------------------------------------------

    public void handleSending(Player sender, String strArgs, @Nullable Player target) {
        String[] args = StringUtils.split(strArgs, " ", -1);

        String content;

        // -GET ARGUMENT
        if (args[0].equalsIgnoreCase("-get")) {
            String name = args[1];
            @Nullable ActionBar bar = instance.getManager().getBar(name);

            if (bar == null) {
                sender.sendMessage(BarsOfAction.PREFIX + ChatColor.RED + "No such ActionBar exists!");
                sender.playSound(sender.getLocation(), "entity.enderman.teleport", 100, 0.5F);
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

        ActionBar actionBar = new ActionBar(sender.getUniqueId(), "nameDoesntMatter",
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
        sender.sendMessage(BarsOfAction.PREFIX + ChatColor.GREEN + "ActionBar message successfully sent.");
    }

}
