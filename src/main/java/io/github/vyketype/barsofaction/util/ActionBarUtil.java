package io.github.vyketype.barsofaction.util;

import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.data.ActionBar;
import io.github.vyketype.barsofaction.data.ActionBarInformation;
import io.github.vyketype.barsofaction.data.SoundInformation;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ActionBarUtil {
    private static final BarsOfAction INSTANCE = BarsOfAction.getINSTANCE();
    
    public static @Nullable ActionBarInformation getActionBarInformation(Player sender, String strArgs) {
        String[] args = StringUtils.split(strArgs, " ", -1);
        
        String content;
        boolean get = args[0].equalsIgnoreCase("-get");
        
        SoundInformation soundInformation = new SoundInformation("", 1F);
        
        // -get argument
        if (get) {
            String name = args[1];
            @Nullable ActionBar bar = INSTANCE.getFileManager().getBar(name);
            
            if (bar == null) {
                ErrorUtil.error(sender, "No such ActionBar exists!");
                return null;
            }
            
            content = bar.content();
        } else {
            content = strArgs;
        }
        
        // -sound argument
        if (args.length > 2) {
            boolean soundAtBeforeLastArg = args[args.length - 2].equalsIgnoreCase("-sound");
            boolean soundAtPreBeforeLastArg = args[args.length - 3].equalsIgnoreCase("-sound");
            
            if (soundAtBeforeLastArg || soundAtPreBeforeLastArg) {
                if (!sender.hasPermission(Permission.ACTIONBAR_SOUND.getName())) {
                    ErrorUtil.error(sender, "You do not have the permission to send sounds with ActionBar messages!");
                    return null;
                }
                
                soundInformation = getSoundInfo(args, soundAtBeforeLastArg, sender);
                
                // Reduce context text to remove sound arguments
                if (!get) {
                    int endIndex = soundAtBeforeLastArg ? args.length - 2 : args.length - 3;
                    content = strArgs.substring(0, endIndex);
                }
                
                // Check if the sound actually exists
                try {
                    Sound.valueOf(soundInformation.sound());
                } catch (IllegalArgumentException noSound) {
                    ErrorUtil.error(sender, "No such sound exists in the Minecraft files!");
                    return null;
                }
            }
        }
        
        ActionBar actionBar = new ActionBar(
                sender.getUniqueId(),
                RandomStringUtils.randomAlphanumeric(9),
                ChatColor.translateAlternateColorCodes('&', getContentWithEscapeSequences(content))
        );
        return new ActionBarInformation(actionBar, soundInformation);
    }
    
    private static SoundInformation getSoundInfo(String[] args, boolean soundAtBeforeLastArg, CommandSender sender) {
        String sound = "";
        float pitch = 1F;
        
        if (soundAtBeforeLastArg) {
            sound = args[args.length - 1].toUpperCase().replace('.', '_');
        } else {
            sound = args[args.length - 2].toUpperCase().replace('.', '_');
            
            try {
                Double.parseDouble(args[args.length - 1]);
            } catch (NumberFormatException badNumber) {
                ErrorUtil.error(sender, "This is not a decimal number!");
            }
            
            pitch = Float.parseFloat(args[args.length - 1]);
        }
        
        return new SoundInformation(sound, pitch);
    }
    
    private static String getContentWithEscapeSequences(String content) {
        if (content.contains("\\-sound") || content.contains("\\-get")) {
            content = content.replace("\\-sound", "-sound");
            content = content.replace("\\-get", "-get");
            content = content.replace("\\-noprefix", "-noprefix");
        }
        return content;
    }
}
