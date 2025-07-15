package euphy.upo.create_cultivation.compat.jade;

import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlock;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements snownee.jade.api.IWailaPlugin {

    public static final ResourceLocation CULTIVATION_TANK_PROVIDER = ResourceLocation.fromNamespaceAndPath(CreateCultivationCraft.MODID, "cultivation_tank");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CultivationTankJadeProvider.INSTANCE, CultivationTankBlock.class);
    }
}