package euphy.upo.create_cultivation.datagen;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipeBuilder;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipeParams;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipeBuilder;
import euphy.upo.create_cultivation.registry.CCRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class CCRecipeProvider extends ProcessingRecipeGen<CultivatingRecipeParams, CultivatingRecipe, CultivatingRecipeBuilder> {

    public CCRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateCultivationCraft.MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CCRecipes.CULTIVATING;
    }

    @Override
    protected CultivatingRecipeBuilder getBuilder(ResourceLocation id) {
        return new CultivatingRecipeBuilder(id);
    }


    @Override
    protected void buildRecipes(RecipeOutput consumer) {

        getBuilder(asResource("wheat_from_seeds"))
                .require(Items.WHEAT_SEEDS)
                .output(Items.WHEAT, 1)
                .output(0.25f, Items.WHEAT, 1)
                .output(Items.WHEAT_SEEDS, 1)
                .output(0.5f, Items.WHEAT_SEEDS, 1)
                .output(0.5f, Items.WHEAT_SEEDS, 1)
                .duration(100)
                .cropBlock(Blocks.WHEAT)
                .build(consumer);

        getBuilder(asResource("carrot"))
                .require(Items.CARROT)
                .output(Items.CARROT, 2)
                .output(0.5f, Items.CARROT, 1)
                .duration(120)
                .cropBlock(Blocks.CARROTS)
                .build(consumer);

        //堆叠生长配方
        createStacking("sugar_cane", b -> b
                .require(Items.SUGAR_CANE)
                .result(new ProcessingOutput(new ItemStack(Items.SUGAR_CANE, 1), 1.0f))
                .duration(180)
                .maxHeight(3)
                .blockToRender(Blocks.SUGAR_CANE)
        ).build(consumer);
    }

    private CultivatingRecipeBuilder createCultivating(String name, UnaryOperator<CultivatingRecipeBuilder> builder) {
        return builder.apply(new CultivatingRecipeBuilder(asResource(name)));
    }

    private StackingCultivatingRecipeBuilder createStacking(String name, UnaryOperator<StackingCultivatingRecipeBuilder> builder) {
        return builder.apply(new StackingCultivatingRecipeBuilder(asResource(name)));
    }
}
