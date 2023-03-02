package fr.alphadesnoc.core;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import fr.alphadesnoc.event.listeners.ArmorListeners;
import fr.alphadesnoc.listener.DispenceArmorListener;

/**
 * @author AlphaDesnoc
 * @since Feb 28, 2023
 */
public final class ArmorAPI extends JavaPlugin {

    private PluginManager pm;

    @Override
    public void onEnable() {
        pm = getServer().getPluginManager();
        saveDefaultConfig();

        pm.registerEvents(new ArmorListeners(getConfig().getStringList("blockedMats")), this);
        try {
            //For 1.13+
            Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
            pm.registerEvents(new DispenceArmorListener(), this);
        }
        catch (Exception ignored) {}
    }

}
