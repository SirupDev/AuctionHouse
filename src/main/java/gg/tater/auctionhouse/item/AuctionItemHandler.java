package gg.tater.auctionhouse.item;

import gg.tater.addons.AddonsPlugin;
import gg.tater.auctionhouse.player.AuctionProfile;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.bedrock.database.BedrockDatabase;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class AuctionItemHandler {

    public AuctionItemHandler(AuctionServer server, BedrockDatabase database) {

        // listen for item expiration so we can keep the redis hash minimal in size.
        AddonsPlugin.SERVICE.scheduleAtFixedRate(() -> {

            Iterator<AuctionItem> iterator = server.getItems().iterator();

            while (iterator.hasNext()) {
                AuctionItem item = iterator.next();

                if (item.hasExpired()) {
                    iterator.remove();
                    database.publish(server);

                    database.getCachedEntity(AuctionProfile.class, item.getSellerUUID().toString())
                            .ifPresent(profile -> {
                                profile.removeListing(item);
                                profile.addReturnItem(item);
                                profile.returnItems();
                                database.publish(profile);
                            });

                    Bukkit.getLogger().info("Auction item expired. Removed from cache with ID: " + item.getUuid().toString());
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }
}
