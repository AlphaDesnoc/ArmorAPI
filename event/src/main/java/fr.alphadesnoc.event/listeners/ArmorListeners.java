package fr.alphadesnoc.event.listeners;


import fr.alphadesnoc.event.enums.ArmorType;
import fr.alphadesnoc.event.events.ArmorEvent;
import fr.alphadesnoc.event.enums.EquipMethod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author AlphaDesnoc
 * @since Feb 28, 2023
 */
public class ArmorListeners implements Listener {

    private final List<String> blockedMats;

    public ArmorListeners(List<String> blockedMats) {
        this.blockedMats = blockedMats;
    }

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public final void inventoryClick(final InventoryClickEvent e) {
        boolean shift = false;
        boolean numberkey = false;
        if (e.isCancelled()) return;
        if (e.getAction() == InventoryAction.NOTHING) return;
        if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
            shift = true;
        }
        if (e.getClick().equals(ClickType.NUMBER_KEY)) {
            numberkey = true;
        }
        if (e.getSlotType() != InventoryType.SlotType.ARMOR && e.getSlotType() != InventoryType.SlotType.QUICKBAR && e.getSlotType() != InventoryType.SlotType.CONTAINER) return;
        if (e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        ArmorType newArmorType = ArmorType.match(shift ? e.getCurrentItem() : e.getCursor());
        if (!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlotNumber()) return;

        if (shift) {
            newArmorType = ArmorType.match(e.getCurrentItem());
            if (newArmorType != null){
                boolean equipping = true;
                if (e.getRawSlot() == newArmorType.getSlotNumber()) equipping = false;
                if (newArmorType.equals(ArmorType.HELMET)
                        && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getHelmet()) : !isAirOrNull(e.getWhoClicked().getInventory().getHelmet())) || newArmorType.equals(ArmorType.CHEST)
                        && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getChestplate()) : !isAirOrNull(e.getWhoClicked().getInventory().getChestplate())) || newArmorType.equals(ArmorType.LEGS)
                        && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getLeggings()) : !isAirOrNull(e.getWhoClicked().getInventory().getLeggings())) || newArmorType.equals(ArmorType.BOOTS)
                        && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getBoots()) : !isAirOrNull(e.getWhoClicked().getInventory().getBoots()))){
                    ArmorEvent armorEvent = new ArmorEvent((Player) e.getWhoClicked(), EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
                    Bukkit.getServer().getPluginManager().callEvent(armorEvent);
                    if(armorEvent.isCancelled()) e.setCancelled(true);
                }
            }
        }
        else {
            ItemStack newArmorPiece = e.getCursor();
            ItemStack oldArmorPiece = e.getCurrentItem();
            if (numberkey) {
                if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                    ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
                    if (!isAirOrNull(hotbarItem)) {
                        newArmorType = ArmorType.match(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
                    }
                    else {
                        newArmorType = ArmorType.match(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
                    }
                }
            }
            else {
                if (isAirOrNull(e.getCursor())
                        && !isAirOrNull(e.getCurrentItem()))
                    newArmorType = ArmorType.match(e.getCurrentItem());
            }
            if (newArmorType != null
                    && e.getRawSlot() == newArmorType.getSlotNumber()) {
                EquipMethod method = EquipMethod.PICK_DROP;
                if (e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = EquipMethod.HOTBAR_SWAP;
                ArmorEvent armorEvent = new ArmorEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
                Bukkit.getServer().getPluginManager().callEvent(armorEvent);
                if (armorEvent.isCancelled()) e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent e){
        if (e.useItemInHand().equals(Event.Result.DENY))return;
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = e.getPlayer();
            if (!e.useInteractedBlock().equals(Event.Result.DENY)){
                if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()){
                    Material mat = e.getClickedBlock().getType();
                    for(String s : blockedMats){
                        if(mat.name().equalsIgnoreCase(s)) return;
                    }
                }
            }
            ArmorType newArmorType = ArmorType.match(e.getItem());
            if (newArmorType != null){
                if (newArmorType.equals(ArmorType.HELMET)
                        && isAirOrNull(e.getPlayer().getInventory().getHelmet()) || newArmorType.equals(ArmorType.CHEST)
                        && isAirOrNull(e.getPlayer().getInventory().getChestplate()) || newArmorType.equals(ArmorType.LEGS)
                        && isAirOrNull(e.getPlayer().getInventory().getLeggings()) || newArmorType.equals(ArmorType.BOOTS)
                        && isAirOrNull(e.getPlayer().getInventory().getBoots())){
                    ArmorEvent armorEvent = new ArmorEvent(e.getPlayer(), EquipMethod.HOTBAR, ArmorType.match(e.getItem()), null, e.getItem());
                    Bukkit.getServer().getPluginManager().callEvent(armorEvent);
                    if (armorEvent.isCancelled()){
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event){
        ArmorType type = ArmorType.match(event.getOldCursor());
        if (event.getRawSlots().isEmpty()) return;
        if (type != null && type.getSlotNumber() == event.getRawSlots().stream().findFirst().orElse(0)){
            ArmorEvent armorEvent = new ArmorEvent((Player) event.getWhoClicked(), EquipMethod.DRAG, type, null, event.getOldCursor());
            Bukkit.getServer().getPluginManager().callEvent(armorEvent);
            if (armorEvent.isCancelled()){
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e){
        ArmorType type = ArmorType.match(e.getBrokenItem());
        if (type != null){
            Player p = e.getPlayer();
            ArmorEvent armorEvent = new ArmorEvent(p, EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(armorEvent);
            if (armorEvent.isCancelled()){
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));
                if (type.equals(ArmorType.HELMET)){
                    p.getInventory().setHelmet(i);
                }else if (type.equals(ArmorType.CHEST)){
                    p.getInventory().setChestplate(i);
                }else if (type.equals(ArmorType.LEGS)){
                    p.getInventory().setLeggings(i);
                }else if (type.equals(ArmorType.BOOTS)){
                    p.getInventory().setBoots(i);
                }
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e){
        Player p = e.getEntity();
        if (e.getKeepInventory()) return;
        for (ItemStack i : p.getInventory().getArmorContents()){
            if (!isAirOrNull(i)){
                Bukkit.getServer().getPluginManager().callEvent(new ArmorEvent(p, EquipMethod.DEATH, ArmorType.match(i), i, null));
            }
        }
    }

    /**
     * A method to support versions that use null or material air for the ItemStacks
     *
     * @param item
     * @return null if itemstack doesn't exist or equal AIR
     */
    public static boolean isAirOrNull(ItemStack item){
        return item == null || item.getType().equals(Material.AIR);
    }
}
