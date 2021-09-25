package gg.tater.auctionhouse.player;

import com.google.common.collect.Lists;
import gg.tater.auctionhouse.item.AuctionItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class AuctionProfile {

    private final UUID uuid;
    private String name;

    private final List<AuctionItem> listings = Lists.newArrayList();

    public boolean hasListing(AuctionItem item) {
        return listings.stream().anyMatch(filtered -> filtered.getUuid().equals(item.getUuid()));
    }

    public void removeListing(AuctionItem item) {
        listings.remove(listings.stream()
                .filter(filtered -> filtered.getUuid().equals(item.getUuid()))
                .findFirst()
                .orElse(null));
    }

    public void addListing(AuctionItem item) {
        listings.add(item);
    }

    public boolean hasEmptyInventory() {
        return toPlayer().getInventory().firstEmpty() != -1;
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
