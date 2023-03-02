package fr.alphadesnoc.listener;

import fr.alphadesnoc.event.enums.ArmorType;
import fr.alphadesnoc.event.enums.EquipMethod;
import fr.alphadesnoc.event.events.ArmorEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;

public class DispenceArmorListener implements Listener {

    /**
     * @author AlphaDesnoc
     * @since Feb 28, 2023
     */
    @EventHandler
    public void dispenseArmorEvent(BlockDispenseArmorEvent event){
        ArmorType type = ArmorType.match(event.getItem());
        if (type != null) {
            if (event.getTargetEntity() instanceof Player) {
                Player p = (Player) event.getTargetEntity();
                ArmorEvent armorEvent = new ArmorEvent(p, EquipMethod.DISPENSER, type, null, event.getItem());
                Bukkit.getServer().getPluginManager().callEvent(armorEvent);
                if (armorEvent.isCancelled()) event.setCancelled(true);
            }
        }
    }

}
