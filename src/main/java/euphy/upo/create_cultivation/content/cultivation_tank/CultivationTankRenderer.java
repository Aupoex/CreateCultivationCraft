package euphy.upo.create_cultivation.content.cultivation_tank;

import com.mojang.blaze3d.vertex.PoseStack;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CultivationTankRenderer implements BlockEntityRenderer<CultivationTankBlockEntity> {

    public CultivationTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CultivationTankBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {


        if (blockEntity.getCurrentRecipe().isEmpty()) {
            return;
        }

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();


        switch (blockEntity.getRecipeMode()) {
            case STAGE_BASED -> renderStageBased(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, blockRenderer);
            case STACK_BASED -> renderStackBased(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, blockRenderer);
        }
    }


    private void renderStageBased(CultivationTankBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BlockRenderDispatcher blockRenderer) {
        blockEntity.getCurrentRecipe().ifPresent(recipeHolder -> {
            if (recipeHolder.value() instanceof CultivatingRecipe recipe) {
                Block cropBlock = recipe.getCropBlock();
                BlockState cropState = cropBlock.defaultBlockState();

                IntegerProperty ageProperty = null;
                for (var property : cropState.getProperties()) {
                    if (property instanceof IntegerProperty i && "age".equals(i.getName())) {
                        ageProperty = i;
                        break;
                    }
                }

                if (ageProperty != null) {
                    float growthRatio = blockEntity.getStageGrowthRatio();
                    int maxAge = ageProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
                    int currentAge = (int) (growthRatio * maxAge);
                    cropState = cropState.setValue(ageProperty, currentAge);
                }

                poseStack.pushPose();
                translateAndScale(poseStack);
                blockRenderer.renderSingleBlock(cropState, poseStack, bufferSource, packedLight, packedOverlay);
                poseStack.popPose();
            }
        });
    }


    private void renderStackBased(CultivationTankBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BlockRenderDispatcher blockRenderer) {
        blockEntity.getCurrentRecipe().ifPresent(recipeHolder -> {
            if (recipeHolder.value() instanceof StackingCultivatingRecipe recipe) {
                Block blockToRender = recipe.getBlockToRender();
                BlockState renderState = blockToRender.defaultBlockState();
                int height = blockEntity.getCurrentHeight();

                poseStack.pushPose();
                translateAndScale(poseStack);


                for (int i = 0; i < height; i++) {
                    poseStack.pushPose();

                    poseStack.translate(0, i, 0);
                    blockRenderer.renderSingleBlock(renderState, poseStack, bufferSource, packedLight, packedOverlay);
                    poseStack.popPose();
                }

                poseStack.popPose();
            }
        });
    }


    private void translateAndScale(PoseStack poseStack) {
        poseStack.translate(0.5, 0.0625, 0.5);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.translate(-0.5, 0, -0.5);
    }
}
