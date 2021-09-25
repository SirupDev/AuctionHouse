package gg.tater.auctionhouse;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import gg.tater.auctionhouse.command.AuctionCommand;
import gg.tater.auctionhouse.gui.GuiHandler;
import gg.tater.auctionhouse.item.AuctionItem;
import gg.tater.auctionhouse.item.AuctionItemEntity;
import gg.tater.auctionhouse.item.AuctionItemHandler;
import gg.tater.auctionhouse.player.AuctionProfile;
import gg.tater.auctionhouse.player.AuctionProfileEntity;
import gg.tater.auctionhouse.player.PlayerListener;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.auctionhouse.server.AuctionServerEntity;
import gg.tater.auctionhouse.util.DataUtil;
import gg.tater.bedrock.database.BedrockDatabase;
import gg.tater.bedrock.database.Credentials;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public final class AuctionHousePlugin extends JavaPlugin {

    private static final AtomicReference<AuctionHousePlugin> REFERENCE = new AtomicReference<>();

    public static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

    private BedrockDatabase database;

    @Override
    public void onEnable() {
        REFERENCE.set(this);
        getConfig().options().copyDefaults(true);
        saveConfig();

        HeadDatabaseAPI api = new HeadDatabaseAPI();
        PaperCommandManager manager = new PaperCommandManager(this);
        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);

        if (provider == null) {
            Bukkit.getLogger().severe("Could not boot OB-AuctionHouse due to vault not being properly hooked.");
            return;
        }

        this.database = new BedrockDatabase((new Credentials(
                getConfig().getString("DATABASE.host"),
                getConfig().getString("DATABASE.password"),
                getConfig().getInt("DATABASE.port"),
                getConfig().getInt("DATABASE.database"))));

        database.registerEntity(AuctionItem.class, new AuctionItemEntity());
        database.registerEntity(AuctionProfile.class, new AuctionProfileEntity());
        database.registerEntity(AuctionServer.class, new AuctionServerEntity());

        new PlayerListener(database);
        new GuiHandler();

        AuctionServer server = database.getCachedEntity(AuctionServer.class, Bukkit.getServer().getName())
                .orElseGet(() -> {
                    AuctionServer newServer = new AuctionServer();
                    database.publish(newServer);
                    return newServer;
                });

        new AuctionItemHandler(server, database);

        manager.getCommandContexts().registerIssuerAwareContext(AuctionProfile.class, context -> {
            if (!context.hasFlag("other")) {
                UUID uuid = context.getPlayer().getUniqueId();
                Optional<AuctionProfile> optional = DataUtil.getProfile(database, AuctionProfile.class, false, uuid.toString());

                if (!optional.isPresent()) {
                    throw new ConditionFailedException("Could not find your auction profile.");
                }

                return optional.get();
            }

            String arg = context.popFirstArg();
            Optional<AuctionProfile> optional = DataUtil.getProfile(database, AuctionProfile.class, true, arg);
            if (!optional.isPresent()) {
                throw new ConditionFailedException("Could not find " + arg + "'s auction profile.");
            }

            return optional.get();
        });

        manager.registerCommand(new AuctionCommand(provider.getProvider(), server, database, api));
    }

    @Override
    public void onDisable() {
        database.getClient().shutdown();
    }

    public static AuctionHousePlugin get() {
        return REFERENCE.get();
    }
}
