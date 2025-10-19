package de.larsensmods.mctrains.networking;

import de.larsensmods.mctrains.interfaces.IChainable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class ClientPacketHandler {

    public static void handleSyncMinecartTrain(ClientboundSyncMinecartTrainPacket payload) {
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        int parentID = payload.parentEntityID;
        int childID = payload.childEntityID;

        if(clientWorld != null) {
            @Nullable Entity parentEntity = clientWorld.getEntityById(parentID);
            @Nullable Entity childEntity = clientWorld.getEntityById(childID);

            if(parentEntity instanceof IChainable chainable){
                chainable.setClientChainedChild(childID);
            }
            if(childEntity instanceof IChainable chainable){
                chainable.setClientChainedParent(parentID);
            }
        }
    }

}