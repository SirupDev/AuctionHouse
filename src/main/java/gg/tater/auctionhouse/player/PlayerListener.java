package gg.tater.auctionhouse.player;

import gg.tater.addons.event.Events;
import gg.tater.bedrock.database.BedrockDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerListener {

    public PlayerListener(BedrockDatabase database) {
        Events.listen(PlayerJoinEvent.class, event -> {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            AuctionProfile profile = database.getCachedEntity(AuctionProfile.class, uuid.toString())
                    .orElseGet(() -> new AuctionProfile(uuid));

            profile.setName(player.getName());
            profile.returnItems();
            database.publish(profile);
        });
    }
}
