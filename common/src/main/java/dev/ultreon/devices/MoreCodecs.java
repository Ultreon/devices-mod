package dev.ultreon.devices;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import java.util.UUID;

public class MoreCodecs {
    public static final Codec<UUID> UUID = Codec.pair(
            Codec.LONG,
            Codec.LONG
    ).xmap(longLongPair -> {
        long first = longLongPair.getFirst();
        long second = longLongPair.getSecond();
        return new UUID(first, second);
    }, uuid -> {
        long first = uuid.getMostSignificantBits();
        long second = uuid.getLeastSignificantBits();
        return new Pair<>(first, second);
    });
}
