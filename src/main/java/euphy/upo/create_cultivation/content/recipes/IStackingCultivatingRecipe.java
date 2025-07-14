package euphy.upo.create_cultivation.content.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.world.level.block.Block;


public interface IStackingCultivatingRecipe {
    ProcessingOutput getResult();
    int getMaxHeight();
    Block getBlockToRender();
}
