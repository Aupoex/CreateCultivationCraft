package euphy.upo.create_cultivation.compat.jade;

import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CultivationTankJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CreateCultivationCraft.MODID, "cultivation_tank");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (!serverData.contains("isGrowing")) {
            return;
        }

        if (accessor.getBlockEntity() instanceof CultivationTankBlockEntity tankBE) {
            tankBE.getControllerBE().getCurrentRecipe().ifPresent(recipeHolder -> {
                ItemStack seedStack = recipeHolder.value().getIngredients().get(0).getItems()[0];
                tooltip.add(Component.translatable("create_cultivation.jade.crop", seedStack.getDisplayName()));
            });
        }

        if (serverData.getBoolean("isMature")) {
            tooltip.add(Component.translatable("create_cultivation.jade.mature"));
        } else {
            String recipeMode = serverData.getString("recipeMode");
            if ("STAGE_BASED".equals(recipeMode)) {
                float speedMultiplier = serverData.getFloat("speedMultiplier");
                if (speedMultiplier < 0.01f) {
                    tooltip.add(Component.translatable("create_cultivation.jade.stalled"));
                } else {
                    int remainingPoints = serverData.getInt("remainingPoints");
                    float pointsPerSecond = serverData.getFloat("pointsPerSecond");
                    if (pointsPerSecond > 0) {
                        float remainingSeconds = remainingPoints / pointsPerSecond;
                        String formattedTime = String.format("%.1f", remainingSeconds);
                        tooltip.add(Component.translatable("create_cultivation.jade.time_remaining", formattedTime));
                    }
                }
            } else if ("STACK_BASED".equals(recipeMode)) {
                int currentHeight = serverData.getInt("currentHeight");
                int maxHeight = serverData.getInt("maxHeight");
                tooltip.add(Component.translatable("create_cultivation.jade.growth", currentHeight, maxHeight));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof CultivationTankBlockEntity tankBE)) {
            return;
        }

        CultivationTankBlockEntity controller = tankBE.getControllerBE();
        if (controller != null && controller.getCurrentRecipe().isPresent()) {
            data.putBoolean("isGrowing", true);
            data.putBoolean("isMature", controller.isMature());
            data.putString("recipeMode", controller.getRecipeMode().name());

            if (!controller.isMature()) {
                if (controller.getRecipeMode() == CultivationTankBlockEntity.RecipeMode.STAGE_BASED) {
                    int totalPoints = controller.getInternalProcessingDuration();
                    int currentPoints = controller.getProgress();
                    float speedMultiplier = controller.getSpeedMultiplier();
                    float lazyTicksPerSecond = 20.0f / controller.getLazyTickRate();
                    float pointsPerSecond = speedMultiplier * lazyTicksPerSecond;

                    data.putInt("remainingPoints", totalPoints - currentPoints);
                    data.putFloat("speedMultiplier", speedMultiplier);
                    data.putFloat("pointsPerSecond", pointsPerSecond);
                } else if (controller.getRecipeMode() == CultivationTankBlockEntity.RecipeMode.STACK_BASED) {
                    data.putInt("currentHeight", controller.getCurrentHeight());
                    controller.getCurrentRecipe().ifPresent(recipeHolder -> {
                        if (recipeHolder.value() instanceof StackingCultivatingRecipe recipe) {
                            data.putInt("maxHeight", recipe.getMaxHeight());
                        }
                    });
                }
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}