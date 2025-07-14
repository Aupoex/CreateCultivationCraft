package euphy.upo.create_cultivation.content.recipes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import euphy.upo.create_cultivation.registry.CCRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class StackingCultivatingRecipe extends ProcessingRecipe<RecipeInput, StackingCultivatingRecipeParams> implements IStackingCultivatingRecipe {

    public StackingCultivatingRecipe(StackingCultivatingRecipeParams params) {
        super(CCRecipes.STACKING_CULTIVATING, params);
    }

    public static StackingCultivatingRecipeBuilder builder(ResourceLocation id) {
        return new StackingCultivatingRecipeBuilder(id);
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        if (recipeInput.isEmpty() || recipeInput.getItem(0).isEmpty())
            return false;
        if (ingredients.isEmpty())
            return false;
        return ingredients.get(0).test(recipeInput.getItem(0));
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    public ProcessingOutput getResult() {
        return this.params.result;
    }

    @Override
    public int getMaxHeight() {
        return this.params.maxHeight;
    }

    @Override
    public Block getBlockToRender() {
        return this.params.blockToRender;
    }

    @Override
    public int getProcessingDuration() {
        return this.processingDuration;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CCRecipes.STACKING_CULTIVATING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CCRecipes.STACKING_CULTIVATING.getType();
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 0;
    }

    public static class Serializer implements RecipeSerializer<StackingCultivatingRecipe> {
        private final MapCodec<StackingCultivatingRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, StackingCultivatingRecipe> streamCodec;

        public Serializer() {
            this.codec = ProcessingRecipe.codec(StackingCultivatingRecipe::new, StackingCultivatingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(StackingCultivatingRecipe::new, StackingCultivatingRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<StackingCultivatingRecipe> codec() { return this.codec; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StackingCultivatingRecipe> streamCodec() { return this.streamCodec; }
    }
}
