package io.github.vyketype.barsofaction.util;

import io.github.vyketype.barsofaction.ActionBar;
import io.github.vyketype.barsofaction.BarsOfAction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages the saving and loading of existing actionbars
 */
public class FileManager {

    private static final BarsOfAction instance = BarsOfAction.getInstance();

    /**
     * Saves the ActionBar in savedbars.yml
     * @param bar ActionBar to be saved
     */
    public void saveBar(ActionBar bar) {
        instance.getConfig().set("actionBars." + bar.name() + ".creator", bar.creator().toString());
        instance.getConfig().set("actionBars." + bar.name() + ".content", bar.content());
        instance.getConfig().save();
    }

    /**
     * Delete a saved ActionBar from savedbars.yml using a name
     * @param name name of the ActionBar to delete
     * @return true if the operation was successful
     */
    public boolean deleteBar(String name) {
        if (instance.getConfig().getConfigurationSection("actionBars." + name) == null) return false;
        instance.getConfig().set("actionBars." + name, null);
        instance.getConfig().save();
        return true;
    }

    /**
     * Gets an ActionBar from savedbars.yml using a name
     * @param name name of the ActionBar to retrieve
     * @return retrieved ActionBar or null if none exists
     */
    public @Nullable ActionBar getBar(String name) {
        if (instance.getConfig().getConfigurationSection("actionBars." + name) == null) return null;
        return new ActionBar(
                UUID.fromString(instance.getConfig().getString("actionBars." + name + ".creator")),
                name,
                instance.getConfig().getString("actionBars." + name + ".content")
        );
    }

    /**
     * Gets the list of all saved ActionBars from savedbars.yml
     * @return list of saved ActionBars
     */
    public List<ActionBar> getSavedBars() {
        List<ActionBar> list = new ArrayList<>();
        Set<String> actionBars = Objects.requireNonNull(
                instance.getConfig().getConfigurationSection("actionBars")
        ).getKeys(false);

        for (String name : actionBars) {
            list.add(getBar(name));
        }

        return list;
    }

}
