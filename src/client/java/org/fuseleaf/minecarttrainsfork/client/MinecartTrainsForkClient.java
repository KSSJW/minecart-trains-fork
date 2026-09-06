package org.fuseleaf.minecarttrainsfork.client;

import java.util.UUID;

import org.fuseleaf.minecarttrainsfork.client.manager.ClientLoadManager;
import org.fuseleaf.minecarttrainsfork.manager.NetworkManager;
import org.fuseleaf.minecarttrainsfork.util.IChainableUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class MinecartTrainsForkClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientLoadManager.init();

        ClientPlayNetworking.registerGlobalReceiver(
            NetworkManager.RelationshipPayload.TYPE,
            (payload, context) -> {
                context.client().execute(() -> {
                    ClientLevel clientWorld = Minecraft.getInstance().level;

                    if (clientWorld != null) {
                        UUID childUUID = payload.childUUID();
                        UUID parentUUID = payload.parentUUID();

                        if (childUUID != null) {
                            IChainableUtil childChainableUtil = (IChainableUtil) clientWorld.getEntity(childUUID);

                            if (childChainableUtil != null) {
                                childChainableUtil.setParentUUID(parentUUID);
                            }
                        }

                        if (parentUUID != null) {
                            IChainableUtil parentChainableUtil = (IChainableUtil) clientWorld.getEntity(parentUUID);

                            if (parentChainableUtil != null) {
                                parentChainableUtil.setChildUUID(childUUID);
                            }
                        }
                    }
                });
            }
        );
    }
}
