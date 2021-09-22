package gg.tater.auctionhouse.server;

import com.google.common.collect.Lists;
import gg.tater.addons.server.ServerEntry;
import gg.tater.auctionhouse.item.AuctionItem;
import lombok.Getter;

import java.util.List;

@Getter
public class AuctionServer {

    private final String name;
    private final List<AuctionItem> listings = Lists.newArrayList();

    // Use OB-Addons ServerEntry server name for AuctionServer name.
    public AuctionServer(ServerEntry entry) {
        this.name = entry.getName();
    }

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
