package gg.tater.auctionhouse.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class InventoryBase implements InventoryHolder {

    private final String title;
    private final int rows;
    private final Gui gui;
    private final Inventory inventory;

    public InventoryBase(String title, int rows, Gui gui) {
        this.title = title;
        this.rows = rows;
        this.gui = gui;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
    }
}
