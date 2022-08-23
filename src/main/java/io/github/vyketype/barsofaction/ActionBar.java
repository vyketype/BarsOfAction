package io.github.vyketype.barsofaction;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public record ActionBar(UUID creator, String name, String content) {

    /**
     * Sends the actionbar packet to a player
     */
    public void send(Player player) {
        try {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            EntityPlayer handle = craftPlayer.getHandle();

            PlayerConnection playerConnection = handle.b;

            JsonElement element = GsonComponentSerializer.gson().serializeToTree(Component.text(content));
            IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(element);
            ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(component, true);

            playerConnection.a(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        // > template : by vPrototype_
        return ChatColor.DARK_GRAY + "> " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + " : by " +
                ChatColor.GRAY + Bukkit.getOfflinePlayer(creator).getName();
    }

}
