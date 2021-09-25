package gg.tater.auctionhouse.player.uuid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class NameUUIDEntry {

    private final String name;
    private final UUID uuid;

}
