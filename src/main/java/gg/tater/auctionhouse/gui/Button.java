package gg.tater.auctionhouse.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Getter
public class Button {

    private final ItemStack stack;

    private BiConsumer<Player, InventoryClickEvent> clickConsumer = ($, event) -> event.setCancelled(true);

    public Button onClick(BiConsumer<Player, InventoryClickEvent> clickConsumer) {
        this.clickConsumer = clickConsumer;
        return this;
    }
}
