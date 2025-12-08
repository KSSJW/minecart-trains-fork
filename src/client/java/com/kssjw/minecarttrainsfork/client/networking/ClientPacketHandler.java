package com.kssjw.minecarttrainsfork.client.networking;

import com.kssjw.minecarttrainsfork.manager.NetWorkManager.ClientboundSyncMinecartTrainPacket;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class ClientPacketHandler {

    public static void handleSyncMinecartTrain(ClientboundSyncMinecartTrainPacket payload) {
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        int parentID = payload.parentEntityID();
        int childID = payload.childEntityID();

        if(clientWorld != null) {
            @Nullable Entity parentEntity = clientWorld.getEntityById(parentID);
            @Nullable Entity childEntity = clientWorld.getEntityById(childID);

            if(parentEntity instanceof IChainableUtil chainable){
                chainable.setClientChainedChild(childID);
            }
            if(childEntity instanceof IChainableUtil chainable){
                chainable.setClientChainedParent(parentID);
            }
        }
    }

}
