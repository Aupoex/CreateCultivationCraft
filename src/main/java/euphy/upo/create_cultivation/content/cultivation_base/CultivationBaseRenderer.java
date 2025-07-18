package euphy.upo.create_cultivation.content.cultivation_base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import euphy.upo.create_cultivation.registry.CCPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class CultivationBaseRenderer extends KineticBlockEntityRenderer<CultivationBaseBlockEntity> {

    public CultivationBaseRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CultivationBaseBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        renderRotatingCogBER(be, partialTicks, ms, buffer, light, overlay);
    }


    protected void renderRotatingCogBER(CultivationBaseBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        //super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        BlockState blockState = be.getBlockState();
        SuperByteBuffer cogWheelSbb = CachedBuffers.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState);

        Direction.Axis axis = Direction.Axis.Y;
        float angleRadians = KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), axis);

        KineticBlockEntityRenderer.kineticRotationTransform(cogWheelSbb, be, axis, angleRadians, light);

        ms.pushPose();

        ms.translate(0, 0, 0);

        cogWheelSbb.overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

        ms.popPose();

        if (blockState.getValue(CultivationBaseBlock.WORKING)) {

            SuperByteBuffer glowLayer = CachedBuffers.partial(CCPartialModels.CULTIVATION_BASE_GLOW, blockState);


            int fullbright = 15728880;


            glowLayer.light(fullbright).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        }

    }
}
