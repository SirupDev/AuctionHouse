package gg.tater.auctionhouse.server;

import com.google.common.collect.Lists;
import gg.tater.addons.server.ServerEntry;
import gg.tater.auctionhouse.item.AuctionItem;
import lombok.Getter;

import java.util.List;

@Getter
public class AuctionServer {

    private final String name;
    private final List<AuctionItem> items = Lists.newArrayList();

    // Use OB-Addons ServerEntry server name for AuctionServer name.
    public AuctionServer(ServerEntry entry) {
        this.name = entry.getName();
    }

    public void addAuctionItem(AuctionItem item) {
        items.add(item);
    }

    public void removeAuctionItem(AuctionItem item) {
        items.remove(item);
    }
}
