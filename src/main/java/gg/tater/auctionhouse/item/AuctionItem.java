package gg.tater.auctionhouse.item;

import com.google.gson.annotations.SerializedName;
import gg.tater.auctionhouse.player.AuctionProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(force = true)
public class AuctionItem {

    private final UUID uuid = UUID.randomUUID();

    private final ItemStack stack;

    @SerializedName("seller_name")
    private final String sellerName;

    @SerializedName("seller_uuid")
    private final UUID sellerUUID;

    private final int price;

    private final AuctionItemHierarchy hierarchy;

    private long end = Instant.now().plus(8L, ChronoUnit.HOURS).toEpochMilli();

    public AuctionItem(AuctionProfile profile, ItemStack stack, int price, AuctionItemHierarchy hierarchy) {
        this.stack = stack;
        this.sellerName = profile.getName();
        this.sellerUUID = profile.getUuid();
        this.price = price;
        this.hierarchy = hierarchy;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() >= end;
    }
}
