package io.github.hydos.capturetheflag.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class CaptureTheFlagMapConfig {
    public static final Codec<CaptureTheFlagMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("spawn_block").forGetter(map -> map.spawnBlock)
    ).apply(instance, CaptureTheFlagMapConfig::new));

    public final BlockState spawnBlock;

    public CaptureTheFlagMapConfig(BlockState spawnBlock) {
        this.spawnBlock = spawnBlock;
    }
}
