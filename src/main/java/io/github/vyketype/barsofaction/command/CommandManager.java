package io.github.vyketype.barsofaction.command;

import co.aikar.commands.BukkitCommandManager;
import io.github.vyketype.barsofaction.BarsOfAction;
import io.github.vyketype.barsofaction.command.impl.ActionBarCommand;
import io.github.vyketype.barsofaction.command.impl.ActionBarCooldownCommand;
import io.github.vyketype.barsofaction.command.impl.ActionBarPrefixCommand;

public class CommandManager {
    private static final BarsOfAction INSTANCE = BarsOfAction.getINSTANCE();
    
    public CommandManager() {
        init();
    }
    
    public void init() {
        BukkitCommandManager bcm = new BukkitCommandManager(INSTANCE);
        
        bcm.registerCommand(new ActionBarCommand());
        bcm.registerCommand(new ActionBarPrefixCommand());
        bcm.registerCommand(new ActionBarCooldownCommand());
        
        bcm.enableUnstableAPI("help");
        bcm.enableUnstableAPI("brigadier");
    }
}
