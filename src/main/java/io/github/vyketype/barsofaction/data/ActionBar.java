package io.github.vyketype.barsofaction.data;

import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.util.ErrorUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a saveable ActionBar.
 *
 * @author vyketype
 */
public record ActionBar(UUID creator, String name, String content) {
    /**
     * Sends the ActionBar to a player.
     *
     * @param player The player to whom the ActionBar should be sent.
     * @param sound The sound to play when the ActionBar is sent.
     * @param pitch The pitch to play the sound.
     */
    public void send(BarsOfAction plugin, Player player, String sound, float pitch) {
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix"));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(prefix + content));
        player.playSound(player.getLocation(), Sound.valueOf(sound.toUpperCase()), 100F, pitch);
    }
    
    public void handleSending(BarsOfAction plugin, Player player, String sound, float pitch, @Nullable Player target) {
        // SEND TO CONSOLE
        if (plugin.getConfig().getBoolean("sendToConsole")) {
            String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix"));
            plugin.getLogger().info("ActionBar message by " + player.getName() + " : " + prefix + content);
        }
    
        // ADD TO HANDLER
        plugin.getHandler().getRecents().put(player.getUniqueId(), content);
    
        // IF TARGET IS NULL, DO BROADCAST, ELSE, SEND TO INDIVIDUAL
        if (target == null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                send(plugin, p, sound, pitch);
            }
            player.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GRAY + "ActionBar message " + ChatColor.GREEN +
                    "successfully broadcast" + ChatColor.GRAY  + ".");
        } else {
            send(plugin, target, sound, pitch);
            player.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GRAY + "ActionBar message " + ChatColor.GREEN +
                    "successfully sent" + ChatColor.GRAY  + ".");
        }
    }

    @Override
    public String toString() {
        // > template : by vPrototype_
        return ChatColor.DARK_GRAY + "> " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + " : by " +
                ChatColor.GRAY + Bukkit.getOfflinePlayer(creator).getName();
    }
    
    /**
     * Checks if an ActionBar with a certain name exists.
     *
     * @param plugin An instance of the plugin.
     * @param player The player who created the ActionBar.
     * @param name The name to check for.
     * @return True if an ActionBar with a certain name exists.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkIfExists(BarsOfAction plugin, Player player, String name) {
        @Nullable ActionBar maybeExists = plugin.getFileManager().getBar(name);
        
        if (maybeExists != null && maybeExists.creator() == player.getUniqueId()) {
            ErrorUtil.error(player, "An ActionBar with that name already exists!");
            return false;
        }
        
        return true;
    }
    
    public static void register(BarsOfAction plugin, Player sender, String strArgs, @Nullable Player target) {
        String[] args = StringUtils.split(strArgs, " ", -1);
    
        String content;
        String sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP.name();
        float pitch = 1F;
        boolean get = false;
    
        // -GET ARGUMENT
        if (args[0].equalsIgnoreCase("-get")) {
            String name = args[1];
            @Nullable ActionBar bar = plugin.getFileManager().getBar(name);
        
            if (bar == null) {
                ErrorUtil.error(sender, "No such ActionBar exists!");
                return;
            }
        
            content = bar.content();
            get = true;
        } else {
            content = strArgs;
        }
    
        // -SOUND ARGUMENT
        if (args.length > 2) {
            if (args[args.length - 2].equalsIgnoreCase("-sound") || args[args.length - 3].equalsIgnoreCase("-sound")) {
                // CHECKING FOR A PITCH ARGUMENT
                if (args[args.length - 2].equalsIgnoreCase("-sound")) {
                    sound = args[args.length - 1].toUpperCase().replace('.', '_');
                    if (!get) content = StringUtils.join(args, " ", 0, args.length - 2);
                } else if (args[args.length - 3].equalsIgnoreCase("-sound")) {
                    sound = args[args.length - 2].toUpperCase().replace('.', '_');
                    if (!get) content = StringUtils.join(args, " ", 0, args.length - 2);
                
                    try {
                        Double.parseDouble(args[args.length - 1]);
                    } catch (NumberFormatException badNumber) {
                        ErrorUtil.error(sender, "This is not a decimal number!");
                    }
                
                    pitch = Float.parseFloat(args[args.length - 1]);
                }
            
                // CHECKS IF THE SOUND EXISTS
                try {
                    Sound.valueOf(sound);
                } catch (IllegalArgumentException noSound) {
                    ErrorUtil.error(sender, "No such sound exists in the Minecraft files!");
                    return;
                }
            }
        }
    
        // ESCAPE SEQUENCES
        if (content.contains("\\-sound") || content.contains("\\-get")) {
            content = content.replace("\\-sound", "-sound");
            content = content.replace("\\-get", "-get");
        }
    
        new ActionBar(
                sender.getUniqueId(),
                "nameDoesNotMatter",
                ChatColor.translateAlternateColorCodes('&', content)
        ).handleSending(plugin, sender, sound, pitch, target);
    }
}
