package fr.alphadesnoc.event.listeners;

import fr.alphadesnoc.event.enums.ArmorType;
import fr.alphadesnoc.event.enums.EquipMethod;
import fr.alphadesnoc.event.events.PlayerChangeArmorEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Listener for armor-related events, including equipping and unequipping armor pieces.
 * @author AlphaDesnoc
 * @since Mar 1, 2024
 */
public class ArmorListeners implements Listener {

    private final List<String> blockedMats;

    /**
     * Constructs an ArmorListeners instance.
     *
     * @param blockedMats A list of materials (as Strings) that are blocked for armor interactions.
     */
    public ArmorListeners(List<String> blockedMats) {
        this.blockedMats = blockedMats;
    }

    /**
     * Handles inventory click events to detect and process armor changes.
     *
     * @param e The inventory click event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        ArmorType newArmorType = null;
        ItemStack newArmorPiece = null;
        EquipMethod method = EquipMethod.PICK_DROP;

        if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
            method = EquipMethod.SHIFT_CLICK;
            newArmorType = ArmorType.match(e.getCurrentItem());
        } else if (e.getClick().equals(ClickType.NUMBER_KEY)) {
            method = EquipMethod.HOTBAR_SWAP;
            ItemStack hotbarItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
            newArmorType = ArmorType.match(hotbarItem);
            newArmorPiece = hotbarItem;
        } else if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.LEFT) {
            newArmorType = ArmorType.match(e.getCursor());
            newArmorPiece = e.getCursor();
        }

        if (newArmorType != null) {
            ItemStack oldArmorPiece = method == EquipMethod.SHIFT_CLICK ? null : e.getCurrentItem();
            triggerArmorChangeEvent(player, newArmorType, method, oldArmorPiece, newArmorPiece, e);
        }
    }

    /**
     * Handles player interaction events to detect and process armor equipping through right-clicking.
     *
     * @param e The player interaction event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        Player player = e.getPlayer();
        ArmorType newArmorType = ArmorType.match(e.getItem());
        if (newArmorType != null && isArmorSlotEmpty(player, newArmorType)) {
            triggerArmorChangeEvent(player, newArmorType, EquipMethod.HOTBAR, null, e.getItem(), e);
        }
    }

    /**
     * Handles inventory drag events to detect and process armor changes.
     *
     * @param e The inventory drag event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent e) {
        ArmorType type = ArmorType.match(e.getOldCursor());
        if (type != null && e.getInventorySlots().contains(type.getSlotNumber())) {
            triggerArmorChangeEvent((Player) e.getWhoClicked(), type, EquipMethod.DRAG, null, e.getOldCursor(), e);
        }
    }

    /**
     * Handles item break events to detect and process armor removal due to breakage.
     *
     * @param e The item break event.
     */
    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent e) {
        ArmorType type = ArmorType.match(e.getBrokenItem());
        if (type != null) {
            triggerArmorChangeEvent(e.getPlayer(), type, EquipMethod.BROKE, e.getBrokenItem(), null, e);
        }
    }

    /**
     * Handles player death events to process armor removal upon death.
     *
     * @param e The player death event.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getKeepInventory()) return;

        Player player = e.getEntity();
        for (ItemStack item : player.getInventory().getArmorContents()) {
            ArmorType type = ArmorType.match(item);
            if (type != null) {
                triggerArmorChangeEvent(player, type, EquipMethod.DEATH, item, null, e);
            }
        }
    }

    /**
     * Triggers a custom armor change event.
     *
     * @param player The player involved in the armor change.
     * @param type The type of armor being changed.
     * @param method The method of the armor change.
     * @param oldItem The old armor item, null if equipping.
     * @param newItem The new armor item, null if unequipping.
     * @param originalEvent The original Bukkit event triggering this change.
     */
    private void triggerArmorChangeEvent(Player player, ArmorType type, EquipMethod method, ItemStack oldItem, ItemStack newItem, Event originalEvent) {
        PlayerChangeArmorEvent armorEvent = new PlayerChangeArmorEvent(player, method, type, oldItem, newItem);
        Bukkit.getServer().getPluginManager().callEvent(armorEvent);
        if (armorEvent.isCancelled() && originalEvent instanceof Cancellable) {
            ((Cancellable) originalEvent).setCancelled(true);
        }
    }

    /**
     * Checks if a specific armor slot of a player is empty.
     *
     * @param player The player to check.
     * @param type The type of armor slot to check.
     * @return True if the slot is empty, false otherwise.
     */
    private boolean isArmorSlotEmpty(Player player, ArmorType type) {
        ItemStack armorPiece = player.getInventory().getItem(type.getSlotNumber());
        return isAirOrNull(armorPiece);
    }

    /**
     * Checks if an ItemStack is null or represents air, indicating no item.
     *
     * @param item The ItemStack to check.
     * @return True if the ItemStack is null or air, false otherwise.
     */
    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
