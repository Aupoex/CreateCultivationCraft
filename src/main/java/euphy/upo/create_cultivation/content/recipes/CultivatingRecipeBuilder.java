package euphy.upo.create_cultivation.content.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class CultivatingRecipeBuilder extends ProcessingRecipeBuilder<CultivatingRecipeParams, CultivatingRecipe, CultivatingRecipeBuilder> {

    public CultivatingRecipeBuilder(ResourceLocation recipeId) {
        super(CultivatingRecipe::new, recipeId);
    }


    public CultivatingRecipeBuilder cropBlock(Block block) {
        this.params.cropBlock = block;
        return this;
    }

    @Override
    protected CultivatingRecipeParams createParams() {
        return new CultivatingRecipeParams();
    }

    @Override
    public CultivatingRecipeBuilder self() {
        return this;
    }
}
