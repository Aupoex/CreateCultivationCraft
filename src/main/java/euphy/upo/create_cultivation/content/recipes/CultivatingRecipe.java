package euphy.upo.create_cultivation.content.recipes;

import com.mojang.serialization.MapCodec;
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

public class CultivatingRecipe extends ProcessingRecipe<RecipeInput, CultivatingRecipeParams> implements ICultivatingRecipe {

    public CultivatingRecipe(CultivatingRecipeParams params) {
        super(CCRecipes.CULTIVATING, params);
    }

    @Override
    public Block getCropBlock() {
        return this.params.cropBlock;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
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
    public int getProcessingDuration() {
        return this.processingDuration;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CCRecipes.CULTIVATING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CCRecipes.CULTIVATING.getType();
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 8;
    }

    public static class Serializer implements RecipeSerializer<CultivatingRecipe> {
        private final MapCodec<CultivatingRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, CultivatingRecipe> streamCodec;

        public Serializer() {
            this.codec = ProcessingRecipe.codec(CultivatingRecipe::new, CultivatingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(CultivatingRecipe::new, CultivatingRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<CultivatingRecipe> codec() { return this.codec; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CultivatingRecipe> streamCodec() { return this.streamCodec; }
    }
}
