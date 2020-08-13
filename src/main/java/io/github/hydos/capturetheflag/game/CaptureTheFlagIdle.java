package io.github.hydos.capturetheflag.game;

import io.github.hydos.capturetheflag.config.CaptureTheFlagConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import xyz.nucleoid.plasmid.game.GameWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.util.BroadcastUtils;

public class CaptureTheFlagIdle {
    private long closeTime = -1;
    public long finishTime = -1;
    private long startTime = -1;
    private final Object2ObjectMap<ServerPlayerEntity, FrozenPlayer> frozen;
    private boolean setSpectator = false;

    public CaptureTheFlagIdle() {
        this.frozen = new Object2ObjectOpenHashMap<>();
        this.frozen.defaultReturnValue(new FrozenPlayer());
    }

    public void onOpen(long time, CaptureTheFlagConfig config) {
        this.startTime = time - (time % 20) + (4 * 20) + 19;
        this.finishTime = this.startTime + (config.timeLimitSecs * 20);
    }

    public IdleTickResult tick(long time, GameWorld world) {
        // Game has finished. Wait a few seconds before finally closing the game.
        if (this.closeTime > 0) {
            if (time >= this.closeTime) {
                return IdleTickResult.GAME_CLOSED;
            }
            return IdleTickResult.TICK_FINISHED;
        }

        // Game hasn't started yet. Display a countdown before it begins.
        if (this.startTime > time) {
            this.tickStartWaiting(time, world);
            return IdleTickResult.TICK_FINISHED;
        }

        // Game has just finished. Transition to the waiting-before-close state.
        if (time > this.finishTime || world.getPlayers().isEmpty()) {
            if (!this.setSpectator) {
                this.setSpectator = true;
                for (ServerPlayerEntity player : world.getPlayers()) {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }

            this.closeTime = time + (5 * 20);

            return IdleTickResult.GAME_FINISHED;
        }

        return IdleTickResult.CONTINUE_TICK;
    }

    private void tickStartWaiting(long time, GameWorld world) {
        float sec_f = (this.startTime - time) / 20.0f;

        if (sec_f > 1) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player.isSpectator()) {
                    continue;
                }

                FrozenPlayer state = this.frozen.get(player);

                if (state.lastPos == null) {
                    state.lastPos = player.getPos();
                }

                player.teleport(state.lastPos.x, state.lastPos.y, state.lastPos.z);
            }
        }

        int sec = (int) Math.floor(sec_f) - 1;

        if ((this.startTime - time) % 20 == 0) {
            if (sec > 0) {
                BroadcastUtils.broadcastTitle(new LiteralText(Integer.toString(sec)).formatted(Formatting.BOLD), world);
                BroadcastUtils.broadcastSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 1.0F, world);
            } else {
                BroadcastUtils.broadcastTitle(new LiteralText("Go!").formatted(Formatting.BOLD), world);
                BroadcastUtils.broadcastSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 2.0F, world);
            }
        }
    }

    public static class FrozenPlayer {
        public Vec3d lastPos;
    }

    public enum IdleTickResult {
        CONTINUE_TICK,
        TICK_FINISHED,
        GAME_FINISHED,
        GAME_CLOSED,
    }
}
