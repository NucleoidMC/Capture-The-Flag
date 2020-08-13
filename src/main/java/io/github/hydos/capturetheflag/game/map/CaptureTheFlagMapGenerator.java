package io.github.hydos.capturetheflag.game.map;

import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import xyz.nucleoid.plasmid.util.BlockBounds;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import io.github.hydos.capturetheflag.game.CaptureTheFlagConfig;

import java.util.concurrent.CompletableFuture;

public class CaptureTheFlagMapGenerator {

    private final CaptureTheFlagMapConfig config;

    public CaptureTheFlagMapGenerator(CaptureTheFlagMapConfig config) {
        this.config = config;
    }

    public CompletableFuture<CaptureTheFlagMap> create() {
        return CompletableFuture.supplyAsync(this::build, Util.getMainWorkerExecutor());
    }

    private CaptureTheFlagMap build() {
        MapTemplate template = MapTemplate.createEmpty();
        CaptureTheFlagMap map = new CaptureTheFlagMap(template, this.config);

        this.buildSpawn(template);
        map.spawn = new BlockPos(0,65,0);

        return map;
    }

    private void buildSpawn(MapTemplate builder) {
        BlockPos min = new BlockPos(-5, 64, -5);
        BlockPos max = new BlockPos(5, 64, 5);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            builder.setBlockState(pos, Blocks.RED_TERRACOTTA.getDefaultState());
        }
    }
}
