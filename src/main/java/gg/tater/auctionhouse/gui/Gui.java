package gg.tater.auctionhouse.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.tater.auctionhouse.util.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class Gui {

    private final Map<Integer, InventoryBase> pageMap = Maps.newHashMap();

    private final Map<InventoryBase, List<AbstractMap.SimpleEntry<Integer, Button>>> buttonEntryMap = Maps.newHashMap();

    private final Map<Player, Integer> profilePageCountMap = Maps.newIdentityHashMap();

    private final String title;
    private final int rows;

    protected abstract List<Button> elements();

    protected abstract Map<Integer, Button> utilityButtons();

    private final Button emptyButton = new Button(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
            .setName(" ")
            .toItemStack())
            .onClick((profile, event) -> event.setCancelled(true));

    private final Button nextButton = new Button(new ItemBuilder(Material.LEVER)
            .setName("&aNext Page &7(Click)")
            .toItemStack())
            .onClick((player, event) -> {
                int currentPageNumber = profilePageCountMap.get(player);
                int nextPageNumber = currentPageNumber + 1;

                InventoryBase next = getPage(nextPageNumber);

                // if there is no next page
                if (next == null) {
                    event.setCancelled(true);
                    return;
                }

                player.openInventory(next.getInventory());
                profilePageCountMap.put(player, nextPageNumber);
            });

    private final Button previousButton = new Button(new ItemBuilder(Material.LEVER)
            .setName("&cPrevious Page &7(Click)")
            .toItemStack())
            .onClick((player, event) -> {
                int currentPageNumber = profilePageCountMap.get(player);
                int previousPageNumber = currentPageNumber - 1;

                InventoryBase previous = getPage(previousPageNumber);

                // if there is no previous page
                if (previous == null) {
                    event.setCancelled(true);
                    return;
                }

                player.openInventory(previous.getInventory());
                profilePageCountMap.put(player, previousPageNumber);
            });

    private void draw() {
        InventoryBase first = new InventoryBase(title + " > Page 1", rows, this);
        fillSideSlots(first);

        int partitionSize = getEmptySlots(first).size();

        if (elements() != null) {
            // if all button elements can fit into one gui
            if (elements().size() <= partitionSize) {
                setButtons(elements(), first, false, false);
            } else {
                // create more pages if elements are larger than the available amount of slots.
                List<List<Button>> partitions = Lists.partition(elements(), partitionSize);

                partitions.forEach(list -> {
                    int pageNumber = pageMap.size() + 1;
                    InventoryBase newPageBase = new InventoryBase(title + " > Page " + pageNumber, rows, this);
                    setButtons(list, newPageBase, true, true);
                });
            }
        }
    }

    private void setButton(int slot, Button button, InventoryBase base) {
        List<AbstractMap.SimpleEntry<Integer, Button>> list = buttonEntryMap.get(base);

        AbstractMap.SimpleEntry<Integer, Button> newEntry = new AbstractMap.SimpleEntry<>(slot, button);

        if (list == null) {
            buttonEntryMap.put(base, Lists.newArrayList(newEntry));
        } else {

            // if there is already an element stored at the slot, remove it
            list.stream().filter(looped -> looped.getKey().equals(newEntry.getKey()))
                    .findFirst()
                    .ifPresent(list::remove);

            list.add(newEntry);
        }

        base.getInventory().setItem(slot, button.getStack());
    }

    private void setDirectiveButtons(InventoryBase base) {
        setButton(0, previousButton, base);
        setButton(8, nextButton, base);
    }

    private void setButtons(List<Button> list, InventoryBase base, boolean directive, boolean fill) {
        if (fill) {
            fillSideSlots(base);
        }

        if (directive) {
            setDirectiveButtons(base);
        }

        Inventory inventory = base.getInventory();

        if (utilityButtons() != null) {
            utilityButtons().forEach((integer, button) -> setButton(integer, button, base));
        }

        for (Button button : list) {
            int firstEmpty = inventory.firstEmpty();
            boolean hasRoom = firstEmpty != -1;

            if (!hasRoom) {
                break;
            }

            setButton(firstEmpty, button, base);
        }

        int pageNumber = pageMap.size() + 1;
        pageMap.put(pageNumber, base);
    }

    public Optional<Button> getButton(int slot, InventoryBase base) {
        List<AbstractMap.SimpleEntry<Integer, Button>> entries = buttonEntryMap.get(base);

        return entries
                .stream()
                .filter(entry -> entry.getKey() == slot)
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public final void open(Player player) {
        profilePageCountMap.putIfAbsent(player, 1);
        draw();
        // open initial inventory
        player.openInventory(getPage(1).getInventory());
    }

    private InventoryBase getPage(int number) {
        return pageMap.get(number);
    }

    private void fillSideSlots(InventoryBase base) {
        Inventory inventory = base.getInventory();

        if (rows >= 3) {
            for (int i = 0; i <= 8; i++) {
                setButton(i, emptyButton, base);
            }

            for (int s = 8; s < (inventory.getSize() - 9); s += 9) {
                int lastSlot = s + 1;
                setButton(s, emptyButton, base);
                setButton(lastSlot, emptyButton, base);
            }

            for (int lr = (inventory.getSize() - 9); lr < inventory.getSize(); lr++) {
                setButton(lr, emptyButton, base);
            }
        }
    }

    private List<Integer> getEmptySlots(InventoryBase base) {
        List<Integer> list = Lists.newArrayList();
        Inventory inventory = base.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                list.add(i);
            }
        }

        return list;
    }
}
