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

    public static String PREFIX = "" + ChatColor.of("#71ae8c") + ChatColor.BOLD + "A" +
            ChatColor.of("#6db091") + ChatColor.BOLD + "C" +
            ChatColor.of("#6ab297") + ChatColor.BOLD + "T" +
            ChatColor.of("#66b39d") + ChatColor.BOLD + "I" +
            ChatColor.of("#63b5a3") + ChatColor.BOLD + "O" +
            ChatColor.of("#60b7a8") + ChatColor.BOLD + "N" +
            ChatColor.of("#5eb8ae") + ChatColor.BOLD + "B" +
            ChatColor.of("#5cbab4") + ChatColor.BOLD + "A" +
            ChatColor.of("#5abbba") + ChatColor.BOLD + "R" +
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

            getLogger().info("Successfully loaded BarsOfAction v1.1-SNAPSHOT by vyketype");
        } catch (Throwable t) {
            t.printStackTrace();
            getLogger().info("Failed to load BarsOfAction v1.1-SNAPSHOT");
        }
    }

    @Override
    public void onDisable() {
        instance = null;

        getLogger().info("Disabled BarsOfAction v1.1-SNAPSHOT");
    }
}
