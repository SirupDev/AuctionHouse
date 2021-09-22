package gg.tater.auctionhouse.player;

import gg.tater.bedrock.database.pubsub.entity.RedisEntity;

public class AuctionProfileEntity implements RedisEntity<AuctionProfile> {

    @Override
    public void handle(AuctionProfile profile) {

    }

    @Override
    public boolean cache() {
        return true;
    }

    @Override
    public String hashName() {
        return "auction_profiles";
    }

    @Override
    public String key(AuctionProfile profile) {
        return profile.getUuid().toString();
    }
}
