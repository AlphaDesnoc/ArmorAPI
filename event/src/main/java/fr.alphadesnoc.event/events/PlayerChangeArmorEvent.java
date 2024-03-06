package fr.alphadesnoc.event.events;

import fr.alphadesnoc.event.enums.ArmorType;
import fr.alphadesnoc.event.enums.EquipMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event called when a player changes their armor.
 * This can be triggered by equipping, unequipping, or altering armor through various means.
 *
 * @author AlphaDesnoc
 * @since Mar 1, 2024
 */
public final class PlayerChangeArmorEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final EquipMethod equipType;
    private final ArmorType type;
    private ItemStack oldArmorPiece;
    private ItemStack newArmorPiece;

    /**
     * Constructs a new PlayerChangeArmorEvent.
     *
     * @param player        The player involved in this event.
     * @param equipType     The method used to change the armor.
     * @param type          The type of armor that was changed.
     * @param oldArmorPiece The armor piece removed, or null if none was removed.
     * @param newArmorPiece The armor piece added, or null if none was added.
     */
    public PlayerChangeArmorEvent(final Player player, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece) {
        super(player);
        this.equipType = equipType;
        this.type = type;
        this.oldArmorPiece = oldArmorPiece;
        this.newArmorPiece = newArmorPiece;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return The list of handlers for this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return The list of handlers for this event.
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Determines if this event is cancelled.
     *
     * @return true if this event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not be executed in the server,
     * but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event.
     */
    @Override
    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets the type of armor that was changed.
     *
     * @return The ArmorType of the changed armor.
     */
    public ArmorType getType() {
        return type;
    }

    /**
     * Gets the old armor piece, if any, that was removed during the armor change.
     *
     * @return The ItemStack of the removed armor piece, or null if none was removed.
     */
    public ItemStack getOldArmorPiece() {
        return oldArmorPiece;
    }

    /**
     * Sets the old armor piece that was removed.
     *
     * @param oldArmorPiece The ItemStack of the armor removed.
     */
    public void setOldArmorPiece(final ItemStack oldArmorPiece) {
        this.oldArmorPiece = oldArmorPiece;
    }

    /**
     * Gets the new armor piece, if any, that was added during the armor change.
     *
     * @return The ItemStack of the added armor piece, or null if none was added.
     */
    public ItemStack getNewArmorPiece() {
        return newArmorPiece;
    }

    /**
     * Sets the new armor piece that was added.
     *
     * @param newArmorPiece The ItemStack of the armor added.
     */
    public void setNewArmorPiece(final ItemStack newArmorPiece) {
        this.newArmorPiece = newArmorPiece;
    }

    /**
     * Gets the method used to change the armor.
     *
     * @return The EquipMethod representing how the armor was changed.
     */
    public EquipMethod getMethod() {
        return equipType;
    }
}
