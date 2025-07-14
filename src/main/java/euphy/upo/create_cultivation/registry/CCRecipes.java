package euphy.upo.create_cultivation.registry;

import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.content.recipes.CCRecipeTypeInfo;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class CCRecipes {

    private static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, CreateCultivationCraft.MODID);

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateCultivationCraft.MODID);


    public static final CCRecipeTypeInfo<CultivatingRecipe> CULTIVATING =
            register("cultivating", CultivatingRecipe.Serializer::new);

    public static final CCRecipeTypeInfo<StackingCultivatingRecipe> STACKING_CULTIVATING =
            register("stacking_cultivating", StackingCultivatingRecipe.Serializer::new);


    public static void register(IEventBus modBus) {
        TYPES.register(modBus);
        SERIALIZERS.register(modBus);
    }

    private static <R extends Recipe<?>> CCRecipeTypeInfo<R> register(String name, Supplier<? extends RecipeSerializer<R>> serializerSupplier) {
        return new CCRecipeTypeInfo<>(name, serializerSupplier, SERIALIZERS, TYPES);
    }
}
