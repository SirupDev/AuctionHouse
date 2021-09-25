package gg.tater.auctionhouse.player.uuid;

import gg.tater.bedrock.database.pubsub.entity.RedisEntity;

public class NameUUIDEntity implements RedisEntity<NameUUIDEntry> {

    @Override
    public void handle(NameUUIDEntry entry) {

    }

    @Override
    public boolean cache() {
        return true;
    }

    @Override
    public String hashName() {
        return "name_uuids";
    }

    @Override
    public String key(NameUUIDEntry entry) {
        return entry.getName();
    }
}
