package gg.tater.auctionhouse.player;

import gg.tater.auctionhouse.event.Events;
import gg.tater.auctionhouse.player.uuid.NameUUIDEntry;
import gg.tater.bedrock.database.BedrockDatabase;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class PlayerListener {

    public PlayerListener(BedrockDatabase database) {
        Events.listen(AsyncPlayerPreLoginEvent.class, event -> {
            UUID uuid = event.getUniqueId();
            String name = event.getName();

            // store name & uuid association storage
            database.getCachedEntity(NameUUIDEntry.class, name).orElseGet(() -> {
                NameUUIDEntry entry = new NameUUIDEntry(name, uuid);
                database.publish(entry);
                return entry;
            });

            AuctionProfile profile = database.getCachedEntity(AuctionProfile.class, uuid.toString())
                    .orElseGet(() -> new AuctionProfile(uuid));

            profile.setName(event.getName());
            database.publish(profile);
        });
    }
}
