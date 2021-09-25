package gg.tater.auctionhouse.event;

import gg.tater.auctionhouse.AuctionHousePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.util.function.Consumer;

public interface Events extends Listener, EventExecutor {

    static <T extends Event> Events listen(Class<T> type, Consumer<T> listener) {
        return listen(type, EventPriority.NORMAL, listener);
    }

    static <T extends Event> Events listen(Class<T> type, EventPriority priority, Consumer<T> listener) {

        final Events events = ($, event) -> listener.accept(type.cast(event));

        Bukkit.getPluginManager().registerEvent(type, events, priority, events, AuctionHousePlugin.get());

        return events;
    }

    static <T extends Event> Events listen(Class<T> type, EventPriority priority, boolean ignoreCancelled, Consumer<T> listener) {
        final Events events = ($, event) -> listener.accept(type.cast(event));
        Bukkit.getPluginManager().registerEvent(type, events, priority, events, AuctionHousePlugin.get(), ignoreCancelled);
        return events;
    }

    static void registerLegacy(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, AuctionHousePlugin.get());
    }

    default void unregister() {
        HandlerList.unregisterAll(this);
    }

}
