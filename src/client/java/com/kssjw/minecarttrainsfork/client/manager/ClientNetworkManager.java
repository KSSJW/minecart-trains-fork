package com.kssjw.minecarttrainsfork.client.manager;

import com.kssjw.minecarttrainsfork.manager.NetworkManager.ClientboundFullSyncTrainPacket;
import com.kssjw.minecarttrainsfork.manager.NetworkManager.ClientboundSyncMinecartTrainPacket;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ClientNetworkManager {

    public static void handleSyncMinecartTrain(ClientboundSyncMinecartTrainPacket payload) {
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        int parentID = payload.parentEntityID();
        int childID = payload.childEntityID();

        if (clientWorld != null) {

            // 处理 child
            Entity childEntity = clientWorld.getEntityById(childID);
            if (childEntity instanceof IChainableUtil chainable) {
                if (parentID == -1) {

                    // 清理掉旧的 parent 引用
                    chainable.setChainedParent(null);
                    chainable.setParentClientID(-1);
                    
                } else {
                    chainable.setClientChainedParent(parentID);
                }
            }

            // 处理 parent
            Entity parentEntity = clientWorld.getEntityById(parentID);
            if (parentEntity instanceof IChainableUtil chainable) {
                if (childID == -1) {

                    // 清理掉旧的 child 引用
                    chainable.setChainedChild(null);
                    chainable.setChildClientID(-1);

                } else {
                    chainable.setClientChainedChild(childID);
                }
            }
        }
    }
    
    public static void handleFullSyncTrain(ClientboundFullSyncTrainPacket payload) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        // 先清空所有链路，避免残留
        for (Entity e : world.getEntities()) {
            if (e instanceof IChainableUtil chainable) {
                chainable.setChainedParent(null);
                chainable.setParentClientID(-1);
                chainable.setChainedChild(null);
                chainable.setChildClientID(-1);
            }
        }

        // 再根据服务端发来的数据重建链路
        for (var link : payload.links()) {
            int parentId = link.getFirst();
            int childId = link.getSecond();

            if (parentId == -1) continue;

            Entity parentEntity = world.getEntityById(parentId);
            Entity childEntity = world.getEntityById(childId);

            if (parentEntity instanceof IChainableUtil parentChainable &&
                childEntity instanceof IChainableUtil childChainable) {

                parentChainable.setClientChainedChild(childId);
                childChainable.setClientChainedParent(parentId);
            }
        }
    }
}