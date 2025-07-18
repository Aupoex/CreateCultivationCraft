package euphy.upo.create_cultivation.registry;

import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.content.recipes.CCRecipeTypeInfo;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CCRecipes {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateCultivationCraft.MODID);

    private static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CreateCultivationCraft.MODID);

    public static final CCRecipeTypeInfo CULTIVATING =
            register("cultivating", CultivatingRecipe.Serializer::new);

    public static final CCRecipeTypeInfo STACKING_CULTIVATING =
            register("stacking_cultivating", StackingCultivatingRecipe.Serializer::new);

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
        TYPES.register(modBus);
    }

    private static CCRecipeTypeInfo register(String name, Supplier<? extends RecipeSerializer<?>> serializerSupplier) {
        return new CCRecipeTypeInfo(name, serializerSupplier, SERIALIZERS, TYPES);
    }
}