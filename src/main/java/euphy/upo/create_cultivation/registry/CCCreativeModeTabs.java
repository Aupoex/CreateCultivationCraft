package euphy.upo.create_cultivation.registry;

import euphy.upo.create_cultivation.CreateCultivationCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CCCreativeModeTabs {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateCultivationCraft.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TAB_REGISTER.register("main_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(CCBlocks.CULTIVATION_TANK.get()))
                    .title(Component.translatable("creativetab.create_cultivation.main_tab"))
                    .build()
    );

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }
}