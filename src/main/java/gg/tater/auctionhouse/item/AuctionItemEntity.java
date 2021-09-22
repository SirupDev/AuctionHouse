package gg.tater.auctionhouse.item;

import gg.tater.bedrock.database.pubsub.entity.RedisEntity;

public class AuctionItemEntity implements RedisEntity<AuctionItem> {

    @Override
    public void handle(AuctionItem item) {
        
    }

    @Override
    public boolean cache() {
        return true;
    }

    @Override
    public String hashName() {
        return "auction_items";
    }

    @Override
    public String key(AuctionItem item) {
        return item.getUuid().toString();
    }
}
