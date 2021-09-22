package gg.tater.auctionhouse;

import co.aikar.commands.PaperCommandManager;
import gg.tater.addons.AddonsPlugin;
import gg.tater.addons.server.ServerEntry;
import gg.tater.addons.util.DataUtil;
import gg.tater.auctionhouse.command.AuctionCommand;
import gg.tater.auctionhouse.item.AuctionItem;
import gg.tater.auctionhouse.item.AuctionItemEntity;
import gg.tater.auctionhouse.item.AuctionItemHandler;
import gg.tater.auctionhouse.player.AuctionProfile;
import gg.tater.auctionhouse.player.AuctionProfileEntity;
import gg.tater.auctionhouse.player.PlayerListener;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.auctionhouse.server.AuctionServerEntity;
import gg.tater.bedrock.BedrockPlugin;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class AuctionHousePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        PaperCommandManager manager = new PaperCommandManager(this);
        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);

        if (provider == null) {
            Bukkit.getLogger().severe("Could not boot OB-AuctionHouse due to vault not being properly hooked.");
            return;
        }

        AddonsPlugin.get().ifPresent(plugin -> {
            ServerEntry localEntry = plugin.getEntry();

            BedrockPlugin.getDatabase().ifPresent(database -> {

                database.registerEntity(AuctionItem.class, new AuctionItemEntity());
                database.registerEntity(AuctionProfile.class, new AuctionProfileEntity());
                database.registerEntity(AuctionServer.class, new AuctionServerEntity());

                new PlayerListener(database);

                AuctionServer server = database.getCachedEntity(AuctionServer.class, localEntry.getName())
                        .orElseGet(() -> {
                            AuctionServer newServer = new AuctionServer(localEntry);
                            database.publish(newServer);
                            return newServer;
                        });

                new AuctionItemHandler(server, database);

                manager.getCommandContexts().registerIssuerAwareContext(AuctionProfile.class, context -> {
                    if (!context.hasFlag("other")) {
                        UUID uuid = context.getPlayer().getUniqueId();
                        return DataUtil.getProfile(database, AuctionProfile.class, false, uuid.toString());
                    }

                    String arg = context.popFirstArg();
                    return DataUtil.getProfile(database, AuctionProfile.class, true, arg);
                });

                manager.registerCommand(new AuctionCommand(provider.getProvider(), server, database, api));
            });
        });
    }

    @Override
    public void onDisable() {

    }
}
