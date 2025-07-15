package euphy.upo.create_cultivation.compat.jade;

import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CultivationTankJadeProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof CultivationTankBlockEntity tankBE)) {
            return;
        }

        CultivationTankBlockEntity controller = tankBE.getControllerBE();
        if (controller == null) {
            return;
        }

        controller.getCurrentRecipe().ifPresent(recipeHolder -> {
            ItemStack seedStack = recipeHolder.value().getIngredients().get(0).getItems()[0];
            tooltip.add(Component.translatable("create_cultivation.jade.crop", seedStack.getDisplayName()));

            if (controller.isMature()) {
                tooltip.add(Component.translatable("create_cultivation.jade.mature"));
            } else {
                switch (controller.getRecipeMode()) {
                    case STAGE_BASED:

                        int totalLazyTicks = controller.getInternalProcessingDuration();
                        int currentLazyTicks = controller.getProgress();
                        int remainingLazyTicks = totalLazyTicks - currentLazyTicks;

                        float lazyTickRate = controller.getLazyTickRate();
                        float secondsPerLazyTick = lazyTickRate / 20.0f;
                        float remainingSeconds = remainingLazyTicks * secondsPerLazyTick;

                        String formattedTime = String.format("%.1f", remainingSeconds);
                        tooltip.add(Component.translatable("create_cultivation.jade.time_remaining", formattedTime));
                        break;

                    case STACK_BASED:
                        if (recipeHolder.value() instanceof StackingCultivatingRecipe recipe) {
                            int maxHeight = recipe.getMaxHeight();
                            tooltip.add(Component.translatable("create_cultivation.jade.growth", controller.getCurrentHeight(), maxHeight));
                        }
                        break;
                }
            }
        });
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("create_cultivation", "cultivation_tank");
    }
}