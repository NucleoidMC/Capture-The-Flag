package io.github.hydos.capturetheflag.game.map;

import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import xyz.nucleoid.plasmid.game.map.template.TemplateChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CaptureTheFlagMap {
    private final MapTemplate template;
    private final CaptureTheFlagMapConfig config;
    public BlockPos spawn;

    public CaptureTheFlagMap(MapTemplate template, CaptureTheFlagMapConfig config) {
        this.template = template;
        this.config = config;
    }

    public ChunkGenerator asGenerator() {
        return new TemplateChunkGenerator(this.template, BlockPos.ORIGIN);
    }
}
