package io.github.vyketype.barsofaction;

import co.aikar.commands.BukkitCommandManager;
import io.github.vyketype.barsofaction.command.ActionBarCommand;
import io.github.vyketype.barsofaction.handler.SaveRecentHandler;
import io.github.vyketype.barsofaction.util.Config;
import io.github.vyketype.barsofaction.util.FileManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BarsOfAction extends JavaPlugin {

    // SINGLETON PATTERN
    @Getter private static BarsOfAction instance;

    public static String PREFIX = "" + ChatColor.of("#2a4858") + ChatColor.BOLD + "A" +
            ChatColor.of("#123c71") + ChatColor.BOLD + "C" +
            ChatColor.of("#007286") + ChatColor.BOLD + "T" +
            ChatColor.of("#008896") + ChatColor.BOLD + "I" +
            ChatColor.of("#009f9e") + ChatColor.BOLD + "O" +
            ChatColor.of("#00b5a0") + ChatColor.BOLD + "N" +
            ChatColor.of("#00cb9a") + ChatColor.BOLD + "B" +
            ChatColor.of("#00e08d") + ChatColor.BOLD + "A" +
            ChatColor.of("#4ff47a") + ChatColor.BOLD + "R" +
            ChatColor.RESET + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "";

    @Getter private Config config;
    @Getter private FileManager manager;
    @Getter private SaveRecentHandler handler;

    @Override
    public void onEnable() {
        try {
            instance = this;

            config = new Config(new File(getDataFolder().getAbsolutePath() + "/savedbars.yml"), "savedbars.yml");
            manager = new FileManager();
            handler = new SaveRecentHandler();

            // Would have done in a separate class if there was more than one command...
            BukkitCommandManager bcm = new BukkitCommandManager(this);
            bcm.registerCommand(new ActionBarCommand());
            bcm.enableUnstableAPI("help");
            bcm.enableUnstableAPI("brigadier");

            getLogger().info("Successfully loaded BarsOfAction v1.0 by vyketype");
        } catch (Throwable t) {
            t.printStackTrace();
            getLogger().info("Failed to load BarsOfAction v1.0");
        }
    }

    @Override
    public void onDisable() {
        instance = null;

        getLogger().info("Disabled BarsOfAction v1.0");
    }
}
