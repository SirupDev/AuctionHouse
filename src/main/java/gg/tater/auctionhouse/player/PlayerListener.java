package gg.tater.auctionhouse.player;

import gg.tater.addons.event.Events;
import gg.tater.bedrock.database.BedrockDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

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

        Events.listen(PlayerLoginEvent.class, event -> {
            Player player = event.getPlayer();

            database.getCachedEntity(AuctionProfile.class, player.getUniqueId().toString()).ifPresent(profile -> {

            });
        });
    }
}
