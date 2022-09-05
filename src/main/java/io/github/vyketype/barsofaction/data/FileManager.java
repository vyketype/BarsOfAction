package io.github.vyketype.barsofaction.data;

import io.github.vyketype.barsofaction.BarsOfAction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages the saving and loading of existing actionbars
 */
public class FileManager {
    private final BarsOfAction plugin;

    public FileManager(BarsOfAction plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves the ActionBar in savedbars.yml.
     *
     * @param bar ActionBar to be saved.
     */
    public void saveBar(ActionBar bar) {
        plugin.getConfig().set("actionBars." + bar.name() + ".creator", bar.creator().toString());
        plugin.getConfig().set("actionBars." + bar.name() + ".content", bar.content());
        plugin.getConfig().save();
    }

    /**
     * Delete a saved ActionBar from savedbars.yml using a name.
     *
     * @param name Name of the ActionBar to delete.
     * @return True if the operation was successful.
     */
    public boolean deleteBar(String name) {
        if (plugin.getConfig().getConfigurationSection("actionBars." + name) == null) return false;
        plugin.getConfig().set("actionBars." + name, null);
        plugin.getConfig().save();
        return true;
    }

    /**
     * Gets an ActionBar from savedbars.yml using a name.
     *
     * @param name Name of the ActionBar to retrieve.
     * @return Retrieved ActionBar or null if none exists.
     */
    public @Nullable ActionBar getBar(String name) {
        if (plugin.getConfig().getConfigurationSection("actionBars." + name) == null) return null;
        return new ActionBar(
                UUID.fromString(plugin.getConfig().getString("actionBars." + name + ".creator")),
                name,
                plugin.getConfig().getString("actionBars." + name + ".content")
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
                plugin.getConfig().getConfigurationSection("actionBars")
        ).getKeys(false);

        for (String name : actionBars) {
            list.add(getBar(name));
        }

        return list;
    }
}
