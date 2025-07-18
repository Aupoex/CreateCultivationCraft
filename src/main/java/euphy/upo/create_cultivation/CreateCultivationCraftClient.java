package euphy.upo.create_cultivation;

import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseRenderer;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankRenderer;
import euphy.upo.create_cultivation.ponder.CCPonderPlugin;
import euphy.upo.create_cultivation.registry.CCBlockEntities;
import euphy.upo.create_cultivation.registry.CCPartialModels;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = CreateCultivationCraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreateCultivationCraftClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CCPartialModels.init();

            PonderIndex.addPlugin(new CCPonderPlugin());
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerBlockEntityRenderer(CCBlockEntities.CULTIVATION_BASE.get(), CultivationBaseRenderer::new);
        event.registerBlockEntityRenderer(CCBlockEntities.CULTIVATION_TANK.get(), CultivationTankRenderer::new);
    }
}