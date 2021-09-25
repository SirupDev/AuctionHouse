package gg.tater.auctionhouse.util;

import gg.tater.auctionhouse.AuctionHousePlugin;
import gg.tater.auctionhouse.gui.Button;
import gg.tater.auctionhouse.gui.impl.DirectiveGui;
import gg.tater.auctionhouse.player.AuctionProfile;
import gg.tater.auctionhouse.server.AuctionServer;
import gg.tater.bedrock.database.BedrockDatabase;
import lombok.experimental.UtilityClass;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

@UtilityClass
public class ButtonUtil {

    public Button getBackButton(AuctionProfile profile, BedrockDatabase database, Economy economy, AuctionServer server, HeadDatabaseAPI api) {
        return new Button(new ItemBuilder(Material.REDSTONE_BLOCK)
                .setName(ChatColor.RED + "Go Back " + ChatColor.GRAY + "(Click)")
                .setLore(Collections.singletonList(ChatColor.GRAY + "Click to go back to the previous menu."))
                .toItemStack())
                .onClick((player, event) ->
                        Bukkit.getScheduler().runTaskLater(AuctionHousePlugin.get(), () -> new DirectiveGui(profile, database, economy, server, api).open(player), 2L));
    }
}
