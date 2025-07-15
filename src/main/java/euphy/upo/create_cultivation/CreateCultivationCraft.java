package euphy.upo.create_cultivation;

import com.simibubi.create.foundation.data.CreateRegistrate;
import euphy.upo.create_cultivation.datagen.DataGenerators;
import euphy.upo.create_cultivation.registry.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;


@Mod(CreateCultivationCraft.MODID)
public class CreateCultivationCraft {
    public static final String MODID = "create_cultivation";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab(CCCreativeModeTabs.MAIN_TAB.getKey());
    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    public CreateCultivationCraft(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        //NeoForge.EVENT_BUS.register(this);
        CCBlocks.register();
        CCBlockEntities.register();
        CCCreativeModeTabs.register(modEventBus);
        CCRecipes.register(modEventBus);
        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(DataGenerators::gatherData);
        modEventBus.addListener(CCBlockEntities::registerCapabilities);
    }
    private void commonSetup(FMLCommonSetupEvent event) {

        event.enqueueWork(CCStress::registerAllStressValues);
        //event.enqueueWork(CCTallCrops::register);
    }
}
