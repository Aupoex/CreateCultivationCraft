package euphy.upo.create_cultivation.content.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;


public class CultivatingRecipeParams extends ProcessingRecipeBuilder.ProcessingRecipeParams {
    public Block cropBlock;
    public int height;

    public CultivatingRecipeParams(ResourceLocation id) {
        super(id);
        this.cropBlock = Blocks.AIR;
        this.height = 1;
    }
}