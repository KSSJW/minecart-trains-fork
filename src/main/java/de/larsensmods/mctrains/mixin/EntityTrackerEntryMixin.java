package de.larsensmods.mctrains.mixin;

import de.larsensmods.mctrains.interfaces.IChainable;
import de.larsensmods.mctrains.networking.ClientboundSyncMinecartTrainPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import io.netty.buffer.Unpooled;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "startTracking(Lnet/minecraft/server/network/ServerPlayerEntity;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onStartedTrackingBy(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void minecarttweaks$sendLinkingInitData(ServerPlayerEntity player, CallbackInfo ci) {
        Entity maybe = this.entity;
        if (maybe instanceof AbstractMinecartEntity && maybe instanceof IChainable minecartChain) {
            // 获取 parent/child 的 IChainable 引用（如果存在）
            IChainable parentChain = minecartChain.getChainedParent();
            IChainable childChain  = minecartChain.getChainedChild();

            // 取得对应实体 id（若无法取得实体则用 -1）
            int parentId = -1;
            if (parentChain != null) {
                Entity parentEntity = parentChain.asEntity(); // 需要接口实现返回底层实体
                if (parentEntity instanceof AbstractMinecartEntity am) parentId = am.getId();
            }

            int childId = -1;
            if (childChain != null) {
                Entity childEntity = childChain.asEntity();
                if (childEntity instanceof AbstractMinecartEntity am) childId = am.getId();
            }

            // parent -> child
            PacketByteBuf buf1 = new PacketByteBuf(Unpooled.buffer());
            new ClientboundSyncMinecartTrainPacket(parentId, maybe.getId()).write(buf1);
            ServerPlayNetworking.send(player, ClientboundSyncMinecartTrainPacket.ID, buf1);

            // child -> parent
            PacketByteBuf buf2 = new PacketByteBuf(Unpooled.buffer());
            new ClientboundSyncMinecartTrainPacket(maybe.getId(), childId).write(buf2);
            ServerPlayNetworking.send(player, ClientboundSyncMinecartTrainPacket.ID, buf2);
        }
}
}