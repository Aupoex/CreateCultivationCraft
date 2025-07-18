package euphy.upo.create_cultivation;

import com.simibubi.create.foundation.data.CreateRegistrate;
import euphy.upo.create_cultivation.datagen.DataGenerators;
import euphy.upo.create_cultivation.registry.CCBlockEntities;
import euphy.upo.create_cultivation.registry.CCBlocks;
import euphy.upo.create_cultivation.registry.CCCreativeModeTabs;
import euphy.upo.create_cultivation.registry.CCRecipes;
import euphy.upo.create_cultivation.registry.CCStress;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateCultivationCraft.MODID)
public class CreateCultivationCraft {
    public static final String MODID = "create_cultivation";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab(CCCreativeModeTabs.MAIN_TAB.getKey());

    public CreateCultivationCraft() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CCBlocks.register();
        CCBlockEntities.register();
        CCCreativeModeTabs.register(modEventBus);
        CCRecipes.register(modEventBus);
        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(this::commonSetup);
        //modEventBus.addListener(DataGenerators::gatherData);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CCStress::registerAllStressValues);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }
}