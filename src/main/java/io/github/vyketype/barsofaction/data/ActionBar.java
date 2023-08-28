package io.github.vyketype.barsofaction.data;

import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.util.ErrorUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a savable ActionBar.
 *
 * @author vyketype
 */
public record ActionBar(UUID creator, String name, String content) {
    private static final BarsOfAction INSTANCE = BarsOfAction.getINSTANCE();
    
    /**
     * Sends the ActionBar to a player with a sound.
     *
     * @param player The player to whom the ActionBar should be sent.
     * @param sound  The sound to play when the ActionBar is sent.
     * @param pitch  The pitch to play the sound.
     * @param prefix If the ActionBar prefix should be included.
     */
    private void send(Player player, String sound, float pitch, boolean prefix) {
        send(player, prefix);
        if (!Objects.equals(sound, "")) {
            player.playSound(player.getLocation(), Sound.valueOf(sound.toUpperCase()), 1, pitch);
        }
    }
    
    /**
     * Sends the ActionBar to a player.
     *
     * @param player The player to whom the ActionBar should be sent.
     * @param prefix If the ActionBar prefix should be included.
     */
    private void send(Player player, boolean prefix) {
        String prefixText = ChatColor.translateAlternateColorCodes('&', INSTANCE.getConfig().getString("prefix"));
        if (!prefix) {
            prefixText = "";
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(prefixText + content));
    }
    
    public void handleSending(Player player, SoundInformation soundInfo, boolean prefix, @Nullable Player target) {
        sendToConsole(player, prefix);
        
        // Add to handler
        INSTANCE.getRecentsHandler().getRecents().put(player.getUniqueId(), content);
        
        // Get sound information
        String sound = soundInfo.sound();
        float pitch = soundInfo.pitch();
        
        // If target is null, broadcast ActionBar, otherwise send it to target
        if (target == null) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                send(onlinePlayer, sound, pitch, prefix);
            }
            player.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GRAY + "ActionBar message " + ChatColor.GREEN +
                    "successfully broadcast" + ChatColor.GRAY + ".");
        } else {
            send(target, sound, pitch, prefix);
            player.sendMessage(BarsOfAction.NAMESPACE + ChatColor.GRAY + "ActionBar message " + ChatColor.GREEN +
                    "successfully sent" + ChatColor.GRAY + ".");
        }
    }
    
    private void sendToConsole(Player player, boolean prefix) {
        if (INSTANCE.getConfig().getBoolean("sendToConsole")) {
            String prefixText = ChatColor.translateAlternateColorCodes('&', INSTANCE.getConfig().getString("prefix"));
            if (!prefix) {
                prefixText = "";
            }
            INSTANCE.getLogger().info("ActionBar message by " + player.getName() + ": " + prefixText + content);
        }
    }
    
    @Override
    public String toString() {
        // TODO: Rework this at some point
        // > template : by vyketype
        return ChatColor.DARK_GRAY + "› " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + " : by " +
                ChatColor.GRAY + Bukkit.getOfflinePlayer(creator).getName();
    }
    
    /**
     * Checks if an ActionBar with a certain name exists.
     *
     * @param plugin An instance of the plugin.
     * @param player The player who created the ActionBar.
     * @param name   The name to check for.
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
}
