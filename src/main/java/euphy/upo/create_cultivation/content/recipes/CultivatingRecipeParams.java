package euphy.upo.create_cultivation.content.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class CultivatingRecipeParams extends ProcessingRecipeParams {

    public Block cropBlock;

    public CultivatingRecipeParams() {
        super();
        this.cropBlock = Blocks.AIR;
    }

    public static final MapCodec<CultivatingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(

            Codec.either(FluidIngredient.CODEC, Ingredient.CODEC).listOf().fieldOf("ingredients").forGetter(p -> {
                List<Either<FluidIngredient, Ingredient>> ingredients = new ArrayList<>();
                p.ingredients.forEach(i -> ingredients.add(Either.right(i)));
                p.fluidIngredients.forEach(i -> ingredients.add(Either.left(i)));
                return ingredients;
            }),
            Codec.either(FluidStack.CODEC, ProcessingOutput.CODEC).listOf().fieldOf("results").forGetter(p -> {
                List<Either<FluidStack, ProcessingOutput>> results = new ArrayList<>();
                p.results.forEach(r -> results.add(Either.right(r)));
                p.fluidResults.forEach(r -> results.add(Either.left(r)));
                return results;
            }),
            Codec.INT.optionalFieldOf("processingDuration", 100).forGetter(p -> p.processingDuration),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("crop_block").forGetter(p -> ((CultivatingRecipeParams)p).cropBlock)

    ).apply(instance, (ingredients, results, duration, cropBlock) -> {
        CultivatingRecipeParams params = new CultivatingRecipeParams();
        ingredients.forEach(either -> either.ifRight(params.ingredients::add).ifLeft(params.fluidIngredients::add));
        results.forEach(either -> either.ifRight(params.results::add).ifLeft(params.fluidResults::add));
        params.processingDuration = duration;
        params.cropBlock = cropBlock;
        return params;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, CultivatingRecipeParams> STREAM_CODEC =
            ProcessingRecipeParams.streamCodec(CultivatingRecipeParams::new);

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(this.cropBlock));
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        this.cropBlock = BuiltInRegistries.BLOCK.get(buffer.readResourceLocation());
    }
}
