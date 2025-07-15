package euphy.upo.create_cultivation;

import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseRenderer;
import euphy.upo.create_cultivation.ponder.CCPonderPlugin;
import euphy.upo.create_cultivation.registry.CCBlockEntities;
import euphy.upo.create_cultivation.registry.CCPartialModels;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankRenderer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = CreateCultivationCraft.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreateCultivationCraftClient {
    public CreateCultivationCraftClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CCPartialModels::init);
        PonderIndex.addPlugin(new CCPonderPlugin());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CCBlockEntities.CULTIVATION_BASE.get(), CultivationBaseRenderer::new);
        event.registerBlockEntityRenderer(CCBlockEntities.CULTIVATION_TANK.get(), CultivationTankRenderer::new);
    }
}
