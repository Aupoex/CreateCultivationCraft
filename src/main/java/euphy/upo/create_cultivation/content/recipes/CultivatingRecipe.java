package euphy.upo.create_cultivation.content.recipes;

import com.google.gson.JsonObject;
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

public class CultivatingRecipe extends ProcessingRecipe<Container> implements ICultivatingRecipe {


    protected Block cropBlock;
    protected int height;

    public CultivatingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CCRecipes.CULTIVATING, params);
        this.cropBlock = Blocks.AIR;
        this.height = 1;
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
    public void readAdditional(JsonObject json) {

        super.readAdditional(json);

        if (json.has("processingDuration")) {
            this.processingDuration = GsonHelper.getAsInt(json, "processingDuration");
        }

        // 读取您的自定义字段
        this.cropBlock = BuiltInRegistries.BLOCK.get(new ResourceLocation(GsonHelper.getAsString(json, "crop_block")));
        this.height = GsonHelper.getAsInt(json, "height", 1);
    }

    @Override
    public void writeAdditional(JsonObject json) {
        super.writeAdditional(json);


        if (this.processingDuration > 0) {
            json.addProperty("processingDuration", this.processingDuration);
        }

        json.addProperty("crop_block", BuiltInRegistries.BLOCK.getKey(this.cropBlock).toString());
        if (this.height > 1) {
            json.addProperty("height", this.height);
        }
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);

        this.cropBlock = BuiltInRegistries.BLOCK.get(buffer.readResourceLocation());
        this.height = buffer.readVarInt();
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);

        buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(this.cropBlock));
        buffer.writeVarInt(this.height);
    }

    @Override
    public Block getCropBlock() {
        return this.cropBlock;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CCRecipes.CULTIVATING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CCRecipes.CULTIVATING.getType();
    }

    @Override
    protected int getMaxInputCount() { return 1; }

    @Override
    protected int getMaxOutputCount() { return 8; }


    public static class Serializer extends ProcessingRecipeSerializer<CultivatingRecipe> {
        public Serializer() {
            super(CultivatingRecipe::new);
        }
    }
}