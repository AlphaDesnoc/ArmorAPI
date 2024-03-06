package fr.alphadesnoc.listener;

import fr.alphadesnoc.event.enums.ArmorType;
import fr.alphadesnoc.event.enums.EquipMethod;
import fr.alphadesnoc.event.events.PlayerChangeArmorEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;

/**
 * Listener to handle armor dispensing events.
 * When an armor piece is dispensed and targets a player, this triggers a custom PlayerChangeArmorEvent.
 * @author AlphaDesnoc
 * @since Mar 1, 2024
 */
public class DispenseArmorListener implements Listener {

    /**
     * Handles the event where armor is dispensed from a dispenser towards a player.
     * This method checks if the dispensed item is armor, and if so, triggers a PlayerChangeArmorEvent.
     *
     * @param event The BlockDispenseArmorEvent triggered by the dispenser.
     */
    @EventHandler
    public void onDispenseArmor(BlockDispenseArmorEvent event) {
        ArmorType type = ArmorType.match(event.getItem());
        if (type != null && event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            PlayerChangeArmorEvent playerChangeArmorEvent = new PlayerChangeArmorEvent(player, EquipMethod.DISPENSER, type, null, event.getItem());
            Bukkit.getServer().getPluginManager().callEvent(playerChangeArmorEvent);
            if (playerChangeArmorEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }
}
