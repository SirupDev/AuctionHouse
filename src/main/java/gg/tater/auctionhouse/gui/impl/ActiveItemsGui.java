package gg.tater.auctionhouse.gui.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.tater.auctionhouse.gui.Button;
import gg.tater.auctionhouse.gui.Gui;
import gg.tater.auctionhouse.item.AuctionItem;
import gg.tater.auctionhouse.item.AuctionItemHierarchy;
import gg.tater.auctionhouse.player.AuctionProfile;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.auctionhouse.util.ButtonUtil;
import gg.tater.auctionhouse.util.ChatUtil;
import gg.tater.auctionhouse.util.ItemBuilder;
import gg.tater.auctionhouse.util.TimeUtil;
import gg.tater.bedrock.database.BedrockDatabase;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ActiveItemsGui extends Gui {

    private final AuctionProfile profile;
    private final BedrockDatabase database;
    private final AuctionServer server;
    private final Economy economy;
    private final List<AuctionItem> items;
    private final HeadDatabaseAPI api;
    private final boolean personal;

    public ActiveItemsGui(AuctionProfile profile, BedrockDatabase database, AuctionServer server, Economy economy, HeadDatabaseAPI api, boolean personal) {
        super(personal ? "Your Listings" : "Server Listings", 5);
        this.profile = profile;
        this.database = database;
        this.server = server;
        this.economy = economy;
        this.items = personal ? profile.getListings() : server.getListings();
        this.api = api;
        this.personal = personal;
    }

    @Override
    protected List<Button> elements() {
        List<Button> buttons = Lists.newArrayList();

        items.stream()
                .filter(item -> !item.hasExpired())
                .sorted(Comparator.comparingInt(item -> item.getHierarchy().getIntegerLevel()))
                .forEach(item ->
                        buttons.add(new Button(new ItemBuilder(item.getStack().clone())
                                .setLore(getLore(item))
                                .toItemStack())
                                .onClick((player, event) -> {
                                    player.closeInventory();

                                    if (item.hasExpired()) {
                                        player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.RED + "This auction listing has expired.");
                                        return;
                                    }

                                    // player does not have inventory room for item
                                    if (!profile.hasEmptyInventory()) {
                                        player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.RED + "You do not have enough inventory space for this item.");
                                        return;
                                    }

                                    // if it is the gui openers own listing
                                    if (profile.hasListing(item)) {
                                        server.removeServerListing(item);
                                        profile.removeListing(item);

                                        database.publish(server);
                                        database.publish(profile);

                                        player.getInventory().addItem(item.getStack());

                                        player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.GREEN + "You have removed your auction listing.");
                                        return;
                                    }

                                    double balance = economy.getBalance(player);
                                    if (balance < item.getPrice()) {
                                        player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.RED + "You do not have enough money to afford this item.");
                                        return;
                                    }

                                    economy.withdrawPlayer(player, item.getPrice());
                                    economy.depositPlayer(Bukkit.getOfflinePlayer(item.getSellerUUID()), item.getPrice());

                                    database.getCachedEntity(AuctionProfile.class, item.getSellerUUID().toString())
                                            .ifPresent(sellerProfile -> {
                                                sellerProfile.removeListing(item);
                                                database.publish(sellerProfile);
                                            });

                                    server.removeServerListing(item);
                                    database.publish(server);

                                    player.getInventory().addItem(item.getStack());

                                    Bukkit.broadcastMessage(ChatUtil.AUCTION_PREFIX
                                            + ChatColor.LIGHT_PURPLE
                                            + player.getName()
                                            + ChatColor.GRAY + " has purchased "
                                            + ChatColor.LIGHT_PURPLE + item.getSellerName() + "'s"
                                            + ChatColor.GRAY + " auction listing for "
                                            + ChatColor.LIGHT_PURPLE
                                            + "$" + ChatUtil.DECIMAL_FORMATTER.format(item.getPrice())
                                            + ChatColor.GRAY + ".");
                                })));

        return buttons;
    }

    @Override
    protected Map<Integer, Button> utilityButtons() {
        Map<Integer, Button> map = Maps.newHashMap();

        if (items.isEmpty()) {
            map.put(22, new Button(new ItemBuilder(Material.BARRIER)
                    .setName(personal ? ChatColor.RED + "You have no auction listings currently!" : ChatColor.RED + "There are no auction items!")
                    .setLore(Collections.singletonList(ChatColor.GRAY + "Add an item with /ah list"))
                    .toItemStack()));
        }

        map.put(40, ButtonUtil.getBackButton(profile, database, economy, server, api));

        return map;
    }

    private List<String> getLore(AuctionItem item) {
        List<String> lore = Lists.newArrayList();
        ItemMeta meta = item.getStack().getItemMeta();

        if (meta != null && meta.getLore() != null) {
            lore.addAll(meta.getLore());
        }

        lore.add(" ");
        lore.add(ChatColor.DARK_PURPLE + "Auction Details:");

        if (item.getHierarchy() != AuctionItemHierarchy.REGULAR) {
            lore.add(" ");
            lore.add(ChatColor.WHITE + "*" + ChatColor.YELLOW + "" + ChatColor.BOLD + " PINNED LISTING! " + ChatColor.WHITE + "*");
            lore.add(" ");
        }

        lore.add(ChatColor.WHITE + "Seller: " + ChatColor.LIGHT_PURPLE + item.getSellerName());
        lore.add(ChatColor.WHITE + "Price: " + ChatColor.LIGHT_PURPLE + "$" + ChatUtil.DECIMAL_FORMATTER.format(item.getPrice()));
        lore.add(ChatColor.WHITE + "Ends in: " + ChatColor.LIGHT_PURPLE + TimeUtil.formatTime(item.getEnd()));
        lore.add(" ");
        lore.add(profile.hasListing(item) ? ChatColor.RED + "Click to remove your listing." : ChatColor.AQUA + "Click to purchase listing!");

        return lore;
    }
}
