package io.github.hydos.capturetheflag.game;

import net.minecraft.server.MinecraftServer;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import io.github.hydos.capturetheflag.game.map.CaptureTheFlagMap;
import io.github.hydos.capturetheflag.game.map.CaptureTheFlagMapGenerator;
import xyz.nucleoid.plasmid.game.world.bubble.BubbleWorldConfig;

import java.util.concurrent.CompletableFuture;

public class CaptureTheFlagWaiting {
    private final GameWorld gameWorld;
    private final CaptureTheFlagMap map;
    private final CaptureTheFlagConfig config;
    private final CaptureTheFlagSpawnLogic spawnLogic;

    private CaptureTheFlagWaiting(GameWorld gameWorld, CaptureTheFlagMap map, CaptureTheFlagConfig config) {
        this.gameWorld = gameWorld;
        this.map = map;
        this.config = config;
        this.spawnLogic = new CaptureTheFlagSpawnLogic(gameWorld, map);
    }

    public static CompletableFuture<Void> open(MinecraftServer server, CaptureTheFlagConfig config) {
        CaptureTheFlagMapGenerator generator = new CaptureTheFlagMapGenerator(config.mapConfig);

        return generator.create().thenAccept(map -> {
            BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                    .setGenerator(map.asGenerator())
                    .setDefaultGameMode(GameMode.SPECTATOR);

            GameWorld gameWorld = GameWorld.open(server, worldConfig);
            CaptureTheFlagWaiting waiting = new CaptureTheFlagWaiting(gameWorld, map, config);

            gameWorld.openGame(builder -> {
                builder.setRule(GameRule.CRAFTING, RuleResult.DENY);
                builder.setRule(GameRule.PORTALS, RuleResult.DENY);
                builder.setRule(GameRule.PVP, RuleResult.DENY);
                builder.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
                builder.setRule(GameRule.HUNGER, RuleResult.DENY);
                builder.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);

                builder.on(RequestStartListener.EVENT, waiting::requestStart);
                builder.on(OfferPlayerListener.EVENT, waiting::offerPlayer);

                builder.on(PlayerAddListener.EVENT, waiting::addPlayer);
                builder.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
            });
        });
    }

    private JoinResult offerPlayer(ServerPlayerEntity player) {
        if (this.gameWorld.getPlayerCount() >= this.config.playerConfig.getMaxPlayers()) {
            return JoinResult.gameFull();
        }

        return JoinResult.ok();
    }

    private StartResult requestStart() {
        PlayerConfig playerConfig = this.config.playerConfig;
        if (this.gameWorld.getPlayerCount() < playerConfig.getMinPlayers()) {
            return StartResult.notEnoughPlayers();
        }

        CaptureTheFlagActive.open(this.gameWorld, this.map, this.config);

        return StartResult.ok();
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnPlayer(player);
    }

    private boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        this.spawnPlayer(player);
        return true;
    }

    private void spawnPlayer(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
    }
}
