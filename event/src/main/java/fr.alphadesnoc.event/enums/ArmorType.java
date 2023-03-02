package fr.alphadesnoc.event.enums;

import fr.alphadesnoc.event.listeners.ArmorListeners;
import org.bukkit.inventory.ItemStack;

/**
 * @author AlphaDesnoc
 * @since Feb 28, 2023
 */
public enum ArmorType {
    HELMET(5),
    CHEST(6),
    LEGS(7),
    BOOTS(8);

    private final int slotNumber;

    ArmorType(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    /**
     * Try to match the itemstack in parameter with one of the pieces of armor
     *
     * @param item The itemstack to match
     * @return The parsed ArmorType, or null if not found
     */

    public static ArmorType match(final ItemStack item){
        if (ArmorListeners.isAirOrNull(item)) return null;
        String itemType = item.getType().name().toLowerCase();
        if (itemType.endsWith("_helmet") || itemType.endsWith("_skull") || itemType.endsWith("_head")) return HELMET;
        else if (itemType.endsWith("_chestplate") || itemType.equals("elytra")) return CHEST;
        else if (itemType.endsWith("_leggings")) return LEGS;
        else if (itemType.endsWith("_boots")) return BOOTS;
        else return null;
    }

    public int getSlotNumber() {
        return slotNumber;
    }
}
