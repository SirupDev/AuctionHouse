package gg.tater.auctionhouse.gui;

import gg.tater.auctionhouse.event.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiHandler {

    public GuiHandler() {
        Events.listen(InventoryClickEvent.class, event -> {
            Inventory inventory = event.getInventory();
            InventoryHolder holder = inventory.getHolder();

            if (holder instanceof InventoryBase) {
                InventoryBase base = (InventoryBase) holder;

                Player who = (Player) event.getWhoClicked();

                int slot = event.getSlot();

                base.getGui().getButton(slot, base)
                        .ifPresent(button -> {
                            event.setCancelled(true);
                            button.getClickConsumer().accept(who, event);
                        });
            }
        });
    }

}
