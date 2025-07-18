package euphy.upo.create_cultivation.content.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;


public class CultivatingRecipeBuilder extends ProcessingRecipeBuilder<CultivatingRecipe> {


    public CultivatingRecipeBuilder(ResourceLocation recipeId) {
        super(CultivatingRecipe::new, recipeId);

        this.params = new CultivatingRecipeParams(recipeId);
    }

    public CultivatingRecipeBuilder cropBlock(Block block) {
        ((CultivatingRecipeParams) this.params).cropBlock = block;
        return this;
    }

    public CultivatingRecipeBuilder height(int height) {
        ((CultivatingRecipeParams) this.params).height = height;
        return this;
    }
}