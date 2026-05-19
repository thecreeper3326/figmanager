package net.johnseagull.figManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
/**
 * FigPacket sends a string form of a <code>Figs</code> instance. Must be converted with <code>FigManager.toString(Figs)</code> and <code>FigManager.fromString(String)</code>
 * */
public record FigPacket(
       String figs
) implements CustomPacketPayload {
    public static final Type<FigPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(FigManager.name, "fig_packet"));
    public static final StreamCodec<FriendlyByteBuf, FigPacket> CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeUtf(p.figs);
            },
            buf -> new FigPacket(buf.readUtf())
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}