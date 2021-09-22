package gg.tater.auctionhouse.player;

import com.google.common.collect.Lists;
import gg.tater.addons.player.profile.Profile;
import gg.tater.auctionhouse.item.AuctionItem;
import gg.tater.auctionhouse.util.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AuctionProfile extends Profile {

    private final List<AuctionItem> listings = Lists.newArrayList();

    private final List<AuctionItem> returns = Lists.newArrayList();

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

    public void addReturnItem(AuctionItem item) {
        returns.add(item);
    }

    public void returnItems() {
        if (returns.isEmpty()) {
            return;
        }

        if (toPlayer() != null) {
            Iterator<AuctionItem> iterator = returns.iterator();

            while (iterator.hasNext()) {
                AuctionItem item = iterator.next();

                if (!hasEmptyInventory()) {
                    toPlayer().sendMessage(ChatUtil.AUCTION_PREFIX
                            + ChatColor.RED + "Auction item "
                            + item.getStack().getType().name()
                            + " must be returned, but you have no inventory space!");
                    return;
                }

                toPlayer().getInventory().addItem(item.getStack());
                iterator.remove();
                toPlayer().sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.GREEN + "You have received an auction listing back because the expire time has concluded.");
            }
        }
    }

    public boolean hasEmptyInventory() {
        return toPlayer().getInventory().firstEmpty() != -1;
    }
}
