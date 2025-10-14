package de.larsensmods.mctrains.mixin.client;

import de.larsensmods.mctrains.MinecartTrains;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(AbstractMinecartEntityRenderer.class)
public abstract class AbstractMinecartEntityRendererMixin<T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> extends EntityRenderer<T, S> {

    @Unique private static final Identifier TEXTURE_ID = Identifier.of(MinecartTrains.MOD_ID, "textures/entity/chain.png");
    @Unique private static final RenderLayer CHAIN_LAYER = RenderLayer.getEntitySmoothCutout(TEXTURE_ID);

    @Unique
    private AbstractMinecartEntity childCart = null;

    protected AbstractMinecartEntityRendererMixin(EntityRendererFactory.Context context) {super(context);}

    @Inject(method = "Lnet/minecraft/client/render/entity/AbstractMinecartEntityRenderer;updateRenderState(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;F)V", at= @At("TAIL"))
    public void mctrains$updateRenderState(T abstractMinecartEntity, S minecartEntityRenderState, float f, CallbackInfo ci){
        childCart = abstractMinecartEntity;
    }

    // *** Develeping code *** //
    
    // @Inject(method = "render(Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    // public void mctrains$render(S minecartEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci){
    //     AbstractMinecartEntity parent = childCart != null ? childCart.getChainedParent() : null;
    //     if(parent != null){
    //         double startX = parent.getX();
    //         double startY = parent.getY();
    //         double startZ = parent.getZ();

    //         double endX = childCart.getX();
    //         double endY = childCart.getY();
    //         double endZ = childCart.getZ();

    //         float distanceX = (float) (startX - endX);
    //         float distanceY = (float) (startY - endY);
    //         float distanceZ = (float) (startZ - endZ);

    //         float distance = childCart.distanceTo(parent);

    //         double hAngle = Math.toDegrees(Math.atan2(endZ - startZ, endX - startX));
    //         hAngle += Math.ceil(-hAngle / 360) * 360;

    //         double vAngle = Math.asin(distanceY / distance);

    //         renderChain(distanceX, distanceY, distanceZ, (float) hAngle, (float) vAngle, matrixStack, vertexConsumerProvider, light);
    //     }
    // }

    @Unique
    public void renderChain(float distanceX, float distanceY, float distanceZ, float hAngle, float vAngle, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        float squaredLength = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
        float length = MathHelper.sqrt(squaredLength) - 1f;

        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-hAngle - 90));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(-vAngle));
        matrixStack.translate(0, 0, 0.5);
        matrixStack.push();

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(CHAIN_LAYER);
        float vertX1 = 0f;
        float vertY1 = 0.25f;
        float vertX2 = MathHelper.sin(6.2831855f) * 0.125f;
        float vertY2 = MathHelper.cos(6.2831855f) * 0.125f;
        float minU = 0f;
        float maxU = 0.1875f;
        float minV = 0f;
        float maxV = length / 10;
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();

        vertexConsumer.vertex(matrix4f, vertX1, vertY1, 0f).color(0, 0, 0, 255).texture(minU, minV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0f, -1f, 0f);
        vertexConsumer.vertex(matrix4f, vertX1, vertY1, length).color(255, 255, 255, 255).texture(minU, maxV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0f, -1f, 0f);
        vertexConsumer.vertex(matrix4f, vertX2, vertY2, length).color(255, 255, 255, 255).texture(maxU, maxV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0f, -1f, 0f);
        vertexConsumer.vertex(matrix4f, vertX2, vertY2, 0f).color(0, 0, 0, 255).texture(maxU, minV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0f, -1f, 0f);

        matrixStack.pop();
        matrixStack.translate(0.19, 0.19, 0);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));

        entry = matrixStack.peek();
        matrix4f = entry.getPositionMatrix();

        vertexConsumer.vertex(matrix4f, vertX1, vertY1, 0f).color(0, 0, 0, 255).texture(minU, minV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0f, -1f, 0f);
        vertexConsumer.vertex(matrix4f, vertX1, vertY1, length).color(255, 255, 255, 255).texture(minU, maxV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0f, -1f, 0f);
        vertexConsumer.vertex(matrix4f, vertX2, vertY2, length).color(255, 255, 255, 255).texture(maxU, maxV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0f, -1f, 0f);
        vertexConsumer.vertex(matrix4f, vertX2, vertY2, 0f).color(0, 0, 0, 255).texture(maxU, minV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0f, -1f, 0f);

        matrixStack.pop();
    }

}
