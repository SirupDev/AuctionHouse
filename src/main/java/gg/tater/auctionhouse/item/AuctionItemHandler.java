package gg.tater.auctionhouse.item;

import gg.tater.auctionhouse.AuctionHousePlugin;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.auctionhouse.util.ChatUtil;
import gg.tater.bedrock.database.BedrockDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class AuctionItemHandler {

    public AuctionItemHandler(AuctionServer server, BedrockDatabase database) {

        // listen for item expiration so we can keep the redis hash minimal in size.
        AuctionHousePlugin.SERVICE.scheduleAtFixedRate(() -> {

            Iterator<AuctionItem> iterator = server.getListings().iterator();

            while (iterator.hasNext()) {
                AuctionItem item = iterator.next();

                if (item.hasExpired()) {
                    Player target = Bukkit.getPlayer(item.getSellerUUID());
                    if (target != null) {
                        target.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.GREEN + "One of your auction items has expired. You may claim it in the auction menu.");
                    }

                    iterator.remove();
                    database.publish(server);
                    Bukkit.getLogger().info("Auction item expired. Removed from server auction cache with ID: " + item.getUuid().toString());
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }
}
