package io.github.hydos.capturetheflag;

import io.github.hydos.capturetheflag.game.CaptureTheFlagWaiting;
import net.fabricmc.api.ModInitializer;
import xyz.nucleoid.plasmid.game.GameType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.hydos.capturetheflag.config.CaptureTheFlagConfig;

public class CaptureTheFlag implements ModInitializer {

    public static final String ID = "capturetheflag";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<CaptureTheFlagConfig> TYPE = GameType.register(
            new Identifier(ID, "capturetheflag"),
            CaptureTheFlagWaiting::open,
            CaptureTheFlagConfig.CODEC
    );

    @Override
    public void onInitialize() {}
}
