package io.github.vyketype.barsofaction.command;

import co.aikar.commands.BukkitCommandManager;
import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.command.impl.ActionBarCommand;
import io.github.vyketype.barsofaction.command.impl.ActionBarPrefixCommand;

public class CommandManager {
    private final BarsOfAction plugin;
    
    public CommandManager(BarsOfAction plugin) {
        this.plugin = plugin;
        init();
    }
    
    public void init() {
        BukkitCommandManager bcm = new BukkitCommandManager(plugin);
        bcm.registerCommand(new ActionBarCommand(plugin));
        bcm.registerCommand(new ActionBarPrefixCommand(plugin));
        bcm.enableUnstableAPI("help");
        bcm.enableUnstableAPI("brigadier");
    }
}
