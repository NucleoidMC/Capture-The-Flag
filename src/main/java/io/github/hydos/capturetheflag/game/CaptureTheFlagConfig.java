package io.github.hydos.capturetheflag.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import io.github.hydos.capturetheflag.game.map.CaptureTheFlagMapConfig;

public class CaptureTheFlagConfig {
    public static final Codec<CaptureTheFlagConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            CaptureTheFlagMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs)
    ).apply(instance, CaptureTheFlagConfig::new));

    public final PlayerConfig playerConfig;
    public final CaptureTheFlagMapConfig mapConfig;
    public final int timeLimitSecs;

    public CaptureTheFlagConfig(PlayerConfig players, CaptureTheFlagMapConfig mapConfig, int timeLimitSecs) {
        this.playerConfig = players;
        this.mapConfig = mapConfig;
        this.timeLimitSecs = timeLimitSecs;
    }
}
