package gg.tater.auctionhouse.util;

import gg.tater.auctionhouse.player.uuid.NameUUIDEntry;
import gg.tater.bedrock.database.BedrockDatabase;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
public class DataUtil {

    public <T> Optional<T> getProfile(BedrockDatabase database, Class<T> clazz, boolean hasFlag, String arg) {
        if (!hasFlag) {
            return database.getCachedEntity(clazz, arg);
        }

        if (arg == null) {
            return Optional.empty();
        }

        AtomicReference<T> reference = new AtomicReference<>();
        database.getCachedEntity(NameUUIDEntry.class, arg)
                .flatMap(entry -> database.getCachedEntity(clazz, entry.getUuid().toString()))
                .ifPresent(reference::set);

        return Optional.ofNullable(clazz.cast(reference.get()));
    }
}
