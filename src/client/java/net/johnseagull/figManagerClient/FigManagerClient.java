package net.johnseagull.figManagerClient;

import net.johnseagull.figManager.FigManager;
import net.johnseagull.figManager.FigPacket;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FigManagerClient {
    public static float ow = 0;
    public static boolean absoluteWidth = false;
    public static Logger clientLogger = LoggerFactory.getLogger("CreeperDev ConFIG Manager Client");
    /**
     * Initializer for the client.
     * Prepares packets and registers GUI
     *
     * @param figs The figs class to be used on the client
     * @param optionWidth Width of options on configuration screen.
     *                    <br>
     *                    Values less than 1.0 will be interpreted as a ratio between the option entry and its label.
     *                    <br>
     *                    Values more than 1.0 will be interpreted as an absolute pixel value for option entries.
     *
     * */

    public void init(Object figs, float optionWidth) {
        ow = optionWidth;
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal(FigManager.name+"_config").executes(context -> {
                Minecraft client = context.getSource().getClient();
                client.execute(() -> {
                    client.setScreen(new FigScreen<>(Component.literal(FigManager.name+"_config"),optionWidth,figs, null));
                });
                return 1;
            }).requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR)));
        });
        ClientPlayNetworking.registerGlobalReceiver(FigPacket.ID, ((payload, context) -> {
            FigManager.FIGS = FigManager.fromString(payload.figs(), figs.getClass());
        }));

    }
}
