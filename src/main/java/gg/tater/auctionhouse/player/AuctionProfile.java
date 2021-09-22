package gg.tater.auctionhouse.player;

import com.google.common.collect.Lists;
import gg.tater.addons.player.profile.Profile;
import gg.tater.auctionhouse.item.AuctionItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AuctionProfile extends Profile {

    private final List<AuctionItem> listings = Lists.newArrayList();

    public AuctionProfile(UUID uuid) {
        super(uuid);
    }

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
}
