package euphy.upo.create_cultivation.content.recipes;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import euphy.upo.create_cultivation.registry.CCRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class StackingCultivatingRecipe extends ProcessingRecipe<Container> implements IStackingCultivatingRecipe {

    protected ProcessingOutput result;
    protected int maxHeight;
    protected Block blockToRender;

    public StackingCultivatingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CCRecipes.STACKING_CULTIVATING, params);
        this.blockToRender = Blocks.AIR;
        this.maxHeight = 3;
    }
    @Override
    public void readAdditional(JsonObject json) {
        super.readAdditional(json);

        if (json.has("processingDuration")) {
            this.processingDuration = GsonHelper.getAsInt(json, "processingDuration");
        }

        this.result = ProcessingOutput.deserialize(GsonHelper.getAsJsonObject(json, "result"));
        this.blockToRender = BuiltInRegistries.BLOCK.get(new ResourceLocation(GsonHelper.getAsString(json, "block_to_render")));
        this.maxHeight = GsonHelper.getAsInt(json, "maxHeight", 3);
    }

    @Override
    public void writeAdditional(JsonObject json) {
        super.writeAdditional(json);

        if (this.processingDuration > 0) {
            json.addProperty("processingDuration", this.processingDuration);
        }

        json.add("result", this.result.serialize());
        json.addProperty("block_to_render", BuiltInRegistries.BLOCK.getKey(this.blockToRender).toString());
        if (this.maxHeight != 3) {
            json.addProperty("maxHeight", this.maxHeight);
        }
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);
        this.result = ProcessingOutput.read(buffer);
        this.blockToRender = BuiltInRegistries.BLOCK.get(buffer.readResourceLocation());
        this.maxHeight = buffer.readVarInt();
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);
        this.result.write(buffer);
        buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(this.blockToRender));
        buffer.writeVarInt(this.maxHeight);
    }
    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pContainer.isEmpty() || pContainer.getItem(0).isEmpty())
            return false;
        if (ingredients.isEmpty())
            return false;
        return ingredients.get(0).test(pContainer.getItem(0));
    }

    @Override
    public ProcessingOutput getResult() {
        return this.result;
    }

    @Override
    public int getMaxHeight() {
        return this.maxHeight;
    }

    @Override
    public Block getBlockToRender() {
        return this.blockToRender;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CCRecipes.STACKING_CULTIVATING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CCRecipes.STACKING_CULTIVATING.getType();
    }

    @Override
    protected int getMaxInputCount() { return 1; }

    @Override
    protected int getMaxOutputCount() { return 0; }

    public static class Serializer extends ProcessingRecipeSerializer<StackingCultivatingRecipe> {
        public Serializer() {
            super(StackingCultivatingRecipe::new);
        }
    }
}