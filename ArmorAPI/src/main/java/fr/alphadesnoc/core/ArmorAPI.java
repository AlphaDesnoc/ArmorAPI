package fr.alphadesnoc.core;

import fr.alphadesnoc.event.listeners.ArmorListeners;
import fr.alphadesnoc.listener.DispenseArmorListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author AlphaDesnoc
 * @since Feb 28, 2023
 */
public final class ArmorAPI extends JavaPlugin {


    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        saveDefaultConfig();

        pm.registerEvents(new ArmorListeners(getConfig().getStringList("blockedMats")), this);
        try {
            Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
            pm.registerEvents(new DispenseArmorListener(), this);
        } catch (ClassNotFoundException ignored) {
        }
    }

}
