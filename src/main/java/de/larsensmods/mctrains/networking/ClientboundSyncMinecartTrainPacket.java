package de.larsensmods.mctrains.networking;

import de.larsensmods.mctrains.MinecartTrains;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientboundSyncMinecartTrainPacket {

    public static final Identifier ID = new Identifier(MinecartTrains.MOD_ID, "sync_minecart_chain");

    public final int parentEntityID;
    public final int childEntityID;

    public ClientboundSyncMinecartTrainPacket(int parentEntityID, int childEntityID) {
        this.parentEntityID = parentEntityID;
        this.childEntityID = childEntityID;
    }

    public static ClientboundSyncMinecartTrainPacket read(PacketByteBuf buf) {
        int parent = buf.readVarInt();
        int child = buf.readVarInt();
        return new ClientboundSyncMinecartTrainPacket(parent, child);
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(parentEntityID);
        buf.writeVarInt(childEntityID);
    }
}