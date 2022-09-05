package io.github.vyketype.barsofaction;

import co.aikar.commands.BukkitCommandManager;
import io.github.vyketype.barsofaction.command.impl.ActionBarCommand;
import io.github.vyketype.barsofaction.command.SaveRecentHandler;
import io.github.vyketype.barsofaction.data.FileManager;
import io.github.vyketype.barsofaction.data.Config;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BarsOfAction extends JavaPlugin {

    public static String NAMESPACE = "" + ChatColor.of("#71ae8c") + ChatColor.BOLD + "A" +
            ChatColor.of("#6db091") + ChatColor.BOLD + "C" +
            ChatColor.of("#6ab297") + ChatColor.BOLD + "T" +
            ChatColor.of("#66b39d") + ChatColor.BOLD + "I" +
            ChatColor.of("#63b5a3") + ChatColor.BOLD + "O" +
            ChatColor.of("#60b7a8") + ChatColor.BOLD + "N" +
            ChatColor.of("#5eb8ae") + ChatColor.BOLD + "B" +
            ChatColor.of("#5cbab4") + ChatColor.BOLD + "A" +
            ChatColor.of("#5abbba") + ChatColor.BOLD + "R" +
            ChatColor.RESET + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "";

    public static String VERSION = "v1.2-SNAPSHOT";

    @Getter
    private Config config;

    @Getter
    private FileManager fileManager;

    @Getter
    private SaveRecentHandler handler;

    @Override
    public void onEnable() {
        try {
            config = new Config(this, new File(getDataFolder().getAbsolutePath() + "/savedbars.yml"), "savedbars.yml");
            fileManager = new FileManager(this);
            handler = new SaveRecentHandler();

            BukkitCommandManager bcm = new BukkitCommandManager(this);
            bcm.registerCommand(new ActionBarCommand(this));
            bcm.enableUnstableAPI("help");
            bcm.enableUnstableAPI("brigadier");

            getLogger().info("Successfully loaded BarsOfAction " + VERSION + " by vyketype");
        } catch (Throwable t) {
            t.printStackTrace();
            getLogger().info("Failed to load BarsOfAction " + VERSION);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled BarsOfAction " + VERSION + " by vyketype");
    }

}
