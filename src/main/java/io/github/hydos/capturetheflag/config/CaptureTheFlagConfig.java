package io.github.hydos.capturetheflag.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.player.GameTeam;

import java.util.List;

public class CaptureTheFlagConfig {
    public static final Codec<CaptureTheFlagConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            GameTeam.CODEC.listOf().fieldOf("teams").forGetter(config -> config.teams),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs)
    ).apply(instance, CaptureTheFlagConfig::new));

    public final PlayerConfig playerConfig;
    public final int timeLimitSecs;
    public final List<GameTeam> teams;

    public CaptureTheFlagConfig(PlayerConfig players, List<GameTeam> teams, int timeLimitSecs) {
        this.playerConfig = players;
        this.teams = teams;
        this.timeLimitSecs = timeLimitSecs;
    }
}
