package gg.tater.auctionhouse.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.tater.addons.builder.ItemBuilder;
import gg.tater.addons.gui.Button;
import gg.tater.addons.gui.Gui;
import gg.tater.auctionhouse.item.AuctionItem;
import gg.tater.auctionhouse.player.AuctionProfile;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.auctionhouse.util.ButtonUtil;
import gg.tater.auctionhouse.util.ChatUtil;
import gg.tater.bedrock.database.BedrockDatabase;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExpiredItemsGui extends Gui {

    private final AuctionProfile profile;
    private final AuctionServer server;
    private final BedrockDatabase database;
    private final Economy economy;
    private final HeadDatabaseAPI api;

    public ExpiredItemsGui(AuctionProfile profile, AuctionServer server, BedrockDatabase database, Economy economy, HeadDatabaseAPI api) {
        super("Expired Items", 5);
        this.profile = profile;
        this.server = server;
        this.database = database;
        this.economy = economy;
        this.api = api;
    }

    @Override
    protected List<Button> elements() {
        List<Button> buttons = Lists.newArrayList();

        profile.getListings()
                .stream()
                .filter(AuctionItem::hasExpired)
                .forEach(item ->
                        buttons.add(new Button(new ItemBuilder(item.getStack().clone())
                                .setLore(Arrays.asList(
                                        " ",
                                        ChatColor.GREEN + "Left-Click " + ChatColor.GRAY + "to re-claim your item.",
                                        ChatColor.GREEN + "Right-Click " + ChatColor.GRAY + "to re-list your item.",
                                        " "))
                                .toItemStack())
                                .onClick((player, event) -> {
                                    player.closeInventory();

                                    switch (event.getClick()) {
                                        case SHIFT_LEFT:
                                        case LEFT:
                                            if (!profile.hasEmptyInventory()) {
                                                player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.RED + "You do not have enough inventory space for this item.");
                                                return;
                                            }

                                            player.getInventory().addItem(item.getStack());
                                            profile.removeListing(item);
                                            database.publish(profile);

                                            player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.GREEN + "You have reclaimed your expired auction listing.");
                                            break;
                                        case RIGHT:
                                        case SHIFT_RIGHT:
                                            item.setEnd(Instant.now().plus(8L, ChronoUnit.HOURS).toEpochMilli());
                                            server.addServerListing(item);

                                            database.publish(server);
                                            database.publish(profile);

                                            player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.GREEN + "You have relisted your expired auction item.");
                                            break;
                                        default:
                                            break;
                                    }
                                })));

        return buttons;
    }

    @Override
    protected Map<Integer, Button> utilityButtons() {
        Map<Integer, Button> map = Maps.newHashMap();

        if (profile.getListings()
                .stream()
                .noneMatch(AuctionItem::hasExpired)) {

            map.put(22, new Button(new ItemBuilder(Material.BARRIER)
                    .setName(ChatColor.RED + "You have no expired auction listings!")
                    .setLore(Collections.singletonList(ChatColor.GRAY + "Add an item with /ah list"))
                    .toItemStack()));
        }

        map.put(40, ButtonUtil.getBackButton(profile, database, economy, server, api));
        return map;
    }
}
