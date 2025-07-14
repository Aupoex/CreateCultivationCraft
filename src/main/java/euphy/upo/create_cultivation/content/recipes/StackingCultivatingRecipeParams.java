package euphy.upo.create_cultivation.content.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class StackingCultivatingRecipeParams extends ProcessingRecipeParams {

    public ProcessingOutput result;
    public int maxHeight;

    public Block blockToRender;

    public StackingCultivatingRecipeParams() {
        super();
        this.blockToRender = Blocks.AIR;
    }


    public static final MapCodec<StackingCultivatingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(p -> p.ingredients.get(0)),
            ProcessingOutput.CODEC.fieldOf("result").forGetter(p -> ((StackingCultivatingRecipeParams)p).result),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block_to_render").forGetter(p -> ((StackingCultivatingRecipeParams)p).blockToRender),
            Codec.INT.optionalFieldOf("processingDuration", 200).forGetter(p -> p.processingDuration),
            Codec.INT.optionalFieldOf("maxHeight", 3).forGetter(p -> ((StackingCultivatingRecipeParams)p).maxHeight)
    ).apply(instance, (ingredient, result, block, duration, maxHeight) -> {
        StackingCultivatingRecipeParams params = new StackingCultivatingRecipeParams();
        params.ingredients.add(ingredient);
        params.result = result;
        params.blockToRender = block;
        params.processingDuration = duration;
        params.maxHeight = maxHeight;
        return params;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, StackingCultivatingRecipeParams> STREAM_CODEC =
            ProcessingRecipeParams.streamCodec(StackingCultivatingRecipeParams::new);


    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ProcessingOutput.STREAM_CODEC.encode(buffer, this.result);
        buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(this.blockToRender));
        buffer.writeInt(this.maxHeight);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        this.result = ProcessingOutput.STREAM_CODEC.decode(buffer);
        this.blockToRender = BuiltInRegistries.BLOCK.get(buffer.readResourceLocation());
        this.maxHeight = buffer.readInt();
    }
}
