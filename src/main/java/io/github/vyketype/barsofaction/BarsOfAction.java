package io.github.vyketype.barsofaction;

import io.github.vyketype.barsofaction.command.CommandManager;
import io.github.vyketype.barsofaction.command.CooldownHandler;
import io.github.vyketype.barsofaction.command.SaveRecentsHandler;
import io.github.vyketype.barsofaction.data.Config;
import io.github.vyketype.barsofaction.data.FileManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@Getter
public class BarsOfAction extends JavaPlugin {
    public static String ACTIONBAR = "" + ChatColor.of("#71ae8c") + ChatColor.BOLD + "A" +
            ChatColor.of("#6db091") + ChatColor.BOLD + "C" +
            ChatColor.of("#6ab297") + ChatColor.BOLD + "T" +
            ChatColor.of("#66b39d") + ChatColor.BOLD + "I" +
            ChatColor.of("#63b5a3") + ChatColor.BOLD + "O" +
            ChatColor.of("#60b7a8") + ChatColor.BOLD + "N" +
            ChatColor.of("#5eb8ae") + ChatColor.BOLD + "B" +
            ChatColor.of("#5cbab4") + ChatColor.BOLD + "A" +
            ChatColor.of("#5abbba") + ChatColor.BOLD + "R" +
            ChatColor.RESET;
    public static String NAMESPACE = ACTIONBAR + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "";
    public static String VERSION = "v1.4-SNAPSHOT";

    private Config savedBars;
    private Config config;

    private FileManager fileManager;
    private SaveRecentsHandler recentsHandler;
    private CooldownHandler cooldownHandler;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        try {
            savedBars = new Config(this, new File(getDataFolder().getAbsolutePath() + "/savedbars.yml"), "savedbars.yml");
            config = new Config(this, new File(getDataFolder().getAbsolutePath() + "/config.yml"), "config.yml");
            fileManager = new FileManager(this);
            recentsHandler = new SaveRecentsHandler();
            cooldownHandler = new CooldownHandler();
            commandManager = new CommandManager(this);

            getLogger().info("Successfully loaded BarsOfAction " + VERSION + " by vyketype");
            
            String prefix = Objects.requireNonNull(config.getString("prefix"));
            if (prefix.isEmpty()) {
                prefix = "None";
            }
            getLogger().info("ActionBar prefix: \"" + prefix + "\"");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            getLogger().info("Failed to load BarsOfAction " + VERSION);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled BarsOfAction " + VERSION + " by vyketype");
    }
}
