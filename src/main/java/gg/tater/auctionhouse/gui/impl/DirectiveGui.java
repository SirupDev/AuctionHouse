package gg.tater.auctionhouse.gui.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.tater.auctionhouse.AuctionHousePlugin;
import gg.tater.auctionhouse.gui.Button;
import gg.tater.auctionhouse.gui.Gui;
import gg.tater.auctionhouse.item.AuctionItem;
import gg.tater.auctionhouse.player.AuctionProfile;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.auctionhouse.util.ItemBuilder;
import gg.tater.bedrock.database.BedrockDatabase;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DirectiveGui extends Gui {

    private final AuctionProfile profile;
    private final BedrockDatabase database;
    private final Economy economy;
    private final AuctionServer server;
    private final HeadDatabaseAPI api;

    public DirectiveGui(AuctionProfile profile, BedrockDatabase database, Economy economy, AuctionServer server, HeadDatabaseAPI api) {
        super("Auction House", 5);
        this.profile = profile;
        this.database = database;
        this.economy = economy;
        this.server = server;
        this.api = api;
    }

    @Override
    protected List<Button> elements() {
        return Lists.newArrayList();
    }

    @Override
    protected Map<Integer, Button> utilityButtons() {
        Map<Integer, Button> map = Maps.newHashMap();

        map.put(20, new Button(new ItemBuilder(api.getItemHead("19464"))
                .setName(ChatColor.BOLD + "" + ChatColor.GOLD + "Live Auctions " + ChatColor.GRAY + "(Click)")
                .setLore(Arrays.asList(
                        " ",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "Need a quick item or want",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "to browse the auction catalog?",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "This is your one-stop shop",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "for every possible need.",
                        " ",
                        ChatColor.GRAY + "There are " + ChatColor.AQUA + server.getListings().size() + ChatColor.GRAY + " active listings!",
                        " "))
                .toItemStack())
                .onClick((player, event) ->

                        Bukkit.getScheduler().runTaskLater(AuctionHousePlugin.get(), () ->
                                new ActiveItemsGui(profile, database, server, economy, api, false).open(profile.toPlayer()), 2L)));

        map.put(22, new Button(new ItemBuilder(api.getItemHead("34670"))
                .setName(ChatColor.BOLD + "" + ChatColor.GOLD + "Your Active Auctions " + ChatColor.GRAY + "(Click)")
                .setLore(Arrays.asList(
                        " ",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "Don't want an item listed",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "on the market anymore?",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "This will have all your",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "active personal listings.",
                        " ",
                        ChatColor.GRAY + "You have " + ChatColor.AQUA + profile.getListings()
                                .stream()
                                .filter(item -> !item.hasExpired())
                                .count() + ChatColor.GRAY + " active listings!",
                        " "))
                .toItemStack())
                .onClick((player, event) ->
                        Bukkit.getScheduler().runTaskLater(AuctionHousePlugin.get(), () ->
                                new ActiveItemsGui(profile, database, server, economy, api, true).open(profile.toPlayer()), 2L)));

        map.put(24, new Button(new ItemBuilder(api.getItemHead("45163"))
                .setName(ChatColor.BOLD + "" + ChatColor.GOLD + "Your Expired Auctions " + ChatColor.GRAY + "(Click)")
                .setLore(Arrays.asList(
                        " ",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "Want to re-list an item",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "or re-claim an item from",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "your previous auctions?",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "This will have all your",
                        ChatColor.YELLOW + "" + ChatColor.ITALIC + "expired auction listings.",
                        " ",
                        ChatColor.GRAY + "You have " + ChatColor.AQUA + profile.getListings()
                                .stream()
                                .filter(AuctionItem::hasExpired)
                                .count() + ChatColor.GRAY + " inactive listings!",
                        " "))
                .toItemStack())
                .onClick((player, event) ->
                        Bukkit.getScheduler().runTaskLater(AuctionHousePlugin.get(), () ->
                                new ExpiredItemsGui(profile, server, database, economy, api).open(profile.toPlayer()), 2L)));

        return map;
    }
}
