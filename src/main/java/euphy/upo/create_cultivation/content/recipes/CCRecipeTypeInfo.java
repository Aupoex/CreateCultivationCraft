package euphy.upo.create_cultivation.content.recipes;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;


public class CCRecipeTypeInfo implements IRecipeTypeInfo {

    private final ResourceLocation id;

    private final RegistryObject<RecipeSerializer<?>> serializer;
    private final RegistryObject<RecipeType<?>> type;

    public CCRecipeTypeInfo(String name, Supplier<? extends RecipeSerializer<?>> serializerSupplier,
                            DeferredRegister<RecipeSerializer<?>> serializerRegister,
                            DeferredRegister<RecipeType<?>> typeRegister) {
        this.id = new ResourceLocation("create_cultivation", name);
        this.serializer = serializerRegister.register(name, serializerSupplier);
        this.type = typeRegister.register(name, () -> RecipeType.simple(this.id));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }


    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer.get();
    }

    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) type.get();
    }


    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {

        return world.getRecipeManager().getRecipeFor(this.getType(), inv, world);
    }

}