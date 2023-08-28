package io.github.vyketype.barsofaction.data;

import io.github.vyketype.barsofaction.BarsOfAction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages the saving and loading of existing ActionBars.
 */
public class FileManager {
    private static final BarsOfAction INSTANCE = BarsOfAction.getINSTANCE();
    
    /**
     * Updates the ActionBar prefix.
     *
     * @param text New prefix.
     */
    public void setPrefix(String text) {
        INSTANCE.getConfig().set("prefix", text);
        INSTANCE.getConfig().save();
    }
    
    /**
     * Saves the ActionBar in savedbars.yml.
     *
     * @param bar ActionBar to be saved.
     */
    public void saveBar(ActionBar bar) {
        INSTANCE.getSavedBars().set("actionBars." + bar.name() + ".creator", bar.creator().toString());
        INSTANCE.getSavedBars().set("actionBars." + bar.name() + ".content", bar.content());
        INSTANCE.getSavedBars().save();
    }
    
    /**
     * Delete a saved ActionBar from savedbars.yml using a name.
     *
     * @param name Name of the ActionBar to delete.
     * @return True if the operation was successful.
     */
    public boolean deleteBar(String name) {
        if (INSTANCE.getSavedBars().getConfigurationSection("actionBars." + name) == null)
            return false;
        INSTANCE.getSavedBars().set("actionBars." + name, null);
        INSTANCE.getSavedBars().save();
        return true;
    }
    
    /**
     * Gets an ActionBar from savedbars.yml using a name.
     *
     * @param name Name of the ActionBar to retrieve.
     * @return Retrieved ActionBar or null if none exists.
     */
    public @Nullable ActionBar getBar(String name) {
        if (INSTANCE.getSavedBars().getConfigurationSection("actionBars." + name) == null)
            return null;
        
        return new ActionBar(
                UUID.fromString(INSTANCE.getSavedBars().getString("actionBars." + name + ".creator")),
                name,
                INSTANCE.getSavedBars().getString("actionBars." + name + ".content")
        );
    }
    
    /**
     * Gets the list of all saved ActionBars from savedbars.yml.
     *
     * @return List of saved ActionBars.
     */
    public List<ActionBar> getSavedBars() {
        List<ActionBar> list = new ArrayList<>();
        Set<String> actionBars = Objects.requireNonNull(
                INSTANCE.getSavedBars().getConfigurationSection("actionBars")
        ).getKeys(false);
        
        for (String name : actionBars) {
            list.add(getBar(name));
        }
        
        return list;
    }
}
