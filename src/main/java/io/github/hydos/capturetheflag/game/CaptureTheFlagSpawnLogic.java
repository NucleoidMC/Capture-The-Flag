package io.github.hydos.capturetheflag.game;

import xyz.nucleoid.plasmid.game.GameWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import io.github.hydos.capturetheflag.CaptureTheFlag;
import io.github.hydos.capturetheflag.game.map.CaptureTheFlagMap;

public class CaptureTheFlagSpawnLogic {
    private final GameWorld gameWorld;
    private final CaptureTheFlagMap map;

    public CaptureTheFlagSpawnLogic(GameWorld gameWorld, CaptureTheFlagMap map) {
        this.gameWorld = gameWorld;
        this.map = map;
    }

    public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
        player.setGameMode(gameMode);

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.NIGHT_VISION,
                20 * 60 * 60,
                1,
                true,
                false
        ));
    }

    public void spawnPlayer(ServerPlayerEntity player) {
        ServerWorld world = this.gameWorld.getWorld();

        BlockPos pos = this.map.spawn;
        if (pos == null) {
            CaptureTheFlag.LOGGER.warn("Cannot spawn player! No spawn is defined in the map!");
            return;
        }

        float radius = 4.5f;
        double x = pos.getX() + MathHelper.nextDouble(player.getRandom(), -radius, radius);
        double z = pos.getZ() + MathHelper.nextFloat(player.getRandom(), -radius, radius);

        player.teleport(world, x, pos.getY() + 0.5, z, 0.0F, 0.0F);
    }
}
