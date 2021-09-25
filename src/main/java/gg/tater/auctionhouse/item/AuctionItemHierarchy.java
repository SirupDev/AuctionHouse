package gg.tater.auctionhouse.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public enum AuctionItemHierarchy {

    ADMIN(1),
    DONATOR(2),
    REGULAR(3);

    private final int integerLevel;

    public static AuctionItemHierarchy getHierarchy(Player player) {
        if (player.hasPermission("auctionhouse.hierarchy.admin")) {
            return ADMIN;
        }

        if (player.hasPermission("auctionhouse.hierarchy.donator")) {
            return DONATOR;
        }

        return REGULAR;
    }
}
