package io.github.vyketype.barsofaction.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
@AllArgsConstructor
public enum Permission {
    ACTIONBAR_BROADCAST(ChatColor.AQUA + "/ab broadcast"),
    ACTIONBAR_BROADCAST_NOPREFIX(ChatColor.AQUA + "/ab bcnopref"),
    ACTIONBAR_CONSOLETOGGLE(ChatColor.AQUA + "/ab consoletoggle"),
    ACTIONBAR_DELETE(ChatColor.AQUA + "/ab delete"),
    ACTIONBAR_PREFIX(ChatColor.AQUA + "/ab prefix"),
    ACTIONBAR_SAVE(ChatColor.AQUA + "/ab save" + ChatColor.WHITE + " and " + ChatColor.AQUA + "/ab saverecent"),
    ACTIONBAR_SEND_SELF(ChatColor.AQUA + "/ab send" + ChatColor.WHITE + " to yourself"),
    ACTIONBAR_SEND_OTHERS(ChatColor.AQUA + "/ab send" + ChatColor.WHITE + " to others"),
    ACTIONBAR_SEND_SELF_NOPREFIX(ChatColor.AQUA + "/ab sendnopref" + ChatColor.WHITE + " to yourself"),
    ACTIONBAR_SEND_OTHERS_NOPREFIX(ChatColor.AQUA + "/ab sendnopref" + ChatColor.WHITE + " to others"),
    ACTIONBAR_SOUND(ChatColor.WHITE + "the use of the " + ChatColor.AQUA + "-sound" + ChatColor.WHITE + " argument");
//    ACTIONBAR_COOLDOWN_SET(ChatColor.AQUA + "/ab cooldown set" + ChatColor.WHITE + " for players"),
//    ACTIONBAR_COOLDOWN_SET_GLOBAL(ChatColor.AQUA + "/ab cooldown set" + ChatColor.WHITE + " globally"),
//    ACTIONBAR_COOLDOWN_QUERY_OTHERS(ChatColor.AQUA + "/ab cooldown query" + ChatColor.WHITE + " for others"),
//    ACTIONBAR_COOLDOWN_BYPASS(ChatColor.WHITE + "bypassing any ActionBar cooldown");
    
    private final String description;
    
    public String getName() {
        return name().toLowerCase().replace('_', '.');
    }
}
