package gg.tater.auctionhouse.server;

import gg.tater.bedrock.database.pubsub.entity.RedisEntity;

public class AuctionServerEntity implements RedisEntity<AuctionServer> {

    @Override
    public void handle(AuctionServer server) {

    }

    @Override
    public boolean cache() {
        return true;
    }

    @Override
    public String hashName() {
        return "auction_servers";
    }

    @Override
    public String key(AuctionServer server) {
        return server.getName();
    }
}
