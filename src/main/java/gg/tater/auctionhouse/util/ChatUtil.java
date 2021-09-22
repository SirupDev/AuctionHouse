package gg.tater.auctionhouse.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;

@UtilityClass
public class ChatUtil {

    public String AUCTION_PREFIX = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Auction] ";

    public DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#,###");

}
