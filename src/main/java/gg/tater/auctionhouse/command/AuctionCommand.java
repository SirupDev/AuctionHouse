package gg.tater.auctionhouse.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import gg.tater.auctionhouse.gui.impl.DirectiveGui;
import gg.tater.auctionhouse.item.AuctionItem;
import gg.tater.auctionhouse.item.AuctionItemHierarchy;
import gg.tater.auctionhouse.player.AuctionProfile;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.auctionhouse.util.ChatUtil;
import gg.tater.bedrock.database.BedrockDatabase;
import lombok.RequiredArgsConstructor;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@CommandAlias("auction|auctionhouse|ah")
@RequiredArgsConstructor
public class AuctionCommand extends BaseCommand {

    private final Economy economy;
    private final AuctionServer server;
    private final BedrockDatabase database;
    private final HeadDatabaseAPI api;

    @Default
    private void onDefault(AuctionProfile profile) {
        new DirectiveGui(profile, database, economy, server, api).open(profile.toPlayer());
    }

    @Subcommand("list")
    @Syntax("<price>")
    private void onList(AuctionProfile profile, int price) {
        Player player = profile.toPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack hand = inventory.getItemInMainHand();

        if (hand.getType() == Material.AIR) {
            player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.RED + "You must have a valid item in your hand!");
            return;
        }

        if (price <= 0) {
            player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.RED + "Please specify a valid price!");
            return;
        }

        ItemStack stack = new ItemStack(hand.clone());
        AuctionItemHierarchy hierarchy = AuctionItemHierarchy.getHierarchy(player);
        AuctionItem item = new AuctionItem(profile, stack, price, hierarchy);

        server.addServerListing(item);
        database.publish(server);

        profile.addListing(item);
        database.publish(profile);

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        player.updateInventory();

        player.sendMessage(ChatUtil.AUCTION_PREFIX + ChatColor.GRAY + "Successfully listed your item for " + ChatColor.LIGHT_PURPLE + "$" + ChatUtil.DECIMAL_FORMATTER.format(price) + ChatColor.GRAY + ".");
    }
}
