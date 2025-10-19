package de.larsensmods.mctrains.mixin;

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

    @Inject(method = "startTracking(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onStartedTrackingBy(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void minecarttweaks$sendLinkingInitData(ServerPlayerEntity player, CallbackInfo ci) {
        if(this.entity instanceof AbstractMinecartEntity minecart) {
            // parent -> child
            PacketByteBuf buf1 = new PacketByteBuf(Unpooled.buffer());
            new ClientboundSyncMinecartTrainPacket(
                minecart.getChainedParent() != null ? minecart.getChainedParent().getId() : -1,
                entity.getId()
            ).write(buf1);
            ServerPlayNetworking.send(player, ClientboundSyncMinecartTrainPacket.ID, buf1);

            // child -> parent
            PacketByteBuf buf2 = new PacketByteBuf(Unpooled.buffer());
            new ClientboundSyncMinecartTrainPacket(
                entity.getId(),
                minecart.getChainedChild() != null ? minecart.getChainedChild().getId() : -1
            ).write(buf2);
            ServerPlayNetworking.send(player, ClientboundSyncMinecartTrainPacket.ID, buf2);
        }
    }
}