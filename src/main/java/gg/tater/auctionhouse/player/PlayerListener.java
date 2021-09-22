package gg.tater.auctionhouse.player;

import gg.tater.addons.event.Events;
import gg.tater.bedrock.database.BedrockDatabase;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class PlayerListener {

    public PlayerListener(BedrockDatabase database) {
        Events.listen(AsyncPlayerPreLoginEvent.class, event -> {
            UUID uuid = event.getUniqueId();

            AuctionProfile profile = database.getCachedEntity(AuctionProfile.class, uuid.toString())
                    .orElseGet(() -> new AuctionProfile(uuid));

            profile.setName(event.getName());
            database.publish(profile);
        });
    }
}
