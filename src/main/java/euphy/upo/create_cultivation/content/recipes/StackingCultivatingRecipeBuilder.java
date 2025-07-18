package euphy.upo.create_cultivation.content.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class StackingCultivatingRecipeBuilder extends ProcessingRecipeBuilder<StackingCultivatingRecipe> {

    public StackingCultivatingRecipeBuilder(ResourceLocation recipeId) {
        super(StackingCultivatingRecipe::new, recipeId);
        this.params = new StackingCultivatingRecipeParams(recipeId);
    }

    public StackingCultivatingRecipeBuilder result(ProcessingOutput output) {
        ((StackingCultivatingRecipeParams) this.params).result = output;
        return this;
    }

    public StackingCultivatingRecipeBuilder maxHeight(int height) {
        ((StackingCultivatingRecipeParams) this.params).maxHeight = height;
        return this;
    }

    public StackingCultivatingRecipeBuilder blockToRender(Block block) {
        ((StackingCultivatingRecipeParams) this.params).blockToRender = block;
        return this;
    }
}