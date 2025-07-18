package euphy.upo.create_cultivation.content.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class StackingCultivatingRecipeParams extends ProcessingRecipeBuilder.ProcessingRecipeParams {
    public ProcessingOutput result;
    public int maxHeight;
    public Block blockToRender;

    public StackingCultivatingRecipeParams(ResourceLocation id) {
        super(id);
        this.blockToRender = Blocks.AIR;
    }
}