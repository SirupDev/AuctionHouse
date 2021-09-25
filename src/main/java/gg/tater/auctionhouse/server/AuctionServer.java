package gg.tater.auctionhouse.server;

import com.google.common.collect.Lists;
import gg.tater.auctionhouse.item.AuctionItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AuctionServer {

    private final String name = Bukkit.getServer().getName();
    private final List<AuctionItem> listings = Lists.newArrayList();

    public void addServerListing(AuctionItem item) {
        listings.add(item);
    }

    public void removeServerListing(AuctionItem item) {
        listings.remove(listings.stream()
                .filter(filtered -> filtered.getUuid().equals(item.getUuid()))
                .findFirst()
                .orElse(null));
    }
}
